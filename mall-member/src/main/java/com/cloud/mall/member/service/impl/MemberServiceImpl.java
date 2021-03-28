package com.cloud.mall.member.service.impl;

import com.cloud.mall.member.entity.MemberLevelEntity;
import com.cloud.mall.member.myException.PhoneExistException;
import com.cloud.mall.member.myException.UserNameExistException;
import com.cloud.mall.member.service.MemberLevelService;
import com.cloud.mall.member.vo.LoginVo;
import com.cloud.mall.member.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.Query;

import com.cloud.mall.member.dao.MemberDao;
import com.cloud.mall.member.entity.MemberEntity;
import com.cloud.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public void reg(RegisterVo registerVo) {
        MemberEntity memberEntity = new MemberEntity();
        // 设置默认得会员等级
        MemberLevelEntity memberLevelEntity =memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status",1));
        if (memberLevelEntity!=null){
            memberEntity.setLevelId(memberLevelEntity.getId());
        }
        // 采用异常机制,检查手机号和用户名是否唯一
        checkPhoneExist(registerVo.getPhone());
        checkUserNameExist(registerVo.getUserName());

        //上面有问题的话就会抛异常被捕获
        memberEntity.setMobile(registerVo.getPhone());
        memberEntity.setUsername(registerVo.getUserName());

        // 设置密码 使用盐值加密,不能被MD5得彩虹表(把md5得明文和密文一个个得列举出来,再去对比查询破解)破解
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(registerVo.getPassword());
        memberEntity.setPassword(encode);
        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneExist(String phone) throws PhoneExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count>0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameExist(String userName) throws UserNameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count>0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(LoginVo loginVo) {
        String loginAccount = loginVo.getLoginAccount();
        String password = loginVo.getPassword();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().and(a -> {
            a.eq("username", loginAccount).or().eq("mobile", loginAccount);
        }));
        if (memberEntity!=null){
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            // 盐值加密得判断密码一致方法
            boolean matches = bCryptPasswordEncoder.matches(password, memberEntity.getPassword());
            if (matches){
                return memberEntity;
            }
        }
        return null;
    }

}