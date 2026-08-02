package site.ethosylar.pos.user.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private UserStatusResponse status;

    private List<UserRoleResponse> roles;

    private Integer failedLoginAttempts;

    private OffsetDateTime lastLoginAt;

    private OffsetDateTime passwordChangedAt;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}