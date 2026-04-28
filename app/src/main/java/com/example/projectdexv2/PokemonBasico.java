package com.example.projectdexv2;

public class PokemonBasico {
    private int numero;
    private String nombre;

    public PokemonBasico(int numero, String nombre) {
        this.numero = numero;
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }
}