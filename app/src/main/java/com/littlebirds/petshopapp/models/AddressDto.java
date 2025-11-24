package com.littlebirds.petshopapp.models;

public class AddressDto {

    private String street;
    private String neighborhood;
    private String zipCode;
    private String city;
    private String state;
    private String complement;
    private String number;

    public AddressDto() {
    }

    public AddressDto(String street, String neighborhood, String zipCode, String city,
                      String state, String complement, String number) {
        this.street = street;
        this.neighborhood = neighborhood;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.complement = complement;
        this.number = number;
    }

    public String getStreet() { return street; }
    public String getNeighborhood() { return neighborhood; }
    public String getZipCode() { return zipCode; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getComplement() { return complement; }
    public String getNumber() { return number; }
}
