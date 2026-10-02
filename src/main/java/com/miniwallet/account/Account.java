package com.miniwallet.account;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Account() {}   // JPA needs this

    public Account(String ownerName, String email, AccountType type) {
        this.ownerName = ownerName;
        this.email = email;
        this.type = type;
    }

    public UUID getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public String getEmail() { return email; }
    public String getCurrency() { return currency; }
    public AccountType getType() { return type; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Instant getCreatedAt() { return createdAt; }
}