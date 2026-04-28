package com.example.projectdexv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipoCardAdapter extends RecyclerView.Adapter<EquipoCardAdapter.ViewHolder> {

    //Declaro las variables
    private List<Equipo> equiposList;
    private Context context;
    private Map<Integer, String> pokemonNombresCache = new HashMap<>();
    private OnPokemonClickListener listener;

    //https://stackoverflow.com/questions/7085999/creating-object-from-onclicklistener-interface
    public interface OnPokemonClickListener {
        void onPokemonClick(int pokemonId);
    }

    //Constructor del adaptador de cards, como todos los demás adaptadores, tiene contexto y la lista
    public EquipoCardAdapter(List<Equipo> equiposList, Context context) {
        this.equiposList = equiposList;
        this.context = context;
    }

    //Getter setter
    public void setOnPokemonClickListener(OnPokemonClickListener listener) {
        this.listener = listener;
    }

    //https://developer.android.com/develop/ui/views/layout/recyclerview?hl=es-419
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.equipotarjeta, parent, false);
        return new ViewHolder(view);
    }

    //https://developer.android.com/develop/ui/views/layout/recyclerview?hl=es-419
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Equipo equipo = equiposList.get(position);
        holder.tvNombreEquipo.setText(equipo.getNombre());
        List<Integer> pokemonIds = equipo.getPokemonIds();
        //Esto es una versión reducida del otro, asignamos fotos y nombres
        ImageView[] imageViews = {
                holder.imgPokemon1, holder.imgPokemon2, holder.imgPokemon3,
                holder.imgPokemon4, holder.imgPokemon5, holder.imgPokemon6
        };
        TextView[] textViews = {
                holder.textPokemon1, holder.textPokemon2, holder.textPokemon3,
                holder.textPokemon4, holder.textPokemon5, holder.textPokemon6
        };
        for (int i = 0; i < 6; i++) {
            final int numero = i;
            if (i < pokemonIds.size()) {
                int pokemonId = pokemonIds.get(i);
                String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png";
                Glide.with(context).load(imageUrl).placeholder(R.drawable.pokeballllena).into(imageViews[i]);
                textViews[i].setText("#" + pokemonId);
                textViews[i].setVisibility(View.VISIBLE);
                imageViews[i].setVisibility(View.VISIBLE);
                cargarNombrePokemon(pokemonId, textViews[i]);
                imageViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && numero < pokemonIds.size()) {
                            int clickedPokemonId = pokemonIds.get(numero);
                            listener.onPokemonClick(clickedPokemonId);
                        }
                    }
                });
                textViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && numero < pokemonIds.size()) {
                            int clickedPokemonId = pokemonIds.get(numero);
                            listener.onPokemonClick(clickedPokemonId);
                        }
                    }
                });
            } else {
                imageViews[i].setImageResource(R.drawable.pokeball);
                textViews[i].setText("Vacío");
                textViews[i].setVisibility(View.VISIBLE);
                imageViews[i].setVisibility(View.VISIBLE);
                imageViews[i].setOnClickListener(null);
                textViews[i].setOnClickListener(null);
                imageViews[i].setClickable(false);
                textViews[i].setClickable(false);
            }
        }
    }

    //Cargar los nombres de los pokemon desde el pokemonNombresCache
    private void cargarNombrePokemon(int pokemonId, TextView textView) {
        if (pokemonNombresCache.containsKey(pokemonId)) {
            String nombre = pokemonNombresCache.get(pokemonId);
            textView.setText(nombre);
            return;
        }
        //Lo mismo que en equipo adaptador
        new Thread(() -> {
            try {
                String nombre = obtenerNombrePokemonDesdeAPI(pokemonId);
                if (nombre != null) {
                    pokemonNombresCache.put(pokemonId, nombre);

                    if (context != null) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            textView.setText(nombre);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //Métodos copiados del equipoadaptador
    private String obtenerNombrePokemonDesdeAPI(int pokemonId) {
        try {
            URL url = new java.net.URL("https://pokeapi.co/api/v2/pokemon/" + pokemonId + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject json = new JSONObject(response.toString());
            String nombre = json.getString("name");
            return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        } catch (Exception e) {
            return "Pokémon #" + pokemonId;
        }
    }

    @Override
    public int getItemCount() {
        return equiposList.size();
    }

    //Esta clase es prácticamente igual que equipoadaptador
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEquipo;
        ImageView imgPokemon1, imgPokemon2, imgPokemon3, imgPokemon4, imgPokemon5, imgPokemon6;
        TextView textPokemon1, textPokemon2, textPokemon3, textPokemon4, textPokemon5, textPokemon6;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreEquipo = itemView.findViewById(R.id.textViewNombreEquipo);

            imgPokemon1 = itemView.findViewById(R.id.imgPokemon1);
            imgPokemon2 = itemView.findViewById(R.id.imgPokemon2);
            imgPokemon3 = itemView.findViewById(R.id.imgPokemon3);
            imgPokemon4 = itemView.findViewById(R.id.imgPokemon4);
            imgPokemon5 = itemView.findViewById(R.id.imgPokemon5);
            imgPokemon6 = itemView.findViewById(R.id.imgPokemon6);

            textPokemon1 = itemView.findViewById(R.id.textPokemon1);
            textPokemon2 = itemView.findViewById(R.id.textPokemon2);
            textPokemon3 = itemView.findViewById(R.id.textPokemon3);
            textPokemon4 = itemView.findViewById(R.id.textPokemon4);
            textPokemon5 = itemView.findViewById(R.id.textPokemon5);
            textPokemon6 = itemView.findViewById(R.id.textPokemon6);
        }
    }
}