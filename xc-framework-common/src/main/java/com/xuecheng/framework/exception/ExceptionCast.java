package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 异常抛出工具类
 */
public class ExceptionCast {

    /**
     * 抛出自定义异常
     * @param resultCode
     */
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
