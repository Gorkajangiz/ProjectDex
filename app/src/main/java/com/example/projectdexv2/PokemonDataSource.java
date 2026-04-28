package com.example.projectdexv2;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PokemonDataSource {

    String servidor = "http://192.168.1.173:9999";

    private JSONArray conseguirArrayDeInternet(String parte) {
        try {
            URL url = new URL(servidor + parte);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);
            int codigo = conexion.getResponseCode();
            if (codigo != 200) {
                return new JSONArray();
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            StringBuilder texto = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) texto.append(linea);
            lector.close();
            conexion.disconnect();
            return new JSONArray(texto.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public ArrayList<Integer> obtenerFavoritos() {
        ArrayList<Integer> lista = new ArrayList<>();
        JSONArray array = conseguirArrayDeInternet("/favoritos");
        for (int i = 0; i < array.length(); i++) {
            lista.add(array.optInt(i));
        }
        return lista;
    }

    public ArrayList<Long> obtenerEquipos() {
        ArrayList<Long> equipos = new ArrayList<>();
        try {
            URL url = new URL(servidor + "/equipos_con_pokemons");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader lector = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) sb.append(linea);
            lector.close();
            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject equipo = array.getJSONObject(i);
                equipos.add(equipo.getLong("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equipos;
    }

    public ArrayList<Equipo> obtenerEquiposConPokemon() {
        ArrayList<Equipo> equipos = new ArrayList<>();
        try {
            JSONArray array = conseguirArrayDeInternet("/equipos_con_pokemons");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                long id = obj.getLong("id");
                String nombre = obj.getString("nombre");
                JSONArray pokes = obj.getJSONArray("pokemons");

                ArrayList<Integer> pokeIds = new ArrayList<>();
                for (int j = 0; j < pokes.length(); j++) {
                    pokeIds.add(pokes.getInt(j));
                }
                equipos.add(new Equipo((int)id, nombre, pokeIds));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equipos;
    }

    public ArrayList<Integer> obtenerPokemonDeEquipo(long equipoId) {
        ArrayList<Integer> pokemons = new ArrayList<>();
        try {
            URL url = new URL(servidor + "/equipos_con_pokemons");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader lector = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) sb.append(linea);
            lector.close();
            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject equipo = array.getJSONObject(i);
                if (equipo.getLong("id") == equipoId) {
                    JSONArray pokes = equipo.getJSONArray("pokemons");
                    for (int j = 0; j < pokes.length(); j++) {
                        pokemons.add(pokes.getInt(j));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pokemons;
    }

    public String obtenerNombreEquipo(long equipoId) {
        try {
            URL url = new URL(servidor + "/equipos_con_pokemons");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader lector = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) sb.append(linea);
            lector.close();
            JSONArray array = new JSONArray(sb.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject equipo = array.getJSONObject(i);
                if (equipo.getLong("id") == equipoId) {
                    return equipo.getString("nombre");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Equipo " + equipoId;
    }

    public boolean agregarFavorito(int pokemonId) {
        try {
            URL url = new URL(servidor + "/agregar_favorito");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("pokemon_id", pokemonId);
            OutputStream os = conn.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));
            os.close();
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarPokemonAEquipo(long equipoId, int pokemonId, int slot) {
        try {
            URL url = new URL(servidor + "/agregar_pokemon_equipo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("equipo_id", equipoId);
            obj.put("pokemon_id", pokemonId);
            obj.put("slot", slot);
            OutputStream os = conn.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));
            os.close();
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}