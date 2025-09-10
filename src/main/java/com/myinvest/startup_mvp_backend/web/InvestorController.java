package com.myinvest.startup_mvp_backend.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InvestorController {
    // GET /investor/pack/detail?tenantId=...&packId=...
    @GetMapping("/investor/pack/detail")
    public Map<String, Object> detail(@RequestParam String tenantId, @RequestParam String packId) {
        return Map.of(
            "packId", packId,
            "project", Map.of("name","ОАО «Предприятие»","stage","Seed"),
            "summary", Map.of(
                "valuation", "$2M",
                "recommended_investment", "$400K",
                "expected_roi", "20x",
                "round", "Seed",
                "confidence_score", 0.90,
                "risk", Map.of("technology","Medium","market","Low","team","Low")
            ),
            "kpis", Map.of("mrr", 12000, "cac", 300, "ltv", 5400, "churnRate", 0.03),
            "charts", Map.of(
                "cashflowForecast", Map.of(
                    "currency", "USD",
                    "months", List.of(
                        Map.of("month","2025-10","revenue",12000,"costs",18000,"net",-6000),
                        Map.of("month","2025-11","revenue",15000,"costs",18500,"net",-3500),
                        Map.of("month","2026-04","revenue",40000,"costs",32000,"net",8000)
                    ),
                    "breakevenMonth","2026-03"
                ),
                "roiScenarios", Map.of(
                    "optimistic", Map.of("roi","20x","prob",0.25),
                    "base", Map.of("roi","8x","prob",0.55),
                    "pessimistic", Map.of("roi","2x","prob",0.20)
                ),
                "riskRadar", List.of(
                    Map.of("axis","Технологический","value",0.6),
                    Map.of("axis","Рыночный","value",0.3),
                    Map.of("axis","Команда","value",0.3),
                    Map.of("axis","Регуляторика","value",0.4),
                    Map.of("axis","Операционный","value",0.5)
                ),
                "competitionScatter", List.of(
                    Map.of("name","Проект","x","функциональность","y","стоимость","value",0.8),
                    Map.of("name","Конкурент A","x","функциональность","y","стоимость","value",0.6)
                )
            ),
            "aiVerification", Map.of(
                "originality_score",0.88,
                "confidence_score",0.90,
                "red_flags", List.of()
            ),
            "recommendation", Map.of(
                "text","Рассмотреть участие в раунде Seed. Пилоты в 2-3 средних банках.",
                "nextSteps", List.of(
                    "Техдью-дилидженс на масштабирование",
                    "Пилот 3–6 месяцев",
                    "Условие транша: MRR ≥ $25k и churn ≤ 3%"
                )
            ),
            "actions", Map.of("follow", true, "pass", true, "exportPdf", true)
        );
    }
}
