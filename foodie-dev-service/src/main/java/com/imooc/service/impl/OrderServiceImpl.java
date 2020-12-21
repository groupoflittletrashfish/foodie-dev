package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @program: foodie-dev
 * @description:
 * @author: noname
 * @create: 2020-12-18 16:14
 **/

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private AddressService addressService;
    @Resource
    private Sid sid;
    @Resource
    private ItemService itemService;
    @Resource
    private OrderItemsMapper orderItemsMapper;
    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrder(SubmitOrderBO submitOrderBO) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethon = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        //包邮字段设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();
        UserAddress address = addressService.queryUserAddress(userId, addressId);

        //新订单数据保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);
        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(StringUtils.join(address.getProvince(), " ", address.getCity(), " ", address.getDistrict(), " ", address.getDetail()));
//        newOrder.setTotalAmount();
//        newOrder.setRealPayAmount();
        newOrder.setPostAmount(postAmount);
        newOrder.setPayMethod(payMethon);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());

        //循环根据itemSpecIds保存订单商品信息表
        String[] itemSpecIdArr = itemSpecIds.split(",");
        Integer totalAmount = 0;    //商品原价累计
        Integer realPayAmount = 0;  //优惠后的实际支付价格累计
        for (String itemSpecId : itemSpecIdArr) {
            //TODO 整合redis后，商品的购买数据重新从redis的购物车中获取
            int buyCounts = 1;

            //根据规格ID，查询规格的具体信息，主要查询价格
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //根据规格id,获得商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            //循环保存订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            //在用户提交订单以后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        //保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);
    }
}