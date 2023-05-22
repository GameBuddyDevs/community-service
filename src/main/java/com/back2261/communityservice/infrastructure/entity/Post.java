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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Serializable {
    @Id
    private UUID postId;

    private String owner;
    private String title;
    private String body;
    private String picture;

    @CreationTimestamp
    private Date createdDate;

    @UpdateTimestamp
    private Date updatedDate;

    private Integer likeCount = 0;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToMany
    @JoinTable(
            name = "post_comments_join",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "post_likes_join",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Gamer> likes;
}
