package cn.com.flaginfo.module.common;

import org.springframework.context.ApplicationEvent;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
public class SpringApplicationStartedEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SpringApplicationStartedEvent(Object source) {
        super(source);
    }
}
