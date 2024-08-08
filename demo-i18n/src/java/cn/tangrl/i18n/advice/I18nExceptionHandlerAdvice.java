package cn.tangrl.i18n.advice;

import cn.tangrl.i18n.annotation.I18nApiException;
import cn.tangrl.i18n.exception.BusinessException;
import cn.tangrl.i18n.message.Result;
import cn.tangrl.i18n.message.ResultCode;
import cn.tangrl.i18n.utils.MessageUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RestControllerAdvice
@Order(1)
public class I18nExceptionHandlerAdvice {

    private final MessageUtil messageUtil;

    public I18nExceptionHandlerAdvice(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    /**
     * 处理自定义业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleServiceException(BusinessException e) {
        log.error("Handler Exception: ", e);
        ResultCode resultCode = e.getResultCode();
        String localizedMessage = MessageUtil.getMessage(resultCode.getMessageKey());
        return Result.error(localizedMessage);
    }

    /**
     * 处理对象类型参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        String error = String.join(",", errors);
        log.info("MethodArgumentNotValidException: {}", error);
        return Result.error(error);
    }

    /**
     * 处理注解的异常，用来兜底，定义未定义的异常消息，返回给前端
     * @param e
     * @param method
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception e, HandlerMethod method) {
        I18nApiException annotation = method.getMethodAnnotation(I18nApiException.class);
        log.error("Handler Exception: ", e);
        ResultCode resultCode = annotation.value();
        String localizedMessage = MessageUtil.getMessage(resultCode.getMessageKey());
        return Result.error(localizedMessage);
    }
}
