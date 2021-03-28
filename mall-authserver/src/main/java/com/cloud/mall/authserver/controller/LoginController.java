package com.cloud.mall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.cloud.common.constant.AuthConstant;
import com.cloud.common.exception.ExceptionCode;
import com.cloud.common.utils.R;
import com.cloud.mall.authserver.feign.MemberFeignService;
import com.cloud.mall.authserver.feign.ThirdPartyFeignService;
import com.cloud.mall.authserver.vo.UserLoginVo;
import com.cloud.mall.authserver.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author ws
 * @Date 2021/3/18 14:31
 * @Version 1.0
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/sms/send/code")
    @ResponseBody
    public R code(@RequestParam("phone") String phone) {
        // 接口防刷()

        // 防止页面刷新,同一个手机号60秒内再次发送验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_PRE + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long codeTime = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - codeTime < 60000) {
                return R.error(ExceptionCode.SMS_CODE_EXCEPTION.getCode(), ExceptionCode.SMS_CODE_EXCEPTION.getMessage());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        String codeCache =  code+ "_" + System.currentTimeMillis();
        // 验证码的再次校验 redis  key:sms:code:phone value:code_当前系统时间  10分钟过期
        stringRedisTemplate.opsForValue().set(AuthConstant.SMS_CODE_PRE + phone, codeCache, 10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }


    @PostMapping("/register")
    // 开启校验
    /**
     * 表单提交的格式不能用 @RequestBody
     */
    public String register(@Validated UserRegisterVo userRegisterVo, BindingResult result, RedirectAttributes redirectAttributes) {
        // 数据格式校验
        if (result.hasErrors()) {
            // 某些字段会两个注解,当出现2个异常时,map的key不能重复,同一个字段不能接收2个异常
            Map<String, String> errorMessages = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            // redirectAttributes只能取一次
            redirectAttributes.addFlashAttribute("errors", errorMessages);
            // 校验出错,利用视图映射返回当前域名下的注册页,不能用转发(forward:/reg.html),视图映射默认要求是get请求,而我们这个请求是post请求
            return "redirect:http://auth.gmall.com/reg.html";
        }

        // 校验验证码
        String code = userRegisterVo.getCode();
        String codeCache = stringRedisTemplate.opsForValue().get(AuthConstant.SMS_CODE_PRE + userRegisterVo.getPhone());
        if (StringUtils.isEmpty(codeCache)||!code.equalsIgnoreCase(codeCache.split("_")[0])){
            HashMap<String, String> map = new HashMap<>();
            map.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gmall.com/reg.html";
        }else {
            // 删除缓存code
            stringRedisTemplate.delete(AuthConstant.SMS_CODE_PRE + userRegisterVo.getPhone());
            // 调用member服务注册
            R r = memberFeignService.register(userRegisterVo);
            if (r.getCode()==0){
                // 注册成功

                // 注册成功返回登录页
                return "redirect:http://auth.gmall.com/login.html";
            }else {
                HashMap<String, String> map = new HashMap<>();
                map.put("msg",r.getMsg());
                redirectAttributes.addFlashAttribute("errors", map);
                return "redirect:http://auth.gmall.com/reg.html";
            }
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo userLoginVo,RedirectAttributes redirectAttributes){

        R login = memberFeignService.login(userLoginVo);
        if (login.getCode()==0){
            // 成功
            return "redirect:http://mall.com";
        }else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("msg",login.getMsg());
            redirectAttributes.addFlashAttribute("errors",hashMap);
            return "redirect:http://auth.gmall.com/login.html";
        }

    }
}
