package com.example.pica.zmap.tablas;

public class Posicion {
    private double latitud, longitud;
    private int fecha;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String info;

    public Posicion(double latitud, double longitud, String info,int fecha) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.info = info;
    }

    public Posicion() {
        this(0,0,"",0);
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Posicion{" +
                "latitud=" + latitud +
                ", longitud=" + longitud +
                ", fecha=" + fecha +
                '}';
    }
}
