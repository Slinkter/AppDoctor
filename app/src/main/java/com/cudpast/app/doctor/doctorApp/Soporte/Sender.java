package com.cudpast.app.doctor.doctorApp.Soporte;

public class Sender {
    public String to ;
    public Notification notification;
    public Data data;

    public Sender() {
    }

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;

    }

    public Sender(String to, Notification notification, Data data) {
        this.to = to;
        this.notification = notification;
        this.data = data;
    }

    public Sender(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
