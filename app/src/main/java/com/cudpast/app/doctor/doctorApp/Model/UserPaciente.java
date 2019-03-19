package com.cudpast.app.doctor.doctorApp.Model;

public class UserPaciente {

    private String dni,correo,password,nombre,apellido,telefono,fecha, direcion;

    public UserPaciente() {
    }

    public UserPaciente(String dni, String correo, String password, String nombre, String apellido, String telefono, String fecha, String direcion) {
        this.dni = dni;
        this.correo = correo;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.fecha = fecha;
        this.direcion = direcion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDirecion() {
        return direcion;
    }

    public void setDirecion(String direcion) {
        this.direcion = direcion;
    }
}
