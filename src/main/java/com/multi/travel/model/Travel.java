package com.multi.travel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Travel {
    private int no;
    private String district;
    private String title;
    private String description;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
}