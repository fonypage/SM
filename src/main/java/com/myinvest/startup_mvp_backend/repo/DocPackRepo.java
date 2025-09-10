package com.myinvest.startup_mvp_backend.repo;

import com.myinvest.startup_mvp_backend.JPA.DocPack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocPackRepo extends JpaRepository<DocPack, Long> {
    Optional<DocPack> findByTenantIdAndPackId(String tenantId, String packId);
    List<DocPack> findAllByTenantIdOrderByIdDesc(String tenantId);
    List<DocPack> findAllByStatusOrderByIdDesc(DocPack.Status status);
    List<DocPack> findAllByTenantIdAndStatusOrderByIdDesc(String tenantId, DocPack.Status status);
}