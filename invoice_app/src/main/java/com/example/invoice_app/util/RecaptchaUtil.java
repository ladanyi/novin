package com.example.invoice_app.util;

import com.example.invoice_app.payload.response.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class RecaptchaUtil {

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.verify.url}")
    private String recaptchaVerifyUrl;

    public boolean verifyRecaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = String.format("%s?secret=%s&response=%s", recaptchaVerifyUrl, recaptchaSecret, recaptchaResponse);
        RecaptchaResponse response = restTemplate.postForObject(requestUrl, null, RecaptchaResponse.class);
        return response != null && response.isSuccess();
    }
}
