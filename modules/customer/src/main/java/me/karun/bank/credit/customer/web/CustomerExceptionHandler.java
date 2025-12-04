package me.karun.bank.credit.customer.web;

import me.karun.bank.credit.customer.api.EmailAlreadyExistsException;
import me.karun.bank.credit.customer.api.InvalidEmailException;
import me.karun.bank.credit.customer.api.WeakPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice(assignableTypes = CustomerController.class)
public class CustomerExceptionHandler {

    @ExceptionHandler(InvalidEmailException.class)
    public ProblemDetail handleInvalidEmail(InvalidEmailException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Email");
        problem.setType(URI.create("https://api.example.com/errors/invalid-email"));
        return problem;
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ProblemDetail handleWeakPassword(WeakPasswordException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Weak Password");
        problem.setType(URI.create("https://api.example.com/errors/weak-password"));
        return problem;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Email Already Registered");
        problem.setType(URI.create("https://api.example.com/errors/email-exists"));
        return problem;
    }
}
