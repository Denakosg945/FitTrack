package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.core.port.PhoneNumberPort;
import gr.hua.fitTrack.core.port.impl.dto.PhoneNumberValidationResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//this class is used to override phone validation for db initialization only in dev spring profile.
@Component
@Profile("dev")
public class PhoneNumberPortDevImpl  implements PhoneNumberPort {
    @Override
    public PhoneNumberValidationResult validate(String phone) {
        return new PhoneNumberValidationResult(
                phone,
                true,
                "mobile",
                phone
        );
    }
}
