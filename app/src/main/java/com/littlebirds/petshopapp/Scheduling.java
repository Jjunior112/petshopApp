package com.littlebirds.petshopapp;

public class Scheduling {

    private Long id;
    private String petName;
    private String workerName;
    private String serviceName;
    private String date;
    private String status;

    public Scheduling(Long id, String petName, String workerName, String serviceName, String date, String status) {
        this.id = id;
        this.petName = petName;
        this.workerName = workerName;
        this.serviceName = serviceName;
        this.date = date;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getPetName() { return petName; }

    public String getWorkerName() { return workerName; }
    public String getServiceType() { return serviceName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}