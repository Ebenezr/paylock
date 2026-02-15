package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.TicketTypeCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.TicketTypeResponseDto;
import com.blind.paylock.service.TicketTypeService;
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
class TicketTypeControllerTest {

    @Mock
    private TicketTypeService ticketTypeService;

    private TicketTypeController controller;

    @BeforeEach
    void setUp() {
        controller = new TicketTypeController(ticketTypeService);
    }

    @Test
    void create_and_list_shouldDelegate() {
        when(ticketTypeService.createTicketType(eq("e1"), any())).thenReturn(ResponseFactory.success(TicketTypeResponseDto.builder().ticketTypeId("tt1").build(), "ref"));
        when(ticketTypeService.listTicketTypes(eq("e1"))).thenReturn(ResponseFactory.success(List.of(TicketTypeResponseDto.builder().ticketTypeId("tt1").build()), "ref"));

        ApiResponse<TicketTypeResponseDto> c = controller.create("e1", new TicketTypeCreateRequestDto()).block();
        ApiResponse<List<TicketTypeResponseDto>> l = controller.list("e1").block();

        assertThat(c).isNotNull();
        assertThat(l).isNotNull();
    }
}
