package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Admin;
import emino.backend.models.Company;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    List<Admin> findByCompany(Company company);
    Optional<Admin> findByEmail(String email);
}