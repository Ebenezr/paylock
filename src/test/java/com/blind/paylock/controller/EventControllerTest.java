package com.blind.paylock.controller;

import com.blind.paylock.datalayer.dto.request.EventCreateRequestDto;
import com.blind.paylock.datalayer.dto.response.EventListItemResponseDto;
import com.blind.paylock.datalayer.dto.response.EventResponseDto;
import com.blind.paylock.service.EventService;
import com.blind.paylock.utils.apis.ApiResponse;
import com.blind.paylock.utils.apis.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventController = new EventController(eventService);
    }

    @Test
    void create_shouldDelegateToService() {
        EventCreateRequestDto req = new EventCreateRequestDto();
        when(eventService.createEvent(eq(req))).thenReturn(ResponseFactory.success(EventResponseDto.builder().eventId("e1").build(), "ref"));

        ApiResponse<EventResponseDto> r = eventController.create(req).block();
        assertThat(r).isNotNull();
    }

    @Test
    void publish_and_cancel_shouldDelegate() {
        when(eventService.publishEvent(eq("e1"))).thenReturn(ResponseFactory.success(null, "ref"));
        when(eventService.cancelEvent(eq("e1"))).thenReturn(ResponseFactory.success(null, "ref"));

        ApiResponse<Void> p = eventController.publish("e1").block();
        ApiResponse<Void> c = eventController.cancel("e1").block();

        assertThat(p).isNotNull();
        assertThat(c).isNotNull();
    }

    @Test
    void listPublished_shouldReturnList() {
        when(eventService.listPublishedEvents()).thenReturn(ResponseFactory.success(List.of(EventListItemResponseDto.builder().eventId("e1").build()), "ref"));
        ApiResponse<List<EventListItemResponseDto>> r = eventController.listPublished(0, 20).block();
        assertThat(r).isNotNull();
        assertThat(r.getBody()).isNotNull();
    }
}
