package com.donation.carebridge.payments.pg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "pg_accounts")
public class PgAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private PgProvider provider;

    @Column(name = "client_id", length = 100, nullable = false)
    private String clientId;

    @Column(name = "api_key_encrypted", length = 500, nullable = false)
    private String apiKeyEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", length = 20, nullable = false)
    private PgEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PgStatus status;
}