package cn.tangrl.i18n.annotation;

import cn.tangrl.i18n.message.ResultCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface I18nApiException {

    ResultCode value();
}
