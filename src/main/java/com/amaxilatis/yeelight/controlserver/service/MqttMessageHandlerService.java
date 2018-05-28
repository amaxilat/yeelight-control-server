package com.amaxilatis.yeelight.controlserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

@Service
public class MqttMessageHandlerService implements MessageHandler {
    
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttMessageHandlerService.class);
    
    @Autowired
    CommandService commandService;
    
    private long lastCommand = 0;
    
    @Override
    public void handleMessage(Message<?> message) {
        try {
            String pString = message.getPayload().toString();
            LOGGER.info("got message : {} / {}", message.getHeaders().get("topic"), pString);
            if (System.currentTimeMillis() - lastCommand < 1000) {
                LOGGER.info("ignoring command");
            } else {
                if (pString.equals("on")) {
                    commandService.sendOn(null);
                } else if (pString.equals("off")) {
                    commandService.sendOff(null);
                } else if (pString.equals("up")) {
                    commandService.sendBrightnessUp(null);
                } else if (pString.equals("down")) {
                    commandService.sendBrightnessDown(null);
                }
                lastCommand = System.currentTimeMillis();
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
    
}
