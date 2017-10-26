package com.sojson.user.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.sojson.common.utils.vcode.AlidayuSMS;
import com.sojson.user.bo.UserOnlineBo;
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
import com.sojson.common.utils.JWT;
import com.sojson.common.utils.LoggerUtils;
import com.sojson.common.utils.StringUtils;
import com.sojson.common.utils.VerifyCodeUtils;
import com.sojson.core.shiro.token.manager.TokenManager;
import com.sojson.user.manager.UserManager;
import com.sojson.user.service.UUserService;

/**
 * 
 * 开发公司：itboy.net<br/>
 * 版权：itboy.net<br/>
 * <p>
 * 
 * 用户登录相关，不需要做登录限制
 * 
 * <p>
 * 
 * 区分　责任人　日期　　　　说明<br/>
 * 创建　周柏成　2016年5月3日 　<br/>
 * <p>
 * *******
 * <p>
 * @author zhou-baicheng
 * @phone  i@itboy.net
 * @version 1.0,2016年5月3日 <br/>
 * 
 */
@Controller
@Scope(value="prototype")
@RequestMapping("login")
public class UserLoginController extends BaseController {

	@Resource
	UUserService userService;
	
	/**
	 * 登录跳转
	 * @return
	 */
	@RequestMapping(value="login",method=RequestMethod.GET)
	public ModelAndView login(){
		
		return new ModelAndView("user/login");
	}
	/**
	 * 注册跳转
	 * @return
	 */
	@RequestMapping(value="register",method=RequestMethod.GET)
	public ModelAndView register(){
		
		return new ModelAndView("user/register");
	}
	/**
	 * 注册 && 登录
	 * @param vcode		验证码	
	 * @param entity	UUser实体
	 * @return
	 */
	@RequestMapping(value="subRegister",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> subRegister(String vcode,UUser entity){
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

	
	/**
	 * 登录提交
	 * Token verification function added by Chenney
	 * @param entity		登录的UUser
	 * @param rememberMe	是否记住
	 * @param request		request，用来取登录之前Url地址，用来登录后跳转到没有登录之前的页面。
	 * @return
	 */
	@RequestMapping(value="submitLogin",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> submitLogin(UUser entity,Boolean rememberMe,HttpServletRequest request){
		
		try {
			UUser login = new UUser(entity);
			
			entity = TokenManager.login(entity,rememberMe);
			String phoneNum = login.getphone();
			//用用户电话号码作为token PAYLOAD那段加密发送到客户端
			String jwtToken = JWT.sign(login, 60L*1000L*30L);
			resultMap.put("status", 200);
			resultMap.put("message", "登录成功");
			resultMap.put("userToken", jwtToken);
			resultMap.put("phoneNumber", phoneNum);
			
			
			/**
			 * shiro 获取登录之前的地址
			 * 之前0.1版本这个没判断空。
			 */
			SavedRequest savedRequest = WebUtils.getSavedRequest(request);
			String url = null ;
			if(null != savedRequest){
				url = savedRequest.getRequestUrl();
			}
			/**
			 * 我们平常用的获取上一个请求的方式，在Session不一致的情况下是获取不到的
			 * String url = (String) request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE);
			 */
			LoggerUtils.fmtDebug(getClass(), "获取登录之前的URL:[%s]",url);
			//如果登录之前没有地址，那么就跳转到首页。
			if(StringUtils.isBlank(url)){
				url = request.getContextPath() + "/user/index.shtml";
			}
			//跳转地址
			resultMap.put("back_url", url);
		/**
		 * 这里其实可以直接catch Exception，然后抛出 message即可，但是最好还是各种明细catch 好点。。
		 */
		} catch (DisabledAccountException e) {
			resultMap.put("status", 500);
			resultMap.put("message", "帐号已经禁用");
		} catch (Exception e) {
			resultMap.put("status", 500);
			resultMap.put("message", "帐号或密码错误");
		}
			
		return resultMap;
	}
	
	/**
	 * 退出
	 * @return
	 */
	@RequestMapping(value="logout",method =RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> logout(){
		try {
			TokenManager.logout();
			resultMap.put("status", 200);
		} catch (Exception e) {
			resultMap.put("status", 500);
			logger.error("errorMessage:" + e.getMessage());
			LoggerUtils.fmtError(getClass(), e, "退出出现错误，%s。", e.getMessage());
		}
		return resultMap;
	}


	/**
	 * 获取验证码
	 * @param entity	登录的UUser

	 * @return
	 */
	@RequestMapping(value="getMessage",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getMessage(UUser entity) {
		try {
			AlidayuSMS test = new AlidayuSMS();
			//生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(6);
			//存入Shiro会话session
			TokenManager.setVal2Session(VerifyCodeUtils.V_CODE, verifyCode);
			if(test.sendMessage(entity.getphone(),verifyCode)){
				resultMap.put("status", 200);
				resultMap.put("message", "发送短信验证码成功");
				resultMap.put("verifyCode", verifyCode);
			} else {
				resultMap.put("status", 500);
				resultMap.put("message", "调用阿里大于出现错误");
			}
		} catch (Exception e) {
			resultMap.put("status", 500);
			resultMap.put("message", "发送短信验证码出现错误");
		}
		return resultMap;
	}


	/**
	 * 忘记密码
	 * @param vcode		验证码
	 * @param entity	UUser实体
	 * @return
	 */
	@RequestMapping(value="forgetPswd",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> forgetPswd(String vcode,UUser entity){
		resultMap.put("status", 400);
	//	System.out.println(vcode);
		if(!VerifyCodeUtils.verifyCode(vcode)){
			resultMap.put("message", "验证码不正确！");
			return resultMap;
		}

		//获取手机号和新密码
		String phone =  entity.getphone();
		String newPswd =  entity.getPswd();
	//	System.out.println(phone);
		resultMap.put("message",phone);
		//根据用户手机号查询。
		UUser user = userService.findUserByphone(phone);
		if(null == user){
			resultMap.put("message", "帐号|phone不存在！");
			return resultMap;
		}
		if("admin".equals(phone)){
			resultMap.put("status", 300);
			resultMap.put("message", "管理员不准修改密码。");
			return resultMap;
		}
		user.setPswd(newPswd);
		//加工密码
		user = UserManager.md5Pswd(user);
		//修改密码
		userService.updateByPrimaryKeySelective(user);
		resultMap.put("status", 200);
		resultMap.put("message", "密码重置成功!");
		//重新登录一次
	//	TokenManager.login(entity, Boolean.TRUE);
		return resultMap;
	}
	/**
	 * 忘记密码
	 * @param vcode		验证码
	 * @param entity	UUser实体
	 * @return
	 */
	@RequestMapping(value="updatePswd1",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> updatePswd1(String vcode,UUser entity){
		resultMap.put("status", 400);
		if(!VerifyCodeUtils.verifyCode(vcode)){
			resultMap.put("message", "验证码不正确！");
			return resultMap;
		}

		String phone =  entity.getphone();
		String newPswd =  entity.getPswd();
		//根据用户手机号查询。
		UUser user = userService.findUserByphone(phone);
		if(null == user){
			resultMap.put("message", "帐号|phone不存在！");
			return resultMap;
		}
		if("admin".equals(phone)){
			resultMap.put("status", 300);
			resultMap.put("message", "管理员不准修改密码。");
			return resultMap;
		}
		user.setPswd(newPswd);
		/*   不需要后台加密密码，只要把前台传过来的RSA加密密码，放进去就行
		//加工密码
		user = UserManager.md5Pswd(user);
		*/
		//修改密码
		userService.updateByPrimaryKeySelective(user);
		resultMap.put("status", 200);
		resultMap.put("message", "密码重置成功!");
		//重新登录一次
		TokenManager.login(entity, Boolean.TRUE);
		return resultMap;
	}

	public static void main(String[] args){
		UserLoginController test = new UserLoginController();
		UUser user = new UUser();
//		user.setNickname("aaa");
		user.setphone("15519089033");
		user.setPswd("123123");
	//	test.forgetPswd(user);
	//	Map<String,Object> a = test.getMessage(user);
		//a.get("verifyCode");
	//	System.out.println(a.get("verifyCode"));
		//user.setPswd("57eb72e6b78a87a12d46a7f5e9315138");

	//	system.

	//	user = TokenManager.login(user,false);

//		test.getMessage1();

	}



}

