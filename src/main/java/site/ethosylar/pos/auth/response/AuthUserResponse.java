package site.ethosylar.pos.auth.response;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthUserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String status;

    private List<String> roles;

    private Map<String, List<String>> permissions;
}