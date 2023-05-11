package com.back2261.communityservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    @NotBlank(message = "Community id is required")
    private String communityId;

    @NotBlank(message = "Title is required")
    private String title;

    private String body;
    private String picture;
}
