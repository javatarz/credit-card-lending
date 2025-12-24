package me.karun.bank.credit.customer.internal.repository;

import me.karun.bank.credit.customer.internal.domain.ProfileAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileAuditRepository extends JpaRepository<ProfileAudit, UUID> {
}
