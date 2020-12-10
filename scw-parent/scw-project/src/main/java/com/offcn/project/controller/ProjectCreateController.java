package com.offcn.project.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.common.enums.ProjectStatusEnume;
import com.offcn.common.response.AppResponse;
import com.offcn.common.vo.BaseVo;
import com.offcn.project.pojo.TReturn;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStoreVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "新建众筹项目的四个步骤")
@RequestMapping("/createProject")
public class ProjectCreateController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private ProjectCreateService projectCreateService;

    @ApiOperation(value = "新建项目第一步:初始化项目")
    @PostMapping("/init")
    public AppResponse<String> init(BaseVo baseVo){
        String accessToken = baseVo.getAccessToken();
        String memberId = redisTemplate.opsForValue().get(accessToken);

        if(memberId==null || memberId.length()==0){
            AppResponse response =AppResponse.fail(null);
            response.setMsg("该用户没有登录，请登录之后再尝试");
            return response;
        }

        String projectToken = projectCreateService.initCreateProject(Integer.parseInt(memberId));
        return AppResponse.ok(projectToken);

    }

    @ApiOperation(value = "新建项目第二步:添加项目基本信息")
    @PostMapping("/saveBaseInfo")
    public AppResponse<String> saveBaseInfo(ProjectBaseInfoVo projectBaseInfoVo){
        String projectToken=projectBaseInfoVo.getProjectToken();
        String projectBaseVo = redisTemplate.opsForValue().get(projectToken);
        ProjectBaseInfoVo projectBaseInfoVo1 = JSON.parseObject(projectBaseVo, ProjectBaseInfoVo.class);
        BeanUtils.copyProperties(projectBaseInfoVo,projectBaseInfoVo1);
        String redisVo = JSON.toJSONString(projectBaseInfoVo1);
        redisTemplate.opsForValue().set(projectToken,redisVo);
        return AppResponse.ok(projectToken);

    }


    @ApiOperation(value = "新建项目第三步:添加项目的回报信息")
    @PostMapping("/saveReturn")
    public AppResponse<String> saveReturn(@RequestBody List<ProjectReturnVo> returnVoList){
            if (returnVoList!=null&&returnVoList.size()>0){
                String projectToken = returnVoList.get(0).getProjectToken();
                String redisVo1 = redisTemplate.opsForValue().get(projectToken);
                ProjectRedisStoreVo redisVoStr = JSON.parseObject(redisVo1, ProjectRedisStoreVo.class);
                List tReturns = new ArrayList();
                for (ProjectReturnVo tReturn1 : returnVoList) {
                    TReturn tReturn = new TReturn();
                    BeanUtils.copyProperties(tReturn1,tReturn);
                    tReturns.add(tReturn);
                }
                redisVoStr.setProjectReturns(tReturns);
                String redisVo = JSON.toJSONString(redisVoStr);
                redisTemplate.opsForValue().set(projectToken,redisVo);
                return AppResponse.ok(projectToken);
            }
       return AppResponse.fail("请输入回报参数！");
    }

    @ApiOperation(value = "新建项目第四步:添加项目到数据库")
    @PostMapping("/saveToMysql")
    public AppResponse<Object> saveToMysql(String projectToken,String accessToken,String status){
    String memberId= redisTemplate.opsForValue().get(accessToken);
    if(memberId==null|| memberId.length()==0){
            return AppResponse.fail("用户未登录");
    }
        String redisVoStr = redisTemplate.opsForValue().get(projectToken);
        ProjectRedisStoreVo redisVo = JSON.parseObject(redisVoStr, ProjectRedisStoreVo.class);
            if (redisVo!=null){
                if (status.equalsIgnoreCase("1")){
                    projectCreateService.saveProjectInfo(ProjectStatusEnume.SUBMIT_AUTH,redisVo);
                  return AppResponse.ok("添加成功");
                }
            }

        return AppResponse.fail(null);
        }
    }



