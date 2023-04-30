package com.back2261.communityservice.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamerDto {

    private String userId;
    private String gamerUsername;
    private String avatar;
    private Boolean isOwner;
}
