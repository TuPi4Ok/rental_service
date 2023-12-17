package ivan.prh.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.repository.TransportRepository;
import ivan.prh.app.util.Mapper;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransportControllerTest {
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
    Transport transport;
    User user;
    @BeforeEach
    void beforeEach() {
        user = createUser();
        var notEncodePassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
        user.setPassword(notEncodePassword);

        transport = transportRepository.save(createTransport());
    }

    private Transport createTransport() {
        var transport = Instancio.of(Transport.class)
                .ignore(Select.field(Transport::getId))
                .supply(Select.field(Transport::getUser), () -> user)
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
    void testGetTransportPositive() throws Exception {

        var request = get("/Transport/" + transport.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultTransport = om.readValue(body, Transport.class);

        assertThat(resultTransport.getId()).isEqualTo(transport.getId());
    }

    @Test
    void testCreateTransportPositive() throws Exception {
        Transport newTransport = createTransport();
        var token = getAuthToken();

        var request = post("/Transport")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(newTransport)));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultTransport = om.readValue(body, Transport.class);

        assertThat(resultTransport.getModel()).isEqualTo(newTransport.getModel());
    }

    @Test
    void testUpdateTransportPositive() throws Exception {
        Transport newTransport = createTransport();
        var token = getAuthToken();

        var request = put("/Transport/" + transport.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(newTransport)));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var resultTransport = om.readValue(body, Transport.class);

        assertThat(resultTransport.getModel()).isEqualTo(newTransport.getModel());
        assertThat(transportRepository.getTransportById(transport.getId()).get().getModel())
                .isEqualTo(newTransport.getModel());
    }

    @Test
    void testDeleteTransportPositive() throws Exception {
        var token = getAuthToken();

        var request = delete("/Transport/" + transport.getId())
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(request)
                .andExpect(status().isOk());
        assertThat(transportRepository.getTransportById(transport.getId()).isPresent()).isFalse();
    }

    @Test
    void testGetTransportNegative() throws Exception {

        var request = get("/Transport/0");

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт с данным id не найден");
    }

    @Test
    void testUpdateTransportNegative() throws Exception {
        Transport newTransport = createTransport();
        var id = transport.getId();
        beforeEach();
        var token = getAuthToken();

        var request = put("/Transport/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mapper.map(newTransport)));

        var result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Недостаточно прав");
    }

    @Test
    void testDeleteTransportNegative() throws Exception {
        var token = getAuthToken();

        var request = delete("/Transport/0")
                .header("Authorization", "Bearer " + token);

        var result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        var message = result.getResponse().getErrorMessage();

        assertThat(message).isEqualTo("Транспорт с данным id не найден");
        assertThat(transportRepository.getTransportById(transport.getId()).isPresent()).isTrue();
    }
}
