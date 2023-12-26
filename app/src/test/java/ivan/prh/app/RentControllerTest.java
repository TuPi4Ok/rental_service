package ivan.prh.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.model.Rent;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.RentRepository;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.service.RentService;
import ivan.prh.app.util.JwtTokenUtils;
import ivan.prh.app.util.Mapper;
import org.aspectj.lang.annotation.Before;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RentControllerTest {
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
    Transport transport;
    User user;
    Rent rent;
    @BeforeEach
    void beforeEach() {
        user = createUser();
        var notEncodePassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
        user.setPassword(notEncodePassword);

        transport = transportRepository.save(createTransport());
        rent = rentRepository.save(createRent());
    }

    @AfterEach
    void afterEach() {
        rentRepository.deleteAll();
        transportRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Transport createTransport() {
        var transport = Instancio.of(Transport.class)
                .ignore(Select.field(Transport::getId))
                .supply(Select.field(Transport::getUser), () -> user)
                .supply(Select.field(Transport::getRents), () -> List.of())
                .supply(Select.field(Transport::isCanBeRented), () -> false)
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

    private Rent createRent() {
        var rent = Instancio.of(Rent.class)
                .ignore(Select.field(Rent::getId))
                .supply(Select.field(Rent::getUser), () -> user)
                .supply(Select.field(Rent::getTransport), () -> transport)
                .supply(Select.field(Rent::getPriceType), () -> "Minutes")
                .supply(Select.field(Rent::getTimeEnd), () -> null)
                .create();
        return rent;
    }

    private String getAuthToken() throws Exception {
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
    void testGetRentByIdPositive() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/" + rent.getId())
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(resultRent.getId()).isEqualTo(rent.getId());
    }

    @Test
    void testGetRentHistoryPositive() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/MyHistory")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Map<String, Object>> resultRent = om.readValue(body, List.class);

        assertThat(om.convertValue(resultRent.get(0), Rent.class).getId()).isEqualTo(rent.getId());
    }

    @Test
    void testGetRentTransportHistoryPositive() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/TransportHistory/" + transport.getId())
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Rent> resultRent = (List<Rent>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, Rent.class))
                .toList();

        assertThat(resultRent).isNotNull();
        assertThat(resultRent.get(0)).isEqualTo(rent);
    }

    @Test
    void testCreateRentPositive() throws Exception {
        var user = createUser();
        var notEncodePassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
        user.setPassword(notEncodePassword);
        var transport = createTransport();
        transport.setRents(null);
        transport.setCanBeRented(true);
        transport.setUser(user);
        transportRepository.save(transport);
        var token = getAuthToken();

        var request = post("/Rent/New/" + transport.getId() + "?rentType=Minutes")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(rentRepository.getRentById(resultRent.getId()).isPresent()).isTrue();
    }

    @Test
    void testEndRentPositive() throws Exception {
        var token = getAuthToken();

        var request = post("/Rent/End/" + rent.getId() + "?lat=10&long=455")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultRent = om.readValue(body, Rent.class);

        assertThat(resultRent.getTimeEnd()).isNotNull();
        assertThat(transportRepository.getTransportById(transport.getId()).get().getLatitude()).isEqualTo(10);
        assertThat(transportRepository.getTransportById(transport.getId()).get().getLongitude()).isEqualTo(455);
    }

    @Test
    void testGetTransportByParamPositive() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/Transport?lat=10&long=455&radius=99999999&type=All")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        List<Transport> resultTransport = (List<Transport>) om.readValue(body, List.class).stream()
                .map(map -> om.convertValue(map, Transport.class))
                .toList();

        assertThat(resultTransport).isNotNull();
        assertThat(resultTransport.get(0)).isEqualTo(transport);
    }

    @Test
    void testGetRentByIdNegative() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/0")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренда не найдена");
    }


    @Test
    void testGetRentHistoryNegative() throws Exception {
        user = createUser();
        var notEncodePassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
        user.setPassword(notEncodePassword);
        var token = getAuthToken();

        var request = get("/Rent/MyHistory")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренды не найдены");
    }

    @Test
    void testGetRentTransportHistoryNegative() throws Exception {
        var token = getAuthToken();
        Transport newTransport = createTransport();
        newTransport =  transportRepository.save(newTransport);
        var request = get("/Rent/TransportHistory/" + newTransport.getId())
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренды не найдены");
    }

    @Test
    void testCreateRentNegative() throws Exception {
        var token = getAuthToken();

        var request = post("/Rent/New/" + transport.getId() + "?rentType=Minutes")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт уже арендован");

    }

    @Test
    void testEndRentNegative() throws Exception {
        var token = getAuthToken();

        rent.setTimeEnd(LocalDateTime.now());
        rentRepository.save(rent);

        var request = post("/Rent/End/" + rent.getId() + "?lat=10&long=455")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Аренда уже завершена");
    }

    @Test
    void testGetTransportByParamNegative() throws Exception {
        var token = getAuthToken();

        var request = get("/Rent/Transport?lat=10&long=455&radius=0&type=All")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт в данном радиусе не найден");
    }
}
