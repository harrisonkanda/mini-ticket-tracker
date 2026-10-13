package com.kiprono.tickettracker.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.kiprono.tickettracker.constant.ApiPaths;
import com.kiprono.tickettracker.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Exercises the API end to end (HTTP layer, validation, JPA, Flyway schema) against a real
 * Postgres started by Testcontainers, so the tests run the same SQL as production.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TicketControllerIntegrationTest {

    private static final String STATS_URL = ApiPaths.TICKETS + ApiPaths.TICKET_STATS;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void cleanDatabase() {
        ticketRepository.deleteAll();
    }

    @Test
    void createTicket_appliesDefaultsAndReturns201() throws Exception {
        createTicket("""
                {"title": "  Login button unresponsive  ", "description": "Occurs on Safari mobile"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Login button unresponsive"))
                .andExpect(jsonPath("$.description").value("Occurs on Safari mobile"))
                .andExpect(jsonPath("$.status").value("open"))
                .andExpect(jsonPath("$.priority").value("medium"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void createTicket_withoutTitle_returns400WithFieldDetail() throws Exception {
        createTicket("""
                {"title": "   ", "priority": "high"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details", hasItem("title: Title is required")));

        mockMvc.perform(get(STATS_URL))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void createTicket_withUnknownPriority_returns400ListingAllowedValues() throws Exception {
        createTicket("""
                {"title": "Printer on fire", "priority": "urgent"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Allowed values: low, medium, high")));
    }

    @Test
    void stats_countsTicketsPerStatusIncludingZeros() throws Exception {
        createTicket("""
                {"title": "Stays open"}
                """);
        createTicket("""
                {"title": "Also stays open"}
                """);
        long closedId = createTicketAndReturnId("""
                {"title": "Gets closed"}
                """);
        updateTicket(closedId, """
                {"status": "closed"}
                """).andExpect(status().isOk());

        mockMvc.perform(get(STATS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byStatus.open").value(2))
                .andExpect(jsonPath("$.byStatus.in_progress").value(0))
                .andExpect(jsonPath("$.byStatus.closed").value(1))
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    void listTickets_filtersByStatusAndPaginatesNewestFirst() throws Exception {
        createTicket("""
                {"title": "First"}
                """);
        createTicket("""
                {"title": "Second"}
                """);
        createTicket("""
                {"title": "Third"}
                """);
        long inProgressId = createTicketAndReturnId("""
                {"title": "Being worked on"}
                """);
        updateTicket(inProgressId, """
                {"status": "in_progress"}
                """);

        mockMvc.perform(get(ApiPaths.TICKETS)
                        .param("status", "open")
                        .param("page", "1")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].status", everyItem(is("open"))))
                .andExpect(jsonPath("$.content[0].title").value("Third"))
                .andExpect(jsonPath("$.content[1].title").value("Second"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get(ApiPaths.TICKETS)
                        .param("status", "open")
                        .param("page", "2")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("First"));
    }

    @Test
    void listTickets_rejectsOutOfRangePaginationAndUnknownStatus() throws Exception {
        mockMvc.perform(get(ApiPaths.TICKETS).param("page", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(ApiPaths.TICKETS).param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("limit must be between")));

        mockMvc.perform(get(ApiPaths.TICKETS).param("status", "done"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Allowed values: open, in_progress, closed")));
    }

    @Test
    void updateTicket_changesOnlyTheFieldsSent() throws Exception {
        long id = createTicketAndReturnId("""
                {"title": "Slow dashboard", "priority": "low"}
                """);

        updateTicket(id, """
                {"status": "in_progress"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in_progress"))
                .andExpect(jsonPath("$.priority").value("low"));

        updateTicket(id, """
                {"priority": "high"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("in_progress"))
                .andExpect(jsonPath("$.priority").value("high"));
    }

    @Test
    void updateTicket_withUnknownIdOrEmptyBody_isRejected() throws Exception {
        updateTicket(999_999L, """
                {"status": "closed"}
                """)
                .andExpect(status().isNotFound());

        long id = createTicketAndReturnId("""
                {"title": "Nothing to change"}
                """);
        updateTicket(id, "{}")
                .andExpect(status().isBadRequest());
    }

    private ResultActions createTicket(String json) throws Exception {
        return mockMvc.perform(post(ApiPaths.TICKETS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private long createTicketAndReturnId(String json) throws Exception {
        String body = createTicket(json)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.parse(body).read("$.id", Long.class);
    }

    private ResultActions updateTicket(long id, String json) throws Exception {
        return mockMvc.perform(patch(ApiPaths.TICKETS + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
