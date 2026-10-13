package it.wat.progettoingsoft.security;

import it.wat.progettoingsoft.entities.Utente;
import it.wat.progettoingsoft.repository.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Carica l'utente per email al login (lo chiama l'AuthenticationManager di SecurityConfig).
 * UsernameNotFoundException viene trasformata da Spring Security in BadCredentialsException:
 * email inesistente e password sbagliata danno lo stesso errore, per non rivelare quali email
 * sono registrate.
 */
@Service
@RequiredArgsConstructor
public class UtenteDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utente> trovato = utenteRepository.findByEmail(email);
        if (trovato.isEmpty()) {
            throw new UsernameNotFoundException("Utente non trovato: " + email);
        }
        return new UtenteAutenticato(trovato.get());
    }
}
