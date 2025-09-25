package com.xiahou.yu.paasdomincore.design.dto;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import lombok.Data;

/**
 * description: 查询结果
 *
 * @author wanghaoxin
 * date     2025/9/25 17:54
 * @version 1.0
 */
@Data
public class DataOperationResult extends AbstractResult {
    public DataOperationResult() {
        super();
    }

    public DataOperationResult(Object data) {
        super(data);
    }

    public DataOperationResult(ResultStatusEnum status) {
        super(status);
    }

    public DataOperationResult(String requestId, ResultStatusEnum status) {
        super(requestId, status);
    }
}
