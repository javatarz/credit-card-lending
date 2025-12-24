package me.karun.bank.credit.customer.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.karun.bank.credit.customer.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer registration and management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer", description = "Creates a new customer account with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer registered successfully",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or weak password",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        return customerService.register(request);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address", description = "Verifies a customer's email using the token sent during registration")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(schema = @Schema(implementation = VerifyEmailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Token not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "410", description = "Token expired",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public VerifyEmailResponse verifyEmail(@RequestBody VerifyEmailRequest request) {
        return customerService.verifyEmail(request);
    }

    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Resend verification email", description = "Requests a new verification email to be sent")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Verification email sent if account exists",
                    content = @Content(schema = @Schema(implementation = ResendVerificationResponse.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResendVerificationResponse resendVerification(@RequestBody ResendVerificationRequest request) {
        return customerService.resendVerification(request);
    }

    // TODO #49: Replace path param with @AuthenticationPrincipal after auth implementation
    // Will become: PUT /api/v1/customers/me/profile with customer ID from SecurityContext
    @PutMapping("/{customerId}/profile")
    @Operation(summary = "Complete customer profile", description = "Submit personal information required for credit application")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile completed successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid profile data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Customer not verified",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProfileResponse completeProfile(
            @PathVariable String customerId,
            @Valid @RequestBody ProfileRequest request) {
        return customerService.completeProfile(customerId, request);
    }

    @GetMapping("/{customerId}/profile")
    @Operation(summary = "Get customer profile", description = "Retrieve customer profile information with masked SSN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer or profile not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProfileResponse getProfile(@PathVariable String customerId) {
        return customerService.getProfile(customerId);
    }
}
