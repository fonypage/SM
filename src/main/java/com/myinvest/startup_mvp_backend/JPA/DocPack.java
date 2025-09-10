package com.myinvest.startup_mvp_backend.JPA;

import jakarta.persistence.*;

@Entity
public class DocPack {
    @Id
    @GeneratedValue
    private Long id;

    private String tenantId;   // чтобы разделять разные компании
    private String packId;     // тот самый "sub_001"
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status = Status.DRAFT;

    private String rejectReason;

    public enum Status {
        DRAFT, IN_REVIEW, APPROVED, REJECTED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
