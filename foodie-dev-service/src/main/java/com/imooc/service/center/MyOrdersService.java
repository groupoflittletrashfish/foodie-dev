package com.imooc.service.center;

import com.imooc.util.PagedGridResult;

/**
 * Created by Administrator on 2020/12/28.
 */
public interface MyOrdersService {

    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);


    /**
     * 订单状态-->商家发货
     *
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);
}
