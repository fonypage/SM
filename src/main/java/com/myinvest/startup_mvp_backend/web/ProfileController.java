package com.myinvest.startup_mvp_backend.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinvest.startup_mvp_backend.JPA.DocPack;
import com.myinvest.startup_mvp_backend.repo.DocPackRepo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final DocPackRepo docPackRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public ProfileController(DocPackRepo docPackRepo) {
        this.docPackRepo = docPackRepo;
    }

    /**
     * Единая точка входа в «личные кабинеты».
     * role=startup | analyst | investor
     */
    @GetMapping
    public Map<String, Object> profile(@RequestParam String tenantId, @RequestParam String role,
                                       @RequestParam(required = false) String packId) {
        switch (role.toLowerCase()) {
            case "startup":
                return startupHome(tenantId);
            case "analyst":
                return analystHome(tenantId, packId);
            case "investor":
                return investorHome(tenantId);
            default:
                return Map.of("error", "Unknown role: " + role);
        }
    }

    // ====== 1) Главная (стартапер) ======
    private Map<String, Object> startupHome(String tenantId) {
        // Предзаполненные данные компании/команды (MVP)
        Map<String, Object> company = Map.of(
                "name", "ООО «Предприятие»",
                "inn", "7700...123",
                "ogrn", "10277...456",
                "site", "https://example.org"
        );
        List<Map<String, Object>> team = List.of(
                Map.of("name", "Иван Петров", "role", "CEO", "email", "ivan@corp.ru"),
                Map.of("name", "Анна Смирнова", "role", "CTO", "email", "anna@corp.ru")
        );

        // «Готовый список документов» на главной (можно получать с бэка; пока — статик)
        List<Map<String, Object>> readyDocs = List.of(
                Map.of("name", "Устав компании.pdf", "type", "charter", "required", true),
                Map.of("name", "Питч-дек.pptx", "type", "pitchdeck", "required", true),
                Map.of("name", "Финмодель.xlsx", "type", "finmodel", "required", true),
                Map.of("name", "Рынок и конкуренты.pdf", "type", "market", "required", false)
        );

        // Пакеты стартапера
        List<Map<String, Object>> packs = docPackRepo.findAllByTenantIdOrderByIdDesc(tenantId)
                .stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("packId", p.getPackId());
                    m.put("name", p.getName());
                    m.put("status", p.getStatus().name());
                    if (p.getRejectReason() != null && !p.getRejectReason().isBlank()) {
                        m.put("rejectReason", p.getRejectReason());
                    }
                    return m;
                }).collect(Collectors.toList());

        // Действие «Отправить»
        Map<String, Object> cta = Map.of(
                "label", "Отправить",
                "method", "POST",
                "url", "/startup/packs/create",         // или твой реальный URL создания/отправки
                "payloadTemplate", Map.of(
                        "tenantId", tenantId,
                        "name", "Demo Pack (отправлено со старта)"
                ),
                "nextRoute", Map.of("role", "analyst")  // после клика фронт может перейти на страницу аналитика
        );

        // Верхняя иконка ЛК
        Map<String, Object> header = Map.of(
                "role", "startup",
                "icon", "account-startup",
                "menu", List.of("Профиль", "Мои пакеты", "Выход")
        );

        return Map.of(
                "header", header,
                "role", "startup",
                "company", company,
                "team", team,
                "readyDocs", readyDocs,
                "packs", packs,
                "cta", cta
        );
    }

    // ====== 2) Страница Аналитика (задачи / загруженные / заключение) ======
    private Map<String, Object> analystHome(String tenantId, String packId) {
        // Очередь задач (IN_REVIEW)
        List<Map<String, String>> queue = docPackRepo
                .findAllByStatusOrderByIdDesc(DocPack.Status.IN_REVIEW)
                .stream().map(p -> Map.of(
                        "tenantId", p.getTenantId(),
                        "packId", p.getPackId(),
                        "name", p.getName(),
                        "status", p.getStatus().name()
                )).collect(Collectors.toList());

        // Определяем «текущий» packId (если не передали — берём первый из очереди конкретного tenant)
        String currentPackId = Optional.ofNullable(packId)
                .orElseGet(() -> docPackRepo.findAllByTenantIdAndStatusOrderByIdDesc(tenantId, DocPack.Status.IN_REVIEW)
                        .stream().findFirst().map(DocPack::getPackId).orElse(null));

        Map<String, Object> uploadedDocsTab = Map.of(
                "docs", sampleUploadedDocs() // список «загруженных» для визуализации (MVP)
        );

        Map<String, Object> conclusionTab = Map.of(
                "items", conclusionItems(tenantId, currentPackId) // краткое описание пунктов/замечаний
        );

        // Вкладки внутри страницы Аналитика
        List<Map<String, Object>> tabs = List.of(
                Map.of("key", "tasks", "title", "Задачи", "data", queue),
                Map.of("key", "uploaded", "title", "Загруженные документы", "data", uploadedDocsTab),
                Map.of("key", "conclusion", "title", "Заключение", "data", conclusionTab)
        );

        // Верхняя иконка ЛК
        Map<String, Object> header = Map.of(
                "role", "analyst",
                "icon", "account-analyst",
                "menu", List.of("Очередь", "KPI", "Выход")
        );

        // Кнопка «Подтвердить документацию» — ты уже сделал POST /analyst/pack/decision
        Map<String, Object> ctaConfirm = Map.of(
                "label", "Подтвердить документацию",
                "method", "POST",
                "url", "/analyst/pack/decision",
                "payloadTemplate", Map.of(
                        "tenantId", tenantId,
                        "packId", currentPackId != null ? currentPackId : "sub_001",
                        "decision", "APPROVED",
                        "reason", "Проверено, комплект полный"
                ),
                "nextRoute", Map.of("role", "investor") // фронт после успеха переходит на страницу инвестора
        );

        return Map.of(
                "header", header,
                "role", "analyst",
                "tabs", tabs,
                "cta", ctaConfirm
        );
    }

    // ====== 3) Страница Инвестора (метрики/графики по одобренным) ======
    private Map<String, Object> investorHome(String tenantId) {
        // Одобренные пакеты
        List<DocPack> approved = docPackRepo
                .findAllByTenantIdAndStatusOrderByIdDesc(tenantId, DocPack.Status.APPROVED);

        // Собираем список проектов
        List<Map<String, Object>> projects = new ArrayList<>();
        for (DocPack p : approved) {
            Map<String, Object> pr = new LinkedHashMap<>();
            pr.put("packId", p.getPackId());
            pr.put("name", p.getName());
            pr.put("status", p.getStatus().name());

            projects.add(pr);
        }

        // Примеры метрик/серий для графиков (можно заменить на реальные)
        Map<String, Object> charts = Map.of(
                "mrr", List.of(10, 14, 19, 27, 38, 54),
                "cac", List.of(200, 190, 185, 170, 160, 155),
                "ltv", List.of(600, 640, 700, 760, 820, 900)
        );

        // Верхняя иконка ЛК
        Map<String, Object> header = Map.of(
                "role", "investor",
                "icon", "account-investor",
                "menu", List.of("Портфель", "Экспорт", "Выход")
        );

        Double avgTech  = avgRisk(projects, "technology_risk");
        Double avgMarket= avgRisk(projects, "market_risk");
        Double avgTeam  = avgRisk(projects, "team_risk");

        Map<String, Object> kpi = new LinkedHashMap<>();
        kpi.put("totalApproved", projects.size());
        if (avgTech   != null) kpi.put("avgTechnologyRisk", avgTech);   // или kpi.put("avgTechnologyRisk", avgTech!=null?avgTech:0.0);
        if (avgMarket != null) kpi.put("avgMarketRisk",     avgMarket);
        if (avgTeam   != null) kpi.put("avgTeamRisk",       avgTeam);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("header", header);
        resp.put("role", "investor");
        resp.put("projects", projects);   // даже пустой список нормален
        resp.put("charts", charts);       // твой Map.of(...) с демо-данными — там null нет
        resp.put("kpi", kpi);
        return resp;
    }

    // ====== helpers ======

    /** Демонстрационный список «загруженных документов» для вкладки Analyst -> uploaded */
    private List<Map<String, Object>> sampleUploadedDocs() {
        return List.of(
                Map.of("name", "Устав компании.pdf", "status", "OK"),
                Map.of("name", "Питч-дек.pptx", "status", "OK"),
                Map.of("name", "Финмодель.xlsx", "status", "WARN", "note", "Не хватает раздела CF"),
                Map.of("name", "Рынок и конкуренты.pdf", "status", "OK")
        );
    }

    /** Краткое «заключение» по ключевым пунктам из sample-analysis.json (если AnalysisResult ещё не в БД) */
    private List<Map<String, Object>> conclusionItems(String tenantId, String packId) {

        // иначе: читаем фикстуру (MVP)
        try (InputStream in = new ClassPathResource("fixtures/sample-analysis.json").getInputStream()) {
            JsonNode root = mapper.readTree(in);

            List<Map<String, Object>> items = new ArrayList<>();
            // примеры полей/пояснений для вкладки «Заключение»:
            JsonNode s1 = root.at("/verification/stage_1_primary_screening");
            if (!s1.isMissingNode()) {
                items.add(Map.of(
                        "section", "Первичный скрининг",
                        "status", "OK",
                        "note", s1.path("conclusion").asText("Соответствует инвестиционной стратегии")
                ));
            }
            JsonNode s2 = root.at("/verification/stage_2_in_depth_analysis");
            if (!s2.isMissingNode()) {
                items.add(Map.of(
                        "section", "Глубинный анализ",
                        "status", "OK",
                        "note", s2.path("conclusion").asText("Сильное позиционирование на рынке")
                ));
            }
            JsonNode s3 = root.at("/verification/stage_3_financial_assessment");
            if (!s3.isMissingNode()) {
                items.add(Map.of(
                        "section", "Финансовая оценка",
                        "status", "WARN",
                        "note", s3.path("conclusion").asText("Риск средний; монетизация перспективна")
                ));
            }
            JsonNode s4 = root.at("/verification/stage_4_ai_verification");
            if (!s4.isMissingNode()) {
                items.add(Map.of(
                        "section", "AI-верификация",
                        "status", "OK",
                        "note", s4.path("conclusion").asText("Оригинальность высокая, red_flags=[]")
                ));
            }
            JsonNode s5 = root.at("/verification/stage_5_recommendations");
            if (!s5.isMissingNode()) {
                items.add(Map.of(
                        "section", "Рекомендации",
                        "status", "OK",
                        "note", s5.path("investment_recommendation").asText("Рассмотреть участие (Seed/Series A)")
                ));
            }
            return items;
        } catch (Exception e) {
            return List.of(Map.of("section", "Ошибка", "status", "FAIL", "note", "Не удалось загрузить заключение"));
        }
    }

    private Double avgRisk(List<Map<String, Object>> projects, String key) {
        // Пример: "Low"=1, "Medium"=2, "High"=3
        Map<String, Integer> scale = Map.of("Low", 1, "Medium", 2, "High", 3);
        List<Integer> vals = projects.stream()
                .map(p -> scale.getOrDefault(String.valueOf(p.getOrDefault(key, "Medium")), 2))
                .collect(Collectors.toList());
        if (vals.isEmpty()) return null;
        return vals.stream().mapToInt(i -> i).average().orElse(0.0);
    }
}