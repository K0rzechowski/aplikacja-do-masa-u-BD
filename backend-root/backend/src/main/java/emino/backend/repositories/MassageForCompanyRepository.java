package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import emino.backend.models.Company;
import emino.backend.models.Massage;
import emino.backend.models.MassageForCompany;

public interface MassageForCompanyRepository extends JpaRepository<MassageForCompany, Long> {
    
    // Optional<MassageForCompany> findByName(String name);
    Optional<MassageForCompany> findByMassage(Massage massage);
    List<MassageForCompany> findByCompany(Company company);
    List<MassageForCompany> findByCompanyCity(String city);
    
    @Query("SELECT DISTINCT m FROM MassageForCompany m JOIN m.employees e WHERE e.id = :employeeId")
    List<MassageForCompany> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Override
    @Modifying
    @Query("DELETE FROM MassageForCompany m WHERE m.id = :id")
    void deleteById(@Param("id") Long id);
}