package com.example.projectdexv2;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParseadorApis {

    /*
    * Esta clase es la dedicada a parsear la API y sacar todos los datos de ella para cargarlos en los
    * otros métodos y clases.
    * https://www.youtube.com/watch?v=xm0GhiLD_gw
    * https://pokeapi.co/
    * */

    public static Pokemon parsear(JSONObject json) throws org.json.JSONException {
        int numeroPokedex = json.getInt("id");
        String nombre = capitalizarNombre(json.getString("name"));
        JSONArray tipos = json.getJSONArray("types");
        String tipoUno = tipos.getJSONObject(0).getJSONObject("type").getString("name");
        String tipoDos = null;
        if (tipos.length() > 1) {
            tipoDos = tipos.getJSONObject(1).getJSONObject("type").getString("name");
        }
        tipoUno = traducirTipo(tipoUno);
        if (tipoDos != null) {
            tipoDos = traducirTipo(tipoDos);
        }
        JSONArray habilidades = json.getJSONArray("abilities");
        String habilidadNormal = null;
        String habilidadOculta = null;
        for (int i = 0; i < habilidades.length(); i++) {
            JSONObject habilidad = habilidades.getJSONObject(i);
            String abilityName = habilidad.getJSONObject("ability").getString("name");
            boolean isHidden = habilidad.getBoolean("is_hidden");
            if (isHidden) {
                habilidadOculta = capitalizarNombre(abilityName);
            } else {
                habilidadNormal = capitalizarNombre(abilityName);
            }
        }
        int altura = json.getInt("height");
        int peso = json.getInt("weight");
        JSONArray estadisticas = json.getJSONArray("stats");
        int hp = 0;
        int ataque = 0;
        int ataqueEspecial = 0;
        int defensa = 0;
        int defensaEspecial = 0;
        int velocidad = 0;
        for (int i = 0; i < estadisticas.length(); i++) {
            JSONObject estadistica = estadisticas.getJSONObject(i);
            String nombreEstadistica = estadistica.getJSONObject("stat").getString("name");
            int numeroStat = estadistica.getInt("base_stat");
            if (nombreEstadistica.equals("hp")) {
                hp = numeroStat;
            } else if (nombreEstadistica.equals("attack")) {
                ataque = numeroStat;
            } else if (nombreEstadistica.equals("special-attack")) {
                ataqueEspecial = numeroStat;
            } else if (nombreEstadistica.equals("defense")) {
                defensa = numeroStat;
            } else if (nombreEstadistica.equals("special-defense")) {
                defensaEspecial = numeroStat;
            } else if (nombreEstadistica.equals("speed")) {
                velocidad = numeroStat;
            }
        }
        int generacion = buscarGeneracion(numeroPokedex);
        JSONObject speciesInfo = json.getJSONObject("species");
        String speciesUrl = speciesInfo.getString("url");
        String[] descripcionYHuevo = obtenerDescripcionYGrupoHuevo(speciesUrl);
        String descripcion = descripcionYHuevo[0];
        String grupoHuevo = descripcionYHuevo[1];
        return new Pokemon(numeroPokedex, nombre, tipoUno, tipoDos, generacion, habilidadNormal, habilidadOculta, altura, peso, hp, ataque, ataqueEspecial, defensa, defensaEspecial, velocidad, descripcion, grupoHuevo);
    }

    private static String[] obtenerDescripcionYGrupoHuevo(String speciesUrl) {
        String[] resultado = new String[2];
        resultado[0] = "Descripción no disponible.";
        resultado[1] = "Desconocido";
        try {
            URL url = new URL(speciesUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            JSONObject speciesJson = new JSONObject(response.toString());
            JSONArray flavorTextEntries = speciesJson.getJSONArray("flavor_text_entries");
            boolean encontrado = false;
            for (int i = 0; i < flavorTextEntries.length(); i++) {
                JSONObject entry = flavorTextEntries.getJSONObject(i);
                String language = entry.getJSONObject("language").getString("name");
                if (language.equals("es")) {
                    String descripcion = entry.getString("flavor_text");
                    resultado[0] = descripcion.replace("\n", " ").replace("\f", " ").replace("\n", " ");
                    encontrado = true;
                    break;
                }
            }
            if (encontrado == false) {
                for (int i = 0; i < flavorTextEntries.length(); i++) {
                    JSONObject entry = flavorTextEntries.getJSONObject(i);
                    String language = entry.getJSONObject("language").getString("name");
                    if (language.equals("en")) {
                        String descripcion = entry.getString("flavor_text");
                        resultado[0] = descripcion.replace("\n", " ").replace("\f", " ").replace("\n", " ");
                        break;
                    }
                }
            }
            JSONArray eggGroups = speciesJson.getJSONArray("egg_groups");
            if (eggGroups.length() > 0) {
                String primerGrupo = eggGroups.getJSONObject(0).getString("name");
                resultado[1] = traducirGrupoHuevo(primerGrupo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    private static String traducirGrupoHuevo(String grupoIngles) {
        if (grupoIngles.equals("monster")) { return "Monstruo"; }
        else if (grupoIngles.equals("water1")) { return "Agua 1"; }
        else if (grupoIngles.equals("water2")) { return "Agua 2"; }
        else if (grupoIngles.equals("water3")) { return "Agua 3"; }
        else if (grupoIngles.equals("bug")) { return "Bicho"; }
        else if (grupoIngles.equals("flying")) { return "Volador"; }
        else if (grupoIngles.equals("field")) { return "Campo"; }
        else if (grupoIngles.equals("fairy")) { return "Hada"; }
        else if (grupoIngles.equals("grass")) { return "Planta"; }
        else if (grupoIngles.equals("human-like")) { return "Humanoide"; }
        else if (grupoIngles.equals("mineral")) { return "Mineral"; }
        else if (grupoIngles.equals("amorphous")) { return "Amorfo"; }
        else if (grupoIngles.equals("ditto")) { return "Ditto"; }
        else if (grupoIngles.equals("dragon")) { return "Dragón"; }
        else if (grupoIngles.equals("no-eggs")) { return "Sin huevos"; }
        else if (grupoIngles.equals("gender-unknown")) { return "Sin género"; }
        else { return capitalizarNombre(grupoIngles); }
    }

    private static int buscarGeneracion(int numero) {
        if (numero <= 151) { return 1; }
        if (numero <= 251) { return 2; }
        if (numero <= 386) { return 3; }
        if (numero <= 493) { return 4; }
        if (numero <= 649) { return 5; }
        if (numero <= 721) { return 6; }
        if (numero <= 809) { return 7; }
        if (numero <= 905) { return 8; }
        return 9;
    }

    private static String traducirTipo(String tipoIngles) {
        if (tipoIngles.equals("normal")) { return "Normal"; }
        else if (tipoIngles.equals("fire")) { return "Fuego"; }
        else if (tipoIngles.equals("water")) { return "Agua"; }
        else if (tipoIngles.equals("electric")) { return "Eléctrico"; }
        else if (tipoIngles.equals("grass")) { return "Planta"; }
        else if (tipoIngles.equals("ice")) { return "Hielo"; }
        else if (tipoIngles.equals("fighting")) { return "Lucha"; }
        else if (tipoIngles.equals("poison")) { return "Veneno"; }
        else if (tipoIngles.equals("ground")) { return "Tierra"; }
        else if (tipoIngles.equals("flying")) { return "Volador"; }
        else if (tipoIngles.equals("psychic")) { return "Psíquico"; }
        else if (tipoIngles.equals("bug")) { return "Bicho"; }
        else if (tipoIngles.equals("rock")) { return "Roca"; }
        else if (tipoIngles.equals("ghost")) { return "Fantasma"; }
        else if (tipoIngles.equals("dragon")) { return "Dragón"; }
        else if (tipoIngles.equals("dark")) { return "Siniestro"; }
        else if (tipoIngles.equals("steel")) { return "Acero"; }
        else if (tipoIngles.equals("fairy")) { return "Hada"; }
        else { return capitalizarNombre(tipoIngles); }
    }

    private static String capitalizarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return nombre;
        }
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
    }
}