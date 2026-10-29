package it.wat.progettoingsoft.service;

import it.wat.progettoingsoft.entities.Issue;
import it.wat.progettoingsoft.repository.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;


    public Issue findById(Integer id) {
        Optional<Issue> trovato = issueRepository.findById(id);
        if (trovato.isEmpty()) {
            throw new EntityNotFoundException("Issue non trovata: " + id);
        }
        return trovato.get();
    }

    // Immagine allegata all'issue; 404 se l'issue non esiste o non ha un'immagine.
    public byte[] getImmagine(Integer id) {
        byte[] immagine = findById(id).getImmagine();
        if (immagine == null) {
            throw new EntityNotFoundException("L'issue #" + id + " non ha un'immagine");
        }
        return immagine;
    }


    public void deleteById(Integer id) {
        issueRepository.deleteById(id);
    }
}
