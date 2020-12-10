package com.offcn.user.controller;
import com.offcn.common.response.AppResponse;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.service.UserService;
import com.offcn.user.utils.SmsTemplate;
import com.offcn.user.vo.UserRedisVo;
import com.offcn.user.vo.response.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api(tags = "用户登录/注册模块（包括忘记密码等）")
public class UserLoginController {
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private  StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;

    @ApiOperation("发送验证码")
   /* @ApiImplicitParams({
            @ApiImplicitParam(name ="phoneNo",value = "手机号",required = true)
    })*/
    @PostMapping("/sendCode")
    public AppResponse<Object> sendCode(String phoneNum){
        //生成随机数保存到redis
        String code = UUID.randomUUID().toString().substring(0,4);
        System.out.println("生成的验证码是:"+code);
        //保存到redis并设置过期时间十分钟
        redisTemplate.opsForValue().set(phoneNum,code,10, TimeUnit.MINUTES);
        //短信发送构造函数
        try {
            String okMsg="短信发送成功";
            // smsTemplate.sendCode(phoneNum,code);
            return AppResponse.ok(okMsg);
            //return AppResponse.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return AppResponse.fail("短信发送失败");
        }
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public AppResponse<Object> register (UserRedisVo userRedisVo){
        //获取验证码
        String code = redisTemplate.opsForValue().get(userRedisVo.getLoginacct());
        if (code!=null&&code.length()>0){
            if(code.equalsIgnoreCase(userRedisVo.getCode())){
                TMember tMember = new TMember();
                // 该方法要求属性名必须一致，否则不一样的属性赋值为空
                BeanUtils.copyProperties(userRedisVo,tMember);
                userService.registerUser(tMember);
                redisTemplate.delete(tMember.getLoginacct());
                return AppResponse.ok("注册成功");

            }else {
                return AppResponse.fail("验证码不一致");
            }

        }else {
            return AppResponse.fail("验证码失效");
        }
    }

    @ApiOperation( value = "用户登录模块")
    @PostMapping("/login")
    public AppResponse<UserRespVo> login(String loginacct,String password){
        TMember member = userService.login(loginacct, password);
        if (member==null){
            AppResponse<UserRespVo>  fail=AppResponse.fail(null);
            fail.setMsg("用户名或密码错误！");
            return fail;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        UserRespVo userRespVo = new UserRespVo();
        userRespVo.setAccessToken(token);
        BeanUtils.copyProperties(member,userRespVo);
        redisTemplate.opsForValue().set(token,member.getId()+"",2,TimeUnit.MINUTES);
        return AppResponse.ok(userRespVo);
    }
    @ApiOperation(value="通过用户ID获取用户信息")
    @GetMapping("/findUser/{id}")
    public AppResponse<UserRespVo> findUserById(@PathVariable("id") Integer id){
        TMember tMemeber = userService.findTMemberById(id);
        UserRespVo userRespVo = new UserRespVo();
        BeanUtils.copyProperties(tMemeber,userRespVo);
        return AppResponse.ok(userRespVo);
    }


    @ApiOperation(value="根据accessToken获取登录用户的地址信息")
    @GetMapping("/findAdress")
    public AppResponse<List<TMemberAddress>> findAdressByAccessToken(String accessToken){
        String memberId = redisTemplate.opsForValue().get(accessToken);
        if (memberId==null&&memberId.length()==0){
            AppResponse response = new AppResponse();
            response.setMsg("用户未登录，请登录后重试");
            return response;
        }
        List<TMemberAddress> adress = userService.findAdressByMemberId(memberId);
        return AppResponse.ok(adress);
    }
}
