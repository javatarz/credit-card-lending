package me.karun.bank.credit.customer.api;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("Verification token not found");
    }
}
