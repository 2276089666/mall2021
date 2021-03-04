package com.cloud.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.common.utils.PageUtils;
import com.cloud.mall.member.entity.MemberEntity;

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
}

