package com.littlebirds.petshopapp;

public class Scheduling {

    private Long id;
    private String petName;

    private String workerName;
    private String serviceType;
    private String date;
    private String status;

    public Scheduling(Long id, String petName, String workerName, String serviceType, String date, String status) {
        this.id = id;
        this.petName = petName;
        this.workerName = workerName;
        this.serviceType = serviceType;
        this.date = date;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getPetName() { return petName; }

    public String getWorkerName() { return workerName; }
    public String getServiceType() { return serviceType; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}