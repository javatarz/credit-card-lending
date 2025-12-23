package me.karun.bank.credit.customer.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "street_address", nullable = false)
    private String street;

    @Column(name = "unit")
    private String unit;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    protected Address() {}

    public Address(String street, String unit, String city, String state, String zipCode) {
        this.street = street;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getUnit() {
        return unit;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }
}
