package com.miniwallet;

import com.miniwallet.account.*;
import com.miniwallet.ledger.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional   // rolls back after each test, so the DB stays clean
class SchemaSmokeTest {

    @Autowired AccountRepository accounts;
    @Autowired WalletTransactionRepository transactions;
    @Autowired LedgerEntryRepository ledger;

    @Test
    void systemAccountIsSeeded() {
        assertThat(accounts.findFirstByType(AccountType.SYSTEM)).isPresent();
    }

    @Test
    void savesAndReadsAccountWithExactBalance() {
        String email = UUID.randomUUID() + "@test.com";
        Account saved = accounts.saveAndFlush(new Account("Ranjith", email, AccountType.USER));

        Account found = accounts.findById(saved.getId()).orElseThrow();

        assertThat(found.getEmail()).isEqualTo(email);
        assertThat(found.getBalance()).isEqualByComparingTo("0");
    }

    @Test
    void ledgerEntriesNetToZero() {
        Account user = accounts.save(new Account("U", UUID.randomUUID() + "@test.com", AccountType.USER));
        Account system = accounts.findFirstByType(AccountType.SYSTEM).orElseThrow();

        WalletTransaction tx = transactions.save(
                new WalletTransaction(TransactionType.TOPUP, TransactionStatus.COMPLETED, new BigDecimal("50.0000")));
        ledger.save(new LedgerEntry(tx, system, EntryDirection.DEBIT,  new BigDecimal("50.0000")));
        ledger.save(new LedgerEntry(tx, user,   EntryDirection.CREDIT, new BigDecimal("50.0000")));
        ledger.flush();

        List<LedgerEntry> entries = ledger.findByTransactionId(tx.getId());
        BigDecimal net = entries.stream()
                .map(e -> e.getDirection() == EntryDirection.CREDIT ? e.getAmount() : e.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(entries).hasSize(2);
        assertThat(net).isEqualByComparingTo("0");
    }
}