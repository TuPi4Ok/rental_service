package ivan.prh.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivan.prh.app.config.DataLoader;
import ivan.prh.app.dto.user.UserDto;
import ivan.prh.app.model.Transport;
import ivan.prh.app.model.User;
import ivan.prh.app.repository.AccountRepository;
import ivan.prh.app.util.JwtTokenUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAccountControllerTest {
	@Autowired
	JwtTokenUtils jwtTokenUtils;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	DataLoader dataLoader;
	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	ObjectMapper om;

	private User user;
	@BeforeEach
	void beforeEach() {
		user = createUser();

		var notEncodePassword = user.getPassword();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		accountRepository.save(user);
		user.setPassword(notEncodePassword);
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
	private User createUser() {
		var user = Instancio.of(User.class)
				.ignore(Select.field(User::getId))
				.supply(Select.field(User::getRoles), () -> List.of(dataLoader.getRoleUser()))
				.supply(Select.field(User::getRents), () -> List.of())
				.supply(Select.field(User::getTransports), () -> List.of())
				.create();
		return user;
	}

	@Test
	void testCreateAuthTokenPositive() throws Exception {
		var token = getAuthToken();
		assertThat(jwtTokenUtils.getUsername(token)).isEqualTo(user.getUserName());
	}


	@Test
	void testCreateNewUserPositive() throws Exception {
		var user = createUser();

		var data = new HashMap<>();
		data.put("username", user.getUserName());
		data.put("password", user.getPassword());

		var request = post("/Account/SignUp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		var result = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();

		var body = result.getResponse().getContentAsString();
		var newUser = om.readValue(body, User.class);

		assertThat(newUser.getUserName()).isEqualTo(user.getUserName());
		assertThat(newUser.getRoles()).isEqualTo(user.getRoles());
	}
	@Test
	void testGetUserPositive() throws Exception {
		var token = getAuthToken();

		var request = get("/Account/Me")
				.header("Authorization", "Bearer " + token);

		var result = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();

		var body = result.getResponse().getContentAsString();
		var resultUser = om.readValue(body, UserDto.class);

		assertThat(resultUser.getUserName()).isEqualTo(user.getUserName());
	}

	@Test
	void testUpdateUserPositive() throws Exception {
		var token = getAuthToken();

		var data = new HashMap<>();
		var newUserName = user.getUserName() + "123";
		data.put("username", newUserName);
		data.put("password", user.getPassword());

		var request = put("/Account/Update")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		var result = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andReturn();

		var body = result.getResponse().getContentAsString();
		var resultUser = om.readValue(body, UserDto.class);

		assertThat(resultUser.getUserName()).isEqualTo(newUserName);
	}

	@Test
	void testSignOutUserPositive() throws Exception {
		var token = getAuthToken();

		var request = post("/Account/SignOut")
				.header("Authorization", "Bearer " + token);

		mockMvc.perform(request)
				.andExpect(status().isOk());

		var request1 = get("/Account/Me")
				.header("Authorization", "Bearer " + token);

		mockMvc.perform(request1)
				.andExpect(status().is(401));
	}

	@Test
	void testCreateAuthTokenNegative() throws Exception {
		var data = new HashMap<>();
		data.put("username", user.getUserName() + "123");
		data.put("password", user.getPassword());

		var request = post("/Account/SignIn")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isBadRequest());

	}

	@Test
	void testCreateNewUserNegative() throws Exception {

		var data = new HashMap<>();
		data.put("username", user.getUserName());
		data.put("password", user.getPassword());

		var request = post("/Account/SignUp")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		var result = mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andReturn();

		var message = result.getResponse().getErrorMessage();

		assertThat(message).isEqualTo("Имя пользователя уже занято");
	}

	@Test
	void testGetUserNegative() throws Exception {
		var token = getAuthToken();

		var request = get("/Account/Me")
				.header("Authorization", "Bearer " + token + "123");

		mockMvc.perform(request)
				.andExpect(status().isUnauthorized());

	}

	@Test
	void testUpdateUserNegative() throws Exception {
		var token = getAuthToken();

		var data = new HashMap<>();
		var newUserName = user.getUserName();
		data.put("username", newUserName);
		data.put("password", user.getPassword());

		var request = put("/Account/Update")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		var result = mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andReturn();

		var message = result.getResponse().getErrorMessage();

		assertThat(message).isEqualTo("Имя пользователя уже занято");
	}

}
