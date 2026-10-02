package com.miniwallet.ledger;

import com.miniwallet.account.Account;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id")
    private WalletTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryDirection direction;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected LedgerEntry() {}

    public LedgerEntry(WalletTransaction transaction, Account account,
                       EntryDirection direction, BigDecimal amount) {
        this.transaction = transaction;
        this.account = account;
        this.direction = direction;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public WalletTransaction getTransaction() { return transaction; }
    public Account getAccount() { return account; }
    public EntryDirection getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}