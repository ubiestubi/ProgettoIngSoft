package it.wat.progettoingsoft.config;

import it.wat.progettoingsoft.security.GestoreErroriSicurezza;
import it.wat.progettoingsoft.security.UtenteDetailsService;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Configurazione di Spring Security: autenticazione con sessione HTTP (cookie JSESSIONID), permessi con
 * @PreAuthorize("hasRole(...)") sui metodi dei controller (@EnableMethodSecurity).
 * Qui la filter chain fa solo: login pubblico, tutto il resto richiede un utente loggato (accesso negato
 * per default, anche sui metodi senza @PreAuthorize).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Hash delle password (creaUtente) e verifica al login.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Verifica email e password al login: carica l'utente con UtenteDetailsService e confronta l'hash BCrypt.
    @Bean
    public AuthenticationManager authenticationManager(UtenteDetailsService utenteDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(utenteDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Dove si salva l'utente loggato tra una richiesta e l'altra: nella sessione HTTP.
    // È un bean perché lo usa anche AutenticazioneController.login per salvarlo dopo l'autenticazione.
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SecurityContextRepository securityContextRepository,
                                                   GestoreErroriSicurezza gestoreErrori) {
        http
                // Il client è JavaFX, non un browser: nessuna pagina di terzi può fargli inviare il cookie
                // di sessione a sua insaputa, quindi la protezione CSRF non serve.
                .csrf(csrf -> csrf.disable())
                // Niente pagina di login né HTTP Basic: il login è POST /api/auth/login con corpo JSON.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // Senza questo, ogni 401 creerebbe una sessione vuota per "ricordare" la richiesta.
                .requestCache(cache -> cache.disable())
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // Pagina di errore interna di Spring (500): altrimenti diventerebbe un 401.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .anyRequest().authenticated())
                // Logout gestito da Spring Security: invalida la sessione e risponde 204,
                // anche se nessuno era loggato.
                .logout(logout -> logout
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults()
                                .matcher(HttpMethod.POST, "/api/auth/logout"))
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .exceptionHandling(errori -> errori
                        .authenticationEntryPoint(gestoreErrori)
                        .accessDeniedHandler(gestoreErrori));
        return http.build();
    }
}
