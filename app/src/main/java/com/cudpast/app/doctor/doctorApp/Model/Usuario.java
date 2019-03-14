package com.cudpast.app.doctor.doctorApp.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String codmedpe;//codigo de medico de peru
    private String correoG;
    private String direccion;
    private String dni;
    private String especialidad;
    private String fecha;
    private String firstname;
    private String image;
    private String lastname;
    private String numphone;
    private String password;
    private String uid;

    public Usuario() {

    }

    //Firebase db_doctor_register
    public Usuario(String dni, String firstname, String lastname, String numphone, String codmedpe, String especialidad, String direccion, String password, String correoG, String fecha, String imagen, String uid) {
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
        this.image = imagen;
        this.uid = uid;
    }


    //Firebase db_doctor_login
    public Usuario(String dni, String password) {
        this.dni = dni;
        this.password = password;
    }

    //Firebase db_doctor_consulta
    public Usuario(String dni, String firstname, String lastname, String numphone, String especialidad, String image) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.especialidad = especialidad;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getCadena() {
        String cadena = "";

        cadena = "codmedpe " + codmedpe + "\n" +
                "correoG; " + correoG + "\n" +
                "direccion; " + direccion + "\n" +
                "dni  " + dni + "\n" +
                "especialidad; " + especialidad + "\n" +
                "fecha  " + fecha + "\n" +
                "firstname  " + firstname + "\n" +
                "image  " + image + "\n" +
                "lastname  " + lastname + "\n" +
                "numphone  " + numphone + "\n" +
                "password  " + password + "\n" +
                "uid   " + uid + "\n";


        return cadena;
    }


}
