package com.amaxilatis.yeelight.controlserver.service;

import com.amaxilatis.yeelight.controlserver.util.Device;
import com.amaxilatis.yeelight.controlserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.net.Socket;

@Service
public class CommandService {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);
    private static final String CMD_TOGGLE = "{\"id\":%id,\"method\":\"toggle\",\"params\":[]}\r\n";
    private static final String CMD_ON = "{\"id\":%id,\"method\":\"set_power\",\"params\":[\"on\",\"smooth\",500]}\r\n";
    private static final String CMD_OFF = "{\"id\":%id,\"method\":\"set_power\",\"params\":[\"off\",\"smooth\",500]}\r\n";
    private static final String CMD_CT = "{\"id\":%id,\"method\":\"set_ct_abx\",\"params\":[%value, \"smooth\", 500]}\r\n";
    private static final String CMD_HSV = "{\"id\":%id,\"method\":\"set_hsv\",\"params\":[%value, 100, \"smooth\", 200]}\r\n";
    private static final String CMD_BRIGHTNESS = "{\"id\":%id,\"method\":\"set_bright\",\"params\":[%value, \"smooth\", 1000]}\r\n";
    private static final String CMD_BRIGHTNESS_SCENE = "{\"id\":%id,\"method\":\"set_bright\",\"params\":[%value, \"smooth\", 500]}\r\n";
    private static final String CMD_COLOR_SCENE = "{\"id\":%id,\"method\":\"set_scene\",\"params\":[\"cf\",1,0,\"100,1,%color,1\"]}\r\n";
    
    private int mCmdId;
    
    @Autowired
    DiscoveryService discoveryService;
    
    @PostConstruct
    public void init() {
        mCmdId = 1;
    }
    
    public Response sendToggle(final String id) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            String cmd = CMD_TOGGLE.replace("%id", String.valueOf(++mCmdId));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendOn(final String id) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            String cmd = CMD_ON.replace("%id", String.valueOf(++mCmdId));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendOff(final String id) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            String cmd = CMD_OFF.replace("%id", String.valueOf(++mCmdId));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendBrightnessUp(final String id) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            int newValue = d.getBright() + 5;
            if (newValue > 100) {
                newValue = 100;
            }
            d.setBright(newValue);
            String cmd = CMD_BRIGHTNESS.replace("%id", String.valueOf(++mCmdId)).replace("%value", String.valueOf(newValue));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendCT(final String id, final int ct) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            d.setCt(ct);
            String cmd = CMD_CT.replace("%id", String.valueOf(++mCmdId)).replace("%value", String.valueOf(d.getCt()));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendHue(final String id, final int hue) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            d.setHue(hue);
            String cmd = CMD_HSV.replace("%id", String.valueOf(++mCmdId)).replace("%value", String.valueOf(d.getHue()));
            connectAndSend(d, cmd);
            return new Response(true);
        }
        return new Response(false);
    }
    
    public Response sendBrightnessDown(final String id) {
        Device d = discoveryService.getDevice(id);
        if (d != null) {
            int newValue = d.getBright() - 5;
            if (newValue < 1) {
                sendOff(id);
            } else {
                d.setBright(newValue);
                String cmd = CMD_BRIGHTNESS.replace("%id", String.valueOf(++mCmdId)).replace("%value", String.valueOf(newValue));
                connectAndSend(d, cmd);
                return new Response(true);
            }
        }
        return new Response(false);
    }
    
    private void connectAndSend(final Device d, final String cmd) {
        try {
            String ip = d.getLocation().split("//")[1].split(":")[0];
            int port = Integer.parseInt(d.getLocation().split(":")[2]);
            
            try (Socket mSocket = new Socket(ip, port)) {
                mSocket.setKeepAlive(true);
                BufferedOutputStream mBos = new BufferedOutputStream(mSocket.getOutputStream());
                mBos.write(cmd.getBytes());
                mBos.flush();
                mBos.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
}
