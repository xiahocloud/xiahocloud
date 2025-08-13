package com.xiahou.yu.paasmetacore.constant.exception;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: paas 异常
 *
 * @author wanghaoxin
 * date     2022/8/12 18:07
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PaaSException extends RuntimeException {

    private Object payload;

    private String message;

    private ResultStatusEnum resultStatusEnum;

    public PaaSException() {
        this(ResultStatusEnum.SYSTEM_ERROR);
    }

    public PaaSException(ResultStatusEnum resultStatusEnum) {
        this(resultStatusEnum, resultStatusEnum.getMessage());
    }

    public PaaSException(ResultStatusEnum resultStatusEnum, String message) {
        this(resultStatusEnum, null, message);
    }

    public PaaSException(ResultStatusEnum resultStatusEnum, Object payload, String message) {
        this(resultStatusEnum, payload, message == null ? resultStatusEnum.getMessage() : message, null);
    }

    public PaaSException(ResultStatusEnum resultStatusEnum, String message, Exception e) {
        this(resultStatusEnum, null, message == null ? resultStatusEnum.getMessage() : message, e);
    }

    public PaaSException(ResultStatusEnum resultStatusEnum, Object payload, String message, Throwable throwable) {
        super(message == null ? resultStatusEnum.getMessage() : message, throwable);
        this.setMessage(message == null ? resultStatusEnum.getMessage() : message);
        this.setPayload(payload);
        this.setResultStatusEnum(resultStatusEnum);
    }
}
