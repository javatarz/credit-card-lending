package me.karun.bank.credit.customer.internal.repository;

import java.util.UUID;
import me.karun.bank.credit.customer.internal.domain.ProfileAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileAuditRepository extends JpaRepository<ProfileAudit, UUID> {}
