package com.cudpast.app.doctor.doctorApp.Model;

public class DoctorProfile {

    private String uid;
    private String imagePhoto;
    private String firstname;
    private String lastname;
    private String numphone;
    private String address;
    private String codmedpe;//codigo de medico de peru
    private String dni;
    private String mail;
    private String password;
    private String especialidad;
    private String createDate;

    public DoctorProfile() {

    }

    // Firebase db_doctor_register
    public DoctorProfile(String uid, String imagePhoto, String firstname, String lastname, String numphone, String address, String codmedpe, String dni, String mail, String password, String especialidad, String createDate) {
        this.uid = uid;
        this.imagePhoto = imagePhoto;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.address = address;
        this.codmedpe = codmedpe;
        this.dni = dni;
        this.mail = mail;
        this.password = password;
        this.especialidad = especialidad;
        this.createDate = createDate;
    }

    //Firebase db_doctor_login
    public DoctorProfile(String dni, String password) {
        this.dni = dni;
        this.password = password;
    }

    //Firebase db_doctor_consulta
    public DoctorProfile(String dni, String firstname, String lastname, String numphone, String especialidad, String imagePhoto) {
        this.dni = dni;
        this.firstname = firstname;
        this.lastname = lastname;
        this.numphone = numphone;
        this.especialidad = especialidad;
        this.imagePhoto = imagePhoto;
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
        return address;
    }

    public void setDireccion(String direccion) {
        this.address = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getImagePhoto() {
        return imagePhoto;
    }

    public void setImagePhoto(String imagePhoto) {
        this.imagePhoto = imagePhoto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
