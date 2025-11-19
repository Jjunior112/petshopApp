package com.littlebirds.petshopapp;

public class Service {
    private Long id;
    private String name;
    private Double price;
    private String serviceType;

    public Service(Long id, String name, Double price, String serviceType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.serviceType = serviceType;
    }

    public Long getId()
    {
        return id;
    }

    public String getName() {
        return name;
    }
    public Double getPrice() {
        return price;
    }

    public String getServiceType() {
        return serviceType;
    }
}

