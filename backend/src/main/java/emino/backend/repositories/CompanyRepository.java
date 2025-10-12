package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByNameAndCity(String name, String city);
    Optional<Company> findByName(String name);
    List<Company> findByCity(String city);
}