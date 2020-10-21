package com.studyhahoho.modules.event;

import com.studyhahoho.WithAccount;
import com.studyhahoho.infra.ContainerBaseTest;
import com.studyhahoho.infra.MockMvcTest;
import com.studyhahoho.modules.account.Account;
import com.studyhahoho.modules.account.AccountFactory;
import com.studyhahoho.modules.account.AccountRepository;
import com.studyhahoho.modules.study.Study;
import com.studyhahoho.modules.study.StudyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest extends ContainerBaseTest {
    
    @Autowired EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired StudyFactory studyFactory;
    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;


    @WithAccount("hahoho")
    @Test
    @DisplayName("선착순 모임 참가 신청 자동수락")
    void 선착순_모임_참가_신청_자동수락() throws Exception {
        // given
        Account hahoho1 = accountFactory.createAccount("hahoho1");
        Study study = studyFactory.createStudy("test-study", hahoho1);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, hahoho1);

        // when
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        // then
        Account hahoho = accountRepository.findByNickname("hahoho");
        isAccepted(hahoho, event);
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("선착순 모임 참가 신청 인원초과")
    void 선착순_모임_참가_신청_인원초과() throws Exception {
        // given
        Account hahoho1 = accountFactory.createAccount("hahoho1");
        Study study = studyFactory.createStudy("test-study", hahoho1);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, hahoho1);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);
        // when
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));
        // then
        Account hahoho = accountRepository.findByNickname("hahoho");
        isNotAccepted(hahoho, event);
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("선착순 모임 확정자 취소시 다음 대기자 신청 확인")
    void 선착순_모임_확정자_취소시_다음_대기자_신청_확인() throws Exception {
        // given
        Account hahoho1 = accountFactory.createAccount("hahoho1");
        Account may = accountFactory.createAccount("may");
        Account hahoho = accountRepository.findByNickname("hahoho");
        Study study = studyFactory.createStudy("test-study", hahoho1);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, hahoho1);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, hahoho);
        eventService.newEnrollment(event, hahoho1);

        isAccepted(may, event);
        isAccepted(hahoho, event);
        isNotAccepted(hahoho1, event);
        // when
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));
        // then
        isAccepted(may, event);
        isAccepted(hahoho1, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, hahoho));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("선착순 모임 비확정자 취소시 기존 확정자 유지")
    void 선착순_모임_비확정자_취소시_기존_확정자_유지() throws Exception {
        // given
        Account hahoho = accountRepository.findByNickname("hahoho");
        Account hahoho1 = accountFactory.createAccount("hahoho1");
        Account may = accountFactory.createAccount("may");
        Study study = studyFactory.createStudy("test-study", hahoho1);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, hahoho1);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, hahoho1);
        eventService.newEnrollment(event, hahoho);

        isAccepted(may, event);
        isAccepted(hahoho1, event);
        isNotAccepted(hahoho, event);
        // when
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));
        // then
        isAccepted(may, event);
        isAccepted(hahoho1, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, hahoho));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("관리자 확인 모임 참가 신청 대기중")
    void 관리자_확인_모임_참가_신청_대기중() throws Exception {
        // given
        Account hahoho1 = accountFactory.createAccount("hahoho1");
        Study study = studyFactory.createStudy("test-study", hahoho1);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, hahoho1);
        // when
        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));
        // then
        Account hahoho = accountRepository.findByNickname("hahoho");
        isNotAccepted(hahoho, event);
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private void isNotAccepted(Account account, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(3));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        return eventService.createEvent(event, study, account);
    }

}