package com.offcn.project.service;

import com.offcn.common.enums.ProjectStatusEnume;
import com.offcn.project.pojo.*;
import com.offcn.project.vo.req.ProjectRedisStoreVo;

import java.util.List;

public interface ProjectCreateService {
   // 1 初始化项目
   public String initCreateProject(Integer memberId);

   // 4 保存项目
   public void saveProjectInfo(ProjectStatusEnume status, ProjectRedisStoreVo redisStoreVo);

   public List<TProject> findAllProject();

  public List<TProjectImages> findProjectImageByProjectId(Integer projectId);

  public TProject findProjectInfoByProjectId(Integer projectId);

  public   List<TReturn> findProjectTReturnByProjectId(Integer projectId);

  public List<TTag> findAllTag();

   public List<TType> findAllType();

   public TReturn findReturnById(Integer returnId);
}
