package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.ClientProfile;
import gr.hua.fitTrack.core.repository.ClientProfileRepository;
import gr.hua.fitTrack.core.service.ClientDataService;
import gr.hua.fitTrack.core.service.mapper.ClientMapper;
import gr.hua.fitTrack.core.service.model.ClientView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientDataServiceImpl implements ClientDataService {


     private final ClientProfileRepository clientProfileRepository;
     private final ClientMapper clientMapper;

    public ClientDataServiceImpl(ClientProfileRepository clientProfileRepository, ClientMapper clientMapper) {
        if (clientProfileRepository == null) throw new NullPointerException("clientProfileRepository is null");
        if (clientMapper == null) throw new NullPointerException("clientMapper is null");
        this.clientProfileRepository = clientProfileRepository;
        this.clientMapper = clientMapper;
    }
    @Transactional(readOnly = true)
    @Override
    public List<ClientView> getAllClients() {
        final List<ClientProfile> clients = clientProfileRepository.findAll();
        final List<ClientView> clientViews = clients
                .stream()
                .map(this.clientMapper::converClientToClientView)
                .toList();
        return clientViews;
    }
}
