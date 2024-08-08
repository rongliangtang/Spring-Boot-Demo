package cn.tangrl.i18n.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserReq {

    /**
     * 账号
     */
    @NotEmpty(message = "{username_not_empty}")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "{password_not_empty}")
    private String password;
}
