package com.sojson.common.utils.vcode;
import com.sojson.common.utils.VerifyCodeUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * Created by mathman002 on 2017/9/29.
 */


public class AlidayuSMS {

    public boolean sendMessage(String phonenum,String verifyCode) {


//官网的URL, http请求就用这个链接
        String url = "http://gw.api.taobao.com/router/rest";
//成为开发者，创建应用后系统自动生成
        String appkey = "24641416";
        String secret = "51a354cb0c169c869c2df55aa8789de5";
//短信内容，请参照阿里大于文档中心的   接入指南 -》应用开发
        //  String json = JsonUtil.toJson(new AlidayuSMSBean());

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456"); //可选， 用于标识用户
        req.setSmsType("normal"); //默认使用 normal
        req.setSmsFreeSignName("注册验证"); //短信签名
        //req.setSmsParam(json); //短信内容
        // {"code":"1234","product":"lmxceshi"}
        //生成随机字串
     //   String verifyCode = VerifyCodeUtils.generateVerifyCode(6);

        req.setSmsParam("{\"code\":\""+verifyCode+"\",\"product\":\"lmxceshi\"}"); //短信内容
        req.setSmsTemplateCode("SMS_53840150"); //短信模板ID
        req.setRecNum(phonenum);//手机号码，如果是多个手机号码可以用逗号隔开

        try {
            AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
            System.out.println(rsp.getBody());
            //判断阿里大于返回码中是否包含“success”
            if(rsp.getBody().toString().contains("success")){
//              //  JSONObject ob= new JSONObject(rsp.getBody().toString());
//                JSONObject ob= new JSONObject()
//                JSONObject obj=ob.getJSONObject(RESUCCESS);
//                JSONObject object=obj.getJSONObject("result");
//                Boolean success=object.getBoolean("success");
                System.out.println("发送成功");
                return true;

            } else {
                System.out.println("连接成功，发送失败");
                return false;
            }


        } catch (Exception e) {
            System.out.println("连接失败");
            return false;
        }
    }


    public static void main(String[] args){
        AlidayuSMS test = new AlidayuSMS();
//        String alidayuCode = test.sendMessage("1551908903","123456");
//        System.out.println(alidayuCode);
        test.sendMessage("15519089033","123456");
    }
}