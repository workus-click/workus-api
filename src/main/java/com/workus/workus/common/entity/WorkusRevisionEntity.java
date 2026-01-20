package com.workus.workus.common.entity;

import com.workus.workus.common.component.WorkusRevisionListener;
import com.workus.workus.common.constant.ActorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;


@Entity
@Table(name = "revinfo")
@RevisionEntity(WorkusRevisionListener.class)
@Getter @Setter
public class WorkusRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long rev;
    @RevisionTimestamp
    private long revtstmp;
    private Long actorId;
    @Enumerated(EnumType.STRING)
    private ActorType actorType;
}
