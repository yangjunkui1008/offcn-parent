package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.common.enums.ProjectStatusEnume;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.pojo.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStoreVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private TProjectMapper projectMapper;
    @Autowired(required = false)
    private TProjectImagesMapper projectImagesMapper;
    @Autowired(required = false)
    private TProjectTagMapper projectTagMapper;
    @Autowired(required = false)
    private TProjectTypeMapper tProjectTypeMapper;
    @Autowired(required = false)
    private TReturnMapper returnMapper;
    @Autowired(required = false)
    private TTagMapper tTagMapper;
    @Autowired(required = false)
    private TTypeMapper tTypeMapper;

    @Override
    public String initCreateProject(Integer memberId) {
        String projectToken = UUID.randomUUID().toString().replace("-", "")+"Project";
        ProjectRedisStoreVo redisVo = new ProjectRedisStoreVo();
        redisVo.setMemberid(memberId);
        String redisVoStr = JSON.toJSONString(redisVo);
        redisTemplate.opsForValue().set(projectToken,redisVoStr);
        return projectToken;
    }

    @Override
    public void saveProjectInfo(ProjectStatusEnume status, ProjectRedisStoreVo redisStoreVo) {
        TProject tProject = new TProject();
        BeanUtils.copyProperties(redisStoreVo,tProject);
        tProject.setStatus(status.getCode()+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tProject.setCreatedate(dateFormat.format(new Date()));
        projectMapper.insert(tProject);
        Integer projectId = tProject.getId();
        //头图
        String headerImage = redisStoreVo.getHeaderImage();
        if (headerImage!=null){
            TProjectImages tProjectImages = new TProjectImages(null,projectId,headerImage, ProjectImageTypeEnume.HEADER.getCode());
        projectImagesMapper.insert(tProjectImages);
        }
        //详情图
        List<String> detailsImages = redisStoreVo.getDetailsImages();
        if (detailsImages!=null&&detailsImages.size()>0){
            for (String detailsImage : detailsImages) {
            TProjectImages detailsImagesObj = new TProjectImages(null,projectId,detailsImage, ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insert(detailsImagesObj);
            }
        }
        //标签
        List<Integer> tagIds = redisStoreVo.getTagIds();
        if(tagIds!=null&&tagIds.size()>0){
            for (Integer tagId : tagIds) {
                TProjectTag tProjectTag = new TProjectTag(null, projectId, tagId);
                projectTagMapper.insert(tProjectTag);
            }
        }
        //类型
        List<Integer> typeIds = redisStoreVo.getTypeIds();
        if(typeIds!=null&&typeIds.size()>0){
            for (Integer typeId : typeIds) {
                TProjectType tProjectType = new TProjectType(null, projectId, typeId);
                tProjectTypeMapper.insert(tProjectType);
            }
        }
        //回报数据
        List<TReturn> projectReturns = redisStoreVo.getProjectReturns();
        if (projectReturns!=null&&projectReturns.size()>0){
            for (TReturn tReturn : projectReturns) {
                tReturn.setProjectid(projectId);
                returnMapper.insert(tReturn);
            }
        }
        //清空redis      redisTemplate.delete(redisStoreVo.getProjectToken());
    }

    //查询所有项目
    @Override
    public List<TProject> findAllProject() {
        return projectMapper.selectByExample(null);
    }

    //查询项目图片信息
    @Override
    public List<TProjectImages> findProjectImageByProjectId(Integer projectId) {
        TProjectImagesExample example = new TProjectImagesExample();
        TProjectImagesExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(projectId);
        return projectImagesMapper.selectByExample(example);
    }

    //查询项目基本信息
    @Override
    public TProject findProjectInfoByProjectId(Integer projectId) {
        return  projectMapper.selectByPrimaryKey(projectId);
    }

    //根据projectId获取回报信息
    @Override
    public List<TReturn> findProjectTReturnByProjectId(Integer projectId) {
        TReturnExample example = new TReturnExample();
        TReturnExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(projectId);
        return returnMapper.selectByExample(example);
    }

    //查询所有标签
    @Override
    public List<TTag> findAllTag() {
        return tTagMapper.selectByExample(null);
    }

    //查询所有类型
    @Override
    public List<TType> findAllType() {
        return tTypeMapper.selectByExample(null);
    }

    //根据回报id查找回报信息
    @Override
    public TReturn findReturnById(Integer returnId) {
        return returnMapper.selectByPrimaryKey(returnId);
    }
}
