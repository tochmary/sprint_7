package model;

import java.util.Set;

public class RespOrders {
    Set<RespDataOrder> orders;

    public RespOrders(Set<RespDataOrder> orders) {
        this.orders = orders;
    }

    public Set<RespDataOrder> getOrders() {
        return orders;
    }
}
