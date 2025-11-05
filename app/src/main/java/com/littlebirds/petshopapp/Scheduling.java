package com.littlebirds.petshopapp;

public class Scheduling {

    private Long id;
    private String petName;
    private String serviceType;
    private String date;
    private String status;

    public Scheduling(Long id, String petName, String serviceType, String date, String status) {
        this.id = id;
        this.petName = petName;
        this.serviceType = serviceType;
        this.date = date;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getPetName() { return petName; }
    public String getServiceType() { return serviceType; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}