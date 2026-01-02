package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.core.port.SmsNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//This class is used to override Sms validation in db initialization , only for spring profile dev.
@Component
@Profile("dev")
public class SmsNotificationPortDevImpl implements SmsNotificationPort {
    private static final Logger log =
            LoggerFactory.getLogger(SmsNotificationPortDevImpl.class);

    @Override
    public boolean sendSms(String e164, String content) {
        log.info("[DEV SMS] Pretending to send SMS to {}: {}",e164, content);
        return true;
    }
}
