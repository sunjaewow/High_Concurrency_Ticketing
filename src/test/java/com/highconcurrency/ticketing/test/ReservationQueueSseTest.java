package com.highconcurrency.ticketing.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationQueueSseTest {

    private static final String RESERVATION_QUEUE_URL = "/reservation-queues/{concertId}/users/{userId}";
    private static final String RESERVATION_QUEUE_EVENTS_URL = "/reservation-queues/{concertId}/users/{userId}/events";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 다음_대기_사용자가_허용되면_SSE로_PERMITTED와_userId를_받는다() throws Exception {
        for (long userId = 1L; userId <= 10001L; userId++) {
            mockMvc.perform(post(RESERVATION_QUEUE_URL, 1L, userId))
                    .andExpect(status().isCreated());
        }

        // SSE 요청은 응답이 바로 끝나지 않고 비동기 스트림으로 열린다.
        MvcResult mvcResult = mockMvc.perform(get(RESERVATION_QUEUE_EVENTS_URL, 1L, 10001L)
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        // 허용된 사용자가 이탈하면 다음 대기자에게 PERMITTED 이벤트가 발행된다.
        mockMvc.perform(delete(RESERVATION_QUEUE_URL, 1L, 1L))
                .andExpect(status().isNoContent());

        // 비동기 SSE 응답이 완료된 뒤 실제 전송된 body를 검증한다.
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PERMITTED")))
                .andExpect(content().string(containsString("\"userId\":10001")));
    }
}
