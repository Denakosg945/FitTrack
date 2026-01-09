package gr.hua.fitTrack.core.service.impl;

import gr.hua.fitTrack.core.model.Appointment;
import gr.hua.fitTrack.core.repository.AppointmentRepository;
import gr.hua.fitTrack.core.service.AppointmentDataService;
import gr.hua.fitTrack.core.service.mapper.AppointmentMapper;
import gr.hua.fitTrack.core.service.model.AppointmentView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentDataServiceImpl implements AppointmentDataService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentDataServiceImpl(AppointmentRepository appointmentRepository,
                                      AppointmentMapper appointmentMapper)
    {
        if (appointmentRepository == null) throw new NullPointerException("appointmentRepository is null");
        if (appointmentMapper == null) throw new NullPointerException("appointmentMapper is null");
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppointmentView> appointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<AppointmentView> appointmentViews = appointments
                .stream()
                .map(this.appointmentMapper::toView)
                .toList();
        return appointmentViews;
    }
}
