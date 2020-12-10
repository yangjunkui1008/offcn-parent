package com.offcn.order.service.impl;

import com.offcn.common.enums.OrderStatusEnumes;
import com.offcn.common.utils.AppDateUtils;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.pojo.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoVo;
import com.offcn.order.vo.resp.TReturn;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProjectServiceFeign projectService;

    @Override
    public TOrder saveOrder(OrderInfoVo infoVo) {
        TOrder tOrder = new TOrder();
        String memberId = redisTemplate.opsForValue().get(infoVo.getAccessToken());
        BeanUtils.copyProperties(infoVo,tOrder);
        //用户id
        tOrder.setMemberid(Integer.parseInt(memberId));
        //订单编号
        String orderNum = UUID.randomUUID().toString().replace("-", "");
        tOrder.setOrdernum(orderNum);
        //创建时间
        tOrder.setCreatedate(AppDateUtils.getFormartTime());
        //订单状态  UNPAY未支付
        tOrder.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
        // money   回报个数 order.getRtncount() * 支持金额  + 运费
      TReturn tRerurn=  projectService.findReturnById(infoVo.getReturnid());
        Integer money=tOrder.getRtncount()*tRerurn.getSupportmoney()+tRerurn.getFreight();
        tOrder.setMoney(money);
        return tOrder;
    }
}
