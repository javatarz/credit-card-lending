package me.karun.bank.credit.customer.internal.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import me.karun.bank.credit.customer.internal.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
  Optional<VerificationToken> findByTokenHash(String tokenHash);

  void deleteByCustomerId(UUID customerId);

  long countByCustomerIdAndCreatedAtAfter(UUID customerId, Instant after);
}
