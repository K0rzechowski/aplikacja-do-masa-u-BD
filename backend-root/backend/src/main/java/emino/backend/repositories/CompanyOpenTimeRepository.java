package emino.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.CompanyOpenTime;

public interface CompanyOpenTimeRepository extends JpaRepository<CompanyOpenTime, Long> {
    Optional<CompanyOpenTime> findByCompanyId(Long companyId);
}
