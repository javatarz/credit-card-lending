package me.karun.bank.credit.customer.api;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Verification token has expired");
    }
}
