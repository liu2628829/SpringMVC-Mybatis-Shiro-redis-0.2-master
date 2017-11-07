package com.sojson.common.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.shiro.session.Session;

import net.sf.json.JSONObject;
/**
 * 
 * 开发公司：itboy.net<br/>
 * 版权：itboy.net<br/>
 * <p>
 * 
 * 用户
 * 
 * <p>
 * 
 * 区分　责任人　日期　　　　说明<br/>
 * 创建　周柏成　2016年5月25日 　<br/>
 * <p>
 * *******
 * <p>
 * @author zhou-baicheng
 * @phone  i@itboy.net
 * @version 1.0,2016年5月25日 <br/>
 * 
 */
public class UUser implements Serializable{
	private static final long serialVersionUID = 1L;
	//0:禁止登录
	public static final Long _0 = new Long(0);
	//1:有效
	public static final Long _1 = new Long(1);
	private Long id;
	/**昵称*/
    private String nickname;
    /**登录帐号*/
    private String loginName;
    /**电话  */
    private String phone;
    /**密码*/
    private transient String password;
    /**记录用户状态的SessionId**/
    private String sessionId;
    /**创建时间*/
    private Date createTime;
    /**最后登录时间*/
    private Date lastLoginTime;
    /**1:有效，0:禁止登录*/
    private Long status;
    /**性别 1:女，0:男*/
    private Long sex;
    /**年龄段*/
    private Long age;
    /**注册用户计数*/
    private String registerCount;
    /**头像*/
    private String userLogo;
    
    
    
    public UUser() {}
    public UUser(UUser user) {
		this.id = user.getId();
		this.nickname = user.getNickname();
		this.loginName = user.getLoginName();
		this.phone = user.getphone();
		this.password = user.getPassword();
		this.sessionId = user.getSession();
		this.createTime = user.getCreateTime();
		this.lastLoginTime = user.getLastLoginTime();
        this.sex  = user.getSex();
        this.age = user.getAge();
        this.registerCount = user.getRegisterCount();
        this.userLogo = user.getUserLogo();
	}


	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getphone() {
        return phone;
    }

    public Long getStatus() {
		return status;
	}
	public void setStatus(Long status) {
		this.status = status;
	}
	public void setphone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    public String toString(){
    	return JSONObject.fromObject(this).toString();
    }
    public JSONObject toJson(){
        JSONObject obj = JSONObject.fromObject(this);
        return obj;
    }
    //JSONObject json = JSONObject.fromObject(obj);//将java对象转换为json对象

	public void setSession(String sessionId) {
		this.sessionId = sessionId;
		
	}
	public String getSession() {
		return sessionId;
	}

    public Long getSex() {
        return sex;
    }

    public void setSex(Long sex) {
        this.sex = sex;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(String registerCount) {
        this.registerCount = registerCount;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }
}


