package com.myinvest.startup_mvp_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ProfileController {
    // GET /profile?tenantId=...&role=...
    @GetMapping("/profile")
    public Map<String, Object> profile(@RequestParam String tenantId, @RequestParam String role) {
        return switch (role) {
            case "startup" -> Map.of(
                "role","startup",
                "company", Map.of("name","ОАО «Предприятие»","inn","7700...","ogrn","10277..."),
                "team", List.of(
                    Map.of("name","Иван Петров","role","CEO","email","ivan@corp.ru"),
                    Map.of("name","Анна Смирнова","role","CTO","email","anna@corp.ru")
                ),
                "packs", List.of(
                    Map.of("packId","p_101","name","Основной пакет KVP","status","IN_REVIEW"),
                    Map.of("packId","p_102","name","Дополнительный пакет","status","DRAFT")
                ),
                "notifications", List.of(
                    Map.of("type","warning","text","A2 требует загрузки платёжного документа")
                ),
                "support", Map.of("ticketsOpen",1,"lastTicketId","SUP-3412")
            );
            case "analyst" -> Map.of(
                "role","analyst",
                "kpi", Map.of("reviewed",12,"approved",7,"avgTimeMin",14,"sla","< 60 мин/пакет"),
                "queue", List.of(
                    Map.of("packId","p_101","title","ОАО «Предприятие»","etaMin",10),
                    Map.of("packId","p_202","title","B2B SaaS","etaMin",18)
                ),
                "savedComments", List.of(
                    Map.of("code","A2","template","Пожалуйста, приложите платёжный документ с верными реквизитами.")
                ),
                "recentActions", List.of(
                    Map.of("at","2025-09-10T09:35:00Z","action","REVIEW_STARTED","packId","p_101"),
                    Map.of("at","2025-09-10T09:50:00Z","action","TASK_MARKED_ERROR(A2)","packId","p_101")
                )
            );
            case "investor" -> Map.of(
                "role","investor",
                "strategy", Map.of(
                    "ticketMin","$100k","ticketMax","$500k",
                    "focusSectors", List.of("FinTech","B2B SaaS"),
                    "geography", List.of("EEU"),
                    "stage", List.of("Seed","Series A")
                ),
                "watchlist", List.of(
                    Map.of("packId","p_101","name","ОАО «Предприятие»","score",0.86,"stage","Seed"),
                    Map.of("packId","p_303","name","MedTech.AI","score",0.74,"stage","Pre-Seed")
                ),
                "alerts", List.of(
                    Map.of("type","milestone","text","Достигнута точка безубыточности по проекту X"),
                    Map.of("type","risk","text","Рост CAC > 15% m/m у проекта Y")
                ),
                "reports", List.of(
                    Map.of("name","Сводка за месяц","period","2025-08","downloadUrl","/reports/m_2025_08.pdf")
                )
            );
            default -> Map.of("role", role, "error", "Unknown role");
        };
    }
}
