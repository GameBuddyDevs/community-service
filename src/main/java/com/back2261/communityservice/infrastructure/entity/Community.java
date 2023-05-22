package com.back2261.communityservice.infrastructure.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "community")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Community implements Serializable {
    @Id
    private UUID communityId;

    private String name;
    private String description;
    private String communityAvatar;
    private String wallpaper;

    @CreationTimestamp
    private Date createdDate;

    @OneToMany(mappedBy = "community")
    private Set<Post> posts;

    @ManyToMany
    @JoinTable(
            name = "community_members_join",
            joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Gamer> members;

    @ManyToOne
    @JoinColumn(name = "owner")
    private Gamer owner;
}
