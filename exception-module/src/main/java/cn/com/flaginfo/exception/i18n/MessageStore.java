package cn.com.flaginfo.exception.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * @author: Meng.Liu
 * @date: 2018/12/17 下午3:10
 */
@Component
@Slf4j
public class MessageStore {

    @Autowired
    private MessageSourceConfiguration messageSourceConfiguration;

    @Autowired
    private MessageSource messageSource;

    private static MessageStore THIS;

    @PostConstruct
    private void init() {
        MessageStore.THIS = this;
    }

    public static String getMessage(String key) {
        return getMessage(LocaleHolder.get(), key);
    }

    public static String getMessage(Locale locale, String key) {
        return getMessage(locale, key, key);
    }

    public static String getMessage(Locale locale, String key, String defaultStr) {
        if( null == THIS || null == THIS.messageSourceConfiguration || null == THIS.messageSource ){
            if (log.isDebugEnabled()) {
                log.debug("message store is not init...");
            }
            return defaultStr;
        }
        if (null == locale) {
            locale = THIS.messageSourceConfiguration.getDefaultLocale();
        }
        String message = null;
        try {
            message = THIS.messageSource.getMessage(key, null, locale);
            if (log.isDebugEnabled()) {
                log.debug("get message key:[{}], value:{}, local:{}", key, message, locale);
            }
        } catch (NoSuchMessageException e) {
            log.error("cannot find the message with key : {}, local: {}", key, locale);
        }
        return null == message ? defaultStr : message;

    }
}
