package cn.com.flaginfo.module.common;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Component
public class SpringApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        event.getApplicationContext().publishEvent(new SpringApplicationStartedEvent(event.getApplicationContext()));
    }
}
