package com.example.meetapp.model;

public class Subject {
    public int resource;
    public String name;
    public boolean isSelected;

    public Subject(int resource, String name) {
        this.resource = resource;
        this.name = name;
        this.isSelected = false;
    }

    public void select(){
        this.isSelected = true;
    }
    public void unSelect(){
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
