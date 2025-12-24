package me.karun.bank.credit.customer.internal.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles", schema = "customer")
public class CustomerProfile {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "ssn_encrypted", nullable = false)
    private String ssnEncrypted;

    @Column(name = "ssn_last_four", nullable = false, length = 4)
    private String ssnLastFour;

    @Embedded
    private Address address;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    protected CustomerProfile() {}

    public CustomerProfile(
        UUID customerId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String ssnEncrypted,
        String ssnLastFour,
        Address address,
        String phone
    ) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ssnEncrypted = ssnEncrypted;
        this.ssnLastFour = ssnLastFour;
        this.address = address;
        this.phone = phone;
        this.createdAt = Instant.now();
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSsnEncrypted() {
        return ssnEncrypted;
    }

    public String getSsnLastFour() {
        return ssnLastFour;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String ssnEncrypted,
        String ssnLastFour,
        Address address,
        String phone
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ssnEncrypted = ssnEncrypted;
        this.ssnLastFour = ssnLastFour;
        this.address = address;
        this.phone = phone;
        this.updatedAt = Instant.now();
    }

    public void updateAddress(Address address) {
        this.address = address;
        this.updatedAt = Instant.now();
    }

    public void updatePhone(String phone) {
        this.phone = phone;
        this.updatedAt = Instant.now();
    }
}
