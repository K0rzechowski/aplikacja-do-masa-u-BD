package emino.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import emino.backend.models.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByBranchOfficeIdAndRoomNumber(Long branchOfficeId, String roomNumber);
    List<Room> findByBranchOfficeId(Long branchOfficeId);
}
