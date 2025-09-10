package com.myinvest.startup_mvp_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myinvest.startup_mvp_backend.JPA.DocPack;
import com.myinvest.startup_mvp_backend.repo.DocPackRepo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalysisService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final DocPackRepo docPackRepo;

    // простое хранилище решений на MVP (ключ: tenantId|packId)
    private final Map<String, Submission> decisions = new ConcurrentHashMap<>();

    public AnalysisService(DocPackRepo docPackRepo) {
        this.docPackRepo = docPackRepo;
    }

    // GET /analyst/pack/review → твой контроллер вызывает это
    public String getAnalysisJson(String tenantId, String packId) {
        try (InputStream in = new ClassPathResource("fixtures/sample-analysis.json").getInputStream()) {
            // читаем как дерево, чтобы убедиться что JSON ок; затем возвращаем как строку
            JsonNode node = mapper.readTree(in);
            return node.toString(); // application/json будет выставлен в контроллере
        } catch (Exception e) {
            // на MVP можно вернуть «пустой» JSON; но лучше кинуть RTE, чтобы увидеть ошибку
            throw new RuntimeException("Cannot load fixtures/sample-analysis.json", e);
        }
    }

    // POST /analyst/pack/decision → твой контроллер ждёт Submission с getSubmissionId()/getStatus()
    public Map<String, Object> decide(String tenantId, String packId, String decision, String reason) {
        DocPack pack = docPackRepo.findByTenantIdAndPackId(tenantId, packId)
                .orElseThrow(() -> new IllegalArgumentException("Pack not found"));

        if ("APPROVED".equalsIgnoreCase(decision)) {
            pack.setStatus(DocPack.Status.APPROVED);
        } else {
            pack.setStatus(DocPack.Status.REJECTED);
            pack.setRejectReason(reason);
        }
        docPackRepo.save(pack);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("packId", pack.getPackId());
        res.put("status", pack.getStatus().name());
        if (reason != null && !reason.isBlank()) {
            res.put("reason", reason);
        }
        return res;
    }

    // (опционально) получить последнее решение — может пригодиться на этапе инвестора
    public Submission getLastDecision(String tenantId, String packId) {
        return decisions.get(tenantId + "|" + packId);
    }
}