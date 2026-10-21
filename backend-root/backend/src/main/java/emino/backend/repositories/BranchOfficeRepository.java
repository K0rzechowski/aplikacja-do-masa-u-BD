package emino.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.BranchOffice;

public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Long> {
    List<BranchOffice> findByCompanyId(Long companyId);
}
