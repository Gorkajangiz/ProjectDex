package com.example.projectdexv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipoAdaptador extends RecyclerView.Adapter<EquipoAdaptador.ViewHolder> {

    //Declaro variables
    List<Equipo> equiposList;
    Context context;
    Map<Integer, String> cacheNombres = new HashMap<>();

    //El constructor, que tiene contexto y el equipo
    public EquipoAdaptador(List<Equipo> equiposList, Context context) {
        this.equiposList = equiposList;
        this.context = context;
    }

    //https://developer.android.com/develop/ui/views/layout/recyclerview?hl=es-419
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipotarjeta, parent, false);
        return new ViewHolder(vista);
    }

    //https://developer.android.com/develop/ui/views/layout/recyclerview?hl=es-419
    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Equipo equipoActual = equiposList.get(pos);

        holder.tvNombreEquipo.setText(equipoActual.getNombre());

        List<Integer> idsPokemon = equipoActual.getPokemonIds();

        //Arrays normales para poner los equipos
        ImageView[] imagenes = {holder.imgPokemon1, holder.imgPokemon2, holder.imgPokemon3, holder.imgPokemon4, holder.imgPokemon5, holder.imgPokemon6};
        TextView[] textos = {holder.textPokemon1, holder.textPokemon2, holder.textPokemon3, holder.textPokemon4, holder.textPokemon5, holder.textPokemon6};

        for (int i = 0; i < 6; i++) {
            if (i < idsPokemon.size()) {
                int idPokemon = idsPokemon.get(i);
                //Esta es la url donde se almacenan las imagenes de la API que las saco directamente de ella
                String urlImagen = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + idPokemon + ".png";
                Glide.with(context).load(urlImagen).placeholder(R.drawable.pokeballllena).into(imagenes[i]);
                textos[i].setText("#" + idPokemon);
                textos[i].setVisibility(View.VISIBLE);
                imagenes[i].setVisibility(View.VISIBLE);
                obtenerNombrePokemon(idPokemon, textos[i]);
            } else {
                imagenes[i].setImageResource(R.drawable.pokeball);
                textos[i].setText("Vacío");
                textos[i].setVisibility(View.VISIBLE);
                imagenes[i].setVisibility(View.VISIBLE);
            }
        }
    }

    //Esto saca los nombres de los opokemon
    void obtenerNombrePokemon(int id, TextView texto) {
        if (cacheNombres.containsKey(id)) {
            texto.setText(cacheNombres.get(id));
            return;
        }
        //Esto francamente entiendo a medias como funciona, overridea el método run en un hilo y lo pone en el caché de nombres. En su momento se que lo puse en un Thread por algo, pero llevamos tanto con el proyecto que no recuerdo por qué
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String nombre = pedirNombreApi(id);
                    if (nombre != null) {
                        cacheNombres.put(id, nombre);
                        if (context != null) {
                            ((android.app.Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    texto.setText(nombre);
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    //Esto saca los nombres de la API unicamente
    //https://www.youtube.com/watch?v=qMmeNXyFr3Q
    String pedirNombreApi(int id) {
        try {
           URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id + "/");
            HttpURLConnection conexion = (java.net.HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            //Aqui pongo timeouts porque si no encuentra lo que necesita me crashea el movil entero
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(5000);
            //Esto saca los datos del archivo
            BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String linea;
            StringBuilder respuesta = new StringBuilder();
            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }
            lector.close();
            //Esto sacoa los datos del JSON que manda la API con los nombres
            JSONObject objeto = new JSONObject(respuesta.toString());
            String nombre = objeto.getString("name");
            //Esto se lo pedí a ChatGPT, no se como funcionan los substrings
            return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            return "Pokémon #" + id;
        }
    }

    //Un método contador habitual
    @Override
    public int getItemCount() {
        return equiposList.size();
    }

    //https://developer.android.com/develop/ui/views/layout/recyclerview?hl=es-419
    //Lo puse en esta clase en vez de hacer otra por comodidad pero luego se me olvidó sacarlo y ya no tiene caso si funciona
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEquipo;
        ImageView imgPokemon1, imgPokemon2, imgPokemon3, imgPokemon4, imgPokemon5, imgPokemon6;
        TextView textPokemon1, textPokemon2, textPokemon3, textPokemon4, textPokemon5, textPokemon6;

        public ViewHolder(View vista) {
            super(vista);

            //Asigna el equipo
            tvNombreEquipo = vista.findViewById(R.id.textViewNombreEquipo);
            //Y a ese equipo le asigna todas las imagenes
            imgPokemon1 = vista.findViewById(R.id.imgPokemon1);
            imgPokemon2 = vista.findViewById(R.id.imgPokemon2);
            imgPokemon3 = vista.findViewById(R.id.imgPokemon3);
            imgPokemon4 = vista.findViewById(R.id.imgPokemon4);
            imgPokemon5 = vista.findViewById(R.id.imgPokemon5);
            imgPokemon6 = vista.findViewById(R.id.imgPokemon6);
            //Y todos los nombres
            textPokemon1 = vista.findViewById(R.id.textPokemon1);
            textPokemon2 = vista.findViewById(R.id.textPokemon2);
            textPokemon3 = vista.findViewById(R.id.textPokemon3);
            textPokemon4 = vista.findViewById(R.id.textPokemon4);
            textPokemon5 = vista.findViewById(R.id.textPokemon5);
            textPokemon6 = vista.findViewById(R.id.textPokemon6);
        }
    }
}