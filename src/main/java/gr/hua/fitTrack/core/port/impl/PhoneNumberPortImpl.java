package gr.hua.fitTrack.core.port.impl;

public class PhoneNumberPort {

    private final RestTemplate restTemplate;

    public PhoneNumberPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public PhoneNumberValidationResult validate(final String rawPhoneNumber) {
        if (rawPhoneNumber == null) throw new NullPointerException();
        if (rawPhoneNumber.isBlank()) throw new IllegalArgumentException();

        // HTTP Request
        // --------------------------------------------------

        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url = baseUrl + "/api/v1/phone-numbers/" + rawPhoneNumber + "/validations";
        final ResponseEntity<PhoneNumberValidationResult> response
                = this.restTemplate.getForEntity(url, PhoneNumberValidationResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final PhoneNumberValidationResult phoneNumberValidationResult = response.getBody();
            if (phoneNumberValidationResult == null) throw new NullPointerException();
            return phoneNumberValidationResult;
        }

        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
