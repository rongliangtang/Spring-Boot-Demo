package cn.tangrl.i18n.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

public class I18NLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        //获取请求中的语言参数
        String language = httpServletRequest.getHeader("Accept-Language");
        Locale locale;
        if (StringUtils.hasText(language)) {
            // 如果请求头中携带了国际化的参数，创建对应的 Locale 对象
            locale = new Locale(language);
        } else {
            //如果没有，使用默认的 Locale 对象（根据主机的语言环境生成一个 Locale ）。
            locale = Locale.getDefault();
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse, Locale locale) {

    }
}