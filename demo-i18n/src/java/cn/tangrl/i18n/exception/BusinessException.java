package cn.tangrl.i18n.exception;

import cn.tangrl.i18n.message.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务逻辑异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class BusinessException extends RuntimeException {

    /**
     * 响应码
     */
    private final ResultCode resultCode;

    /**
     * 构造函数
     *
     * @param resultCode 响应码
     */
    public BusinessException(ResultCode resultCode) {
        // 使用ResultCode的名字作为异常消息
        super(resultCode.getDefaultMessage());
        this.resultCode = resultCode;
    }

    public int getCode() {
        return resultCode.getCode();
    }
}
