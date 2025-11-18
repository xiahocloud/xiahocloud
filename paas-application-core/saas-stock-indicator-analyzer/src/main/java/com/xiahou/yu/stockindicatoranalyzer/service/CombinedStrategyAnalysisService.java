package com.xiahou.yu.stockindicatoranalyzer.service;

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
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CombinedStrategyAnalysisService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;

    @Value("${indicator.macd.cross.epsilon:0.0001}")
    private double macdCrossEpsilon;

    /**
     * 查找满足“买入条件”的股票代码列表。
     */
    public List<String> findBuyCandidates(
            int lookbackDays,
            int withinDays,
            int expmaShort,
            int expmaLong,
            int sustainDays,
            boolean allowSideway,
            double sidewayEpsilonRatio,
            int macdShort,
            int macdLong,
            int macdSignal,
            int bollPeriod,
            double bollStdDev,
            int volumeWindow,
            double volumeFactor
    ) {
        return scan(lookbackDays, withinDays, expmaShort, expmaLong, sustainDays, allowSideway, sidewayEpsilonRatio, macdShort, macdLong, macdSignal, bollPeriod, bollStdDev, volumeWindow, volumeFactor, ScanMode.BUY);
    }

    /**
     * 查找满足“卖出条件”的股票代码列表。
     */
    public List<String> findSellCandidates(
            int lookbackDays,
            int withinDays,
            int expmaShort,
            int expmaLong,
            int sustainDays,
            int macdShort,
            int macdLong,
            int macdSignal,
            int bollPeriod,
            double bollStdDev,
            int volumeWindow,
            double volumeFactor
    ) {
        // 卖出条件简化：
        // - 趋势：EXPMA(12) 持续低于 EXPMA(50) 至少 sustainDays（空头或转弱）
        // - MACD：最新交叉为死叉，且 withinDays 限制（可选）
        // - Bollinger：当前收盘低于中轨，或最近 withinDays 触及上轨且当前跌破中轨
        // - Volume（可选）：当前量能 >= 均量 × factor（放量下跌）
        return scan(lookbackDays, withinDays, expmaShort, expmaLong, sustainDays, false, 0.0, macdShort, macdLong, macdSignal, bollPeriod, bollStdDev, volumeWindow, volumeFactor, ScanMode.SELL);
    }

    /**
     * 查找满足“观望条件（冲突）”的股票代码列表。
     */
    public List<String> findWatchCandidates(
            int lookbackDays,
            int withinDays,
            int expmaShort,
            int expmaLong,
            int sustainDays,
            int macdShort,
            int macdLong,
            int macdSignal,
            int bollPeriod,
            double bollStdDev
    ) {
        // 观望条件简化：
        // - 趋势与 MACD 信号冲突（如多头趋势但最新交叉为死叉；或空头趋势但最新交叉为金叉）
        // - Bollinger 不强（当前位于中轨附近），仅用于提示冲突关注
        return scan(lookbackDays, withinDays, expmaShort, expmaLong, sustainDays, true, 0.005, macdShort, macdLong, macdSignal, bollPeriod, bollStdDev, 0, 0.0, ScanMode.WATCH);
    }

    private enum ScanMode { BUY, SELL, WATCH }

    private List<String> scan(
            int lookbackDays,
            int withinDays,
            int expmaShort,
            int expmaLong,
            int sustainDays,
            boolean allowSideway,
            double sidewayEpsilonRatio,
            int macdShort,
            int macdLong,
            int macdSignal,
            int bollPeriod,
            double bollStdDev,
            int volumeWindow,
            double volumeFactor,
            ScanMode mode
    ) {
        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        List<Candidate> candidates = new ArrayList<>();

        int need = lookbackDays + Math.max(Math.max(expmaLong, macdLong), bollPeriod);
        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            if (stockCode == null || stockCode.isEmpty()) continue;

            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(stockCode, need, 0);
            if (latestDesc == null || latestDesc.size() < Math.max(Math.max(expmaLong, macdLong), bollPeriod)) continue;

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
            if (series.getBarCount() < Math.max(Math.max(expmaLong, macdLong), bollPeriod)) continue;

            ClosePriceIndicator close = new ClosePriceIndicator(series);
            EMAIndicator expShort = new EMAIndicator(close, expmaShort);
            EMAIndicator expLong = new EMAIndicator(close, expmaLong);
            int lastIndex = series.getBarCount() - 1;

            boolean sustainAbove = true;
            for (int i = Math.max(0, lastIndex - sustainDays + 1); i <= lastIndex; i++) {
                if (expShort.getValue(i).doubleValue() <= expLong.getValue(i).doubleValue()) { sustainAbove = false; break; }
            }
            boolean sustainBelow = true;
            for (int i = Math.max(0, lastIndex - sustainDays + 1); i <= lastIndex; i++) {
                if (expShort.getValue(i).doubleValue() >= expLong.getValue(i).doubleValue()) { sustainBelow = false; break; }
            }
            boolean sideway = false;
            if (allowSideway) {
                sideway = true;
                for (int i = Math.max(0, lastIndex - sustainDays + 1); i <= lastIndex; i++) {
                    double diff = Math.abs(expShort.getValue(i).doubleValue() - expLong.getValue(i).doubleValue());
                    double px = close.getValue(i).doubleValue();
                    double ratio = (px == 0.0) ? diff : diff / px;
                    if (ratio > sidewayEpsilonRatio) { sideway = false; break; }
                }
            }
            boolean bullTrend = sustainAbove || sideway;
            boolean bearTrend = sustainBelow;

            MACDIndicator macd = new MACDIndicator(close, macdShort, macdLong);
            EMAIndicator dea = new EMAIndicator(macd, macdSignal);
            LocalDate now = LocalDate.now();
            LocalDate lastGoldenDate = null;
            LocalDate lastDeathDate = null;
            for (int i = Math.max(1, series.getBarCount() - lookbackDays); i < series.getBarCount(); i++) {
                double sPrev = macd.getValue(i - 1).doubleValue() - dea.getValue(i - 1).doubleValue();
                double sNow  = macd.getValue(i).doubleValue()     - dea.getValue(i).doubleValue();
                boolean golden = (sPrev <= 0.0) && (sNow > macdCrossEpsilon);
                boolean death  = (sPrev >= 0.0) && (sNow < -macdCrossEpsilon);
                if (golden) {
                    lastGoldenDate = series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate();
                } else if (death) {
                    lastDeathDate = series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate();
                }
            }

            SMAIndicator middle = new SMAIndicator(close, bollPeriod);
            StandardDeviationIndicator sd = new StandardDeviationIndicator(close, bollPeriod);
            double midNow = middle.getValue(lastIndex).doubleValue();
            double sdNow = sd.getValue(lastIndex).doubleValue();
            double upperNow = midNow + sdNow * bollStdDev;
            double lowerNow = midNow - sdNow * bollStdDev;
            double closeNow = close.getValue(lastIndex).doubleValue();
            boolean aboveMiddleNow = closeNow >= midNow;
            boolean belowMiddleNow = closeNow < midNow;
            boolean touchedLowerRecently = false;
            boolean touchedUpperRecently = false;
            int lookStart = Math.max(0, lastIndex - (withinDays > 0 ? withinDays : lookbackDays));
            for (int i = lookStart; i <= lastIndex; i++) {
                double mid = middle.getValue(i).doubleValue();
                double sdv = sd.getValue(i).doubleValue();
                double lower = mid - sdv * bollStdDev;
                double upper = mid + sdv * bollStdDev;
                double c = close.getValue(i).doubleValue();
                if (c <= lower) touchedLowerRecently = true;
                if (c >= upper) touchedUpperRecently = true;
            }

            boolean volumeOk = true;
            if (mode != ScanMode.WATCH && volumeWindow > 0 && volumeFactor > 0) {
                int startVol = Math.max(0, lastIndex - volumeWindow);
                double sum = 0.0; int cnt = 0;
                for (int i = startVol; i < lastIndex; i++) { sum += series.getBar(i).getVolume().doubleValue(); cnt++; }
                double avg = cnt == 0 ? 0.0 : sum / cnt;
                double volNow = series.getBar(lastIndex).getVolume().doubleValue();
                volumeOk = avg == 0.0 ? true : volNow >= avg * volumeFactor;
            }
            if (!volumeOk) continue;

            boolean pass = false;
            LocalDate sortDate = null;
            if (mode == ScanMode.BUY) {
                // 买入：多头或震荡 + 最新金叉（且 withinDays 限制） + 中轨之上或下轨反弹站上中轨
                if (lastGoldenDate == null) { pass = false; }
                else {
                    boolean withinOk = withinDays <= 0 || (ChronoUnit.DAYS.between(lastGoldenDate, now) >= 0 && ChronoUnit.DAYS.between(lastGoldenDate, now) <= withinDays);
                    boolean bollOk = aboveMiddleNow || (touchedLowerRecently && aboveMiddleNow);
                    pass = bullTrend && withinOk && bollOk;
                    sortDate = lastGoldenDate;
                }
            } else if (mode == ScanMode.SELL) {
                // 卖出：空头或转弱 + 最新死叉（且 withinDays 限制） + 中轨之下或上轨触及后跌破中轨
                if (lastDeathDate == null) { pass = false; }
                else {
                    boolean withinOk = withinDays <= 0 || (ChronoUnit.DAYS.between(lastDeathDate, now) >= 0 && ChronoUnit.DAYS.between(lastDeathDate, now) <= withinDays);
                    boolean bollOk = belowMiddleNow || (touchedUpperRecently && belowMiddleNow);
                    pass = bearTrend && withinOk && bollOk;
                    sortDate = lastDeathDate;
                }
            } else { // WATCH
                // 观望：趋势与 MACD 冲突
                boolean conflict1 = bullTrend && lastDeathDate != null && (withinDays <= 0 || ChronoUnit.DAYS.between(lastDeathDate, now) <= withinDays);
                boolean conflict2 = bearTrend && lastGoldenDate != null && (withinDays <= 0 || ChronoUnit.DAYS.between(lastGoldenDate, now) <= withinDays);
                pass = (conflict1 || conflict2);
                sortDate = conflict1 ? lastDeathDate : (conflict2 ? lastGoldenDate : null);
            }

            if (pass) {
                candidates.add(new Candidate(stockCode, sortDate));
            }
        }

        candidates.sort(Comparator.comparing((Candidate c) -> c.lastCrossDate).reversed());
        return candidates.stream().map(c -> c.stockCode).collect(Collectors.toList());
    }

    private static class Candidate {
        String stockCode;
        LocalDate lastCrossDate;
        Candidate(String code, LocalDate d) { this.stockCode = code; this.lastCrossDate = d; }
    }
}