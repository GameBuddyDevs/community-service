package com.back2261.communityservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {

    private String postId;
    private String username;
    private String avatar;
    private String communityName;
    private String title;
    private String body;
    private String picture;
    private Date updatedDate;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
}
