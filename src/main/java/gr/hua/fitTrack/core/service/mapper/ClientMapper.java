package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.service.model.ClientView;
import gr.hua.fitTrack.core.service.model.ProgressView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMapper {

    private final ProgressMapper progressMapper;

    public ClientMapper(ProgressMapper progressMapper) {
        this.progressMapper = progressMapper;
    }

    public ClientView converClientToClientView(ClientProfile client) {

        List<ProgressView> progressViews =
                client.getProgress() == null
                        ? List.of()
                        : client.getProgress()
                        .stream()
                        .map(progressMapper::toView)
                        .toList();

        return new ClientView(
                client.getId(),
                client.getPerson().getFirstName(),
                client.getPerson().getLastName(),
                client.getWeight(),
                client.getHeight(),
                client.getGoals(),
                progressViews
        );
    }
}

