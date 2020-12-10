package com.offcn.order.service;


import com.offcn.common.response.AppResponse;
import com.offcn.order.service.impl.ProjectServiceFeignImpl;
import com.offcn.order.vo.resp.TReturn;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value ="SCW-PROJECT",fallback = ProjectServiceFeignImpl.class)
public interface ProjectServiceFeign {
    //查询回报信息根据回报id
    @GetMapping("/project/findReturnById/{returnId}")
    public TReturn findReturnById(@PathVariable("returnId")Integer returnId);

    //查询回报信息根据回报id
    @GetMapping("/project/details/returns/{projectId}")
    public AppResponse<List<TReturn>> getReturnByProjectId(@PathVariable("projectId")Integer projectId);


}
