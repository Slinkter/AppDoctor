package com.cudpast.app.doctor.doctorregisterapp.Model;

public class Usuario {


    private String dni ;
    private String firstname;
    private String lastname;
    private String numphone;
    private String codmedpe;//codigo de medico de peru
    private String especialidad;
    private String direccion;
    private String password;
    private String correoG;
    private String fecha;

    public Usuario() {

    }
    //Firebase db_doctor_register
    public Usuario(String dni, String firstname, String lastname, String numphone, String codmedpe, String especialidad, String direccion, String password, String correoG, String fecha) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.codmedpe = codmedpe;
        this.especialidad = especialidad;
        this.direccion = direccion;
        this.password = password;
        this.correoG = correoG;
        this.fecha = fecha;
    }
    //Firebase db_doctor_login
    public Usuario(String dni, String password) {
        this.dni = dni;
        this.password = password;
    }
    //Firebase db_doctor_consulta
    public Usuario(String dni, String firstname, String lastname, String numphone, String especialidad) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.especialidad = especialidad;
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
