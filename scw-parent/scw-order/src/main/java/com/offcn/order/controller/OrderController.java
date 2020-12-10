package com.offcn.order.controller;

import com.offcn.common.response.AppResponse;
import com.offcn.order.pojo.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "订单模块")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "创建订单")
    @PostMapping("/createOrder")
    public AppResponse<TOrder> createOrder(@RequestBody OrderInfoVo orderInfoVo){
        String accessToken = orderInfoVo.getAccessToken();
        String memberId="";
        try {
            memberId = redisTemplate.opsForValue().get(accessToken);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("未登录");
            return AppResponse.fail(null);
        }

        if ("".equals(memberId)){
            System.out.println("未登录");
            return AppResponse.fail(null);
        }
        try {
            TOrder tOrder = orderService.saveOrder(orderInfoVo);
            return AppResponse.ok(tOrder);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("调用service异常");
            return AppResponse.fail(null);
        }


    }
}
