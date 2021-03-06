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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudySettingsControllerTest extends ContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyFactory studyFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AccountRepository accountRepository;
    @Autowired StudyRepository studyRepository;

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 소개 수정 폼 조회_실패_권한없는 유저")
    void 스터디_소개_수정_폼_조회_실패_권한없는_유저() throws Exception {
        // given
        Account hahaha = accountFactory.createAccount("hahaha");
        Study study = studyFactory.createStudy("test-study", hahaha);
        // when

        // then
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("스터디 소개 수정 폼 조회_성공")
    void 스터디_소개_수정_폼_조회_성공() throws Exception {
        // given
        Account hahoho =accountRepository.findByNickname("hahoho");
        Study study = studyFactory.createStudy("test-study", hahoho);
        // when

        // then
        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("hahoho")
    @DisplayName("스터디 소개 수정_성공")
    void 스터디_소개_수정_성공() throws Exception {
        Account hahoho = accountRepository.findByNickname("hahoho");
        Study study = studyFactory.createStudy("test-study", hahoho);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("hahoho")
    @DisplayName("스터디 소개 수정_실패")
    void 스터디_소개_수정_실패() throws Exception {
        Account hahoho = accountRepository.findByNickname("hahoho");
        Study study = studyFactory.createStudy("test-study", hahoho);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }
}