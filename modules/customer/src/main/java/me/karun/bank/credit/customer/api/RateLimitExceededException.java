package me.karun.bank.credit.customer.api;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Too many verification requests. Please try again later.");
    }
}
