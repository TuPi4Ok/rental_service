package ivan.prh.app.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.dto.rent.RentDtoRequest;
import ivan.prh.app.dto.user.AuthUserRequest;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.RentRepository;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.UserService;
import ivan.prh.app.util.Mapper;
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

import java.time.LocalDateTime;
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
public class AdminRentControllerTest {
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
    RentRepository rentRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    UserService userService;
    private List<User> userList = new ArrayList<>();
    private List<Transport> transportList = new ArrayList<>();
    private List<Rent> rentList = new ArrayList<>();
    private final String baseUrl = "/Admin";
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
        for (int i = 0; i < 10; i++) {
            var rent = createRent(i);
            rent = rentRepository.save(rent);
            rentList.add(rent);
        }
    }

    @AfterEach
    void afterEach() {
        rentRepository.deleteAll();
        transportRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Transport createTransport(int index) {
        var transport = Instancio.of(Transport.class)
                .ignore(Select.field(Transport::getId))
                .supply(Select.field(Transport::getUser), () -> userList.get(index + 1))
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

    private Rent createRent(int index) {
        var rent = Instancio.of(Rent.class)
                .ignore(Select.field(Rent::getId))
                .supply(Select.field(Rent::getUser), () -> userList.get(index))
                .supply(Select.field(Rent::getTransport), () -> transportList.get(index))
                .supply(Select.field(Rent::getPriceType), () -> "Minutes")
                .supply(Select.field(Rent::getTimeEnd), () -> null)
                .create();
        return rent;
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
    void testGetRentPositive() throws Exception {

        var request = get(baseUrl + "/Rent/" + rentList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(resultRent).isNotNull();
        assertThat(resultRent).isEqualTo(rentList.get(1));
    }

    @Test
    void testGetRentHistoryPositive() throws Exception {

        var request = get(baseUrl + "/UserHistory/" + userList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Rent> resultRent = (List<Rent>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, Rent.class))
                .toList();

        assertThat(resultRent).isNotNull();
        assertThat(resultRent.get(0)).isEqualTo(rentList.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> resultRent.get(1));
    }

    @Test
    void testGetTransportRentHistoryPositive() throws Exception {

        var request = get(baseUrl + "/TransportHistory/" + transportList.get(1).getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Rent> resultRent = (List<Rent>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, Rent.class))
                .toList();

        assertThat(resultRent).isNotNull();
        assertThat(resultRent.get(0)).isEqualTo(rentList.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> resultRent.get(1));
    }

    @Test
    void testCreateRentPositive() throws Exception {
        var rentDtoRequest = Instancio.of(RentDtoRequest.class)
                .supply(Select.field(RentDtoRequest::getUserId), () -> userList.get(1).getId())
                .supply(Select.field(RentDtoRequest::getTransportId), () -> transportList.get(1).getId())
                .supply(Select.field(RentDtoRequest::getPriceType), () -> "Minutes")
                .supply(Select.field(RentDtoRequest::getTimeStart), () -> LocalDateTime.now().toString())
                .supply(Select.field(RentDtoRequest::getTimeEnd), () -> null)
                .create();

        var request = post(baseUrl + "/Rent")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(rentDtoRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(rentRepository.getRentById(resultRent.getId())).isNotNull();
    }

    @Test
    void testEndRentPositive() throws Exception {

        var request = post(baseUrl + "/Rent/End/" + rentList.get(4).getId() + "?lat=122&long=123")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(resultRent.getTimeEnd()).isNotNull();
        assertThat(rentRepository.getRentById(rentList.get(4).getId()).get().getTimeEnd()).isNotNull();
    }

    @Test
    void testUpdateRentPositive() throws Exception {
        var rentDtoRequest = Instancio.of(RentDtoRequest.class)
                .supply(Select.field(RentDtoRequest::getUserId), () -> userList.get(1).getId())
                .supply(Select.field(RentDtoRequest::getTransportId), () -> transportList.get(1).getId())
                .supply(Select.field(RentDtoRequest::getPriceType), () -> "Minutes")
                .supply(Select.field(RentDtoRequest::getTimeStart), () -> LocalDateTime.now().toString())
                .supply(Select.field(RentDtoRequest::getTimeEnd), () -> null)
                .create();

        var request = put(baseUrl + "/Rent/" + rentList.get(5).getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(rentDtoRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(rentRepository.getRentById(resultRent.getId()).get().getFinalPrice())
                .isEqualTo(rentDtoRequest.getFinalPrice());
    }

    @Test
    void testDeleteRentPositive() throws Exception {
        var id  = rentList.get(1).getId();
        var request = delete(baseUrl + "/Rent/" + id)
                .header("Authorization", "Bearer " + adminToken);

        mockMvc.perform(request)
                .andExpect(status().isOk());


        assertThat(rentRepository.getRentById(id).isEmpty()).isTrue();
    }

    @Test
    void testGetRentNegative() throws Exception {

        var request = get(baseUrl + "/Rent/0")
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренда с таки id не найдена");
    }

    @Test
    void testGetRentHistoryNegative() throws Exception {
        var user = createUser();
        user = userService.createNewUser(new AuthUserRequest(user.getUserName(), user.getPassword()));

        var request = get(baseUrl + "/UserHistory/" + user.getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренды у пользователя не найдены");
    }

    @Test
    void testGetTransportRentHistoryNegative() throws Exception {
        var transport = createTransport(1);
        transport = transportRepository.save(transport);

        var request = get(baseUrl + "/TransportHistory/" + transport.getId())
                .header("Authorization", "Bearer " + adminToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренды не найдены");
    }

    @Test
    void testCreateRentNegative() throws Exception {
        var rentDtoRequest = Instancio.of(RentDtoRequest.class)
                .supply(Select.field(RentDtoRequest::getUserId), () -> userList.get(1).getId())
                .supply(Select.field(RentDtoRequest::getTransportId), () -> transportList.get(0).getId())
                .supply(Select.field(RentDtoRequest::getPriceType), () -> "Minutes")
                .supply(Select.field(RentDtoRequest::getTimeStart), () -> LocalDateTime.now().toString())
                .supply(Select.field(RentDtoRequest::getTimeEnd), () -> null)
                .create();

        var request = post(baseUrl + "/Rent")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(rentDtoRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Нельзя арендовать собственный транспорт");
    }

}
