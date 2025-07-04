package com.backend.global.common;

import lombok.*;

@Setter
@Getter
@ToString
public class OrgDTO {

    private int org_id;
    private String name;
    private String address;
    private String email;
    private String representative_name;
    private String phone;
    private String business_number;
    private String logo_img_url;
}
