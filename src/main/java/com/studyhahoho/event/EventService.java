package com.studyhahoho.event;

import com.studyhahoho.domain.Account;
import com.studyhahoho.domain.Event;
import com.studyhahoho.domain.Study;
import com.studyhahoho.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;


    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        // TODO 모집인원을 늘린 선착순 모임의 경우, 자동으로 추가 인원의 참가 신청을 확정상태로 변경
    }
}
