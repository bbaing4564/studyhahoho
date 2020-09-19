package com.studyhahoho.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhahoho.WithAccount;
import com.studyhahoho.account.AccountRepository;
import com.studyhahoho.account.AccountService;
import com.studyhahoho.domain.Account;
import com.studyhahoho.domain.Tag;
import com.studyhahoho.settings.form.TagForm;
import com.studyhahoho.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정 폼")
    void 프로필_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
        
        // when

        // then
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정_입력값 정상")
    void 프로필_수정_입력값_정상() throws Exception {
        // given
        String bio = "update bio";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
        // when
                .param("bio", bio)
                .with(csrf()))

        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertEquals(bio, hahoho.getBio());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정_입력값 에러")
    void 프로필_수정_입력값_에러() throws Exception {
        // given
        String bio = "too long too long too long too long too long too long too long too long too long too long too long too long too long too long too long ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)

        // when
                .param("bio", bio)
                .with(csrf()))

        // then
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertNull(hahoho.getBio());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정 폼")
    void 패스워드_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))

        // when

        // then
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("passwordForm"));

    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정_입력값 정상")
    void 패스워드_수정_입력값_정상() throws Exception {
        // given
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
        // when
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345678")
            .with(csrf()))
        // then
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(flash().attributeExists("message"))
            .andDo(print());

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertTrue(passwordEncoder.matches("12345678", hahoho.getPassword()));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정_입력값 에러_패스워드 불일치")
    void 패스워드_수정_입력값_에러_패스워드_불일치() throws Exception {
        // given
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
        // when
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345677")
            .with(csrf()))
        // then
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("passwordForm"))
            .andExpect(model().attributeExists("account"))
            .andDo(print());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("닉네임 수정 폼")
    void 닉네임_수정_폼() throws Exception {
        // given
              
        // when
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
        // then
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("닉네임 수정_입력값 정상")
    void 닉네임_수정_입력값_정상() throws Exception {
        // given
        String newNickname = "hahoho";
        // when
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("hahoho"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("닉네임 수정_입력값 에러")
    void 닉네임_수정_입력값_에러() throws Exception {
        // given
        String newNickname = "¯\\_(ツ)_/¯";
        // when
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
        // then
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정의 태그 수정 폼")
    void 계정의_태그_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
        // when

        // then
            .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("whitelist"))
            .andExpect(model().attributeExists("tags"));    
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정에 태그 추가")
    void 계정에_태그_추가() throws Exception {
        // given
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");
        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
        // when
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
        // then
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account hahoho = accountRepository.findByNickname("hahoho");
        assertTrue(hahoho.getTags().contains(newTag));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정에 태그 삭제")
    void 계정에_태그_삭제() throws Exception {
        // given
        Account hahoho = accountRepository.findByNickname("hahoho");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(hahoho, newTag);
        assertTrue(hahoho.getTags().contains(newTag));
        // when
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");
        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
        // then
                .andExpect(status().isOk());

        assertFalse(hahoho.getTags().contains(newTag));
    }
}