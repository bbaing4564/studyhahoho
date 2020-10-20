package com.studyhahoho.modules.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhahoho.modules.account.Account;
import com.studyhahoho.modules.account.AccountRepository;
import com.studyhahoho.modules.account.CurrentAccount;
import com.studyhahoho.modules.event.Enrollment;
import com.studyhahoho.modules.event.EnrollmentRepository;
import com.studyhahoho.modules.study.Study;
import com.studyhahoho.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            List<Enrollment> enrollmentList = enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true);
            List<Study> studyList = studyRepository.findByAccount(accountLoaded.getTags(), accountLoaded.getZones());
            List<Study> studyManagerOf = studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false);
            List<Study> studyMemberOf = studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false);
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentList);
            model.addAttribute("studyList", studyList);
            model.addAttribute("studyManagerOf", studyManagerOf);
            model.addAttribute("studyMemberOf", studyMemberOf);
            return "index-after-login";
        }
        model.addAttribute("studyList", studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
        return "index";
    }

    @GetMapping("/login")
    public String login( ){
        return "/login";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                                      Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        String sortProperty = pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount";
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", sortProperty);
        return "search";
    }
}
