package com.studyhahoho.modules.study;

import com.studyhahoho.modules.account.Account;
import com.studyhahoho.modules.study.event.StudyCreatedEvent;
import com.studyhahoho.modules.study.event.StudyUpdateEvent;
import com.studyhahoho.modules.study.form.StudyDescriptionForm;
import com.studyhahoho.modules.tag.Tag;
import com.studyhahoho.modules.tag.TagRepository;
import com.studyhahoho.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.studyhahoho.modules.study.form.StudyForm.VALID_PATH_PATTERN;


@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TagRepository tagRepository;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = studyRepository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }

    public Study getStudyToUpdate(Account account, String path) {
        Study study = this.getStudy(path);
        checkIfManager(account, study);
        return study;
    }

    public Study getStudy(String path) {
        Study study = this.studyRepository.findByPath(path);
        checkIfExistStudy(path, study);
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디 소개를 수정했습니다."));
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }


    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = studyRepository.findStudyWithTagsByPath(path);
        return getStudy(account, path, study);
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = studyRepository.findStudyWithZonesByPath(path);
        return getStudy(account, path, study);
    }

    private Study getStudy(Account account, String path, Study study) {
        checkIfExistStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    private void checkIfManager(Account account, Study study) {
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 존재하지 않습니다.");
        }
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = studyRepository.findStudyWithManagersByPath(path);
        checkIfExistStudy(path, study);
        checkIfManager(account, study);
        return study;
    }

    public void publish(Study study) {
        study.publish();
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    public void close(Study study) {
        study.close();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "스터디가 종료되었습니다."));
    }

    public void startRecruit(Study study) {
        study.startRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "인원 모집을 시작합니다."));
    }

    public void stopRecruit(Study study) {
        study.stopRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study, "인원 모집을 종료했습니다."));
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        return !studyRepository.existsByPath(newPath);
    }

    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateStudyTitle(Study study, String newTitle) {
        study.setTitle(newTitle);
    }

    public void removeStudy(Study study) {
        if (study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void addMember(Study study, Account account) {
        study.addMember(account);
    }

    public void removeMember(Study study, Account account) {
        study.removeMember(account);
    }

    public Study getStudyToEnroll(String path) {
        Study study = studyRepository.findStudyOnlyByPath(path);
        checkIfExistStudy(path, study);
        return study;
    }

    public void generateTestStudies(Account account) {
        for (int i = 0; i < 30; i++) {
            String randomValue = RandomString.make(5);
            Study study = Study.builder()
                    .title("테스트 스터디 " + randomValue)
                    .path("test-" + randomValue)
                    .shortDescription("study for test")
                    .fullDescription("test")
                    .build();
            study.publish();
            Study newStudy = this.createNewStudy(study, account);
            Tag jpa = tagRepository.findByTitle("JPA");
            newStudy.getTags().add(jpa);
        }
    }
}


