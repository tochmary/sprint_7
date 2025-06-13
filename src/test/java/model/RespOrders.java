package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RespOrders {
    Set<RespDataOrder> orders;
}
