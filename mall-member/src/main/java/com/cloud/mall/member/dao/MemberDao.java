package com.cloud.mall.member.dao;

import com.cloud.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author ws
 * @email 2276089666@qq.com
 * @date 2021-01-09 16:19:53
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
