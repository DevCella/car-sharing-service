package com.carsharing.repository;

import com.carsharing.model.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = "rental.user")
    Optional<Payment> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = "rental.user")
    Page<Payment> findByRentalUserId(Long userId, Pageable pageable);

    Optional<Payment> findByRentalIdAndRentalUserId(
            Long rentalId, Long userId);
}
