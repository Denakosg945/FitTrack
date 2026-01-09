package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.service.model.*;

public interface ClientService {
    CreateClientResult createClientProfile(final CreateClientRequest createClientRequest, final boolean notify);
    default  CreateClientResult createClientProfile(final CreateClientRequest createClientRequest){
        return this.createClientProfile(createClientRequest, false);
    }

    ClientView getClientProfileByPersonId(Long testClientPersonId);


    void updateGoalsForTestClient(float weightGoal, int runningTimeGoal, int bodyFatPercentageGoal);

    void addProgressForTestClient(EditProgressForm form);

    ClientProfile getByEmail(String email);


}
