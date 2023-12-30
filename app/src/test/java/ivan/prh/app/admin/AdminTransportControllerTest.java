package ivan.prh.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.dto.transport.AdminTransportDto;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.RentRepository;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.Mapper;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.*;
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
public class AdminTransportControllerTest {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    DataLoader dataLoader;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TransportRepository transportRepository;
    @Autowired
    ObjectMapper om;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    UserService userService;
    private List<User> userList = new ArrayList<>();
    private List<Transport> transportList = new ArrayList<>();
    private final String baseUrl = "/Admin/Transport";
    private String adminToken;
    private User admin;

    @BeforeEach
    void beforeEach() throws Exception {
        accountRepository.deleteAll();
        admin = accountRepository.save(createAdmin());
        admin.setPassword("admin");
        userList.add(admin);
        adminToken = getAuthToken(admin);
        for (int i = 0; i < 10; i++) {
            var user = createUser();
            user = userService.createNewUser(new AuthUserRequest(user.getUserName(), user.getPassword()));
            userList.add(user);
        }
        for (int i = 0; i < 10; i++) {
            var transport = createTransport(i);
            transport = transportRepository.save(transport);
            transportList.add(transport);
        }
    }

    @AfterEach
    void afterEach() {
        transportRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Transport createTransport(int index) {
        var transport = Instancio.of(Transport.class)
                .ignore(Select.field(Transport::getId))
                .supply(Select.field(Transport::getUser), () -> userList.get(index))
                .supply(Select.field(Transport::getRents), () -> List.of())
                .supply(Select.field(Transport::getTransportType), () -> "Car")
                .create();
        return transport;
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

    @Test
    void testGetTransportsPositive() throws Exception {

        var request = get(baseUrl + "?start=4&count=3")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Transport> resultTransport = (List<Transport>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, Transport.class))
                .toList();

        assertThat(resultTransport.get(0)).isEqualTo(transportList.get(3));
        assertThat(resultTransport.get(2)).isEqualTo(transportList.get(5));
        assertThrows(IndexOutOfBoundsException.class, () -> resultTransport.get(3));
    }

    @Test
    void testGetTransportPositive() throws Exception {

        var request = get(baseUrl + "/" + transportList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultTransport = om.readValue(body, Transport.class);

        assertThat(resultTransport).isNotNull();
        assertThat(resultTransport).isEqualTo(transportList.get(1));
    }

    @Test
    void testCreateTransportPositive() throws Exception {
        var adminTransportDto = Instancio.of(AdminTransportDto.class)
                .supply(Select.field(AdminTransportDto::getOwnerId), () -> userList.get(1).getId())
                .supply(Select.field(AdminTransportDto::getTransportType), () -> "Car")
                .create();

        var request = post(baseUrl)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminTransportDto));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultTransport = om.readValue(body, Transport.class);

        assertThat(transportRepository.getTransportById(resultTransport.getId())).isNotNull();
    }

    @Test
    void testUpdateTransportPositive() throws Exception {
        var adminTransportDto = Instancio.of(AdminTransportDto.class)
                .supply(Select.field(AdminTransportDto::getOwnerId), () -> userList.get(1).getId())
                .supply(Select.field(AdminTransportDto::getTransportType), () -> "Car")
                .create();

        var id  = transportList.get(1).getId();
        var request = put(baseUrl + "/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminTransportDto));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(transportRepository.getTransportById(id)).isNotNull();
        assertThat(transportRepository.getTransportById(id).get().getColor()).isEqualTo(adminTransportDto.getColor());
    }

    @Test
    void testDeleteTransportPositive() throws Exception {
        var id  = transportList.get(1).getId();
        var request = delete(baseUrl + "/" + id)
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(request)
                .andExpect(status().isOk());


        assertThat(transportRepository.getTransportById(id).isEmpty()).isTrue();
    }

    @Test
    void testGetTransportsNegative() throws Exception {

        var request = get(baseUrl + "?start=11&count=3")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорты не найдены");
    }

    @Test
    void testGetTransportNegative() throws Exception {

        var request = get(baseUrl + "/0")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт не найден");
    }

    @Test
    void testUpdateTransportNegative() throws Exception {
        var adminTransportDto = Instancio.of(AdminTransportDto.class)
                .supply(Select.field(AdminTransportDto::getOwnerId), () -> userList.get(1).getId())
                .supply(Select.field(AdminTransportDto::getTransportType), () -> "Car")
                .create();

        var id  = transportList.get(1).getId();
        var request = put(baseUrl + "/0")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminTransportDto));

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();


        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт с таким id не найден");
    }

}
