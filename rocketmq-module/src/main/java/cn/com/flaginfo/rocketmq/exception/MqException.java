package cn.com.flaginfo.rocketmq.exception;

/**
 * @author: Meng.Liu
 * @date: 2018/11/22 上午10:23
 */
public class MqException extends Exception {

    private static final long serialVersionUID = 894798122053539231L;

    private static final long DEFAULT_EXCEPTION_CODE = 700000;

    /**
     * 错误码
     */
    private long code;

    public MqException(String msg){
        this(DEFAULT_EXCEPTION_CODE, msg);
    }

    public MqException(long code, String msg){
        super(msg);
        this.code = code;
    }

    public MqException(String msg, Throwable throwable){
        this(DEFAULT_EXCEPTION_CODE, msg, throwable);
    }

    public MqException(long code, String msg, Throwable throwable){
        super(msg, throwable);
        this.code = code;
    }

    public MqException(Throwable throwable){
        this(DEFAULT_EXCEPTION_CODE, throwable);
    }

    public MqException(long code, Throwable throwable){
        super(throwable);
        this.code = code;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " : [" + code + "], " + getMessage();
    }



}
