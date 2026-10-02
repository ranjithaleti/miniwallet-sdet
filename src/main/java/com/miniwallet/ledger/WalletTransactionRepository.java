package com.miniwallet.ledger;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {}