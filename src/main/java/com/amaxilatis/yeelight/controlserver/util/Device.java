package com.amaxilatis.yeelight.controlserver.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Device {
    private String id;
    private String location;
    private String server;
    private String model;
    private int fwVer;
    private String support;
    private boolean power;
    private int bright;
    private int colorMode;
    private int ct;
    private int rgb;
    private int hue;
    private int sat;
    private String name;
}
