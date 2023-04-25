package com.back2261.communityservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private String commentId;
    private String username;
    private String avatar;
    private String message;
    private Integer likeCount;
    private Date updatedDate;
}
