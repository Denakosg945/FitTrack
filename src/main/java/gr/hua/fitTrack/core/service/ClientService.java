package gr.hua.fitTrack.core.service;

import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.CreateClientRequest;
import gr.hua.fitTrack.core.service.model.CreateClientResult;
import gr.hua.fitTrack.core.service.model.EditProgressForm;

public interface ClientService {
    CreateClientResult createClientProfile(final CreateClientRequest createClientRequest, final boolean notify);
    default  CreateClientResult createClientProfile(final CreateClientRequest createClientRequest){
        return this.createClientProfile(createClientRequest, false);
    }

    ClientView getClientProfileByPersonId(Long testClientPersonId);


    void updateGoalsForTestClient(float weightGoal, int runningTimeGoal, int bodyFatPercentageGoal);

    void addProgressForTestClient(EditProgressForm form);

}
