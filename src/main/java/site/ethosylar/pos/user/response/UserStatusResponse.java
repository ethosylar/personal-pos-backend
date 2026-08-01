package site.ethosylar.pos.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatusResponse {

    private Long id;
    private String code;
    private String name;
}