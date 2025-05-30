package org.quwerty.notepadserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quwerty.notepadserver.dto.AuthStructDTO;
import org.quwerty.notepadserver.dto.JwtTokenDTO;
import org.quwerty.notepadserver.dto.RegStructDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerSuccessT1() throws Exception {
        var response = mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegStructDTO.builder()
                                .email("new@user.com")
                                        .username("new_user")
                                        .password("Pass123")
                                        .build()
                                )
                        )

        ).andReturn().getResponse();
        objectMapper.readValue(response.getContentAsString(), JwtTokenDTO.class);
        assertEquals(response.getStatus(), HttpStatus.CREATED.value());
    }

    @Test
    void registerUserAlreadyExistT2() throws Exception {
        mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegStructDTO.builder()
                                        .email("exists@user.com")
                                        .username("exists_user")
                                        .password("Pass123")
                                        .build()
                                )
                        )

        ).andReturn();

        var response2 = mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegStructDTO.builder()
                                        .email("exists@user.com")
                                        .username("exists_user")
                                        .password("Pass123")
                                        .build()
                                )
                        )

        ).andReturn().getResponse();

        assertEquals(response2.getStatus(), HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void loginSuccessT3() throws Exception {
        mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegStructDTO.builder()
                                        .email("valid@user.com")
                                        .username("valid_user")
                                        .password("Pass123")
                                        .build()
                                )
                        )

        ).andReturn();
        var response = mvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AuthStructDTO.builder()
                                .username("valid_user")
                                .password("Pass123")
                                .build())
                        )
        ).andReturn();
        assertEquals(response.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    void loginBadCredentialsT4() throws Exception {
        mvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RegStructDTO.builder()
                                        .email("invalid@user.com")
                                        .username("invalid_user")
                                        .password("Pass123")
                                        .build()
                                )
                        )

        ).andReturn();
        var response = mvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AuthStructDTO.builder()
                                .username("invalid_user")
                                .password("wrong")
                                .build())
                        )
        ).andReturn();
        assertEquals(response.getResponse().getStatus(), HttpStatus.UNAUTHORIZED.value());
    }
}