package dev.prvt.yawiki.core.permission.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
//    @Query("select p from Permission p inner join ResourcePermission rp on rp.requiredPermission = p")
//    Optional<Permission> findPermissionBySubjectResourceId(UUID id);

    @Query("select p from Permission p where p.create = :c and p.read = :r and p.update = :u and p.delete = :d and p.manage = :m")
    Optional<Permission> findByAllAttributes(@Param("c") int c,
                                             @Param("r") int r,
                                             @Param("u") int u,
                                             @Param("d") int d,
                                             @Param("m") int m
    );
}
