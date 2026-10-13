package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Notifica;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.repository.NotificaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificaService {

    private final NotificaRepository notificaRepository;

    public Notifica creaNotifica(UtenteNormale destinatario, String testo) {
        Notifica notifica = new Notifica();
        notifica.setDestinatario(destinatario);
        notifica.setTesto(testo);
        notifica.setData(LocalDateTime.now(ZoneId.of("Europe/Rome")));
        // Una notifica appena creata non è ancora stata letta.
        notifica.setLetta(false);
        return notificaRepository.save(notifica);
    }

    public List<Notifica> findByDestinatarioId(Integer destinatarioId) {
        return notificaRepository.findByDestinatarioIdOrderByDataDescIdDesc(destinatarioId);
    }

    public List<Notifica> findAll() {
        return notificaRepository.findAll();
    }

    public Notifica findById(Integer id) {
        Optional<Notifica> trovato = notificaRepository.findById(id);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Notifica non trovata: " + id);
        }
        return trovato.get();
    }

    public Notifica save(Notifica notifica) {
        return notificaRepository.save(notifica);
    }

    public void deleteById(Integer id) {
        notificaRepository.deleteById(id);
    }
}
