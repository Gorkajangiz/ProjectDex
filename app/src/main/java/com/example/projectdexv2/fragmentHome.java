package com.example.projectdexv2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragmentHome extends Fragment {
    private PokemonDataSource dataSource;
    private RecyclerView recyclerViewFavoritos;
    private RecyclerView recyclerViewEquipos;
    private PokemonFavoritoAdapter favoritosAdapter;
    private EquipoCardAdapter equiposAdapter;
    private List<Pokemon> pokemonFavoritos;
    private List<Equipo> listaEquipos;
    private TextView txtBienvenida;
    private ImageView imgPerfil;
    private TextView txtNoFavoritos;
    private TextView txtNoEquipos;
    private Handler mainHandler;
    private ExecutorService executor;
    private Set<Integer> pokemonIdsCargados = new HashSet<>();
    private boolean isLoadingFavoritos = false;
    private int currentPokemonId = 0;
    private List<Equipo> equiposDisponibles = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newFixedThreadPool(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentohome, container, false);
        //Asigno todos los datos al crear
        txtBienvenida = view.findViewById(R.id.txtBienvenida);
        imgPerfil = view.findViewById(R.id.imgPerfil);
        recyclerViewFavoritos = view.findViewById(R.id.recyclerViewFavoritos);
        recyclerViewEquipos = view.findViewById(R.id.recyclerViewEquipos);
        txtNoFavoritos = view.findViewById(R.id.txtNoFavoritos);
        txtNoEquipos = view.findViewById(R.id.txtNoEquipos);
        TextView txtNombreEntrenador = view.findViewById(R.id.txtNombreEntrenador);
        TextView txtPokemonFavorito = view.findViewById(R.id.txtPokemonFavorito);
        TextView txtNumeroEquipos = view.findViewById(R.id.txtNumeroEquipos);
        TextView txtNumeroFavoritos = view.findViewById(R.id.txtNumeroFavoritos);
        txtBienvenida.setText("Bienvenido de vuelta Entrenador!");
        txtNombreEntrenador.setText("Grokajangiz");
        txtPokemonFavorito.setText("Pokémon favorito: Gardevoir");
        txtNumeroEquipos.setText("Número de equipos: 30");
        txtNumeroFavoritos.setText("Número de favoritos: 10");

        //Uso picasso para cargar una foto de perfil de prueba
        Picasso.get().load(R.drawable.foto_perfil).placeholder(R.drawable.foto_perfil).into(imgPerfil);
        dataSource = new PokemonDataSource();
        pokemonFavoritos = new ArrayList<>();
        listaEquipos = new ArrayList<>();
        LinearLayoutManager favoritosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFavoritos.setLayoutManager(favoritosLayoutManager);
        favoritosAdapter = new PokemonFavoritoAdapter(pokemonFavoritos, getContext());
        recyclerViewFavoritos.setAdapter(favoritosAdapter);
        LinearLayoutManager equiposLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewEquipos.setLayoutManager(equiposLayoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewEquipos);
        equiposAdapter = new EquipoCardAdapter(listaEquipos, requireContext());
        recyclerViewEquipos.setAdapter(equiposAdapter);
        equiposAdapter.setOnPokemonClickListener(new EquipoCardAdapter.OnPokemonClickListener() {
            @Override
            public void onPokemonClick(int pokemonId) {
                mostrarDetallePokemon(pokemonId);
            }
        });
        txtNoFavoritos.setText("Cargando favoritos...");
        txtNoFavoritos.setVisibility(View.VISIBLE);
        txtNoEquipos.setText("Cargando equipos...");
        txtNoEquipos.setVisibility(View.VISIBLE);
        cargarDatosDesdeBD();
        favoritosAdapter.setOnPokemonClickListener(new PokemonFavoritoAdapter.OnPokemonClickListener() {
            @Override
            public void onPokemonClick(Pokemon pokemon) {
                mostrarDetallePokemon(pokemon.getNumeroPokedex());
            }
        });

        return view;
    }

    private void cargarDatosDesdeBD() {
        if (isLoadingFavoritos) {
            return;
        }
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(3);
        }
        isLoadingFavoritos = true;
        pokemonIdsCargados.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Integer> idsFavoritos = dataSource.obtenerFavoritos();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (idsFavoritos.isEmpty()) {
                                    txtNoFavoritos.setText("No tienes Pokémon favoritos aún");
                                    txtNoFavoritos.setVisibility(View.VISIBLE);
                                    isLoadingFavoritos = false;
                                } else {
                                    txtNoFavoritos.setVisibility(View.GONE);
                                    pokemonFavoritos.clear();
                                    favoritosAdapter.notifyDataSetChanged();
                                    cargarPokemonsFavoritos(idsFavoritos);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtNoFavoritos.setText("Error cargando favoritos");
                                txtNoFavoritos.setVisibility(View.VISIBLE);
                                isLoadingFavoritos = false;
                            }
                        });
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Equipo> equipos = dataSource.obtenerEquiposConPokemon();

                    if (getActivity() != null) {
                        if (equipos.isEmpty()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtNoEquipos.setText("No has creado equipos aún");
                                    txtNoEquipos.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtNoEquipos.setVisibility(View.GONE);
                                    listaEquipos.clear();
                                    listaEquipos.addAll(equipos);
                                    equiposAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtNoEquipos.setText("Error cargando equipos");
                                txtNoEquipos.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void cargarPokemonsFavoritos(ArrayList<Integer> idsFavoritos) {
        if (executor == null || executor.isShutdown()) {
            isLoadingFavoritos = false;
            return;
        }
        for (int pokemonId : idsFavoritos) {
            if (pokemonIdsCargados.contains(pokemonId)) {
                continue;
            }
            pokemonIdsCargados.add(pokemonId);
            final int finalPokemonId = pokemonId;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Pokemon pokemon = obtenerPokemonDesdeAPI(finalPokemonId);
                        if (pokemon != null && getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean yaExiste = false;
                                    for (Pokemon p : pokemonFavoritos) {
                                        if (p.getNumeroPokedex() == finalPokemonId) {
                                            yaExiste = true;
                                            break;
                                        }
                                    }

                                    if (!yaExiste) {
                                        pokemonFavoritos.add(pokemon);
                                        favoritosAdapter.notifyItemInserted(pokemonFavoritos.size() - 1);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        pokemonIdsCargados.remove(finalPokemonId);
                    }
                }
            });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoadingFavoritos = false;
            }
        }, 2000);
    }

    private Pokemon obtenerPokemonDesdeAPI(int id) {
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject json = new JSONObject(response.toString());
            return ParseadorApis.parsear(json);
        } catch (Exception e) {
            return new Pokemon(id, "Pokémon " + id, "Normal", null, 1, null, null, 0, 0, 0, 0, 0, 0, 0, 0, "No se pudo cargar la información", null);
        }
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
        btnCerrar.setOnClickListener(v -> {
            ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView();
            rootView.removeView(popupView);
        });
        popupView.findViewById(R.id.detallePopup).setOnClickListener(v -> {
            ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView();
            rootView.removeView(popupView);
        });
        LinearLayout btnFavorito = popupView.findViewById(R.id.btnFavorito);
        btnFavorito.setOnClickListener(v -> {
            agregarAFavoritos(currentPokemonId, popupView);
        });
        LinearLayout btnReservado = popupView.findViewById(R.id.btnReservado);
        btnReservado.setOnClickListener(v -> {
            cargarEquiposParaSeleccion(currentPokemonId, popupView);
        });
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
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject json = new JSONObject(response.toString());
                Pokemon pokemon = ParseadorApis.parsear(json);
                mainHandler.post(() -> {
                    actualizarPopupConDatos(popupView, pokemon);
                });
            } catch (Exception e) {
                e.printStackTrace();
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
        if (pokemon.getTipoUno() != null) {
            lytTipos.addView(crearTipoView(pokemon.getTipoUno()));
        }
        if (pokemon.getTipoDos() != null && !pokemon.getTipoDos().isEmpty()) {
            lytTipos.addView(crearTipoView(pokemon.getTipoDos()));
        }
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
        if (pokemon.getHabilidad() != null) {
            habilidades.append(capitalizar(pokemon.getHabilidad().replace("-", " ")));
        }
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

    private void agregarAFavoritos(int pokemonId, View popupView) {
        new Thread(() -> {
            try {
                if (dataSource == null) {
                    dataSource = new PokemonDataSource();
                }
                Thread.sleep(300);
                boolean exito = dataSource.agregarFavorito(pokemonId);
                mainHandler.post(() -> {
                    if (exito) {
                        Toast.makeText(requireContext(), "¡Pokémon añadido a favoritos!", Toast.LENGTH_SHORT).show();
                        TextView btnText = popupView.findViewById(R.id.tvTextoFavorito);
                        if (btnText != null) {
                            String textoOriginal = btnText.getText().toString();
                            btnText.setText("¡AÑADIDO!");
                            new Handler().postDelayed(() -> {
                                btnText.setText(textoOriginal);
                            }, 2000);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error al añadir a favoritos", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void cargarEquiposParaSeleccion(final int pokemonId, View popupView) {
        new Thread(() -> {
            try {
                if (dataSource == null) {
                    dataSource = new PokemonDataSource();
                }
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
                mainHandler.post(() -> {
                    mostrarPopupSeleccionEquipo(pokemonId, popupView);
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Error al cargar equipos", Toast.LENGTH_SHORT).show();
                });
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
        Toast.makeText(requireContext(), "Seleccionar equipo para " + nombrePokemon, Toast.LENGTH_SHORT).show();
        ViewGroup rootView = (ViewGroup) requireActivity().getWindow().getDecorView();
        rootView.removeView(popupView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataSource != null) {
            if (!isLoadingFavoritos) {
                if (executor == null || executor.isShutdown()) {
                    executor = Executors.newFixedThreadPool(3);
                }
                cargarDatosDesdeBD();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}