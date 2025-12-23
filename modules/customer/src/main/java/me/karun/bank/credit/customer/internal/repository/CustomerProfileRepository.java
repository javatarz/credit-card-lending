package me.karun.bank.credit.customer.internal.repository;

import me.karun.bank.credit.customer.internal.domain.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {
}
