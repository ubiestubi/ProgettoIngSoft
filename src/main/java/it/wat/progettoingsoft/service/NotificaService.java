package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Notifica;
import it.wat.progettoingsoft.entities.UtenteNormale;
import it.wat.progettoingsoft.repository.NotificaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


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


    public void deleteById(Integer id) {
        notificaRepository.deleteById(id);
    }
}
