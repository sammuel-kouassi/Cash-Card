package org.example.cashcard;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("CASH_CARD")
public record CashCard(@Id @Column("ID") Long id,
                       @Column("AMOUNT") Double amount,
                       @Column("OWNER") String owner) {

    public CashCard(Long id, Double amount) {
        this(id, amount, null);
    }
}