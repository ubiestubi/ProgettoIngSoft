package it.wat.progettoingsoft.security;

import it.wat.progettoingsoft.costanti.Ruolo;
import it.wat.progettoingsoft.entities.Utente;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * L'utente loggato per Spring Security. Viene salvato nella sessione (dentro il SecurityContext)
 * e i controller lo ricevono con @AuthenticationPrincipal. Contiene l'id, così i controller
 * possono passarlo ai service senza rileggere l'utente dal DB.
 * L'autorità è "ROLE_" + ruolo, così @PreAuthorize("hasRole('AMMINISTRATORE')") funziona.
 */
@Getter
public class UtenteAutenticato implements UserDetails {

    private final Integer id;
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;
    private final Ruolo ruolo;

    public UtenteAutenticato(Utente utente) {
        this.id = utente.getId();
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        this.email = utente.getEmail();
        this.password = utente.getPassword();
        this.ruolo = utente.getRuolo();
    }

    // Per Spring Security lo "username" è l'email.
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> autorità = new ArrayList<>();
        autorità.add(new SimpleGrantedAuthority("ROLE_" + ruolo.name()));
        return autorità;
    }
}
