package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.service.model.*;
import jakarta.validation.Valid;

public interface ClientService {
    CreateClientResult createClientProfile(final CreateClientRequest createClientRequest, final boolean notify);
    default  CreateClientResult createClientProfile(final CreateClientRequest createClientRequest){
        return this.createClientProfile(createClientRequest, false);
    }

    ClientView getClientProfileByPersonId(Long testClientPersonId);


    void updateGoals(String email, float weightGoal, int runningTimeGoal, int bodyFatPercentageGoal);

    void addProgress(String email,EditProgressForm form);

    ClientProfile getByEmail(String email);

    ClientView getViewByEmail(String email);


}
