package com.studyhahoho.account;

import com.studyhahoho.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @Test
    @DisplayName("회원 가입 화면 출력 테스트")
    void 회원_가입_화면_출력_테스트() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리_입력값 오류")
    void 회원_가입_처리_입력값_오류() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(post("/sign-up")
                .param("nickname", "hahoho")
                .param("email", "abcabc")
                .param("password", "1234")
                .with((csrf())))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());

    }

    @Test
    @DisplayName("회원 가입 처리_입력값 정상")
    void 회원_가입_처리_입력값_정상() throws Exception {
        // given

        // when

        // then
        mockMvc.perform(post("/sign-up")
                .param("nickname", "hahoho")
                .param("email", "abcabc@gaga.com")
                .param("password", "12341234")
                .with((csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("hahoho"));


        Account account = accountRepository.findByEmail("abcabc@gaga.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12341234");
        assertNotNull(account.getEmailCheckToken());

        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("인증 메일 확인_입력값 오류")
    void 인증_메일_확인_입력값_오류() throws Exception {
        // given

        
        // when

        // then
        mockMvc.perform(get("/check-email-token")
                .param("token", "hahahoho")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인_입력값 정상")
    void 인증_메일_확인_입력값_정상() throws Exception {
        // given
        Account account = Account.builder()
                .email("test@email.com")
                .nickname("hahoho")
                .password("12341234")
                .build();

        // when
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        // then
        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated());
    }
}