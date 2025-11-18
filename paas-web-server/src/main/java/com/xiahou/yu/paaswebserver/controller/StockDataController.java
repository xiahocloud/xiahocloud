package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.PageResponse;
import com.xiahou.yu.stockindicatoranalyzer.dto.BollingerNarrowResult;
import com.xiahou.yu.stockindicatoranalyzer.dto.BollingerUpperChannelResult;
import com.xiahou.yu.stockindicatoranalyzer.dto.MacdCrossResult;
import com.xiahou.yu.stockindicatoranalyzer.dto.VolumeCompareResult;
import com.xiahou.yu.stockindicatoranalyzer.dto.VolumeUpResult;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.service.StockDataService;
import com.xiahou.yu.stockindicatoranalyzer.service.BollingerAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.VolumeAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.VolumeUpAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.IndicatorAugmentationService;
import com.xiahou.yu.paaswebserver.service.DeepSeekAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.MacdAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.CombinedStrategyAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis.StockDtoAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.StockScreeningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockDataController {

    private final StockDataService stockDataService;
    private final BollingerAnalysisService bollingerAnalysisService;
    private final VolumeAnalysisService volumeAnalysisService;
    private final VolumeUpAnalysisService volumeUpAnalysisService;
    private final IndicatorAugmentationService indicatorAugmentationService;
    private final DeepSeekAnalysisService deepSeekAnalysisService;
    private final MacdAnalysisService macdAnalysisService;
    private final CombinedStrategyAnalysisService combinedStrategyAnalysisService;
    private final StockDtoAnalysisService stockDtoAnalysisService;
    private final StockScreeningService stockScreeningService;

    public StockDataController(StockDataService stockDataService, BollingerAnalysisService bollingerAnalysisService, VolumeAnalysisService volumeAnalysisService, VolumeUpAnalysisService volumeUpAnalysisService, IndicatorAugmentationService indicatorAugmentationService, DeepSeekAnalysisService deepSeekAnalysisService, MacdAnalysisService macdAnalysisService, CombinedStrategyAnalysisService combinedStrategyAnalysisService, StockDtoAnalysisService stockDtoAnalysisService, StockScreeningService stockScreeningService) {
        this.stockDataService = stockDataService;
        this.bollingerAnalysisService = bollingerAnalysisService;
        this.volumeAnalysisService = volumeAnalysisService;
        this.volumeUpAnalysisService = volumeUpAnalysisService;
        this.indicatorAugmentationService = indicatorAugmentationService;
        this.deepSeekAnalysisService = deepSeekAnalysisService;
        this.macdAnalysisService = macdAnalysisService;
        this.combinedStrategyAnalysisService = combinedStrategyAnalysisService;
        this.stockDtoAnalysisService = stockDtoAnalysisService;
        this.stockScreeningService = stockScreeningService;
    }

    /**
     * 基础信息分页列表
     * @param pageNum 页码（从1开始，默认1）
     * @param pageSize 每页条数（默认20）
     * @param exchange 交易所（可选）
     * @param assetType 资产类型（可选）
     * @param status 状态（可选）
     */
    @GetMapping("/info")
    public PageResponse<StocksInfoMaster> listInfos(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "exchange", required = false) String exchange,
            @RequestParam(value = "assetType", required = false) String assetType,
            @RequestParam(value = "status", required = false) String status
    ) {
        List<StocksInfoMaster> items = stockDataService.listInfos(exchange, assetType, status, pageNum, pageSize);
        long total = stockDataService.countInfos(exchange, assetType, status);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 每日行情分页列表（所有）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认50）
     */
    @GetMapping("/daily")
    public PageResponse<StocksDailyData> listDaily(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<StocksDailyData> items = stockDataService.listDaily(pageNum, pageSize);
        long total = stockDataService.countDaily();
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定股票的每日行情分页列表
     * @param stockCode 股票代码
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认50）
     */
    @GetMapping("/daily/{stockCode}")
    public PageResponse<StocksDailyData> listDailyByCode(
            @PathVariable("stockCode") String stockCode,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<StocksDailyData> items = stockDataService.listDaily(stockCode, pageNum, pageSize);
        long total = stockDataService.countDaily(stockCode);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定股票在日期范围内的每日行情分页列表
     * @param stockCode 股票代码
     * @param start 起始日期（yyyy-MM-dd）
     * @param end 结束日期（yyyy-MM-dd）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认50）
     */
    @GetMapping("/daily/{stockCode}/range")
    public PageResponse<StocksDailyData> listDailyByCodeAndRange(
            @PathVariable("stockCode") String stockCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<StocksDailyData> items = stockDataService.listDaily(stockCode, startDate, endDate, pageNum, pageSize);
        long total = stockDataService.countDaily(stockCode, startDate, endDate);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 分析：最近 N 天中出现布林线窄通道的股票列表
     * @param lookbackDays 回看天数（默认50），取最近 lookbackDays 天进行检测
     * @param period 布林线均线周期（默认20），用于计算中轨均线与标准差
     * @param k 标准差倍数（默认2.0），用于计算上下轨
     * @param threshold 窄通道阈值（默认0.04），(upper - lower) / middle <= threshold
     * @param allowedExceedDays 允许在检测窗口中有不超过 N 天宽度超过阈值的违规天数（默认0，表示不容忍）
     */
    @GetMapping("/analysis/bollinger/narrow")
    public List<BollingerNarrowResult> listBollingerNarrow(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "50") int lookbackDays,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k,
            @RequestParam(value = "threshold", required = false, defaultValue = "0.04") double threshold,
            @RequestParam(value = "allowedExceedDays", required = false, defaultValue = "0") int allowedExceedDays
    ) {
        return bollingerAnalysisService.findNarrowChannelStocks(lookbackDays, period, k, threshold, allowedExceedDays);
    }

    /**
     * 分析：成交量倍数排序（返回股票代码）
     * @param recentDays 语义：X 表示最近 X+1 天
     * @param previousDays 语义：Y 表示紧邻上述窗口之前的 Y 天（最近(X+Y+1) 天到最近 (X+1) 天）
     */
    @GetMapping("/analysis/volume/surge")
    public List<String> listVolumeSurgeCodes(
            @RequestParam(value = "recentDays") int recentDays,
            @RequestParam(value = "previousDays") int previousDays
    ) {
        return volumeAnalysisService.findStocksByVolumeSurge(recentDays, previousDays);
    }

    /**
     * 分析：成交量倍数排序（返回详细信息）
     * @param recentDays 语义：X 表示最近 X+1 天
     * @param previousDays 语义：Y 表示紧邻上述窗口之前的 Y 天（最近(X+Y+1) 天到最近 (X+1) 天）
     */
    @GetMapping("/analysis/volume/surge/details")
    public List<VolumeCompareResult> listVolumeSurgeDetails(
            @RequestParam(value = "recentDays") int recentDays,
            @RequestParam(value = "previousDays") int previousDays
    ) {
        return volumeAnalysisService.findVolumeSurgeDetails(recentDays, previousDays);
    }

    /**
     * 分析：放量上涨（返回股票代码）
     * @param lookbackDays 回看天数（默认20），在最近 lookbackDays 内判断是否存在满足条件的日期
     * @param volumePeriod 成交量均值窗口（默认20），用于计算 Volume 的 SMA
     * @param volumeRatioThreshold 放量阈值（默认1.5），当日成交量 / SMA(volume, period) >= 该阈值
     * @param minPriceChangePercent 最小涨幅百分比（默认0.5），当日涨幅 >= 该值（百分比）
     */
    @GetMapping("/analysis/volume-up")
    public List<String> listVolumeUpCodes(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "20") int lookbackDays,
            @RequestParam(value = "volumePeriod", required = false, defaultValue = "20") int volumePeriod,
            @RequestParam(value = "volumeRatioThreshold", required = false, defaultValue = "1.5") double volumeRatioThreshold,
            @RequestParam(value = "minPriceChangePercent", required = false, defaultValue = "0.5") double minPriceChangePercent
    ) {
        return volumeUpAnalysisService.findVolumeUpStockCodes(lookbackDays, volumePeriod, volumeRatioThreshold, minPriceChangePercent);
    }

    /**
     * 分析：放量上涨（返回详细信息）
     * @param lookbackDays 回看天数（默认20），在最近 lookbackDays 内判断是否存在满足条件的日期
     * @param volumePeriod 成交量均值窗口（默认20），用于计算 Volume 的 SMA
     * @param volumeRatioThreshold 放量阈值（默认1.5），当日成交量 / SMA(volume, period) >= 该阈值
     * @param minPriceChangePercent 最小涨幅百分比（默认0.5），当日涨幅 >= 该值（百分比）
     */
    @GetMapping("/analysis/volume-up/details")
    public List<VolumeUpResult> listVolumeUpDetails(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "20") int lookbackDays,
            @RequestParam(value = "volumePeriod", required = false, defaultValue = "20") int volumePeriod,
            @RequestParam(value = "volumeRatioThreshold", required = false, defaultValue = "1.5") double volumeRatioThreshold,
            @RequestParam(value = "minPriceChangePercent", required = false, defaultValue = "0.5") double minPriceChangePercent
    ) {
        return volumeUpAnalysisService.findVolumeUpDetails(lookbackDays, volumePeriod, volumeRatioThreshold, minPriceChangePercent);
    }

    /**
     * 分析：最近 N 天内收盘价位于中轨与上轨之间（允许不超过 allowedExceedDays 天不满足）——返回股票代码
     * @param lookbackDays 回看天数（默认50）
     * @param period 布林线均线周期（默认20）
     * @param k 标准差倍数（默认2.0）
     * @param allowedExceedDays 允许的违规天数（默认0）
     */
    @GetMapping("/analysis/bollinger/upper-channel")
    public List<String> listBollingerUpperChannelCodes(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "50") int lookbackDays,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k,
            @RequestParam(value = "allowedExceedDays", required = false, defaultValue = "0") int allowedExceedDays
    ) {
        return bollingerAnalysisService.findUpperChannelStockCodes(lookbackDays, period, k, allowedExceedDays);
    }

    /**
     * 分析：最近 N 天内收盘价位于中轨与上轨之间（允许不超过 allowedExceedDays 天不满足）——返回详细信息
     * @param lookbackDays 回看天数（默认50）
     * @param period 布林线均线周期（默认20）
     * @param k 标准差倍数（默认2.0）
     * @param allowedExceedDays 允许的违规天数（默认0）
     */
    @GetMapping("/analysis/bollinger/upper-channel/details")
    public List<BollingerUpperChannelResult> listBollingerUpperChannelDetails(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "50") int lookbackDays,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k,
            @RequestParam(value = "allowedExceedDays", required = false, defaultValue = "0") int allowedExceedDays
    ) {
        return bollingerAnalysisService.findUpperChannelDetails(lookbackDays, period, k, allowedExceedDays);
    }

    @GetMapping("/analysis/indicators/augment-dto")
    public List<DailyLineDTO> augmentIndicatorsDto(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return stockDtoAnalysisService.augmentByCodeBetween(stockCode, startDate, endDate, period, k);
    }

    @GetMapping("/analysis/indicators/augment-dto/latest")
    public List<DailyLineDTO> augmentIndicatorsDtoLatest(
            @RequestParam("stockCode") String stockCode,
            @RequestParam(value = "limit", required = false, defaultValue = "60") int limit,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k
    ) {
        return stockDtoAnalysisService.augmentLatest(stockCode, limit, period, k);
    }

    @GetMapping("/analysis/indicators/augment")
    public List<StocksDailyData> augmentIndicators(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return indicatorAugmentationService.augment(stockCode, startDate, endDate, period, k);
    }

    @GetMapping("/analysis/deepseek/advice")
    public String deepSeekAdvice(
            @RequestParam("stockCode") String stockCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k,
            // 新增：MACD 指标参数（可选覆盖默认配置）
            @RequestParam(value = "macdShort", required = false) Integer macdShort,
            @RequestParam(value = "macdLong", required = false) Integer macdLong,
            @RequestParam(value = "macdSignal", required = false) Integer macdSignal,
            @RequestParam(value = "macdHistMultiplier", required = false) Double macdHistMultiplier,
            @RequestParam(value = "recentDays", required = false, defaultValue = "30") int recentDays,
            @RequestParam(value = "format", required = false, defaultValue = "text") String format,
            @RequestParam(value = "buyPrice", required = false) Double buyPrice,
            @RequestParam(value = "pnl", required = false) Double floatingPnLAmount,
            @RequestParam(value = "positionQty", required = false) Double positionQty,
            @RequestParam(value = "positionRatio", required = false) Double positionRatio,
            @RequestParam(value = "totalPositionAmount", required = false) Double totalPositionAmount
    ) throws Exception {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<StocksDailyData> augmented = indicatorAugmentationService.augment(stockCode, startDate, endDate, period, k, macdShort, macdLong, macdSignal, macdHistMultiplier);
        return deepSeekAnalysisService.analyze(stockCode, augmented, period, k, recentDays, format, buyPrice, floatingPnLAmount, positionQty, positionRatio, totalPositionAmount);
    }

    @GetMapping("/analysis/macd/cross")
    public List<String> listMacdCrossCodes(
            @RequestParam(value = "type", required = false, defaultValue = "golden") String type,
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "50") int lookbackDays,
            @RequestParam(value = "withinDays", required = false, defaultValue = "0") int withinDays,
            @RequestParam(value = "shortPeriod", required = false, defaultValue = "12") int shortPeriod,
            @RequestParam(value = "longPeriod", required = false, defaultValue = "26") int longPeriod,
            @RequestParam(value = "signalPeriod", required = false, defaultValue = "9") int signalPeriod
    ) {
        return macdAnalysisService.findMacdCrossStockCodes(type, lookbackDays, withinDays, shortPeriod, longPeriod, signalPeriod);
    }

    @GetMapping("/analysis/macd/cross/details")
    public List<MacdCrossResult> listMacdCrossDetails(
            @RequestParam(value = "type", required = false, defaultValue = "both") String type,
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "50") int lookbackDays,
            @RequestParam(value = "shortPeriod", required = false, defaultValue = "12") int shortPeriod,
            @RequestParam(value = "longPeriod", required = false, defaultValue = "26") int longPeriod,
            @RequestParam(value = "signalPeriod", required = false, defaultValue = "9") int signalPeriod
    ) {
        return macdAnalysisService.findMacdCrossDetails(lookbackDays, shortPeriod, longPeriod, signalPeriod, type);
    }

    @GetMapping("/analysis/strategy/expma-macd-boll/buy")
    public List<String> listExpmaMacdBollBuyCodes(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "120") int lookbackDays,
            @RequestParam(value = "withinDays", required = false, defaultValue = "7") int withinDays,
            @RequestParam(value = "expmaShort", required = false, defaultValue = "12") int expmaShort,
            @RequestParam(value = "expmaLong", required = false, defaultValue = "50") int expmaLong,
            @RequestParam(value = "sustainDays", required = false, defaultValue = "5") int sustainDays,
            @RequestParam(value = "allowSideway", required = false, defaultValue = "true") boolean allowSideway,
            @RequestParam(value = "sidewayEpsilonRatio", required = false, defaultValue = "0.005") double sidewayEpsilonRatio,
            @RequestParam(value = "macdShort", required = false, defaultValue = "12") int macdShort,
            @RequestParam(value = "macdLong", required = false, defaultValue = "26") int macdLong,
            @RequestParam(value = "macdSignal", required = false, defaultValue = "9") int macdSignal,
            @RequestParam(value = "bollPeriod", required = false, defaultValue = "20") int bollPeriod,
            @RequestParam(value = "bollStdDev", required = false, defaultValue = "2.0") double bollStdDev,
            @RequestParam(value = "volumeWindow", required = false, defaultValue = "10") int volumeWindow,
            @RequestParam(value = "volumeFactor", required = false, defaultValue = "1.2") double volumeFactor
    ) {
        return combinedStrategyAnalysisService.findBuyCandidates(
                lookbackDays,
                withinDays,
                expmaShort,
                expmaLong,
                sustainDays,
                allowSideway,
                sidewayEpsilonRatio,
                macdShort,
                macdLong,
                macdSignal,
                bollPeriod,
                bollStdDev,
                volumeWindow,
                volumeFactor
        );
    }

    @GetMapping("/analysis/strategy/expma-macd-boll/sell")
    public List<String> listExpmaMacdBollSellCodes(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "120") int lookbackDays,
            @RequestParam(value = "withinDays", required = false, defaultValue = "7") int withinDays,
            @RequestParam(value = "expmaShort", required = false, defaultValue = "12") int expmaShort,
            @RequestParam(value = "expmaLong", required = false, defaultValue = "50") int expmaLong,
            @RequestParam(value = "sustainDays", required = false, defaultValue = "5") int sustainDays,
            @RequestParam(value = "macdShort", required = false, defaultValue = "12") int macdShort,
            @RequestParam(value = "macdLong", required = false, defaultValue = "26") int macdLong,
            @RequestParam(value = "macdSignal", required = false, defaultValue = "9") int macdSignal,
            @RequestParam(value = "bollPeriod", required = false, defaultValue = "20") int bollPeriod,
            @RequestParam(value = "bollStdDev", required = false, defaultValue = "2.0") double bollStdDev,
            @RequestParam(value = "volumeWindow", required = false, defaultValue = "10") int volumeWindow,
            @RequestParam(value = "volumeFactor", required = false, defaultValue = "1.2") double volumeFactor
    ) {
        return combinedStrategyAnalysisService.findSellCandidates(
                lookbackDays, withinDays,
                expmaShort, expmaLong, sustainDays,
                macdShort, macdLong, macdSignal,
                bollPeriod, bollStdDev,
                volumeWindow, volumeFactor
        );
    }

    @GetMapping("/analysis/strategy/expma-macd-boll/watch")
    public List<String> listExpmaMacdBollWatchCodes(
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "120") int lookbackDays,
            @RequestParam(value = "withinDays", required = false, defaultValue = "7") int withinDays,
            @RequestParam(value = "expmaShort", required = false, defaultValue = "12") int expmaShort,
            @RequestParam(value = "expmaLong", required = false, defaultValue = "50") int expmaLong,
            @RequestParam(value = "sustainDays", required = false, defaultValue = "5") int sustainDays,
            @RequestParam(value = "macdShort", required = false, defaultValue = "12") int macdShort,
            @RequestParam(value = "macdLong", required = false, defaultValue = "26") int macdLong,
            @RequestParam(value = "macdSignal", required = false, defaultValue = "9") int macdSignal,
            @RequestParam(value = "bollPeriod", required = false, defaultValue = "20") int bollPeriod,
            @RequestParam(value = "bollStdDev", required = false, defaultValue = "2.0") double bollStdDev
    ) {
        return combinedStrategyAnalysisService.findWatchCandidates(
                lookbackDays, withinDays,
                expmaShort, expmaLong, sustainDays,
                macdShort, macdLong, macdSignal,
                bollPeriod, bollStdDev
        );
    }
    @GetMapping("/analysis/screen/industry-growth-and-indicators")
    public List<String> listIndustryGrowthAndFavorableStocks(
            @RequestParam(value = "exchange", required = false) String exchange,
            @RequestParam(value = "assetType", required = false) String assetType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "limit", required = false, defaultValue = "50") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "lookbackDays", required = false, defaultValue = "60") int lookbackDays,
            @RequestParam(value = "industryLookbackDays", required = false, defaultValue = "60") int industryLookbackDays,
            @RequestParam(value = "withinDays", required = false, defaultValue = "7") int withinDays,
            @RequestParam(value = "bollPeriod", required = false, defaultValue = "20") int bollPeriod,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k,
            @RequestParam(value = "emaSlopeDays", required = false, defaultValue = "3") int emaSlopeDays
    ) {
        return stockScreeningService.screenGrowthAndIndicatorsFiltered(exchange, assetType, status, limit, offset, lookbackDays, industryLookbackDays, withinDays, bollPeriod, k, emaSlopeDays);
    }
}