package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Bug;
import it.wat.progettoingsoft.repository.BugRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;

    public List<Bug> findByAssegnatarioId(Integer utenteId) {
        return bugRepository.findByAssegnatarioId(utenteId);
    }

    public List<Bug> findAll() {
        return bugRepository.findAll();
    }

    public Bug findById(Integer id) {
        Optional<Bug> trovato = bugRepository.findById(id);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Bug non trovato: " + id);
        }
        return trovato.get();
    }

    public Bug save(Bug bug) {
        return bugRepository.save(bug);
    }

    public void deleteById(Integer id) {
        bugRepository.deleteById(id);
    }
}
