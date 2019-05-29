package com.cudpast.app.doctor.doctorApp.Soporte;

public class Data {
    public String title;
    public String body;
    public String pToken;
    public String dToken;
    public String json_lat_log;
    public String pacienteUID;

    public Data() {

    }

    public Data(String title, String body, String pToken, String dToken, String json_lat_log, String pacienteUID) {
        this.title = title;
        this.body = body;
        this.pToken = pToken;
        this.dToken = dToken;
        this.json_lat_log = json_lat_log;
        this.pacienteUID = pacienteUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getpToken() {
        return pToken;
    }

    public void setpToken(String pToken) {
        this.pToken = pToken;
    }

    public String getdToken() {
        return dToken;
    }

    public void setdToken(String dToken) {
        this.dToken = dToken;
    }

    public String getJson_lat_log() {
        return json_lat_log;
    }

    public void setJson_lat_log(String json_lat_log) {
        this.json_lat_log = json_lat_log;
    }

    public String getPacienteUID() {
        return pacienteUID;
    }

    public void setPacienteUID(String pacienteUID) {
        this.pacienteUID = pacienteUID;
    }
}
