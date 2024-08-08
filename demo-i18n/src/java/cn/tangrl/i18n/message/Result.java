package cn.tangrl.i18n.message;

import java.io.Serializable;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 通用api接口返回数据格式
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 成功标志
     */
    private boolean success = true;

    /**
     * 返回处理消息
     */
    private String message = "";

    /**
     * 返回代码
     */
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    private T result;

    public Result() {
    }

    /**
     * 构造函数
     *
     * @param code
     * @param message
     */
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(String msg) {
        return ok(HttpStatus.OK.value(), msg, null);
    }

    public static <T> Result<T> ok(T data) {
        return ok(HttpStatus.OK.value(), null, data);
    }

    public static <T> Result<T> ok(Integer code, String message, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(code);
        r.setMessage(message);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> error(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        return error(code, msg, null);
    }

    public static <T> Result<T> error(int code, String msg, T data) {
        Result<T> r = new Result<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        r.setResult(data);
        return r;
    }
}