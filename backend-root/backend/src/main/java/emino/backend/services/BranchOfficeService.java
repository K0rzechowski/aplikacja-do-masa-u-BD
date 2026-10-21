package emino.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emino.backend.dto.BranchOfficeDto;
import emino.backend.dto.CompanyOpenTimeDto;
import emino.backend.dto.RoomDto;
import emino.backend.exception.AccessDeniedException;
import emino.backend.models.Admin;
import emino.backend.models.BranchOffice;
import emino.backend.models.Company;
import emino.backend.models.CompanyOpenTime;
import emino.backend.models.Room;
import emino.backend.repositories.BranchOfficeRepository;
import emino.backend.repositories.CompanyOpenTimeRepository;
import emino.backend.repositories.CompanyRepository;
import emino.backend.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchOfficeService {

    private final BranchOfficeRepository branchOfficeRepository;
    private final CompanyRepository companyRepository;
    private final CompanyOpenTimeRepository openTimeRepository;
    private final RoomRepository roomRepository;

    public Optional<BranchOffice> getBranchOfficeById(Long id) {
        return branchOfficeRepository.findById(id);
    }

    public List<BranchOffice> getBranchOfficesByCompany(Long companyId) {
        return branchOfficeRepository.findByCompanyId(companyId);
    }

    public BranchOffice getBranchOfficeForAdmin(Long branchOfficeId, Admin admin) {
        BranchOffice branchOffice = branchOfficeRepository.findById(branchOfficeId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej placówki"));

        if (!branchOffice.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("Placówka należy do innej firmy");
        }
        return branchOffice;
    }

    public BranchOffice createBranchOffice(Long companyId, BranchOfficeDto request, Admin admin) {
        if (!admin.getCompany().getId().equals(companyId)) {
            throw new AccessDeniedException("Nie można dodać placówki do innej firmy");
        }

        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danej firmy"));

        BranchOffice branchOffice = BranchOffice.builder()
            .company(company)
            .name(request.getName())
            .city(request.getCity())
            .address(request.getAddress())
            .build();

        return branchOfficeRepository.save(branchOffice);
    }

    public BranchOffice editBranchOffice(Long branchOfficeId, BranchOfficeDto request, Admin admin) {
        BranchOffice branchOffice = getBranchOfficeForAdmin(branchOfficeId, admin);

        if (request.getName() != null) {
            branchOffice.setName(request.getName());
        }
        if (request.getCity() != null) {
            branchOffice.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            branchOffice.setAddress(request.getAddress());
        }

        return branchOfficeRepository.save(branchOffice);
    }

    public void removeBranchOffice(Long branchOfficeId, Admin admin) {
        BranchOffice branchOffice = getBranchOfficeForAdmin(branchOfficeId, admin);
        branchOfficeRepository.delete(branchOffice);
    }

    public Optional<CompanyOpenTime> getOpenTime(Long branchOfficeId) {
        return openTimeRepository.findByBranchOfficeId(branchOfficeId);
    }

    public CompanyOpenTime setOpenTime(Long branchOfficeId, CompanyOpenTimeDto request, Admin admin) {
        BranchOffice branchOffice = getBranchOfficeForAdmin(branchOfficeId, admin);

        if (!request.getOpenTime().isBefore(request.getCloseTime())) {
            throw new IllegalStateException("Godzina otwarcia musi być wcześniejsza niż godzina zamknięcia");
        }

        CompanyOpenTime openTime = openTimeRepository.findByBranchOfficeId(branchOfficeId)
            .orElseGet(() -> CompanyOpenTime.builder().branchOffice(branchOffice).build());

        openTime.setOpenTime(request.getOpenTime());
        openTime.setCloseTime(request.getCloseTime());

        return openTimeRepository.save(openTime);
    }

    public Room createRoom(Long branchOfficeId, RoomDto request, Admin admin) {
        BranchOffice branchOffice = getBranchOfficeForAdmin(branchOfficeId, admin);

        Room room = Room.builder()
            .branchOffice(branchOffice)
            .roomNumber(request.getRoomNumber())
            .build();

        return roomRepository.save(room);
    }

    public Optional<Room> getRoomByBranchOfficeAndRoomNumber(Long branchOfficeId, String roomNumber) {
        return roomRepository.findByBranchOfficeIdAndRoomNumber(branchOfficeId, roomNumber);
    }

    public List<Room> getRoomsByBranchOffice(Long branchOfficeId) {
        return roomRepository.findByBranchOfficeId(branchOfficeId);
    }

    public Room editRoom(Long branchOfficeId, Long roomId, RoomDto request, Admin admin) {
        Room room = getRoomForAdmin(branchOfficeId, roomId, admin);
        room.setRoomNumber(request.getRoomNumber());
        return roomRepository.save(room);
    }

    public void removeRoom(Long branchOfficeId, Long roomId, Admin admin) {
        Room room = getRoomForAdmin(branchOfficeId, roomId, admin);
        roomRepository.delete(room);
    }

    private Room getRoomForAdmin(Long branchOfficeId, Long roomId, Admin admin) {
        getBranchOfficeForAdmin(branchOfficeId, admin);

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono danego pokoju"));

        if (!room.getBranchOffice().getId().equals(branchOfficeId)) {
            throw new AccessDeniedException("Pokój należy do innej placówki");
        }
        return room;
    }

    public BranchOfficeDto getBranchOfficeResponse(BranchOffice branchOffice) {
        return BranchOfficeDto.builder()
            .id(branchOffice.getId())
            .companyId(branchOffice.getCompany().getId())
            .name(branchOffice.getName())
            .city(branchOffice.getCity())
            .address(branchOffice.getAddress())
            .build();
    }

    public CompanyOpenTimeDto getOpenTimeResponse(CompanyOpenTime openTime) {
        return CompanyOpenTimeDto.builder()
            .id(openTime.getId())
            .branchOfficeId(openTime.getBranchOffice().getId())
            .openTime(openTime.getOpenTime())
            .closeTime(openTime.getCloseTime())
            .build();
    }

    public RoomDto getRoomResponse(Room room) {
        return RoomDto.builder()
            .id(room.getId())
            .branchOfficeId(room.getBranchOffice().getId())
            .roomNumber(room.getRoomNumber())
            .build();
    }
}
