package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.dto.VolumeCompareResult;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成交量分析服务：比较最近N个交易日与之前M个交易日的成交量总和，计算倍数并排序。
 *
 * 参数语义（按用户描述）：
 * - recentDaysParam：传入值X，表示“最近X+1天”（例如传2表示最近3天）
 * - previousDaysParam：传入值Y，表示“最近(X+Y+1)天到最近(X+1)天”之间的Y天（例如传3表示最近6天到最近3天这3天）
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VolumeAnalysisService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;

    /**
     * 返回股票代码列表，按照 (最近recent窗口成交量总和 / 之前previous窗口成交量总和) 倍数倒序排序。
     * recentDaysParam 语义：传入2表示最近3天（窗口大小 recentSize = recentDaysParam + 1）
     * previousDaysParam 语义：传入3表示“最近6天到最近3天”（窗口大小 previousSize = previousDaysParam，紧邻 recent 窗口之前）
     */
    public List<String> findStocksByVolumeSurge(int recentDaysParam, int previousDaysParam) {
        return findVolumeSurgeDetails(recentDaysParam, previousDaysParam).stream()
                .map(VolumeCompareResult::getStockCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回详细结果：包含股票代码、两段成交量总和以及倍数，按倍数倒序。
     */
    public List<VolumeCompareResult> findVolumeSurgeDetails(int recentDaysParam, int previousDaysParam) {
        // 按用户定义的语义计算窗口大小
        int recentSize = recentDaysParam + 1; // 例如 recentDaysParam=2 => 最近3天
        int previousSize = previousDaysParam;  // 例如 previousDaysParam=3 => 最近6天到最近3天（3天）

        if (recentSize <= 0 || previousSize <= 0) {
            throw new IllegalArgumentException("recentDaysParam必须>=0且previousDaysParam必须>0");
        }
        int need = recentSize + previousSize;
        List<VolumeCompareResult> results = new ArrayList<>();

        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        for (StocksInfoMaster info : allInfos) {
            String code = info.getStockCode();
            if (code == null || code.isEmpty()) continue;

            // 获取最近 need 条（trade_date DESC，最新在前）
            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(code, need, 0);
            if (latestDesc == null || latestDesc.size() < need) {
                // 数据不足，跳过该股票
                continue;
            }

            long recentSum = latestDesc.stream()
                    .limit(recentSize)
                    .mapToLong(d -> d.getVolume() == null ? 0L : d.getVolume())
                    .sum();

            long previousSum = latestDesc.stream()
                    .skip(recentSize)
                    .limit(previousSize)
                    .mapToLong(d -> d.getVolume() == null ? 0L : d.getVolume())
                    .sum();

            if (previousSum <= 0) {
                // 为避免除零或无意义比值，跳过previous为0的情况；也可选择设置为极大值或1。
                continue;
            }
            double ratio = recentSum * 1.0 / previousSum;
            results.add(new VolumeCompareResult(code, recentSum, previousSum, ratio));
        }

        results.sort(Comparator.comparing(VolumeCompareResult::getRatio).reversed());
        return results;
    }
}