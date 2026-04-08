package me.karun.bank.credit.customer.internal.repository;

import java.util.Optional;
import java.util.UUID;
import me.karun.bank.credit.customer.internal.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  Optional<Customer> findByEmail(String email);
}
