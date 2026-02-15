package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.response.EventAvailabilityResponseDto;
import com.blind.paylock.service.EventCatalogService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCatalogControllerTest {

    @Mock
    private EventCatalogService eventCatalogService;

    private EventCatalogController controller;

    @BeforeEach
    void setUp() {
        controller = new EventCatalogController(eventCatalogService);
    }

    @Test
    void availability_shouldDelegateToService() {
        when(eventCatalogService.getEventAvailability(eq("e1"))).thenReturn(ResponseFactory.success(EventAvailabilityResponseDto.builder().eventId("e1").build(), "ref"));
        ApiResponse<EventAvailabilityResponseDto> r = controller.availability("e1").block();
        assertThat(r).isNotNull();
    }
}
