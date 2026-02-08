package com.blind.paylock.component;

import com.blind.paylock.repository.ReservationRepository;
import com.blind.paylock.utils.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryProcessor {

    private final ReservationRepository reservationRepository;
    private final WalletProcessor walletProcessor;

    public Mono<Void> expireReservations() {

        return reservationRepository
            .findExpiredUnpaid(LocalDateTime.now())
            .flatMap(reservation -> {

                BigDecimal refundAmount = reservation.getAmountPaid();

                Mono<Void> refundFlow =
                    refundAmount.compareTo(BigDecimal.ZERO) > 0
                        ? walletProcessor.credit(
                            reservation.getUserId(),
                            refundAmount,
                            "RESERVATION_EXPIRED",
                            reservation.getId().toString()
                        )
                        : Mono.empty();

                return refundFlow.then(
                    reservationRepository.save(
                        reservation.toBuilder()
                            .status(ReservationStatus.EXPIRED)
                            .isNew(false)
                            .build()
                    )
                );
            })
            .then();
    }
}
