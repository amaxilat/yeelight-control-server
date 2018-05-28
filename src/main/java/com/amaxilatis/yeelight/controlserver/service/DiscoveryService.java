package com.amaxilatis.yeelight.controlserver.service;

import com.amaxilatis.yeelight.controlserver.util.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DiscoveryService {
    
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryService.class);
    private static final String MESSAGE = "M-SEARCH * HTTP/1.1\r\n" + "HOST:%s:%d\r\n" + "MAN:\"ssdp:discover\"\r\n" + "ST:wifi_bulb\r\n";
    
    @Value("${upnp.ip:239.255.255.250}")
    private String udpHost;
    @Value("${upnp.ip:1982}")
    private int udpPort = 1982;
    
    DatagramSocket mDSocket;
    final List<HashMap<String, String>> mDeviceList = new ArrayList<>();
    final Set<Device> devices = new HashSet<>();
    
    @PostConstruct
    public void init() {
        LOGGER.debug("init");
        mDeviceList.clear();
        setup();
    }
    
    private void setup() {
        try {
            mDSocket = new DatagramSocket();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        new Thread(() -> {
            try {
                while (true) {
                    final byte[] buf = new byte[1024];
                    final DatagramPacket dpRecv = new DatagramPacket(buf, buf.length);
                    LOGGER.debug("waiting for data...");
                    mDSocket.receive(dpRecv);
                    final byte[] bytes = dpRecv.getData();
                    final StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < dpRecv.getLength(); i++) {
                        // parse /r
                        if (bytes[i] == 13) {
                            continue;
                        }
                        buffer.append((char) bytes[i]);
                    }
                    LOGGER.debug("socket got message:" + buffer.toString());
                    if (!buffer.toString().contains("yeelight")) {
                        return;
                    }
                    final String[] infos = buffer.toString().split("\n");
                    final HashMap<String, String> bulbInfo = new HashMap<>();
                    for (String str : infos) {
                        int index = str.indexOf(":");
                        if (index == -1) {
                            continue;
                        }
                        final String title = str.substring(0, index);
                        final String value = str.substring(index + 1);
                        bulbInfo.put(title, value);
                    }
                    if (!hasAdd(bulbInfo)) {
                        mDeviceList.add(bulbInfo);
                        try {
                            final Device d = new Device(bulbInfo.get("id").trim(), bulbInfo.get("Location").trim(), bulbInfo.get("Server").trim(), bulbInfo.get("model").trim(), Integer.parseInt(bulbInfo.get("fw_ver").trim()), bulbInfo.get("support").trim(), Boolean.parseBoolean(bulbInfo.get("power").trim()), Integer.parseInt(bulbInfo.get("bright").trim()), Integer.parseInt(bulbInfo.get("color_mode").trim()), Integer.parseInt(bulbInfo.get("ct").trim()), Integer.parseInt(bulbInfo.get("rgb").trim()), Integer.parseInt(bulbInfo.get("hue").trim()), Integer.parseInt(bulbInfo.get("sat").trim()), bulbInfo.get("name").trim());
                            LOGGER.debug(d.toString());
                            devices.add(d);
                        } catch (Exception e) {
                            LOGGER.error(e.getLocalizedMessage(), e);
                        }
                    }
                    
                }
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
            
        }).start();
    }
    
    @Scheduled(fixedRate = 30000)
    public void search() {
        if (mDSocket != null) {
            try {
                LOGGER.info("searching...");
                final String upnpMessage = String.format(MESSAGE, udpHost, udpPort);
                final DatagramPacket dpSend = new DatagramPacket(upnpMessage.getBytes(), upnpMessage.getBytes().length, InetAddress.getByName(udpHost), udpPort);
                mDSocket.send(dpSend);
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
    }
    
    public Device getDevice(final String id) {
        if (id == null) {
            return devices.iterator().next();
        }
        for (final Device device : devices) {
            if (id.equals(device.getId())) {
                return device;
            }
        }
        return null;
    }
    
    public Set<Device> getDevices() {
        return devices;
    }
    
    private boolean hasAdd(final HashMap<String, String> bulbinfo) {
        for (final HashMap<String, String> info : mDeviceList) {
            LOGGER.debug("location params = " + bulbinfo.get("Location"));
            if (info.get("Location").equals(bulbinfo.get("Location"))) {
                return true;
            }
        }
        return false;
    }
}
