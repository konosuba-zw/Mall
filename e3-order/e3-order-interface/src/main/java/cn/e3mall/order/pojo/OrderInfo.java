package cn.e3mall.order.pojo;

import cn.e3mall.common.pojo.TbOrder;
import cn.e3mall.common.pojo.TbOrderItem;
import cn.e3mall.common.pojo.TbOrderShipping;

import java.io.Serializable;
import java.util.List;

public class OrderInfo extends TbOrder implements Serializable {

    private List<TbOrderItem> orderItems;
    private TbOrderShipping orderShipping;

    public List<TbOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public TbOrderShipping getOrderShipping() {
        return orderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        this.orderShipping = orderShipping;
    }
}
