package com.studyhahoho.modules.study;

import com.studyhahoho.WithAccount;
import com.studyhahoho.infra.ContainerBaseTest;
import com.studyhahoho.infra.MockMvcTest;
import com.studyhahoho.modules.account.Account;
import com.studyhahoho.modules.account.AccountFactory;
import com.studyhahoho.modules.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudyControllerTest extends ContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;

    @Autowired StudyFactory studyFactory;


    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 개설 폼 조회")
    void 스터디_개설_폼_조회() throws Exception {
        // given

        // when
        mockMvc.perform(get("/new-study"))
        // then
            .andExpect(status().isOk())
            .andExpect(view().name("study/form"))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("studyForm"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 개설 완료")
    void 스터디_개설_완료() throws Exception {
        // given
        String testPath = "test-path";
        
        // when
        mockMvc.perform(post("/new-study")
            .param("path", testPath)
            .param("title", "study title")
            .param("shortDescription", "short description of a study")
            .param("fullDescription", "full description of a study")
            .with(csrf()))
        // then
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/study/" + testPath));

        Study study = studyRepository.findByPath(testPath);
        assertNotNull(study);
        Account account = accountRepository.findByNickname("hahoho");
        assertTrue(study.getManagers().contains(account));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 개설 실패")
    void 스터디_개설_실패() throws Exception {
        // given

        // when
        mockMvc.perform(post("/new-study")
                .param("path", "wrong path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));

        Study study = studyRepository.findByPath("test-path");
        assertNull(study);
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 조회")
    void 스터디_조회() throws Exception {
        // given
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        // when
        Account hahoho = accountRepository.findByNickname("hahoho");
        studyService.createNewStudy(study, hahoho);

        // then
        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 가입")
    void 스터디_가입() throws Exception {
        // given
        Account hahaha = accountFactory.createAccount("hahaha");
        Study study = studyFactory.createStudy("test-study", hahaha);
        // when

        // then
        mockMvc.perform(get("/study/" + study.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertTrue(study.getMembers().contains(hahoho));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 탈퇴")
    void 스터디_탈퇴() throws Exception {
        // given
        Account hahaha = accountFactory.createAccount("hahaha");
        Study study = studyFactory.createStudy("test-study", hahaha);
        // when
        Account hahoho = accountRepository.findByNickname("hahoho");
        studyService.addMember(study, hahoho);
        // then
        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        assertFalse(study.getMembers().contains(hahaha));
    }

}