package cn.tangrl.i18n.config;

import cn.tangrl.i18n.utils.I18NLocaleResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 获取LocaleResolver
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new I18NLocaleResolver();
    }
}
