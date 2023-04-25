package com.back2261.communityservice.interfaces.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    private String postId;
    private String message;
}
