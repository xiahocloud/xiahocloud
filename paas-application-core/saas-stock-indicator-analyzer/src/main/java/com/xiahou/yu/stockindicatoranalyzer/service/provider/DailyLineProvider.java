package com.xiahou.yu.stockindicatoranalyzer.service.provider;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 多态的日线数据提供者接口：股票 / 指数 / 行业 的统一抽象。
 */
public interface DailyLineProvider {

    /** 根据代码与区间获取升序日线列表 */
    List<DailyLineDTO> listByCodeBetween(String code, LocalDate start, LocalDate end);

    /** 获取最近 N 条（按日期降序取 N，再反转为升序） */
    List<DailyLineDTO> listLatestAsc(String code, int limit);
}