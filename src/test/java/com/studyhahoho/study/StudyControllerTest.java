package com.studyhahoho.study;

import com.studyhahoho.account.UserAccount;
import com.studyhahoho.domain.Account;
import com.studyhahoho.domain.Study;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudyControllerTest {
    
    Study study;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        study = new Study();
        account = new Account();
        account.setNickname("hahoho");
        account.setPassword("1234");
        userAccount = new UserAccount(account);
    }

    @Test
    @DisplayName("스터디 멤버인지 확인")
    void 스터디_멤버인지_확인() throws Exception {
        // given
        study.addMember(account);
        // when

        // then
        assertTrue(study.isMember(userAccount));
    }

    @Test
    @DisplayName("스터디 관리자인지 확인")
    void 스터디_관리자인지_확인() throws Exception {
        // given
        study.addManager(account);
        // when

        // then
        assertTrue(study.isManager(userAccount));
    }

    @Test
    @DisplayName("스터디가 비공개이거나 인원모집 중이 아니면 스터디 가입 불가능")
    void 스터디가_비공개이거나_인원모집_중이_아니면_스터디_가입_불가능() throws Exception {

        study.setPublished(true);
        study.setRecruiting(false);
        assertFalse(study.isJoinable(userAccount));

        study.setPublished(false);
        study.setRecruiting(true);
        assertFalse(study.isJoinable(userAccount));
    }

    @Test
    @DisplayName("스터디 공개 인원모집중이지만 스터디 멤버는 스터디 재가입 불가")
    void 스터디_공개_인원모집중이지만_스터디_멤버는_스터디_재가입_불가() throws Exception {
        // given

        // when
        study.setPublished(true);
        study.setRecruiting(true);
        study.addMember(account);

        // then
        assertFalse(study.isJoinable(userAccount));
    }

    @Test
    @DisplayName("스터디 공개 인원모집중이지만 스터디 매니는 스터디 재가입 불가")
    void 스터디_공개_인원모집중이지만_스터디_매니는_스터디_재가입_불가() throws Exception {
        // given

        // when
        study.setPublished(true);
        study.setRecruiting(true);
        study.addManager(account);

        // then
        assertFalse(study.isJoinable(userAccount));
    }

    @Test
    @DisplayName("스터디 공개 인원모집 멤버나 매니저가 아니라면 가입 가능")
    void 스터디_공개_인원모집_멤버나_매니저가_아니라면_가입_가능() throws Exception {
        // given

        // when
        study.setPublished(true);
        study.setRecruiting(true);

        // then
        assertTrue(study.isJoinable(userAccount));
    }
    
}