package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.dto.MacdCrossEvent;
import com.xiahou.yu.stockindicatoranalyzer.dto.MacdCrossResult;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MacdAnalysisService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;

    @Value("${indicator.macd.shortPeriod}")
    private int defaultShortPeriod;

    @Value("${indicator.macd.longPeriod}")
    private int defaultLongPeriod;

    @Value("${indicator.macd.signalPeriod}")
    private int defaultSignalPeriod;

    @Value("${indicator.macd.hist.multiplier}")
    private double histMultiplier;

    @Value("${indicator.macd.cross.epsilon}")
    private double crossEpsilon;

    /**
     * 返回在最近 lookbackDays 窗口内，"最新一次" MACD 交叉事件类型匹配的股票代码列表，并可限制最新交叉发生在 withinDays 天内。
     * 仅当最近窗口内最后一条交叉事件为所请求类型（golden/death）时才返回该股票。
     */
    public List<String> findMacdCrossStockCodes(String type, int lookbackDays, int withinDays, int shortPeriod, int longPeriod, int signalPeriod) {
        String t = (type == null || type.isEmpty()) ? "golden" : type.toLowerCase();
        List<MacdCrossResult> details = findMacdCrossDetails(lookbackDays, shortPeriod, longPeriod, signalPeriod, "both");
        LocalDate now = LocalDate.now();
        return details.stream()
                .filter(r -> r.getEvents() != null && !r.getEvents().isEmpty())
                .filter(r -> {
                    MacdCrossEvent last = r.getEvents().get(r.getEvents().size() - 1);
                    if ("golden".equals(t) && !"golden".equals(last.getType())) return false;
                    if ("death".equals(t) && !"death".equals(last.getType())) return false;
                    if (withinDays > 0) {
                        long days = ChronoUnit.DAYS.between(last.getDate(), now);
                        if (days < 0 || days > withinDays) return false; // 仅保留最近 withinDays 天内发生的最新交叉
                    }
                    return true;
                })
                .sorted(Comparator.comparing((MacdCrossResult r) -> r.getEvents().get(r.getEvents().size() - 1).getDate()).reversed())
                .map(MacdCrossResult::getStockCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回在最近 lookbackDays 窗口内发生 MACD 交叉事件的详细信息。
     * @param type 交叉类型："golden"、"death" 或 "both"（默认）
     */
    public List<MacdCrossResult> findMacdCrossDetails(int lookbackDays, int shortPeriod, int longPeriod, int signalPeriod, String type) {
        String t = (type == null || type.isEmpty()) ? "both" : type.toLowerCase();
        int sp = shortPeriod > 0 ? shortPeriod : defaultShortPeriod;
        int lp = longPeriod > 0 ? longPeriod : defaultLongPeriod;
        int sgp = signalPeriod > 0 ? signalPeriod : defaultSignalPeriod;
        List<MacdCrossResult> results = new ArrayList<>();

        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            if (stockCode == null || stockCode.isEmpty()) continue;

            int need = lookbackDays + Math.max(lp, sgp);
            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(stockCode, need, 0);
            if (latestDesc == null || latestDesc.size() < Math.max(lp, sgp)) continue;

            List<StocksDailyData> latestAsc = latestDesc.stream()
                    .sorted(Comparator.comparing(StocksDailyData::getTradeDate))
                    .collect(Collectors.toList());

            BarSeries series = new BaseBarSeriesBuilder().withName(stockCode).build();
            for (StocksDailyData d : latestAsc) {
                Instant endTime = d.getTradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
                series.addBar(series.barBuilder()
                        .timePeriod(Duration.ofDays(1))
                        .endTime(endTime)
                        .openPrice(d.getOpenPrice() == null ? 0.0 : d.getOpenPrice().doubleValue())
                        .highPrice(d.getHighPrice() == null ? 0.0 : d.getHighPrice().doubleValue())
                        .lowPrice(d.getLowPrice() == null ? 0.0 : d.getLowPrice().doubleValue())
                        .closePrice(d.getClosePrice() == null ? 0.0 : d.getClosePrice().doubleValue())
                        .volume(d.getVolume() == null ? 0.0 : d.getVolume().doubleValue())
                        .build());
            }

            if (series.getBarCount() < Math.max(lp, sgp)) continue;

            ClosePriceIndicator close = new ClosePriceIndicator(series);
            MACDIndicator macd = new MACDIndicator(close, sp, lp); // DIF
            EMAIndicator dea = new EMAIndicator(macd, sgp); // DEA

            List<MacdCrossEvent> events = new ArrayList<>();
            int startIndex = Math.max(1, series.getBarCount() - lookbackDays);
            for (int i = startIndex; i < series.getBarCount(); i++) {
                double difPrev = macd.getValue(i - 1).doubleValue();
                double difNow = macd.getValue(i).doubleValue();
                double deaPrev = dea.getValue(i - 1).doubleValue();
                double deaNow = dea.getValue(i).doubleValue();
                double sPrev = difPrev - deaPrev;
                double sNow = difNow - deaNow;
                boolean golden = (sPrev <= 0.0) && (sNow > crossEpsilon);
                boolean death = (sPrev >= 0.0) && (sNow < -crossEpsilon);
                String eventType = golden ? "golden" : (death ? "death" : null);
                if (eventType == null) continue;
                if ("golden".equals(t) && !golden) continue;
                if ("death".equals(t) && !death) continue;

                double hist = (difNow - deaNow) * histMultiplier; // 直方图乘配置倍数
                MacdCrossEvent evt = new MacdCrossEvent(
                        series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate(),
                        eventType,
                        difNow,
                        deaNow,
                        hist
                );
                events.add(evt);
            }

            if (!events.isEmpty()) {
                results.add(new MacdCrossResult(stockCode, events));
            }
        }

        results.sort(Comparator.comparing((MacdCrossResult r) -> r.getEvents().size()).reversed());
        return results;
    }
}