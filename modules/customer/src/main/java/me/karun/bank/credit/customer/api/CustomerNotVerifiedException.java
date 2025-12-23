package me.karun.bank.credit.customer.api;

public class CustomerNotVerifiedException extends RuntimeException {
    public CustomerNotVerifiedException(String message) {
        super(message);
    }
}
