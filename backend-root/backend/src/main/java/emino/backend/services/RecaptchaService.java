package emino.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import emino.backend.config.RecaptchaSettings;
import emino.backend.models.RecaptchaVerificationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecaptchaService {
    
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify"; 

    private final RecaptchaSettings recaptchaSettings;
    private final RestTemplate restTemplate;

    public boolean verifyRecaptcha(String recaptchaResponse) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            return false;
        }

        String url = String.format("%s?secret=%s&response=%s",
            RECAPTCHA_VERIFY_URL,
            recaptchaSettings.getSecret(),
            recaptchaResponse
        );

        try {
            RecaptchaVerificationResponse response = restTemplate.postForObject(url, null, RecaptchaVerificationResponse.class);

            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
}
