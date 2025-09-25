package com.xiahou.yu.paasdomincore.design.dto;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import lombok.Data;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/25 17:54
 * @version 1.0
 */
@Data
public abstract class AbstractResult {
    private String requestId;
    private String code;
    private String message;
    private ResultStatusEnum status;
    private Object data;

    public AbstractResult() {
        this.status = ResultStatusEnum.SUCCESS;
        this.code = this.status.getCode();
        this.message = this.status.getMessage();
    }

    public AbstractResult(Object data) {
        this.status = ResultStatusEnum.SUCCESS;
        this.data = data;
        this.code = this.status.getCode();
        this.message = this.status.getMessage();
    }

    public AbstractResult(ResultStatusEnum status) {
        this.status = status;
        this.code = this.status.getCode();
        this.message = this.status.getMessage();
    }

    public AbstractResult(String requestId, ResultStatusEnum status) {
        this.requestId = requestId;
        this.status = status;
        this.code = this.status.getCode();
        this.message = this.status.getMessage();
    }


}
