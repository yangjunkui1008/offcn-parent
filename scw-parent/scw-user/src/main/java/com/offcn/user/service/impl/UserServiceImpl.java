package com.offcn.user.service.impl;


import com.offcn.user.exception.UserException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.pojo.TMemberAddressExample;
import com.offcn.user.pojo.TMemberExample;
import com.offcn.user.service.UserService;

import enums.UserExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired(required = false)
    private TMemberMapper memberMapper;
    @Autowired(required = false)
    private TMemberAddressMapper addressMapper;

    @Override
    public void registerUser(TMember tMember) {
        // 1. 验证手机号是否在数据表中存在
        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(tMember.getLoginacct());
        List<TMember> tMembers = memberMapper.selectByExample(example);
        if(tMembers.size()>0){
            throw new UserException(UserExceptionEnum.LOGINACCT_EXIST);//被注册
        }
        //未被注册
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        tMember.setUserpswd(encoder.encode(tMember.getUserpswd()));
        tMember.setAuthstatus("0"); // 未实名认证
        tMember.setAccttype("0");  // 个人用户
        tMember.setUsertype("0"); // 用户类型
        memberMapper.insert(tMember);
    }

    @Override
    public TMember login(String loginacct, String password) {

        TMemberExample example = new TMemberExample();
        TMemberExample.Criteria criteria = example.createCriteria();
        criteria.andLoginacctEqualTo(loginacct);
        List<TMember> tMembers = memberMapper.selectByExample(example);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (tMembers !=null && tMembers.size()==1){
            TMember tMember = tMembers.get(0);
            //encoder.matches是用加密对象来匹配密码
            boolean matches = encoder.matches(password, tMember.getUserpswd());
            return matches?tMember:null;

        }
        return null;
    }

    @Override
    public TMember findTMemberById(Integer id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TMemberAddress> findAdressByMemberId(String memberId) {
        TMemberAddressExample example = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = example.createCriteria();
        criteria.andMemberidEqualTo(Integer.parseInt(memberId));
        return  addressMapper.selectByExample(example);
    }
}
