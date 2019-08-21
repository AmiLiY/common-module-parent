package cn.com.flaginfo.exception;

import cn.com.flaginfo.exception.i18n.MessageStore;

/**
 * 通用错误码
 * code错误码
 * message国际化key
 * @author: Meng.Liu
 * @date: 2018/12/17 上午10:52
 */
public interface Error {

    Long code();

    String message();
}
