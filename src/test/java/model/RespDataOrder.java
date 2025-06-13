package model;

import lombok.Data;

import java.util.Set;
@Data
public class RespDataOrder {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private Integer rentTime;
    private String deliveryDate;
    private Integer track;
    private Integer status;
    private Set<String> color;
    private String comment;
    private Boolean cancelled;
    private Boolean finished;
    private Boolean inDelivery;
    private String courierFirstName;
    private String createdAt;
    private String updatedAt;
}
