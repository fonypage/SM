package com.myinvest.startup_mvp_backend.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class HealthController {
    @GetMapping("/healthz")
    public Map<String,String> ok(){ return Map.of("status","ok"); }
}
