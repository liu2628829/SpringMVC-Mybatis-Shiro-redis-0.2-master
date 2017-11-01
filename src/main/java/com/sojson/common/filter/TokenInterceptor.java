package com.sojson.common.filter;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.sojson.common.model.UUser;
import com.sojson.common.utils.JWT;
import com.sojson.common.utils.responsedata.ResponseData;

public class TokenInterceptor implements HandlerInterceptor{


	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}


	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	//拦截每个请求
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		response.setCharacterEncoding("utf-8");
		String jwtToken = request.getParameter("userToken");
		ResponseData responseData = ResponseData.ok();
		//token不存在
		if(null != jwtToken) {
			UUser login = JWT.unsign(jwtToken, UUser.class);
			String phoneNum = request.getParameter("phoneNumber");
			//解密token后的phoneNum与用户传来的phoneNum不一致，一般都是token过期
			if(null != phoneNum && null != login) {
				if(phoneNum.equals(login.getphone())) {
					return true;
				}
				else
				{
					responseData = ResponseData.forbidden();
					responseMessage(response, response.getWriter(), responseData);
					return false;
				}
			}
			else
			{
				responseData = ResponseData.forbidden();
				responseMessage(response, response.getWriter(), responseData);
				return false;
			}
		}
		else
		{
			responseData = ResponseData.forbidden();
			responseMessage(response, response.getWriter(), responseData);
			return false;
		}
	}

	//	请求不通过，返回错误信息给客户端
	private void responseMessage(HttpServletResponse response, PrintWriter out, ResponseData responseData) {
		responseData = ResponseData.forbidden();
		response.setContentType("application/json; charset=utf-8");
		String json = JSONObject.toJSONString(responseData);
		out.print(json);
		out.flush();
		out.close();
	}
}
