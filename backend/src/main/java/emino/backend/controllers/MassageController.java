package emino.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.services.EmployeeService;
import emino.backend.services.MassageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/massages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MassageController {
    
    private final EmployeeService emplService;
    private final MassageService massageService;

    // TODO
    // @PostMapping("/create")
    // public ResponseEntity createMassage(@RequestBody MassageDto request) {
    //     Admin admin = securityService.getCurrentAdmin();
    //     Massage empl = massageService.createMassage(request, admin);
    //     return ResponseEntity.ok(empl);
    // }

    // TODO 
    // @GetMapping("/getByCompany/{companyId}")
    // public ResponseEntity<List<Massage>> getMassagesByCompany() {
    //     Admin admin = securityService.getCurrentAdmin();
    //     Long companyId = admin.getCompany().getId();
    //     List<Massage> massage = massageService.getMassagesByCompany(companyId);
    //     List<Massage> massagesAlphabetically = massage.stream()
    //         .sorted((e1, e2) -> {
    //             return e1.getName().compareToIgnoreCase(e2.getName());
    //         })
    //         .toList();
    //     return  ResponseEntity.ok(massagesAlphabetically);
    // }

    // TODO
    // @GetMapping("/{companyId}")
    // public ResponseEntity<List<Massage>> getMassagesByCompany() {
    //     Admin admin = securityService.getCurrentAdmin();
    //     Long companyId = admin.getCompany().getId();
    //     List<Massage> empls = massageService.getMassagesByCompany(companyId);
    //     List<Massage> emplsAlphabetically = empls.stream()
    //         .sorted((e1, e2) -> {
    //             int surnameComparison = e1.getSurname().compareToIgnoreCase(e2.getSurname());
    //             return surnameComparison != 0 ? surnameComparison : e1.getName().compareToIgnoreCase(e2.getName());
    //         })
    //         .toList();
    //     return  ResponseEntity.ok(emplsAlphabetically);
    // }

    // @GetMapping("/all")
    // public ResponseEntity<List<Massage>> getAllMassages() {
    //     List<Massage> massages = massageService.getAllMassages();
    //     return  ResponseEntity.ok(massages);
    // }

    // TODO
    // @PutMapping("/edit/{id}")
    // public ResponseEntity editMassage(@PathVariable Long id, @RequestBody MassageUpdateDto request) {
    //     Admin admin = securityService.getCurrentAdmin();
    //     Massage updatedMassage = massageService.editMassage(id, request, admin);
    //     return ResponseEntity.ok(updatedMassage);
    // }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity removeMassage(@PathVariable Long id) {
        massageService.removeMassage(id);
        return ResponseEntity.ok().build();
    }
}
