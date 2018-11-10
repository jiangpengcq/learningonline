package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 */
@ControllerAdvice
@ResponseBody
public class ExceptionCatch {

    //控制台打印日志
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //在类初始化时，map构造器中加入一些基础的异常类型判断
    private static ImmutableMap<Class<? extends Exception>,ResultCode> exceptionCode;
    private static ImmutableMap.Builder<Class<? extends Exception>,ResultCode> builder = ImmutableMap.builder();
    static {
        builder.put( HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM );
    }

    /**
     * 捕获自定义异常
     * @param customException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseResult customException(CustomException customException){
        LOGGER.error("catch exception:{}\r\nexception:",customException.getMessage(),customException);
        return new ResponseResult(customException.getResultCode());
    }

    /**
     * 捕获不可预知异常
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception exception){
        LOGGER.error("catch exception:{}\r\nexception:",exception.getMessage(),exception);
        if (exceptionCode == null) exceptionCode=builder.build();
        ResultCode resultCode = exceptionCode.get(exception.getClass());
        if (resultCode != null)  return new ResponseResult(resultCode);
        return  new ResponseResult(CommonCode.SERVER_ERROR);
    }
}
