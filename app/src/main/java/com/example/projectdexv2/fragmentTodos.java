package com.example.projectdexv2;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class fragmentTodos extends Fragment implements ImagenAdaptador.OnPokemonClickListener {

    //Creo las variables
    private ArrayList<PokemonBasico> pokedexCompleta = new ArrayList<>();
    private ImagenAdaptador adaptador;
    private RecyclerView rv;
    private SearchView sv;
    private boolean cargado = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private int currentPokemonId = 0;
    private PokemonDataSource dataSource;
    private List<Equipo> equiposDisponibles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentotodos, container, false);
        dataSource = new PokemonDataSource();
        rv = view.findViewById(R.id.recyclerView);
        sv = view.findViewById(R.id.searchView);
        adaptador = new ImagenAdaptador(pokedexCompleta, requireContext());
        adaptador.setOnPokemonClickListener(this);
        rv.setAdapter(adaptador);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { return false; }
            @Override
            public boolean onQueryTextChange(String s) {
                filtrarPokemon(s);
                return true;
            }
        });
        if (!cargado) {
            cargarTodosPokemon();
            cargado = true;
        }
        return view;
    }

    private void cargarEquiposParaSeleccion(final int pokemonId, View popupView) {
        new Thread(() -> {
            try {
                if (dataSource == null) dataSource = new PokemonDataSource();
                Thread.sleep(300);
                ArrayList<Long> teamIds = dataSource.obtenerEquipos();
                List<Equipo> equipos = new ArrayList<>();
                for (Long teamId : teamIds) {
                    String nombre = dataSource.obtenerNombreEquipo(teamId);
                    ArrayList<Integer> pokemonIds = dataSource.obtenerPokemonDeEquipo(teamId);
                    equipos.add(new Equipo(teamId.intValue(), nombre, pokemonIds));
                }
                equiposDisponibles.clear();
                equiposDisponibles.addAll(equipos);
                mainHandler.post(() -> mostrarPopupSeleccionEquipo(pokemonId, popupView));
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(requireContext(), "Error al cargar equipos", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void mostrarPopupSeleccionEquipo(int pokemonId, View popupViewDetalle) {
        View seleccionView = LayoutInflater.from(requireContext()).inflate(R.layout.equipo_seleccion_popup, null);
        TextView tvNombreDetalle = popupViewDetalle.findViewById(R.id.tvNombreDetalle);
        String nombrePokemon = tvNombreDetalle.getText().toString();
        setupPopupSeleccion(seleccionView, pokemonId, nombrePokemon);
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView();
        rootView.addView(seleccionView);
    }

    private void setupPopupSeleccion(View popupView, int pokemonId, String nombrePokemon) {
        TextView tvPokemonNombre = popupView.findViewById(R.id.tvPokemonNombreSeleccion);
        tvPokemonNombre.setText(nombrePokemon);
        ImageView btnCerrar = popupView.findViewById(R.id.btnCerrarSeleccion);
        btnCerrar.setOnClickListener(v -> ((ViewGroup) requireActivity().getWindow().getDecorView()).removeView(popupView));
        popupView.findViewById(R.id.equipoSeleccionPopup).setOnClickListener(v -> ((ViewGroup) requireActivity().getWindow().getDecorView()).removeView(popupView));
        Spinner spinnerEquipos = popupView.findViewById(R.id.spinnerEquipos);
        List<String> nombresEquipos = new ArrayList<>();
        for (Equipo equipo : equiposDisponibles) nombresEquipos.add(equipo.getNombre());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombresEquipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipos.setAdapter(adapter);
        popupView.findViewById(R.id.btnNuevoEquipo).setOnClickListener(v -> {
            int selectedPosition = spinnerEquipos.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < equiposDisponibles.size()) {
                Equipo equipoSeleccionado = equiposDisponibles.get(selectedPosition);
                agregarPokemonAEquipo(equipoSeleccionado.getId(), pokemonId, popupView);
                ((ViewGroup) requireActivity().getWindow().getDecorView()).removeView(popupView);
            } else Toast.makeText(requireContext(), "Selecciona un equipo primero", Toast.LENGTH_SHORT).show();
        });
        TextView btnTexto = popupView.findViewById(R.id.tvTextoBotonEquipo);
        btnTexto.setText("AÑADIR AL EQUIPO");
    }

    private void agregarPokemonAEquipo(int equipoId, int pokemonId, View popupViewDetalle) {
        new Thread(() -> {
            try {
                if (dataSource == null) dataSource = new PokemonDataSource();
                Thread.sleep(300);
                ArrayList<Integer> pokemonIds = dataSource.obtenerPokemonDeEquipo(equipoId);
                if (pokemonIds.size() >= 6) {
                    mainHandler.post(() -> Toast.makeText(requireContext(), "El equipo está lleno (max 6 Pokémon)", Toast.LENGTH_SHORT).show());
                    return;
                }
                boolean exito = dataSource.agregarPokemonAEquipo(equipoId, pokemonId, pokemonIds.size() + 1);
                mainHandler.post(() -> {
                    if (exito) {
                        Toast.makeText(requireContext(), "Pokémon añadido al equipo", Toast.LENGTH_SHORT).show();
                        TextView btnText = popupViewDetalle.findViewById(R.id.tvTextoFavorito);
                        if (btnText != null) {
                            String textoOriginal = btnText.getText().toString();
                            btnText.setText("¡AÑADIDO A EQUIPO!");
                            new Handler().postDelayed(() -> btnText.setText(textoOriginal), 2000);
                        }
                    } else Toast.makeText(requireContext(), "Error al añadir al equipo", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void agregarAFavoritos(int pokemonId, View popupView) {
        new Thread(() -> {
            try {
                if (dataSource == null) dataSource = new PokemonDataSource();
                Thread.sleep(300);
                boolean exito = dataSource.agregarFavorito(pokemonId);
                mainHandler.post(() -> {
                    if (exito) {
                        Toast.makeText(requireContext(), "Pokémon añadido a favoritos", Toast.LENGTH_SHORT).show();
                        TextView btnText = popupView.findViewById(R.id.tvTextoFavorito);
                        if (btnText != null) {
                            String textoOriginal = btnText.getText().toString();
                            btnText.setText("¡AÑADIDO!");
                            new Handler().postDelayed(() -> btnText.setText(textoOriginal), 2000);
                        }
                    } else Toast.makeText(requireContext(), "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> Toast.makeText(requireContext(), "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onPokemonClick(PokemonBasico pokemonBasico) {
        mostrarDetallePokemon(pokemonBasico.getNumero());
    }

    private void mostrarDetallePokemon(int pokemonId) {
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.pokemon_detalle_popup, null);
        setupPopupView(popupView, pokemonId);
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView();
        rootView.addView(popupView);
    }

    private void setupPopupView(View popupView, int pokemonId) {
        currentPokemonId = pokemonId;
        ImageView btnCerrar = popupView.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> ((ViewGroup) requireActivity().getWindow().getDecorView()).removeView(popupView));
        popupView.findViewById(R.id.detallePopup).setOnClickListener(v -> ((ViewGroup) requireActivity().getWindow().getDecorView()).removeView(popupView));
        LinearLayout btnFavorito = popupView.findViewById(R.id.btnFavorito);
        btnFavorito.setOnClickListener(v -> agregarAFavoritos(currentPokemonId, popupView));
        LinearLayout btnReservado = popupView.findViewById(R.id.btnReservado);
        btnReservado.setOnClickListener(v -> cargarEquiposParaSeleccion(currentPokemonId, popupView));
        ImageView ivPokemon = popupView.findViewById(R.id.ivPokemonDetalle);
        String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png";
        Glide.with(requireContext()).load(imageUrl).placeholder(R.drawable.pokeballllena).into(ivPokemon);
        TextView tvNumero = popupView.findViewById(R.id.tvNumeroDetalle);
        tvNumero.setText(String.format("#%03d", pokemonId));
        new Thread(() -> {
            try {
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemonId + "/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();
                JSONObject json = new JSONObject(response.toString());
                Pokemon pokemon = ParseadorApis.parsear(json);
                mainHandler.post(() -> actualizarPopupConDatos(popupView, pokemon));
            } catch (Exception e) {
                mainHandler.post(() -> {
                    TextView tvNombre = popupView.findViewById(R.id.tvNombreDetalle);
                    tvNombre.setText("Pokémon #" + pokemonId);
                    Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void actualizarPopupConDatos(View popupView, Pokemon pokemon) {
        TextView tvNombre = popupView.findViewById(R.id.tvNombreDetalle);
        tvNombre.setText(pokemon.getNombre());
        TextView tvNumero = popupView.findViewById(R.id.tvNumeroDetalle);
        tvNumero.setText(String.format("#%03d", pokemon.getNumeroPokedex()));
        LinearLayout lytTipos = popupView.findViewById(R.id.lytTipos);
        lytTipos.removeAllViews();
        if (pokemon.getTipoUno() != null) lytTipos.addView(crearTipoView(pokemon.getTipoUno()));
        if (pokemon.getTipoDos() != null && !pokemon.getTipoDos().isEmpty()) lytTipos.addView(crearTipoView(pokemon.getTipoDos()));
        TextView tvDescripcion = popupView.findViewById(R.id.tvDescripcion);
        tvDescripcion.setText(pokemon.getDescripcion());
        TextView tvGrupoHuevo = popupView.findViewById(R.id.tvGrupoHuevo);
        tvGrupoHuevo.setText(pokemon.getGrupoHuevo());
        TextView tvGeneracion = popupView.findViewById(R.id.tvGeneracion);
        tvGeneracion.setText("Gen " + pokemon.getGeneracion());
        TextView tvAltura = popupView.findViewById(R.id.tvAltura);
        TextView tvPeso = popupView.findViewById(R.id.tvPeso);
        tvAltura.setText(String.format("%.1f m", pokemon.getAltura() / 10.0));
        tvPeso.setText(String.format("%.1f kg", pokemon.getPeso() / 10.0));
        TextView tvHabilidades = popupView.findViewById(R.id.tvHabilidades);
        StringBuilder habilidades = new StringBuilder();
        if (pokemon.getHabilidad() != null) habilidades.append(capitalizar(pokemon.getHabilidad().replace("-", " ")));
        if (pokemon.getHabilidadOculta() != null) {
            if (habilidades.length() > 0) habilidades.append(", ");
            habilidades.append(capitalizar(pokemon.getHabilidadOculta().replace("-", " ")));
        }
        tvHabilidades.setText(habilidades.toString());
        setupEstadisticasPopup(popupView, pokemon);
    }

    private TextView crearTipoView(String tipo) {
        TextView tipoView = new TextView(requireContext());
        tipoView.setText(tipo);
        tipoView.setPadding(16, 8, 16, 8);
        tipoView.setTextSize(14);
        tipoView.setTextColor(0xFFFFFFFF);
        tipoView.setTypeface(null, android.graphics.Typeface.BOLD);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(20);
        shape.setColor(getColorForTipo(tipo));
        tipoView.setBackground(shape);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 8, 0);
        tipoView.setLayoutParams(params);
        return tipoView;
    }

    private int getColorForTipo(String tipo) {
        if (tipo.equals("Normal")) return getResources().getColor(R.color.tipo_normal);
        else if (tipo.equals("Fuego")) return getResources().getColor(R.color.tipo_fuego);
        else if (tipo.equals("Agua")) return getResources().getColor(R.color.tipo_agua);
        else if (tipo.equals("Eléctrico")) return getResources().getColor(R.color.tipo_electrico);
        else if (tipo.equals("Planta")) return getResources().getColor(R.color.tipo_planta);
        else if (tipo.equals("Hielo")) return getResources().getColor(R.color.tipo_hielo);
        else if (tipo.equals("Lucha")) return getResources().getColor(R.color.tipo_lucha);
        else if (tipo.equals("Veneno")) return getResources().getColor(R.color.tipo_veneno);
        else if (tipo.equals("Tierra")) return getResources().getColor(R.color.tipo_tierra);
        else if (tipo.equals("Volador")) return getResources().getColor(R.color.tipo_volador);
        else if (tipo.equals("Psíquico")) return getResources().getColor(R.color.tipo_psiquico);
        else if (tipo.equals("Bicho")) return getResources().getColor(R.color.tipo_bicho);
        else if (tipo.equals("Roca")) return getResources().getColor(R.color.tipo_roca);
        else if (tipo.equals("Fantasma")) return getResources().getColor(R.color.tipo_fantasma);
        else if (tipo.equals("Dragón")) return getResources().getColor(R.color.tipo_dragon);
        else if (tipo.equals("Siniestro")) return getResources().getColor(R.color.tipo_siniestro);
        else if (tipo.equals("Acero")) return getResources().getColor(R.color.tipo_acero);
        else if (tipo.equals("Hada")) return getResources().getColor(R.color.tipo_hada);
        else return getResources().getColor(R.color.tipo_normal);
    }

    private void setupEstadisticasPopup(View popupView, Pokemon pokemon) {
        TextView tvHP = popupView.findViewById(R.id.tvHP);
        ProgressBar pbHP = popupView.findViewById(R.id.pbHP);
        tvHP.setText(String.valueOf(pokemon.getHp()));
        pbHP.setProgress(pokemon.getHp());
        TextView tvAtaque = popupView.findViewById(R.id.tvAtaque);
        ProgressBar pbAtaque = popupView.findViewById(R.id.pbAtaque);
        tvAtaque.setText(String.valueOf(pokemon.getAtaque()));
        pbAtaque.setProgress(pokemon.getAtaque());
        TextView tvDefensa = popupView.findViewById(R.id.tvDefensa);
        ProgressBar pbDefensa = popupView.findViewById(R.id.pbDefensa);
        tvDefensa.setText(String.valueOf(pokemon.getDefensa()));
        pbDefensa.setProgress(pokemon.getDefensa());
        TextView tvAtaqueEspecial = popupView.findViewById(R.id.tvAtaqueEspecial);
        ProgressBar pbAtaqueEspecial = popupView.findViewById(R.id.pbAtaqueEspecial);
        tvAtaqueEspecial.setText(String.valueOf(pokemon.getAtaqueEspecial()));
        pbAtaqueEspecial.setProgress(pokemon.getAtaqueEspecial());
        TextView tvDefensaEspecial = popupView.findViewById(R.id.tvDefensaEspecial);
        ProgressBar pbDefensaEspecial = popupView.findViewById(R.id.pbDefensaEspecial);
        tvDefensaEspecial.setText(String.valueOf(pokemon.getDefensaEspecial()));
        pbDefensaEspecial.setProgress(pokemon.getDefensaEspecial());
        TextView tvVelocidad = popupView.findViewById(R.id.tvVelocidad);
        ProgressBar pbVelocidad = popupView.findViewById(R.id.pbVelocidad);
        tvVelocidad.setText(String.valueOf(pokemon.getVelocidad()));
        pbVelocidad.setProgress(pokemon.getVelocidad());
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void cargarTodosPokemon() {
        new Thread(() -> {
            try {
                ArrayList<PokemonBasico> listaTotal = new ArrayList<>();
                String nextUrl = "https://pokeapi.co/api/v2/pokemon?limit=100";
                final int TOTAL_POKEMON = 1025;
                while (nextUrl != null && listaTotal.size() < TOTAL_POKEMON) {
                    URL url = new URL(nextUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray results = json.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject pokemon = results.getJSONObject(i);
                        String name = pokemon.getString("name");
                        String urlString = pokemon.getString("url");
                        String[] parts = urlString.split("/");
                        int id = Integer.parseInt(parts[parts.length - 1]);
                        if (id <= TOTAL_POKEMON) {
                            String nombreCapitalizado = name.substring(0, 1).toUpperCase() + name.substring(1);
                            listaTotal.add(new PokemonBasico(id, nombreCapitalizado));
                        }
                        if (listaTotal.size() >= TOTAL_POKEMON) break;
                    }
                    if (listaTotal.size() % 200 == 0 || listaTotal.size() >= TOTAL_POKEMON) {
                        final ArrayList<PokemonBasico> copiaParaUI = new ArrayList<>(listaTotal);
                        mainHandler.post(() -> {
                            pokedexCompleta.clear();
                            pokedexCompleta.addAll(copiaParaUI);
                            adaptador.actualizarLista(pokedexCompleta);
                        });
                    }
                    nextUrl = json.isNull("next") ? null : json.getString("next");
                    Thread.sleep(200);
                }
                mainHandler.post(() -> {
                    pokedexCompleta.clear();
                    pokedexCompleta.addAll(listaTotal);
                    adaptador.actualizarLista(pokedexCompleta);
                    precargarPrimerasImagenes();
                });
            } catch (Exception e) {
                mainHandler.post(this::cargarFallback);
            }
        }).start();
    }

    private void cargarFallback() {
        String[] primeros100 = {"Bulbasaur", "Ivysaur", "Venusaur", "Charmander", "Charmeleon", "Charizard", "Squirtle", "Wartortle", "Blastoise", "Caterpie", "Metapod", "Butterfree", "Weedle", "Kakuna", "Beedrill", "Pidgey", "Pidgeotto", "Pidgeot", "Rattata", "Raticate", "Spearow", "Fearow", "Ekans", "Arbok", "Pikachu", "Raichu", "Sandshrew", "Sandslash", "Nidoran♀", "Nidorina", "Nidoqueen", "Nidoran♂", "Nidorino", "Nidoking", "Clefairy", "Clefable", "Vulpix", "Ninetales", "Jigglypuff", "Wigglytuff", "Zubat", "Golbat", "Oddish", "Gloom", "Vileplume", "Paras", "Parasect", "Venonat", "Venomoth", "Diglett", "Dugtrio", "Meowth", "Persian", "Psyduck", "Golduck", "Mankey", "Primeape", "Growlithe", "Arcanine", "Poliwag", "Poliwhirl", "Poliwrath", "Abra", "Kadabra", "Alakazam", "Machop", "Machoke", "Machamp", "Bellsprout", "Weepinbell", "Victreebel", "Tentacool", "Tentacruel", "Geodude", "Graveler", "Golem", "Ponyta", "Rapidash", "Slowpoke", "Slowbro", "Magnemite", "Magneton", "Farfetch'd", "Doduo", "Dodrio", "Seel", "Dewgong", "Grimer", "Muk", "Shellder", "Cloyster", "Gastly", "Haunter", "Gengar", "Onix", "Drowzee", "Hypno", "Krabby", "Kingler", "Voltorb", "Electrode"};
        pokedexCompleta.clear();
        for (int i = 0; i < primeros100.length; i++) pokedexCompleta.add(new PokemonBasico(i + 1, primeros100[i]));
        adaptador.actualizarLista(pokedexCompleta);
    }

    private void filtrarPokemon(String texto) {
        if (pokedexCompleta.isEmpty()) return;
        ArrayList<PokemonBasico> listaFiltrada = new ArrayList<>();
        if (texto.isEmpty()) adaptador.actualizarLista(pokedexCompleta);
        else {
            for (PokemonBasico pokemon : pokedexCompleta) {
                if (pokemon.getNombre().toLowerCase().contains(texto.toLowerCase()) || String.valueOf(pokemon.getNumero()).contains(texto)) listaFiltrada.add(pokemon);
            }
            adaptador.actualizarLista(listaFiltrada);
        }
    }

    private void precargarPrimerasImagenes() {
        if (!isAdded() || getContext() == null || pokedexCompleta.isEmpty()) return;
        try {
            for (int i = 0; i < Math.min(30, pokedexCompleta.size()); i++) {
                PokemonBasico pokemon = pokedexCompleta.get(i);
                String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumero() + ".png";
                Glide.with(requireContext()).load(url).preload(120, 120);
            }
        } catch (Exception e) {}
    }
}