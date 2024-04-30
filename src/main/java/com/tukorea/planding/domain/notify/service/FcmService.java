package com.tukorea.planding.domain.notify.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.tukorea.planding.domain.notify.dto.FcmToClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    @Value("${firebase.key-path}")
    private String fcmKeyPath;

    private FirebaseMessaging instance;

    @PostConstruct
    public void getFcmCredential() {
        try {
            InputStream refreshToken = new ClassPathResource(fcmKeyPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken)).build();

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            this.instance = FirebaseMessaging.getInstance(firebaseApp);
            log.info("Fcm Setting Completed");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void pushAlarm(FcmToClientRequest fcmToClientRequest) throws FirebaseMessagingException {
        Message message = getMessage(fcmToClientRequest);
        sendMessage(message);
    }

    public String sendMessage(Message message) throws FirebaseMessagingException {
        return this.instance.send(message);
    }

    public Message getMessage(FcmToClientRequest fcmToClientRequest) {
        return Message
                .builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(fcmToClientRequest.groupName() + "메시지가 도착하였습니다.")
                                .setBody(fcmToClientRequest.message())
                                .build()
                )
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setNotification(
                                        AndroidNotification.builder()
                                                .setTitle(fcmToClientRequest.groupName() + "메시지가 도착하였습니다.")
                                                .setBody(fcmToClientRequest.message())
                                                .setClickAction("push_click")
                                                .build()
                                )
                                .build()
                )
                .putData("type", fcmToClientRequest.type().name())
                .putData("url", fcmToClientRequest.url())
                .build();
    }

}
