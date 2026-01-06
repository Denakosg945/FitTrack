package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.model.TrainerClientNotes;
import gr.hua.fitTrack.core.model.TrainerProfile;
import gr.hua.fitTrack.core.service.model.TrainerClientNotesView;

import java.util.List;

public interface TrainerClientNotesService {

    List<TrainerClientNotes> getNotesForTrainer(
            TrainerProfile trainer
    );

    List<TrainerClientNotesView>
    getNotesForTrainer(Long trainerPersonId);



    TrainerClientNotes getOrCreate(
            TrainerProfile trainer,
            ClientProfile client
    );

    void updateNotes(
            Long notesId,
            String newNotes
    );
}
