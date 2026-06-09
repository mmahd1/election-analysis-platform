package com.team7.hboict.api;

import com.team7.hboict.dto.PartyDTO;
import com.team7.hboict.interfaces.PartyInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PartyControllerTest {

    @Mock
    private PartyInterface partyService;

    @InjectMocks
    private PartyController partyController;

    /**
     * Verifies that the controller returns the party list for an explicitly
     * requested election id.
     */
    @Test
    public void happyFlow_returnsPartiesForRequestedElection() throws Exception {
        when(partyService.getAllParties("TK2025"))
                .thenReturn(List.of(new PartyDTO("VVD", "Liberaal")));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(partyController).build();

        mockMvc.perform(get("/api/parties").param("electionId", "TK2025"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"VVD\",\"description\":\"Liberaal\"}]"));

        verify(partyService).getAllParties("TK2025");
    }

    /**
     * Verifies that the controller falls back to its default election id when
     * the request parameter is omitted by the client.
     */
    @Test
    public void unhappyFlow_usesDefaultElectionIdWhenRequestParamIsMissing() throws Exception {
        when(partyService.getAllParties("TK2025")).thenReturn(List.of());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(partyController).build();

        mockMvc.perform(get("/api/parties"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(partyService).getAllParties("TK2025");
    }

    /**
     * Verifies that multiple parties including their descriptions are
     * serialized correctly in the JSON response.
     */
    @Test
    public void happyFlow_returnsMultiplePartiesIncludingDescriptions() throws Exception {
        when(partyService.getAllParties("TK2023"))
                .thenReturn(List.of(
                        new PartyDTO("GL-PvdA", "Sociaaldemocratisch"),
                        new PartyDTO("VVD", "Liberaal")
                ));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(partyController).build();

        mockMvc.perform(get("/api/parties").param("electionId", "TK2023"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"name":"GL-PvdA","description":"Sociaaldemocratisch"},
                          {"name":"VVD","description":"Liberaal"}
                        ]
                        """));

        verify(partyService).getAllParties("TK2023");
    }
}
