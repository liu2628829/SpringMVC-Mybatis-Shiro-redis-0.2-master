package com.sojson.user.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.sojson.common.utils.vcode.AlidayuSMS;
import net.sf.json.JSONObject;

import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sojson.common.controller.BaseController;
import com.sojson.common.model.UUser;
import com.sojson.common.utils.LoggerUtils;
import com.sojson.common.utils.StringUtils;
import com.sojson.common.utils.VerifyCodeUtils;
import com.sojson.core.shiro.token.manager.TokenManager;
import com.sojson.user.manager.UserManager;
import com.sojson.user.service.UUserService;

/**
 * Created by mathman002 on 2017/10/20.
 */
@Controller
@Scope(value="prototype")
@RequestMapping("test")
public class RegistTest extends BaseController{

    @Resource
    UUserService userService;

    @RequestMapping(value="subRegister",method= RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> subRegister(String vcode, UUser entity){
        resultMap.put("status", 400);
        if(!VerifyCodeUtils.verifyCode(vcode)){
            resultMap.put("message", "验证码不正确！");
            return resultMap;
        }

        String phone =  entity.getphone();
/*		//
		AlidayuSMS test = new AlidayuSMS();
		test.sendMessage(phone);
		*/

        UUser user = userService.findUserByphone(phone);
        if(null != user){
            resultMap.put("message", "帐号|phone已经存在！");
            return resultMap;
        }
		/* else  发送手机短信验证码  */
        //	else AlidayuSMS.sendMessage(phone);



        Date date = new Date();
        entity.setCreateTime(date);
        entity.setLastLoginTime(date);
        //把密码md5
        entity = UserManager.md5Pswd(entity);
        //设置有效
        entity.setStatus(UUser._1);

        entity = userService.insert(entity);
        LoggerUtils.fmtDebug(getClass(), "注册插入完毕！", JSONObject.fromObject(entity).toString());
        entity = TokenManager.login(entity, Boolean.TRUE);
        LoggerUtils.fmtDebug(getClass(), "注册后，登录完毕！", JSONObject.fromObject(entity).toString());
        resultMap.put("message", "注册成功！");
        resultMap.put("status", 200);
        return resultMap;
    }

}
