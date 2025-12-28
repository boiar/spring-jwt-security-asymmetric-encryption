package com.example.auth_security.category.entity;

import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.todo.entity.Todo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SuperBuilder
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private EntityAuditTimingData timingData = new EntityAuditTimingData();

    @Embedded
    private EntityAuditActorData actorData = new EntityAuditActorData();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    /* Relations */
    @OneToMany(mappedBy = "category")
    private List<Todo> todos = new ArrayList<>();



    @PrePersist
    public void prePersist() {
        timingData.setCreatedDate(LocalDateTime.now());
        timingData.setUpdatedDate(LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        timingData.setUpdatedDate(LocalDateTime.now());
    }

}
