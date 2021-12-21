package com.example.certamen2;

public class Localizacion {
    private String idLocalizacion;
    private String tituloLocal;
    private String descripcionLocal;
    private double latitudLocal;
    private double longitudLocal;

    public Localizacion() {
    }

    public String getIdLocalizacion() {
        return idLocalizacion;
    }

    public void setIdLocalizacion(String idLocalizacion) {
        this.idLocalizacion = idLocalizacion;
    }

    public String getTitulo() {
        return tituloLocal;
    }

    public void setTitulo(String titulo) {
        this.tituloLocal = titulo;
    }

    public String getDescripcion() {
        return descripcionLocal;
    }

    public void setDescripcion(String descripcion) {
        this.descripcionLocal = descripcion;
    }

    public double getLatitud() {
        return latitudLocal;
    }

    public void setLatitud(double latitudLocal) {
        this.latitudLocal = latitudLocal;
    }

    public double getLongitud() {
        return longitudLocal;
    }

    public void setLongitud(double longitudLocal) {
        this.longitudLocal = longitudLocal;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "idLocalizacion='" + idLocalizacion + '\'' +
                ", tituloLocal='" + tituloLocal + '\'' +
                ", descripcionLocal='" + descripcionLocal + '\'' +
                ", latitudLocal=" + latitudLocal +
                ", longitudLocal=" + longitudLocal +
                '}';
    }
}
