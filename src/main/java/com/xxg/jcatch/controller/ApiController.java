package com.xxg.jcatch.controller;

import com.xxg.jcatch.api.ApiResult;
import com.xxg.jcatch.mbg.bean.TException;
import com.xxg.jcatch.mbg.mapper.TAppMapper;
import com.xxg.jcatch.service.ExceptionSubmitService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by wucao on 17/3/14.
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TAppMapper appMapper;

    @Autowired
    private ExceptionSubmitService exceptionSubmitService;

    @ResponseBody
    @RequestMapping(value = "/submitExceptionJson", method = RequestMethod.POST)
    public ApiResult submitExceptionJson(@RequestBody String jsonString, HttpServletRequest request, String appId) {

        if (!StringUtils.hasText(appId)) {
            return ApiResult.fail("AppId is required");
        }
        if (appMapper.selectByPrimaryKey(appId) == null) {
            return ApiResult.fail("Unknown appId: " + appId);
        }

        JSONObject jsonObject = new JSONObject(jsonString);

        TException tException = new TException();
        tException.setAppId(appId);

        String ip = request.getHeader("X-Real-IP");
        if (ip == null) {
            tException.setRemoteAddr(request.getRemoteAddr());
        } else {
            tException.setRemoteAddr(ip);
        }
        tException.setStackTrace(jsonObject.optString("stackTrace", null));
        tException.setExceptionName(jsonObject.optString("exceptionName", null));
        tException.setMessage(jsonObject.optString("message", null));
        tException.setClassName(jsonObject.optString("className", null));
        tException.setFileName(jsonObject.optString("fileName", null));
        tException.setMethodName(jsonObject.optString("methodName", null));
        tException.setLineNumber(jsonObject.optInt("lineNumber", 0));
        exceptionSubmitService.submit(tException);

        return ApiResult.success();
    }
}
