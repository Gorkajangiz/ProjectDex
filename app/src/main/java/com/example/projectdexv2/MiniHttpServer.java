package com.example.projectdexv2;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.ArrayList;

public class MiniHttpServer extends NanoHTTPD {

    /*
    * Transparencia: Esta clase no la he hecho yo, antes tenía todo en local menos la api, le pedí a
    * chatgpt todo el codigo necesario para levantar el servidor desde el movil, se como funciona
    * pero no sabría replicarlo
    * */


    public PokemonDataSource dataSource;

    public MiniHttpServer(int port) {
        super(port);
        dataSource = new PokemonDataSource();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String jsonResponse = "";

        if (uri.equals("/favoritos")) {
            ArrayList<Integer> favoritos = dataSource.obtenerFavoritos();
            jsonResponse = "[";
            for (int i = 0; i < favoritos.size(); i++) {
                jsonResponse = jsonResponse + favoritos.get(i);
                if (i != favoritos.size() - 1) {
                    jsonResponse = jsonResponse + ",";
                }
            }
            jsonResponse = jsonResponse + "]";
        } else if (uri.equals("/equipos")) {
            ArrayList<Long> equipos = dataSource.obtenerEquipos();
            jsonResponse = "[";
            for (int i = 0; i < equipos.size(); i++) {
                jsonResponse = jsonResponse + equipos.get(i);
                if (i != equipos.size() - 1) {
                    jsonResponse = jsonResponse + ",";
                }
            }
            jsonResponse = jsonResponse + "]";
        } else {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
        }

        Response response = newFixedLengthResponse(Response.Status.OK, "application/json", jsonResponse);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        return response;
    }

    public void stopServer() {
        stop();
    }
}