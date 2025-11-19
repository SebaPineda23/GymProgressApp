package Application.GymProgress.Repositories;

import Application.GymProgress.Entities.SetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SetRecordRepository extends JpaRepository<SetRecord, Long> {
}
