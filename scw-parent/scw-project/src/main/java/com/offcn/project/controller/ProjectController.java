package com.offcn.project.controller;

import com.offcn.common.response.AppResponse;
import com.offcn.common.utils.OSSTemplate;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.resp.ProjectDetailVo;
import com.offcn.project.vo.resp.ProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "项目模块")
@RequestMapping("/project")
public class ProjectController {
        @Autowired
        private OSSTemplate ossTemplate;
        @Autowired
        private ProjectCreateService projectService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传图片方法")
    public AppResponse<Map<String,Object>> uploadFile(@RequestParam("file")MultipartFile[] files) throws IOException {
            Map<String,Object> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            if (files.length>0&&files!=null){
                for (MultipartFile item : files) {
                    String fileUrl = ossTemplate.upload(item.getInputStream(), item.getOriginalFilename());
                    list.add(fileUrl);
                }
            }
            map.put("urls",list);
            return  AppResponse.ok(map);
        }

    @GetMapping("/all")
    @ApiOperation(value = "显示所有的项目")
    public AppResponse<List<ProjectVo>> findAllProject(){
        List<ProjectVo> list=new ArrayList<>();
        List<TProject> prosVos=projectService.findAllProject();
        for (TProject project : prosVos) {
            // 获取当前项目名
            Integer projectId = project.getId();
            // 根据项目Id查询头图片
           List<TProjectImages> images= projectService.findProjectImageByProjectId(projectId);
            ProjectVo projectVo = new ProjectVo();
            // 赋值属性
            BeanUtils.copyProperties(project,projectVo);
            // 还少头图片，添加头图片
            if (images!=null&&images.size()>0){
                for (TProjectImages image : images) {
                    if (image.getImgtype()==0){
                        projectVo.setHeaderImage(image.getImgurl());
                    }
                }
            }
            list.add(projectVo);
        }
        return  AppResponse.ok(list);
    }

    @GetMapping("/findProjectInfo/{projectId}")
    @ApiOperation(value = "查看项目的详细信息")
    public AppResponse<ProjectDetailVo> findProjectInfo(@PathVariable("projectId")Integer projectId ){
        //设置一个空视图，然后从数据库中查询信息添加到视图中返回到静态页面
        ProjectDetailVo detailVo = new ProjectDetailVo();
        //从数据库查找项目基本信息
     TProject projectInfo= projectService.findProjectInfoByProjectId(projectId);
        BeanUtils.copyProperties(projectInfo,detailVo);
        detailVo.setDetailsImage(new ArrayList<>());
        //从数据库获取图片
        List<TProjectImages> images = projectService.findProjectImageByProjectId(projectId);
        //添加图片
        if (images==null){
        detailVo.setHeaderImage(null);
        }else{
            for (TProjectImages image : images) {
                if (image.getImgtype()==0){
                    //添加头图片
                    detailVo.setHeaderImage(image.getImgurl());
                }else{
                    //添加详细图片
                    detailVo.getDetailsImage().add(image.getImgurl());
                }
            }
        }
        //从数据库获得回报信息
    List<TReturn>  returns=  projectService.findProjectTReturnByProjectId(projectId);
        detailVo.setProjectReturn(returns);
        return AppResponse.ok(detailVo);
    }
            //查询所有标签
            @ApiOperation(value = "查询所有标签")
        @GetMapping("/findAllTag")
        public AppResponse<List<TTag>> findAllTag(){
            List<TTag> tags = projectService.findAllTag();
            return AppResponse.ok(tags);
        }
        //查询所有类型

        @ApiOperation(value = "查询所有类型")
        @GetMapping("/findAllType")
        public AppResponse<List<TType>> findAllType(){
            List<TType> types = projectService.findAllType();
            return AppResponse.ok(types);
        }
        //查询回报信息根据回报id
    @GetMapping("/findReturnById/{returnId}")
    @ApiOperation(value = "根据回报ID查询回报信息")
    public TReturn findReturnById(@PathVariable("returnId")Integer returnId){
        return projectService.findReturnById(returnId);
    }

    //查询回报信息根据回报id
    @GetMapping("/details/returns/{projectId}")
    @ApiOperation(value = "根据项目ID查询回报信息")
    public AppResponse<List<TReturn>> getReturnByProjectId(@PathVariable("projectId")Integer projectId){
     List<TReturn> returns =projectService.findProjectTReturnByProjectId(projectId);
     return AppResponse.ok(returns);
    }
    }
