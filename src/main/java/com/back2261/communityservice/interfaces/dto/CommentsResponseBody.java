package com.back2261.communityservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsResponseBody extends BaseModel {

    List<CommentDto> comments;
}
