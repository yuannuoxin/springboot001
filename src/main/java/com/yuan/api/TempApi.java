package com.yuan.api;

import com.yuan.util.IpUtil;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yl
 * @date 2021/12/18 22:26
 */
@RestController
public class TempApi {
    @RequestMapping("/**")
    public Map t1(@RequestHeader Map<String, Object> headers, HttpServletRequest request) {
        String ipAddress = IpUtil.getIpAddr(request);
        headers.put("xxx-ipAddress",ipAddress);
        headers.put("xxx-getRequestURI",request.getRequestURL());
        headers.put("xxx-getServletPath",request.getServletPath());
        headers.put("xxx-getRequestURL",request.getRequestURL());
        headers.put("xxx-getRemoteAddr",request.getRemoteAddr());
        headers.put("xxx-getRemoteUser",request.getRemoteUser());
        headers.put("xxx-getUserPrincipal",request.getUserPrincipal());
        headers.put("xxx-getQueryString",request.getQueryString());
        return headers;
    }
}
