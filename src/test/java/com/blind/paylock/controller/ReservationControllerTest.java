package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.ReservationCreateRequestDto;
import com.blind.paylock.datalayer.dto.request.ReservationPaymentRequestDto;
import com.blind.paylock.datalayer.dto.response.ReservationResponseDto;
import com.blind.paylock.service.ReservationService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    private ReservationController controller;

    @BeforeEach
    void setUp() {
        controller = new ReservationController(reservationService);
    }

    @Test
    void create_and_pay_and_cancel_and_get_shouldDelegate() {
        when(reservationService.createReservationWithFirstPayment(any())).thenReturn(ResponseFactory.success(ReservationResponseDto.builder().reservationId("r1").build(), "ref"));
        when(reservationService.makePayment(eq("r1"), any())).thenReturn(ResponseFactory.success(ReservationResponseDto.builder().reservationId("r1").build(), "ref"));
        when(reservationService.cancelReservation(eq("r1"))).thenReturn(ResponseFactory.success(null, "ref"));
        when(reservationService.getReservation(eq("r1"))).thenReturn(ResponseFactory.success(ReservationResponseDto.builder().reservationId("r1").build(), "ref"));

        ApiResponse<ReservationResponseDto> c = controller.create(new ReservationCreateRequestDto()).block();
        ApiResponse<ReservationResponseDto> p = controller.pay("r1", new ReservationPaymentRequestDto()).block();
        ApiResponse<Void> x = controller.cancel("r1").block();
        ApiResponse<ReservationResponseDto> g = controller.get("r1").block();

        assertThat(c).isNotNull();
        assertThat(p).isNotNull();
        assertThat(x).isNotNull();
        assertThat(g).isNotNull();
    }

    @Test
    void myReservations_shouldDelegate() {
        when(reservationService.listUserReservations(eq(0), eq(10))).thenReturn(ResponseFactory.success(List.of(ReservationResponseDto.builder().reservationId("r1").build()), "ref"));
        ApiResponse<List<ReservationResponseDto>> r = controller.myReservations(0, 10).block();
        assertThat(r).isNotNull();
    }
}
