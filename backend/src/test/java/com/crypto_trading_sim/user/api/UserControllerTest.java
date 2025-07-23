package com.crypto_trading_sim.user.api;

import com.crypto_trading_sim.config.WithMockCustomUser;
import com.crypto_trading_sim.user.domain.dto.UserDto;
import com.crypto_trading_sim.user.facade.UserFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFacade userFacade;

    @Test
    @WithMockCustomUser(username = "testuser_user")
    void getUser_whenUserExists_shouldReturnUserDto() throws Exception {
        UserDto userDto = new UserDto("testuser_user", "Test", "User");
        when(userFacade.getUserByUsername("testuser_user")).thenReturn(userDto);

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser_user"));
    }
}