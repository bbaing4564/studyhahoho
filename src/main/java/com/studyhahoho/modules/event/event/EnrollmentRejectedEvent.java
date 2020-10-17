package com.studyhahoho.modules.event.event;

import com.studyhahoho.modules.event.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청이 거절되었습니다.");
    }
}
