package com.cudpast.app.doctor.doctorregisterapp.Model;

public class Usuario {


    private String dni ;
    private String firstname;
    private String lastname;
    private String numphone;
    private String codmedpe;//codigo de medico de peru
    private String usuario;//dirección
    private String password;
    private String correoG;
    private String fecha;

    public Usuario() {

    }

    public Usuario(String dni, String firstname, String lastname, String numphone, String codmedpe, String usuario, String password, String correoG, String fecha) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.codmedpe = codmedpe;
        this.usuario = usuario;
        this.password = password;
        this.correoG = correoG;
        this.fecha = fecha;
    }


    public Usuario(String dni, String firstname, String lastname, String numphone, String codmedpe, String usuario, String password, String correoG) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.codmedpe = codmedpe;
        this.usuario = usuario;
        this.password = password;
        this.correoG = correoG;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNumphone() {
        return numphone;
    }

    public void setNumphone(String numphone) {
        this.numphone = numphone;
    }

    public String getCodmedpe() {
        return codmedpe;
    }

    public void setCodmedpe(String codmedpe) {
        this.codmedpe = codmedpe;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreoG() {
        return correoG;
    }

    public void setCorreoG(String correoG) {
        this.correoG = correoG;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
