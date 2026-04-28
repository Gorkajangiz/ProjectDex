package com.example.projectdexv2;

import java.util.ArrayList;

public class Equipo {

    //Creo variables
    public int id;
    public String nombre;
    public ArrayList<Integer> pokemonIds;

    //Constructor de equipos, tienen id nombre y las ids de los pokemon en un arrayList
    public Equipo(int id, String nombre, ArrayList<Integer> pokemonIds) {
        this.id = id;
        this.nombre = nombre;
        //Esta comprobación la metí por si acaso cuando me estaba dando problemas cargar desde la API, la he dejado por comodidad
        if (pokemonIds != null) {
            this.pokemonIds = pokemonIds;
        } else {
            this.pokemonIds = new ArrayList<Integer>();
        }
    }

    //Aqui un segundo constructor que usa solo id y nombre
    public Equipo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.pokemonIds = new ArrayList<Integer>();
    }

    //Getter Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Integer> getPokemonIds() {
        return pokemonIds;
    }

    public void setPokemonIds(ArrayList<Integer> pokemonIds) {
        this.pokemonIds = pokemonIds;
    }

    //Este método es para sacar cantidades en la lista de ids para poner contadores y así
    public int getCantidadPokemon() {
        if (pokemonIds != null) {
            return pokemonIds.size();
        } else {
            return 0;
        }
    }

    //Esto es un boolean para saber si el pokemon está en la lista con contains
    public boolean tienePokemon(int pokemonId) {
        //Estas dos comprobaciones, como todas las demás, estan aqui porque todo daba fallos y no entiendo por qué
        if (pokemonIds == null) {
            return false;
        }
        if (pokemonIds.isEmpty()) {
            return false;
        }
        boolean tiene = pokemonIds.contains(pokemonId);
        return tiene;
    }
}