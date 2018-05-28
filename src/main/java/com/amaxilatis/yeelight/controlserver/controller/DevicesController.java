package com.amaxilatis.yeelight.controlserver.controller;

import com.amaxilatis.yeelight.controlserver.service.CommandService;
import com.amaxilatis.yeelight.controlserver.service.DiscoveryService;
import com.amaxilatis.yeelight.controlserver.util.Device;
import com.amaxilatis.yeelight.controlserver.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Api(tags = "Devices API", description = "Device list and actuation operations")
@RestController
public class DevicesController {
    
    @Autowired
    DiscoveryService discoveryService;
    @Autowired
    CommandService commandService;
    
    @ApiOperation(value = "Retrieve a collection of devices discovered", nickname = "retrieveDevices", response = Device[].class)
    @GetMapping("/v1/devices")
    public Set<Device> getDevices() {
        return discoveryService.getDevices();
    }
    
    @ApiOperation(value = "Toggle the state of a device", nickname = "toggleDevice", response = Response.class)
    @PostMapping("/v1/device/toggle/{id}")
    public Response doToggle(@PathVariable("id") final String id) {
        return commandService.sendToggle(id);
    }
    
    @ApiOperation(value = "Switch a device ON", nickname = "setOn", response = Response.class)
    @PostMapping("/v1/device/on/{id}")
    public Response postOn(@PathVariable("id") final String id) {
        return commandService.sendOn(id);
    }
    
    @ApiOperation(value = "Switch a device OFF", nickname = "setOff", response = Response.class)
    @PostMapping("/v1/device/off/{id}")
    public Response postOff(@PathVariable("id") final String id) {
        return commandService.sendOff(id);
    }
    
    @ApiOperation(value = "Set the Color Temperature of a device", nickname = "setCT", response = Response.class)
    @PostMapping("/v1/device/ct/{id}")
    public Response doCt(@PathVariable("id") final String id, @RequestParam("ct") final int ct) {
        return commandService.sendCT(id, ct);
    }
    
    @ApiOperation(value = "Set the Color of a device using Hue", nickname = "setHue", response = Response.class)
    @PostMapping("/v1/device/hue/{id}")
    public Response doHue(@PathVariable("id") final String id, @RequestParam("hue") final int hue) {
        return commandService.sendHue(id, hue);
    }
}
