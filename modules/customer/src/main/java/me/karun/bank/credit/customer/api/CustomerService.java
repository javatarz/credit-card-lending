package me.karun.bank.credit.customer.api;

public interface CustomerService {
    RegistrationResponse register(RegistrationRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

    ResendVerificationResponse resendVerification(ResendVerificationRequest request);

    ProfileResponse completeProfile(String customerId, ProfileRequest request);
}
