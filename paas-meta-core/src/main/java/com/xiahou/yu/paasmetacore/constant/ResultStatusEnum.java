package com.xiahou.yu.paasmetacore.constant;

import lombok.Getter;

/**
 * description 返回数据状态
 *
 * @author wanghaoxin
 * date     2021/11/28 10:42
 * @version 1.0
 */
@Getter
public enum ResultStatusEnum {
    /**
     * 成功
     */
    SUCCESS("00000", "成功。"),

    META_CORE_ERROR("A00001", "模型层异常。"),

    DATA_CORE_ERROR("A00002", "数据核心层异常。"),

    SYSTEM_ERROR("B00001", "服务器异常。"),

    PARAMS_NOT_MATCHES("A0421", "参数不匹配。");

    private String code;

    private String message;

    ResultStatusEnum(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
