package org.example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    @Query("SELECT id, amount, owner FROM cash_card WHERE id = :id AND owner = :owner")
    CashCard findByIdAndOwner(@Param("id") Long id, @Param("owner") String owner);

    boolean existsByIdAndOwner(Long id, String owner);

    Page<CashCard> findByOwner(String owner, Pageable pageable);
}
