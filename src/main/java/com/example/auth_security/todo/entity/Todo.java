package com.example.auth_security.todo.entity;

import com.example.auth_security.category.entity.Category;
import com.example.auth_security.common.entity.BaseEntity;
import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "todos")
@EntityListeners(AuditingEntityListener.class)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EntityAuditTimingData timingData = new EntityAuditTimingData();

    @Embedded
    private EntityAuditActorData actorData = new EntityAuditActorData();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;


    @Column(name = "is_done", nullable = false)
    private boolean isDone;

    /* Relations */

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    // Todo Category

}
