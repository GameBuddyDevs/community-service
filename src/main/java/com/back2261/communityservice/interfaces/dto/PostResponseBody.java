package com.back2261.communityservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostResponseBody extends BaseModel {

    private List<PostDto> posts;
}
