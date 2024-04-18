package com.example.cmscreditstatementservice.listener;

import com.example.cmscreditstatementservice.service.CreditAccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CreditAccountListener {

    @Value("${COM_KAFKA_CONSUMER_TOPIC}")
    private static final String KAFKA_TOPIC = "cms-credit-account-service.cms_account_dev.credit_accounts";

    @Value("${KAFKA_CONSUMER_GROUP_ID}")
    private static final String KAFKA_GROUP_ID = "cms-statement-account-spring-consumer";

    private final CreditAccountService creditAccountService;

    public CreditAccountListener(CreditAccountService creditAccountService) {
        this.creditAccountService = creditAccountService;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = KAFKA_GROUP_ID)
    public void listen(String message) {

        //System.out.println(message);
        try {
            creditAccountService.processCreditAccountMessage(message);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }


}
