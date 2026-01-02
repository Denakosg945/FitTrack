package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.core.port.PhoneNumberPort;
import gr.hua.fitTrack.core.port.impl.dto.PhoneNumberValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!dev")
public class PhoneNumberPortImpl implements PhoneNumberPort {
    @Value("${sms.service.url}")
    private String smsBaseUrl;

    //Boolean used for skipping phone validation step for db initialization.
    @Value("${phone.validation.enabled:true}")
    private boolean validationEnabled;

    private final RestTemplate restTemplate;

    public PhoneNumberPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public PhoneNumberValidationResult validate(final String rawPhoneNumber) {
        if (rawPhoneNumber == null) throw new NullPointerException();
        if (rawPhoneNumber.isBlank()) throw new IllegalArgumentException();


        final String url = smsBaseUrl + "/api/v1/phone-numbers/" + rawPhoneNumber + "/validations";
        final ResponseEntity<PhoneNumberValidationResult> response
                = this.restTemplate.getForEntity(url, PhoneNumberValidationResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final PhoneNumberValidationResult phoneNumberValidationResult = response.getBody();
            return phoneNumberValidationResult;
        }


        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
