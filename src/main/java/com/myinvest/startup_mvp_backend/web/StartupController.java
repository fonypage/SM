package com.myinvest.startup_mvp_backend.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StartupController {
    // GET /startup/pack?tenantId=...&packId=...
    @GetMapping("/startup/pack")
    public Map<String, Object> getStartupPack(
            @RequestParam String tenantId,
            @RequestParam String packId
    ) {
        return Map.of(
            "packId", packId,
            "project", Map.of("name", "Orbita.Center"),
            "status", "NEW",
            "summary", Map.of(
                "docsCount", 12,
                "next", "Перейти к проверке аналитиком"
            ),
            "docsPreview", List.of(
                Map.of("name","ustav_26.pdf","kind","charter"),
                Map.of("name","pp_25.pdf","kind","payment"),
                Map.of("name","izm_ustav_08.pdf","kind","charter_amendment"),
                Map.of("name","prot.pdf","kind","protocol"),
                Map.of("name","sopr.pdf","kind","agreement"),
                Map.of("name","dsur.pdf","kind","other")
            )
        );
    }
}
