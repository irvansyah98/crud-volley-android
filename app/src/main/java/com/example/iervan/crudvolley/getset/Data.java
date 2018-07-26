package com.example.iervan.crudvolley.getset;

public class Data {
    private String id, name, posisi, gajih;

    public Data(){

    }

    public Data(String id, String name, String posisi, String gajih) {
        this.id = id;
        this.name = name;
        this.posisi = posisi;
        this.gajih = gajih;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String posisi) {
        this.posisi = posisi;
    }

    public String getGajih() {
        return gajih;
    }

    public void setGajih(String gajih) {
        this.gajih = gajih;
    }
}
