package gr.hua.fitTrack.core.service.mapper;

import gr.hua.fitTrack.core.model.Progress;
import gr.hua.fitTrack.core.service.model.ProgressView;
import org.springframework.stereotype.Component;

@Component
public class ProgressMapper {

    public ProgressView toView(Progress progress) {
        return new ProgressView(
                progress.getEntryDate(),
                progress.getWeight(),
                progress.getRunningTimeSeconds(),
                progress.getBodyFatPercentage(),
                progress.getWaterIntake()
        );
    }
}

