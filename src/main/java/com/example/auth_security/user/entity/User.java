package com.example.auth_security.user.entity;

import com.example.auth_security.common.entity.BaseEntity;
import com.example.auth_security.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {


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

    @Column(name = "date_of_birth")
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
    public String getUsername() {
        return this.email;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String getPassword(){
        return this.password;
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
}
