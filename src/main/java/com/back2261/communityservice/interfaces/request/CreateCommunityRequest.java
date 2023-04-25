package com.back2261.communityservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommunityRequest {
    @NotBlank(message = "Title of the community is required")
    private String name;

    @NotBlank(message = "Description of the community is required")
    private String description;

    private String avatar;
    private String wallpaper;
}
