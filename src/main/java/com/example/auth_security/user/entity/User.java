package com.example.auth_security.user.entity;

import com.example.auth_security.common.entity.EntityAuditActorData;
import com.example.auth_security.common.entity.EntityAuditTimingData;
import com.example.auth_security.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Embedded
    private EntityAuditTimingData timingData = new EntityAuditTimingData();

    @Embedded
    private EntityAuditActorData actorData = new EntityAuditActorData();


    /* Cols */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "is_account_locked")
    private boolean locked;

    @Column(name = "is_credentials_expired")
    private boolean credentialsExpired;

    @Column(name = "is_email_verified")
    private boolean emailVerified;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "is_phone_verified")
    private boolean phoneVerified;


    /* Relations */
    @ManyToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.EAGER
    )
    @JoinTable(
        name = "users_roles",
        joinColumns = {
            @JoinColumn(name = "user_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "role_id")
        }
    )
    private List<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (CollectionUtils.isEmpty(this.roles)){
            return List.of();
        }
        return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialsExpired;
    }


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
