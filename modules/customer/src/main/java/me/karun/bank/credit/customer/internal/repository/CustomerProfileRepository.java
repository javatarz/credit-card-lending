package me.karun.bank.credit.customer.internal.repository;

import java.util.UUID;
import me.karun.bank.credit.customer.internal.domain.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {}
