package emino.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emino.backend.config.SecurityService;
import emino.backend.dto.BranchOfficeDto;
import emino.backend.dto.CompanyOpenTimeDto;
import emino.backend.dto.RoomDto;
import emino.backend.models.BranchOffice;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.Room;
import emino.backend.services.BranchOfficeService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BranchOfficeController {

    private final BranchOfficeService branchOfficeService;
    private final SecurityService securityService;

    @PostMapping("/company/{companyId}")
    public ResponseEntity<BranchOfficeDto> createBranchOffice(@PathVariable Long companyId, @RequestBody BranchOfficeDto request) {
        BranchOffice branchOffice = branchOfficeService.createBranchOffice(companyId, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getBranchOfficeResponse(branchOffice));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BranchOfficeDto>> getBranchOfficesByCompany(@PathVariable Long companyId) {
        List<BranchOfficeDto> dto = branchOfficeService.getBranchOfficesByCompany(companyId)
            .stream()
            .map(branchOfficeService::getBranchOfficeResponse)
            .toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchOfficeDto> getById(@PathVariable Long id) {
        return branchOfficeService.getBranchOfficeById(id)
            .map(branchOffice -> ResponseEntity.ok(branchOfficeService.getBranchOfficeResponse(branchOffice)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchOfficeDto> editBranchOffice(@PathVariable Long id, @RequestBody BranchOfficeDto request) {
        BranchOffice branchOffice = branchOfficeService.editBranchOffice(id, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getBranchOfficeResponse(branchOffice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBranchOffice(@PathVariable Long id) {
        branchOfficeService.removeBranchOffice(id, securityService.getCurrentAdmin());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{branchId}/addOpenTime")
    public ResponseEntity<CompanyOpenTimeDto> setOpenTime(@PathVariable Long branchId, @RequestBody CompanyOpenTimeDto request) {
        CompanyOpenTime openTime = branchOfficeService.setOpenTime(branchId, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getOpenTimeResponse(openTime));
    }

    @GetMapping("/{branchId}/openTime")
    public ResponseEntity<CompanyOpenTimeDto> getOpenTime(@PathVariable Long branchId) {
        return branchOfficeService.getOpenTime(branchId)
            .map(openTime -> ResponseEntity.ok(branchOfficeService.getOpenTimeResponse(openTime)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{branchId}/editOpenTime")
    public ResponseEntity<CompanyOpenTimeDto> editOpenTime(@PathVariable Long branchId, @RequestBody CompanyOpenTimeDto request) {
        CompanyOpenTime openTime = branchOfficeService.setOpenTime(branchId, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getOpenTimeResponse(openTime));
    }

    @PostMapping("/{branchId}/room")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long branchId, @RequestBody RoomDto request) {
        Room room = branchOfficeService.createRoom(branchId, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getRoomResponse(room));
    }

    @GetMapping("/{branchId}/room/{roomNumber}")
    public ResponseEntity<RoomDto> getRoomByBranchOfficeAndRoomNumber(@PathVariable Long branchId, @PathVariable String roomNumber) {
        return branchOfficeService.getRoomByBranchOfficeAndRoomNumber(branchId, roomNumber)
            .map(room -> ResponseEntity.ok(branchOfficeService.getRoomResponse(room)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{branchId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByBranchOffice(@PathVariable Long branchId) {
        List<RoomDto> dto = branchOfficeService.getRoomsByBranchOffice(branchId)
            .stream()
            .map(branchOfficeService::getRoomResponse)
            .toList();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{branchId}/room/edit/{roomId}")
    public ResponseEntity<RoomDto> editRoom(@PathVariable Long branchId, @PathVariable Long roomId, @RequestBody RoomDto request) {
        Room room = branchOfficeService.editRoom(branchId, roomId, request, securityService.getCurrentAdmin());
        return ResponseEntity.ok(branchOfficeService.getRoomResponse(room));
    }

    @DeleteMapping("/{branchId}/room/remove/{roomId}")
    public ResponseEntity<Void> removeRoom(@PathVariable Long branchId, @PathVariable Long roomId) {
        branchOfficeService.removeRoom(branchId, roomId, securityService.getCurrentAdmin());
        return ResponseEntity.ok().build();
    }
}
