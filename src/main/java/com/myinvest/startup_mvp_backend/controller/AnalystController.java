package com.myinvest.startup_mvp_backend.controller;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analyst/pack")
public class AnalystController {
    public record StartRequest(@NotBlank String tenantId, @NotBlank String packId){}
    public record DecisionRequest(@NotBlank String tenantId, @NotBlank String packId, @NotBlank String decision, String reason){}

    // POST /analyst/pack/start
    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody StartRequest body) {
        return Map.of(
            "packId", body.packId(),
            "status", "IN_REVIEW",
            "startedAt", "2025-09-10T12:00:00Z"
        );
    }

    // GET /analyst/pack/review?tenantId=...&packId=...
    @GetMapping("/review")
    public Map<String, Object> review(@RequestParam String tenantId, @RequestParam String packId) {
        return Map.of(
            "packId", packId,
            "verification", Map.of(
                "stage_1_primary_screening", Map.of(
                    "juridical_check", "Компания зарегистрирована в РФ, юридические риски не выявлены.",
                    "market_check", "Финтех рынок соответствует стратегии инвестора, TAM/SAM/SOM валидны.",
                    "technology_stack", "Java, Spring, PostgreSQL",
                    "stage_of_project", "MVP",
                    "conclusion", "Проходит скрининг"
                ),
                "stage_2_in_depth_analysis", Map.of(
                    "knowledge_graph", Map.of(
                        "technologies", List.of("Java","Spring","PostgreSQL"),
                        "market_segments", List.of("крупные и средние банки РФ"),
                        "team", "Сбалансирована",
                        "investors_partners", "клиенты-пилоты указаны"
                    ),
                    "competition", Map.of(
                        "list", List.of("ЦФТ","Abanking"),
                        "positioning", "нишевое преимущество"
                    ),
                    "usp", List.of("WhiteLabel","Гибкая кастомизация"),
                    "conclusion", "Сильное позиционирование"
                ),
                "stage_3_financial_assessment", Map.of(
                    "burnRate", 25000,
                    "breakevenMonth", 18,
                    "scenarios", Map.of("optimistic","20x","base","8x","pessimistic","2x"),
                    "conclusion", "Финансовая модель устойчива"
                ),
                "stage_4_ai_verification", Map.of(
                    "originality_score", 0.88,
                    "confidence_score", 0.90,
                    "red_flags", List.of(),
                    "conclusion", "Материалы оригинальны"
                ),
                "stage_5_recommendations", Map.of(
                    "investment_recommendation", "Рассмотреть участие в Seed",
                    "risks", Map.of("technology","Medium","market","Low","team","Low"),
                    "expected_roi", "Высокий потенциал"
                )
            )
        );
    }

    // POST /analyst/pack/decision
    @PostMapping("/decision")
    public Map<String, Object> decision(@RequestBody DecisionRequest body) {
        String status = "APPROVED".equalsIgnoreCase(body.decision()) ? "APPROVED" : "REJECTED";

        Map<String, Object> res = new LinkedHashMap<>(); // допускает null, но мы его не кладём
        res.put("packId", body.packId());
        res.put("status", status);
        if (body.reason() != null && !body.reason().isBlank()) {
            res.put("reason", body.reason());
        }
        return res;
    }
}
