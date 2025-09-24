package com.multi.travel.model;

import lombok.Getter;
import lombok.Setter;
// import java.util.List; // 이제 리스트는 필요 없습니다.

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

    private String mainImage;
}