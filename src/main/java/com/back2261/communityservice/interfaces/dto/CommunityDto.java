package com.back2261.communityservice.interfaces.dto;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityDto {

    private String communityId;
    private String name;
    private String description;
    private String communityAvatar;
    private String wallpaper;
    private Date createdDate;
    private List<GamerDto> members;
}
