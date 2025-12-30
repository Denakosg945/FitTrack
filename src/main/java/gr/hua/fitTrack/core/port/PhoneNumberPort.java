package gr.hua.fitTrack.core.port;

import gr.hua.fitTrack.core.port.impl.dto.PhoneNumberValidationResult;

public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String phoneNumber);
}
