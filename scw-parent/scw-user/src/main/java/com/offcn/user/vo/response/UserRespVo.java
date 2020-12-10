package com.offcn.user.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class UserRespVo {

    private String accessToken; // 用户令牌
    // 没有密码属性
    private Integer id;
    @ApiModelProperty(value = "手机号")
    private String loginacct;
    private String username;
    private String email;
    private String authstatus;
    private String usertype;
    private String realname;
    private String cardnum;
    private String accttype;
}
