package com.xiahou.yu.paasmetacore.constant;

/**
 * description 返回数据状态
 *
 * @author wanghaoxin
 * date     2021/11/28 10:42
 * @version 1.0
 */
public enum ResultStatusEnum {
    /**
     * 成功
     */
    SUCCESS("00000", "成功。"),

    SYSTEM_ERROR("B0001", "服务器异常。"),

    PARAMS_NOT_MATCHES("A0421", "参数不匹配。");

    private String code;

    private String message;

    ResultStatusEnum(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
