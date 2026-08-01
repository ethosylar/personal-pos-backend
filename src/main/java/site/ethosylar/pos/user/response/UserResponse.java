package site.ethosylar.pos.user.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private UserStatusResponse status;

    private List<UserRoleResponse> roles;

    private OffsetDateTime lastLoginAt;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}