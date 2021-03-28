package com.cloud.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.cloud.common.exception.ExceptionCode;
import com.cloud.mall.member.myException.PhoneExistException;
import com.cloud.mall.member.myException.UserNameExistException;
import com.cloud.mall.member.vo.LoginVo;
import com.cloud.mall.member.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.mall.member.entity.MemberEntity;
import com.cloud.mall.member.service.MemberService;
import com.cloud.common.utils.PageUtils;
import com.cloud.common.utils.R;



/**
 * 会员
 *
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:19:53
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 注册
     * @param registerVo
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody RegisterVo registerVo){

        try {
            memberService.reg(registerVo);
        }catch (PhoneExistException e){
            return R.error(ExceptionCode.PHONE_EXIST_EXCEPTION.getCode(),ExceptionCode.PHONE_EXIST_EXCEPTION.getMessage());
        }catch (UserNameExistException e){
            return R.error(ExceptionCode.USER_EXIST_EXCEPTION.getCode(),ExceptionCode.USER_EXIST_EXCEPTION.getMessage());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        MemberEntity memberEntity=memberService.login(loginVo);
        if (memberEntity!=null){
            return R.ok().put("memberInfo",memberEntity);
        }else {
            return R.error(ExceptionCode.LOGIN_ACCOUNT_OR_PASSWORD_EXCEPTION.getCode(),ExceptionCode.LOGIN_ACCOUNT_OR_PASSWORD_EXCEPTION.getMessage());
        }

    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
