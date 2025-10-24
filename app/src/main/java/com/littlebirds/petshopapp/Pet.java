package com.littlebirds.petshopapp;

public class Pet {

    private Long id;
    private String name;
    private String petType;
    private String race;
    private String color;

    public Pet(Long id, String name, String petType, String race, String color) {
        this.id = id;
        this.name = name;
        this.petType = petType;
        this.race = race;
        this.color = color;
    }

    public Long getId(){return id;}
    public String getName() { return name; }
    public String getPetType() { return petType; }
    public String getRace() { return race; }
    public String getColor() { return color; }
}
