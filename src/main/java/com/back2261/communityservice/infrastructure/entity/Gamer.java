package com.back2261.communityservice.infrastructure.entity;

import io.github.GameBuddyDevs.backendlibrary.enums.Role;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "gamer", schema = "schauth")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Gamer implements UserDetails {
    @Id
    private String userId;

    @Column(name = "username", unique = true)
    private String gamerUsername;

    @Column(unique = true, nullable = false)
    private String email;

    private Integer age;
    private String country;
    private UUID avatar;

    @UpdateTimestamp
    private Date lastModifiedDate;

    private String pwd;
    private String gender;
    private Integer coin;
    private Boolean isBlocked;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "community_members_join",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id"))
    private Set<Community> joinedCommunities;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Community> ownedCommunities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
