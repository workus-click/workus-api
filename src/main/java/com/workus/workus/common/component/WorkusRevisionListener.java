package com.workus.workus.common.component;

import com.workus.workus.common.constant.ActorType;
import com.workus.workus.common.entity.WorkusRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.stereotype.Component;

@Component
public class WorkusRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        WorkusRevisionEntity rev = (WorkusRevisionEntity) revisionEntity;

        rev.setActorType(ActorType.USER);
        rev.setActorId(0L); // TODO: 추후 SecurityContext에서 실제 사용자 ID를 가져오도록 수정
    }
}
