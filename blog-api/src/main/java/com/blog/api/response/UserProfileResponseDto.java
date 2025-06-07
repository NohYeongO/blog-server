package com.blog.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String githubId;
    private String name;
    private String avatarUrl;
    private boolean isAdmin;
}
