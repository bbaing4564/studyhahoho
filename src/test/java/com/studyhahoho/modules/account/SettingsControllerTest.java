package com.studyhahoho.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhahoho.WithAccount;
import com.studyhahoho.infra.AbstractContainerBaseTest;
import com.studyhahoho.infra.MockMvcTest;
import com.studyhahoho.modules.tag.Tag;
import com.studyhahoho.modules.tag.TagForm;
import com.studyhahoho.modules.tag.TagRepository;
import com.studyhahoho.modules.zone.Zone;
import com.studyhahoho.modules.zone.ZoneForm;
import com.studyhahoho.modules.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.studyhahoho.modules.account.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;
    @Autowired TagRepository tagRepository;
    @Autowired AccountService accountService;
    @Autowired ZoneRepository zoneRepository;

    private final Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @BeforeEach
    void beforeEach() {
        zoneRepository.save(testZone);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
        zoneRepository.deleteAll();
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정 폼")
    void 프로필_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(ROOT + SETTINGS + PROFILE))

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
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)
                // when
                .param("bio", bio)
                .with(csrf()))

                // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PROFILE))
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
        mockMvc.perform(post(ROOT + SETTINGS + PROFILE)

                // when
                .param("bio", bio)
                .with(csrf()))

                // then
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PROFILE))
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
        mockMvc.perform(get(ROOT + SETTINGS + PASSWORD))

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
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                // when
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + PASSWORD))
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
        mockMvc.perform(post(ROOT + SETTINGS + PASSWORD)
                // when
                .param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345677")
                .with(csrf()))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + PASSWORD))
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
        mockMvc.perform(get(ROOT + SETTINGS + ACCOUNT))
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
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT + SETTINGS + ACCOUNT))
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
        mockMvc.perform(post(ROOT + SETTINGS + ACCOUNT)
                .param("nickname", newNickname)
                .with(csrf()))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS + ACCOUNT))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정의 태그 수정 폼")
    void 계정의_태그_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(ROOT + SETTINGS + TAGS))
                // when

                // then
                .andExpect(view().name(SETTINGS + TAGS))
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
        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/add")
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
        mockMvc.perform(post(ROOT + SETTINGS + TAGS + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                // then
                .andExpect(status().isOk());

        assertFalse(hahoho.getTags().contains(newTag));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정의 지역 정보 수정 폼")
    void 계정의_지역_정보_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(ROOT + SETTINGS + ZONES))
                // when

                // then
                .andExpect(view().name(SETTINGS + ZONES))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정의 지역 정보 추가")
    void 계정의_지역_정보_추가() throws Exception {
        // given
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());
        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
                // when
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                // then
                .andExpect(status().isOk());

        Account hahoho = accountRepository.findByNickname("hahoho");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertTrue(hahoho.getZones().contains(zone));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("계정에 지역 정보 삭제")
    void 계정에_지역_정보_삭제() throws Exception {
        // given
        Account hahoho = accountRepository.findByNickname("hahoho");
        Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());

        accountService.addZone(hahoho, zone);
        assertTrue(hahoho.getZones().contains(zone));

        // when
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());

        mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                // then
                .andExpect(status().isOk());

        assertFalse(hahoho.getZones().contains(zone));
    }
}