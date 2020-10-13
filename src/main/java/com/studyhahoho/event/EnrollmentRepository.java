package com.studyhahoho.event;

import com.studyhahoho.domain.Account;
import com.studyhahoho.domain.Enrollment;
import com.studyhahoho.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
