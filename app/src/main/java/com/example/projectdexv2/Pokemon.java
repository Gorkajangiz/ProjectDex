package com.example.projectdexv2;

public class Pokemon {
    private int numeroPokedex;
    private String nombre;
    private String tipoUno;
    private String tipoDos;
    private int generacion;
    private String habilidad;
    private String habilidadOculta;
    private double altura;
    private double peso;
    private int hp;
    private int ataque;
    private int ataqueEspecial;
    private int defensa;
    private int defensaEspecial;
    private int velocidad;
    private String descripcion;
    private String grupoHuevo;

    public Pokemon(int numeroPokedex, String nombre, String tipoUno, String tipoDos, int generacion, String habilidad, String habilidadOculta, double altura, double peso, int hp, int ataque, int ataqueEspecial, int defensa, int defensaEspecial, int velocidad, String descripcion, String grupoHuevo) {
        this.numeroPokedex = numeroPokedex;
        this.nombre = nombre;
        this.tipoUno = tipoUno;
        this.tipoDos = tipoDos;
        this.generacion = generacion;
        this.habilidad = habilidad;
        this.habilidadOculta = habilidadOculta;
        this.altura = altura;
        this.peso = peso;
        this.hp = hp;
        this.ataque = ataque;
        this.ataqueEspecial = ataqueEspecial;
        this.defensa = defensa;
        this.defensaEspecial = defensaEspecial;
        this.velocidad = velocidad;
        this.descripcion = descripcion;
        this.grupoHuevo = grupoHuevo;
    }

    public int getNumeroPokedex() {
        return numeroPokedex;
    }
    public void setNumeroPokedex(int numeroPokedex) {
        this.numeroPokedex = numeroPokedex;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTipoUno() {
        return tipoUno;
    }
    public void setTipoUno(String tipoUno) {
        this.tipoUno = tipoUno;
    }
    public String getTipoDos() {
        return tipoDos;
    }
    public void setTipoDos(String tipoDos) {
        this.tipoDos = tipoDos;
    }
    public int getGeneracion() {
        return generacion;
    }
    public void setGeneracion(int generacion) {
        this.generacion = generacion;
    }
    public String getHabilidad() {
        return habilidad;
    }
    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }
    public String getHabilidadOculta() {
        return habilidadOculta;
    }
    public void setHabilidadOculta(String habilidadOculta) {
        this.habilidadOculta = habilidadOculta;
    }
    public double getAltura() {
        return altura;
    }
    public void setAltura(double altura) {
        this.altura = altura;
    }
    public double getPeso() {
        return peso;
    }
    public void setPeso(double peso) {
        this.peso = peso;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getAtaque() {
        return ataque;
    }
    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }
    public int getAtaqueEspecial() {
        return ataqueEspecial;
    }
    public void setAtaqueEspecial(int ataqueEspecial) {
        this.ataqueEspecial = ataqueEspecial;
    }
    public int getDefensa() {
        return defensa;
    }
    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }
    public int getDefensaEspecial() {
        return defensaEspecial;
    }
    public void setDefensaEspecial(int defensaEspecial) {
        this.defensaEspecial = defensaEspecial;
    }
    public int getVelocidad() {
        return velocidad;
    }
    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getGrupoHuevo() {
        return grupoHuevo;
    }
    public void setGrupoHuevo(String grupoHuevo) {
        this.grupoHuevo = grupoHuevo;
    }
}