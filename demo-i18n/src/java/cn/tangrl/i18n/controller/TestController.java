package cn.tangrl.i18n.controller;

import cn.tangrl.i18n.annotation.I18nApiException;
import cn.tangrl.i18n.controller.request.UserReq;
import cn.tangrl.i18n.exception.BusinessException;
import cn.tangrl.i18n.message.Result;
import cn.tangrl.i18n.message.ResultCode;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/case1")
    public void testCase1() throws Exception {
        throw new BusinessException(ResultCode.BUSINESS_EXCEPTION);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/case2")
    @I18nApiException(ResultCode.ERROR)
    public void testCase2() throws Exception {
        throw new Exception();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/case3")
    @I18nApiException(ResultCode.ERROR)
    public void login(@Validated @RequestBody UserReq userRequest) {

    }
}
