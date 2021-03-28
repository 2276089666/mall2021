package com.cloud.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.member.entity.MemberEntity;
import com.cloud.mall.member.myException.PhoneExistException;
import com.cloud.mall.member.myException.UserNameExistException;
import com.cloud.mall.member.vo.LoginVo;
import com.cloud.mall.member.vo.RegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:19:53
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void reg(RegisterVo registerVo)throws PhoneExistException,UserNameExistException;

    void checkPhoneExist(String phone) throws PhoneExistException;

    void checkUserNameExist(String userName) throws UserNameExistException;

    MemberEntity login(LoginVo loginVo);
}

