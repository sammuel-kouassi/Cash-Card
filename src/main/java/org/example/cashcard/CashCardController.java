package org.example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.security.Principal;



@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCardOptional;

        //User connected or not
        if (principal == null) {
            cashCardOptional = cashCardRepository.findById(requestedId);
        } else {
            cashCardOptional = Optional.ofNullable(
                    cashCardRepository.findByIdAndOwner(requestedId, principal.getName())
            );
        }
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());// 404 NOT FOUND
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb) {
        // Gestion des doublons: si un ID est fourni et existe déjà, renvoyer 409 CONFLICT
        if (newCashCardRequest.id() != null && cashCardRepository.existsById(newCashCardRequest.id())) {
            URI locationOfExisting = buildLocation(newCashCardRequest.id(), ucb);
            return ResponseEntity.status(409).location(locationOfExisting).build();// 409 CONFLICT
        }
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = buildLocation(savedCashCard.id(), ucb);
        return ResponseEntity.created(locationOfNewCashCard).build(); // 201 CREATED
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(
            @PageableDefault(sort = "amount", direction = Sort.Direction.ASC) Pageable pageable,
            Principal principal) {
        // Only return cards owned by the authenticated user
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(), pageable);
        return ResponseEntity.ok(page.getContent());// 200 OK
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // 401 UNAUTHORIZED
        }
        CashCard owned = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        if (owned == null) {
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        }
        CashCard updatedCashCard = new CashCard(owned.id(), cashCardUpdate.amount(), owned.owner());
        cashCardRepository.save(updatedCashCard);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }

    // Méthode utilitaire pour construire l'URI d'une resource CashCard
    private URI buildLocation(Long id, UriComponentsBuilder ucb) {
        return ucb
                .path("/cashcards/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // 401 UNAUTHORIZED
        }
        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        }
        cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();// 204 NO CONTENT
    }
}