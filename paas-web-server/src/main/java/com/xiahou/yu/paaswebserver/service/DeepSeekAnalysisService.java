package com.xiahou.yu.paaswebserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeepSeekAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekAnalysisService.class);

    private final ObjectMapper objectMapper;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.model}")
    private String model;

    @Value("${deepseek.api.temperature:0.7}")
    private double temperature;

    public String analyze(String stockCode, List<StocksDailyData> data, int bollingerPeriod, double bollingerK, int recentDays, String outputFormat,
                          Double buyPrice, Double floatingPnLAmount, Double positionQty, Double positionRatio, Double totalPositionAmount) throws IOException, InterruptedException {
        if (data == null || data.isEmpty()) {
            return "无有效数据，无法分析";
        }
        // 仅传输最近 N 个交易日，避免数据量过大
        List<StocksDailyData> trimmed = data;
        if (recentDays > 0 && data.size() > recentDays) {
            trimmed = data.subList(data.size() - recentDays, data.size());
        }
        String summary = buildPromptSummary(stockCode, trimmed, bollingerPeriod, bollingerK, buyPrice, floatingPnLAmount, positionQty, positionRatio, totalPositionAmount);

        // 构造提示消息
        String systemPrompt = "角色（Persona）：你是一名资深量化交易分析师与研究员，擅长技术分析、量价关系解析、风险控制与交易策略制定。任务/目标：基于提供的逐日数据与技术指标，结合用户的买入价、浮动盈亏金额、持仓数量、当前仓位比例与总仓位金额，完成短期（5-10日）与中期（1-2月）走势研判，评估是否适合买入或加/补仓，并给出明确买/卖点位与风险控制建议。分析维度：1）技术面（趋势、支撑阻力、布林带、SMA/EMA、形态与动量）；2）量价关系（成交量、换手率、价量背离、缩量/放量特征、量价配合）；3）基本面（如未提供则说明限制并仅基于技术/量价判断）；4）情绪面（波动率、长上下影线、跳空缺口等情绪信号）；5）风险评估（关键风险、止盈/止损、仓位与资金管理，考虑当前仓位较高可能限制补仓的约束）。输出格式与语气：使用中文，专业、简洁、可执行。";
        boolean wantJson = outputFormat != null && outputFormat.equalsIgnoreCase("json");
        if (wantJson) {
            systemPrompt += " 必须返回严格合法的 JSON（UTF-8，无 Markdown、无多余文本），包含如下字符串字段：shortTerm、midTerm、recommendation、entryExit、risk、notes。请在 entryExit 中给出加仓/补仓/卖出的详细建议（价格区间、触发条件、分批策略、仓位比例），在 risk 中明确止盈与止损点位及仓位建议，并说明在当前仓位与总仓位金额约束下的可执行性。";
        } else {
            systemPrompt += " 严格按照以下结构化格式输出：\\n一、短期走势（5-10日）及关键依据\\n二、中期走势（1-2月）及关键依据\\n三、买入/持有/观望建议（给出明确结论与简短理由）\\n四、建议的买入点位与卖出点位（价格或区间，说明触发条件或判断依据）\\n五、风险评估与风控建议（止盈/止损位、关键风险、仓位与资金管理）\\n六、加仓/补仓/卖出建议与细节（价格区间、触发条件、分批策略、仓位比例；若当前仓位较高或总仓位金额已接近上限，请给出可执行替代方案，如分批减仓或观望）\\n七、补充说明（若基本面信息缺失，请说明限制并基于技术/量价给出判断）。";
        }

        String tail;
        if (wantJson) {
            tail = "请将结论以 JSON 输出，字段为：shortTerm、midTerm、recommendation、entryExit、risk、notes。entryExit 字段中需包含加仓/补仓/卖出建议及具体价位或区间、触发条件、分批策略与仓位比例；risk 字段中需包含止盈与止损点位及仓位管理建议，并说明在当前仓位与总仓位金额约束下的可执行性。示例：\\n{" +
                    "\"shortTerm\": \"...\", \"midTerm\": \"...\", \"recommendation\": \"...\", \"entryExit\": \"(加仓/补仓/卖出细节与触发条件; 分批策略; 仓位比例; 约束说明)\", \"risk\": \"(止盈/止损点位与仓位管理; 约束下的风险提示)\", \"notes\": \"...\"}";
        } else {
            tail = "请严格按以下结构输出：\\n一、短期走势（5-10日）及关键依据（重点结合量价关系与技术指标）\\n二、中期走势（1-2月）及关键依据（重点结合量价关系与技术指标）\\n三、买入/持有/观望建议（明确结论与简短理由）\\n四、建议的买入点位与卖出点位（给出价格或区间，说明触发条件或判断依据）\\n五、风险评估与风控建议（止盈/止损点位、关键风险、仓位与资金管理）\\n六、加仓/补仓/卖出建议与细节（分批策略、价格区间、触发条件、仓位比例；若当前仓位较高或总仓位金额已接近上限，请给出可执行替代方案，如分批减仓或观望）\\n七、补充说明（若基本面信息缺失，请说明限制并基于技术/量价给出判断）";
        }
        String userContent = summary + "\n" + tail;

        String requestBody = objectMapper.createObjectNode()
                .put("model", model)
                .put("temperature", temperature)
                .set("messages", objectMapper.valueToTree(java.util.List.of(
                        java.util.Map.of("role", "system", "content", systemPrompt),
                        java.util.Map.of("role", "user", "content", userContent)
                )))
                .toString();

        // 记录日志：调用 DeepSeek 将发送的数据与提示词
        log.info("[DeepSeek] request meta: model={}, url={}, stockCode={}, period={}, k={}, recentDays={}, format={}, buyPrice={}, pnlAmount={}, positionQty={}, positionRatio={}, totalPositionAmount={}, rows={}",
                model, apiUrl, stockCode, bollingerPeriod, bollingerK, recentDays, outputFormat, buyPrice, floatingPnLAmount, positionQty, positionRatio, totalPositionAmount, trimmed.size());
        log.info("[DeepSeek] system prompt:\n{}", systemPrompt);
        log.info("[DeepSeek] user content (summary + tail):\n{}", userContent);
        log.info("[DeepSeek] payload body:\n{}", requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("[DeepSeek] response status={}, body length={}", response.statusCode(), response.body() == null ? 0 : response.body().length());
        log.info("[DeepSeek] response body:\n{}", response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode content = choices.get(0).path("message").path("content");
                if (!content.isMissingNode()) {
                    return content.asText();
                }
            }
            return response.body();
        } else {
            return "DeepSeek调用失败: status=" + response.statusCode() + ", body=" + response.body();
        }
    }

    private String buildPromptSummary(String stockCode, List<StocksDailyData> data, int bollingerPeriod, double bollingerK,
                                      Double buyPrice, Double floatingPnLAmount, Double positionQty, Double positionRatio, Double totalPositionAmount) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String header = String.format(
                "股票代码: %s\n数据包含字段: 日期、开盘、最高、最低、收盘、成交量、成交额、涨跌额、涨跌幅、换手率、总市值、流通市值，以及技术指标(布林上/中/下、SMA5/10/20/60、EMA5/10/20/60、MACD DIF/DEA/Hist)。\n日期范围: %s 至 %s（最新收盘价为结束日期的收盘价）\n布林参数: period=%d, k=%.2f",
                stockCode,
                data.get(0).getTradeDate().format(fmt),
                data.get(data.size()-1).getTradeDate().format(fmt),
                bollingerPeriod, bollingerK);
        if (buyPrice != null) {
            header += String.format("\n用户买入价: %.4f", buyPrice);
        }
        if (floatingPnLAmount != null) {
            header += String.format("\n当前浮动盈亏(按金额): %.2f", floatingPnLAmount);
        }
        if (positionQty != null) {
            header += String.format("\n当前持仓数量: %.4f", positionQty);
        }
        if (positionRatio != null) {
            header += String.format("\n当前仓位比例(百分比): %.2f%%", positionRatio);
        }
        if (totalPositionAmount != null) {
            header += String.format("\n总仓位金额: %.2f", totalPositionAmount);
        }
        String rows = data.stream().map(d -> String.format(
                "%s 开:%s 高:%s 低:%s 收:%s 量:%s 额:%s 涨跌额:%s 涨跌幅:%s%% 换手率:%s 市值:%s 流通市值:%s | BB(上/中/下): %s/%s/%s | SMA(5/10/20/60): %s/%s/%s/%s | EMA(5/10/20/60): %s/%s/%s/%s | MACD(DIF/DEA/Hist): %s/%s/%s",
                d.getTradeDate().format(fmt),
                d.getOpenPrice(), d.getHighPrice(), d.getLowPrice(), d.getClosePrice(),
                d.getVolume(), d.getTurnover(), d.getChangeAmount(), d.getChangePercentage(), d.getTurnoverRate(),
                d.getTotalMarketValue(), d.getCirculatingMarketValue(),
                d.getBbUpper(), d.getBbMiddle(), d.getBbLower(),
                d.getSma5(), d.getSma10(), d.getSma20(), d.getSma60(),
                d.getEma5(), d.getEma10(), d.getEma20(), d.getEma60(),
                d.getMacdDif(), d.getMacdDea(), d.getMacdHist()
        )).collect(Collectors.joining("\n"));
        String tail = "请在结论中明确：\n- 短期走势判断与关键依据（重点结合量价关系）\n- 中期走势判断与关键依据（重点结合量价关系）\n- 是否适合买入/持有/观望，简短理由\n- 建议的买入点位与卖出点位（价格或区间）\n- 在给定买入价、浮动盈亏金额、持仓数量、当前仓位比例与总仓位金额的约束下，提供加仓/补仓/卖出更合适且细节化的结论（含分批策略、触发条件、价格区间与仓位比例；若仓位较高限制补仓，请给出可执行替代方案）\n- 给出合理的止盈与止损点位（结合支撑/阻力、波动、布林带与均线）";
        return header + "\n" + rows + "\n" + tail;
    }
}