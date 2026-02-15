package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.response.TicketResponseDto;
import com.blind.paylock.service.TicketService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    private TicketController controller;

    @BeforeEach
    void setUp() {
        controller = new TicketController(ticketService);
    }

    @Test
    void listMyTickets_shouldDelegate() {
        when(ticketService.listMyTickets()).thenReturn(ResponseFactory.success(List.of(TicketResponseDto.builder().ticketId("t1").build()), "ref"));
        ApiResponse<List<TicketResponseDto>> r = controller.listMyTickets().block();
        assertThat(r).isNotNull();
    }
}
