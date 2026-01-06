package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerClientNotes;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.repository.TrainerClientNotesRepository;
import gr.hua.fitTrack.core.repository.TrainerProfileRepository;
import gr.hua.fitTrack.core.service.TrainerClientNotesService;
import gr.hua.fitTrack.core.service.model.TrainerClientNotesView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerClientNotesServiceImpl
        implements TrainerClientNotesService {

    private final TrainerClientNotesRepository trainerClientNotesRepository;
    private final TrainerProfileRepository trainerProfileRepository;


    public TrainerClientNotesServiceImpl(
            TrainerClientNotesRepository trainerClientNotesRepository,
            TrainerProfileRepository trainerProfileRepository
    ) {
        this.trainerClientNotesRepository = trainerClientNotesRepository;
        this.trainerProfileRepository = trainerProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerClientNotes> getNotesForTrainer(
            TrainerProfile trainer
    ) {
        return trainerClientNotesRepository.findByTrainer(trainer);
    }

    @Override
    @Transactional
    public TrainerClientNotes getOrCreate(
            TrainerProfile trainer,
            ClientProfile client
    ) {
        return trainerClientNotesRepository
                .findByTrainerAndClient(trainer, client)
                .orElseGet(() ->
                        trainerClientNotesRepository.save(
                                new TrainerClientNotes(trainer, client, "")
                        )
                );
    }

    @Override
    @Transactional
    public void updateNotes(
            Long notesId,
            String newNotes
    ) {
        TrainerClientNotes notes =
                trainerClientNotesRepository.findById(notesId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "TrainerClientNotes not found"
                                )
                        );

        notes.setNotes(newNotes);
    }

    @Override
    public List<TrainerClientNotesView>
    getNotesForTrainer(Long trainerPersonId) {

        TrainerProfile trainer =
                trainerProfileRepository
                        .findByPersonId(trainerPersonId)
                        .orElseThrow();

        return trainerClientNotesRepository
                .findByTrainer(trainer)
                .stream()
                .map(TrainerClientNotesView::from)
                .toList();
    }



}
