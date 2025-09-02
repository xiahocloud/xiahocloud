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
    SUCCESS("A00000", "成功。"),
    SYSTEM_ERROR("A00001", "服务器异常。"),

    META_CORE_ERROR("A10001", "模型层异常。"),

    DOMAIN_CORE_ERROR("A20001", "数据核心层异常。"),

    APPLICATION_CORE_ERROR("A30001", "应用层异常。"),

    /**
     * 根据key获取枚举，未匹配到
     */
    ENUM_NOT_MATCHES("A00002", "枚举未匹配。"),

    PARAMS_NOT_MATCHES("B00001", "业务异常。");




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
