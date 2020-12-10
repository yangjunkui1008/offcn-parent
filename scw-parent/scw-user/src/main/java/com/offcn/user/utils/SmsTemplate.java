package com.offcn.user.utils;

import com.offcn.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsTemplate {
    @Value("${sms.host}")
    private String host;
    @Value("${sms.path}")
    private String path;
    @Value("${sms.method}")
    private String method;
    @Value("${sms.appCode}")
    private String appCode;

    public String sendCode(String phoneNum,String code){
        Map<String, String> header = new HashMap();
        header.put("Authorization",appCode);
        header.put("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
       Map<String, String> querys=new HashMap();
       querys.put("mobile",phoneNum);
       querys.put("param","code:"+code);
       querys.put("tpl_id","TP1711063");
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, header, querys, "");
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
