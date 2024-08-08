package cn.tangrl.i18n.message;

import lombok.Getter;

public enum ResultCode {
    SUCCESS(200, "success", "success"),
    ERROR(500, "error", "error"),
    BUSINESS_EXCEPTION(502, "business.exception", "business exception");

    @Getter
    private final int code;

    @Getter
    private final String messageKey;

    @Getter
    private final String defaultMessage;

    ResultCode(int code, String messageKey, String defaultMessage) {
        this.code = code;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
    }
}
