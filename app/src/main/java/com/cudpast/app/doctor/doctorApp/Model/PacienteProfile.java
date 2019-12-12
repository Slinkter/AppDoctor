package com.cudpast.app.doctor.doctorApp.Model;

public class PacienteProfile {

    private String address;
    private String dateborn;
    private String dni;
    private String firstname;
    private String lastname;
    private String mail;
    private String phone;

    public PacienteProfile() {
    }

    public PacienteProfile(String address, String dateborn, String dni, String firstname, String lastname, String mail, String phone) {
        this.address = address;
        this.dateborn = dateborn;
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.mail = mail;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateborn() {
        return dateborn;
    }

    public void setDateborn(String dateborn) {
        this.dateborn = dateborn;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
