package com.studyhahoho.zone;

import com.studyhahoho.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndProvince(String citiName, String provinceName);
}
