package ivan.prh.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.dto.admin.AdminRequest;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.dto.user.UserDto;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.JwtTokenUtils;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminAccountControllerTest {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    DataLoader dataLoader;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper om;

    private List<User> userList = new ArrayList<>();
    private final String baseUrl = "/Admin/Account";
    private String adminToken;
    private User admin;
    @BeforeEach
    void beforeEach() throws Exception {
        accountRepository.deleteAll();
        admin = accountRepository.save(createAdmin());
        admin.setPassword("admin");
        adminToken = getAuthToken(admin);
        for (int i = 0; i < 10; i++) {
            var user = createUser();
            user = userService.createNewUser(new AuthUserRequest(user.getUserName(), user.getPassword()));
            userList.add(user);
        }
    }

    private String getAuthToken(User user) throws Exception {
        var data = new HashMap<>();
        data.put("username", user.getUserName());
        data.put("password", user.getPassword());

        var request = post("/Account/SignIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        return om.readValue(body, Map.class).get("token").toString();
    }
    private User createUser() {
        var user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getRoles), () -> List.of(dataLoader.getRoleUser()))
                .supply(Select.field(User::getRents), () -> List.of())
                .supply(Select.field(User::getTransports), () -> List.of())
                .create();
        return user;
    }

    private User createAdmin() {
        var admin = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getPassword), () -> "$2a$10$OOh2OpOoEmrHf59JlRurHuzfFzFxfCo4TkmR8QEO5MiCQJxbhdunC")
                .supply(Select.field(User::getRoles), () -> List.of(dataLoader.getRoleAdmin()))
                .supply(Select.field(User::getRents), () -> List.of())
                .supply(Select.field(User::getTransports), () -> List.of())
                .create();
        return admin;
    }

    @Test
    void testGetUsersPositive() throws Exception {

        var request = get(baseUrl + "?start=4&count=3")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<User> resultUsers = (List<User>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, User.class))
                .toList();

        assertThat(resultUsers.get(0)).isEqualTo(userList.get(3));
        assertThat(resultUsers.get(2)).isEqualTo(userList.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> resultUsers.get(3));
    }

    @Test
    void testGetUserPositive() throws Exception {

        var request = get(baseUrl + "/" + userList.get(0).getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultUser = om.readValue(body, User.class);

        assertThat(resultUser).isEqualTo(userList.get(0));
    }

    @Test
    void testCreateUserPositive() throws Exception {
        var adminRequest = Instancio.of(AdminRequest.class).create();
        var request = post(baseUrl)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultUser = om.readValue(body, User.class);

        assertThat(accountRepository.findUserByUserName(adminRequest.getUsername())).isNotNull();
        assertThat(resultUser.getUserName()).isEqualTo(adminRequest.getUsername());
    }

    @Test
    void testUpdateUserPositive() throws Exception {
        var adminRequest = Instancio.of(AdminRequest.class).create();
        var request = put(baseUrl + "/" + userList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultUser = om.readValue(body, User.class);

        assertThat(accountRepository.findUserByUserName(adminRequest.getUsername()).get().getId()).isEqualTo(userList.get(1).getId());
        assertThat(resultUser.getUserName()).isEqualTo(adminRequest.getUsername());
    }

    @Test
    void testDeleteUserPositive() throws Exception {
        var request = delete(baseUrl + "/" + userList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(accountRepository.findUserById(userList.get(1).getId()).isEmpty()).isTrue();
    }

    @Test
    void testGetUsersNegative() throws Exception {

        var request = get(baseUrl + "?start=11&count=3")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Пользователи не найдены");
    }
}
