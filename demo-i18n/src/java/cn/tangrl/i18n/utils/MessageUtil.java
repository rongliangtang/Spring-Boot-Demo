package cn.tangrl.i18n.utils;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    private static MessageSource messageSource;

    public static String getMessage(String messageKey, String... dynamicValues) {
        return messageSource.getMessage(messageKey, dynamicValues, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String messageKey, Locale locale, String... dynamicValues) {
        return messageSource.getMessage(messageKey, dynamicValues, locale);
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource1) {
        messageSource = messageSource1;
    }
}
