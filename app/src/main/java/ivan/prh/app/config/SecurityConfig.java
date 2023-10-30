package ivan.prh.app.config;

import ivan.prh.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/Account/Me").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/Account/SignOut").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT,"/Account/Update").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/Admin/Account").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/Admin/Account/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/Admin/Account").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/Admin/Account/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/Admin/Account/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/Payment/Hesoyam/{accountId}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/Transport").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT,"/Transport/{id}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE,"/Transport/{id}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/Admin/Transport").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/Admin/Transport/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST ,"/Admin/Transport").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT,"/Admin/Transport/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/Admin/Transport/{id}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/Rent/{rentId}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/Rent/MyHistory").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET,"/Rent/TransportHistory/{transportId}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/Rent/New/{transportId}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST,"/Rent/End/{rentId}").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET ,"/Admin/Rent/{rentId}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET ,"/Admin/UserHistory/{userId}").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET ,"/Admin/TransportHistory/{transportId}").hasAnyRole("ADMIN")
                .anyRequest().permitAll().and()
                .sessionManagement(h -> h.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        httpC -> httpC.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
