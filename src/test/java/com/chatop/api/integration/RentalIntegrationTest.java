package com.chatop.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RentalIntegrationTest {

    private static Path storageDir;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void setUpDir() throws IOException {
        storageDir = tempDir.resolve("storage");
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("app.file-storage.location", () -> storageDir.toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndUpdateRentalFlow() throws Exception {
        String registerPayload = "{" +
                "\"email\":\"owner@example.com\"," +
                "\"name\":\"Owner\"," +
                "\"password\":\"Password123!\"}";

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode registerJson = objectMapper.readTree(registerResponse);
        String token = registerJson.get("auth").get("token").asText();
        assertThat(token).isNotBlank();

        MockMultipartFile picture = new MockMultipartFile("picture", "home.jpg", "image/jpeg", "image-data".getBytes());

        String rentalResponse = mockMvc.perform(multipart("/api/rentals")
                        .file(picture)
                        .param("name", "Charming house")
                        .param("surface", "80")
                        .param("price", "1500")
                        .param("description", "A beautiful house")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Charming house"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode rentalJson = objectMapper.readTree(rentalResponse);
        long rentalId = rentalJson.get("id").asLong();

        mockMvc.perform(get("/api/rentals/" + rentalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalId))
                .andExpect(jsonPath("$.pictureUrl").isNotEmpty());

        MockMultipartFile newPicture = new MockMultipartFile("picture", "new.jpg", "image/jpeg", "new-data".getBytes());

        mockMvc.perform(multipart("/api/rentals/" + rentalId)
                        .file(newPicture)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("name", "Updated house")
                        .param("surface", "90")
                        .param("price", "1600")
                        .param("description", "Updated description")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated house"))
                .andExpect(jsonPath("$.surface").value(90));

        mockMvc.perform(get("/api/rentals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Updated house"));
    }
}
