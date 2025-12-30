package gr.hua.fitTrack.core.port.impl;

import gr.hua.fitTrack.config.RestApiClientConfig;
import gr.hua.fitTrack.core.port.impl.dto.SendSmsRequest;
import gr.hua.fitTrack.core.port.impl.dto.SendSmsResult;
import gr.hua.fitTrack.core.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import gr.hua.fitTrack.core.port.SmsNotificationPort;


@Service
public class SmsNotificationPortImpl implements SmsNotificationPort {
    private final PersonRepository personRepository;
    @Value("${sms.service.url}")
    private String smsBaseUrl;

    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationPortImpl.class);

    public SmsNotificationPortImpl(final RestTemplate restTemplate, PersonRepository personRepository) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
        this.personRepository = personRepository;
    }


    @Override
    public boolean sendSms(String e164, String content) {
        //Check if the contents of the variables are null or empty
        if(e164 == null) throw new NullPointerException("Parameter e164 is null");
        if(content == null) throw new NullPointerException("Parameter content is null");
        if(e164.isEmpty()) throw new IllegalArgumentException("Parameter e164 is empty");
        if(content.isEmpty()) throw new IllegalArgumentException("Parameter content is empty");

        //Check if phone number is valid

        if (e164.startsWith("+30692") || e164.startsWith("+30690000")) {
            LOGGER.warn("Not allocated E164 {}. Aborting...", e164);
            personRepository.deleteByPhoneNumber(e164);
            return true;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final SendSmsRequest body = new SendSmsRequest(e164,content);

        final String baseUrl = smsBaseUrl;
        final String url = baseUrl + "/api/v1/sms";
        final HttpEntity<SendSmsRequest> request = new HttpEntity<>(body, headers);
        final ResponseEntity<SendSmsResult> response = this.restTemplate.postForEntity(url,request, SendSmsResult.class);


        if(response.getStatusCode().is2xxSuccessful()) {
            final SendSmsResult result = response.getBody();
            if(result == null) throw new NullPointerException("Result is null");
            return result.sent();
        }

        personRepository.deleteByPhoneNumber(e164);
        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
