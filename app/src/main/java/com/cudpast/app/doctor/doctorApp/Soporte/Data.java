package com.cudpast.app.doctor.doctorApp.Soporte;

public class Data {

    public String title;
    public String descripcion;
    public String extra;

    public Data() {
    }


    public Data(String title, String descripcion) {
        this.title = title;
        this.descripcion = descripcion;
    }

    public Data(String extra) {
        this.extra = extra;
    }





    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
