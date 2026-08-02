package site.ethosylar.pos.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String tokenType;

    private String accessToken;

    private Long expiresIn;

    private AuthUserResponse user;
}