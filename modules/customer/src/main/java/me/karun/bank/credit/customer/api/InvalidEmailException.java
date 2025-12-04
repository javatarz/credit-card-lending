package me.karun.bank.credit.customer.api;

public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String email) {
        super("Invalid email format: " + email);
    }
}
