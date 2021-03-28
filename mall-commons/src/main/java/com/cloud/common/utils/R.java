/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 复杂的Collection<T>类型的数据存取
     * @param data
     * @return
     */
    public R setData(Object data) {
        put("data", data);
        return this;
    }

    /**
     * 复杂的Collection<T>类型的数据应使用这个拿到我们的对象
     * @param <T>
     * @param tTypeReference
     * @return
     */
    public <T> T getData(TypeReference<T> tTypeReference) {
        Object data = get("data");
        String dataString = JSON.toJSONString(data);
        return JSON.parseObject(dataString,tTypeReference);
    }

    public String getMsg(){
        return (String) get("msg");
    }

    public R() {
        put("code", 0);
        put("msg", "success");
    }


    public static R error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode() {

        return (Integer) this.get("code");
    }

}
