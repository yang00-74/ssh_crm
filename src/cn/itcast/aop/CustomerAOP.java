package cn.itcast.aop;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import cn.itcast.entity.Permission;
import cn.itcast.entity.User;

/**
 * ssh使用AOP 1：创建AOP类，声明其为切面@Aspect， 声明方法为增强@Around(value = "execution(*
 * cn.itcast.action.CustomerAction.toAddPage(..))") 2.在spring配置文件中声明bean，并开启aop
 * <!-- 注解实现aop 需要开启aop操作 -->
 * <aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
 *
 * 3.使用SysContext中的ThreadLocal 保存request 和response
 * 4.使用Filter的功能为SysContext中的request 和 request 赋值 5.AOP类中获取request和response
 * 实现拦截逻辑
 */

@Aspect
public class CustomerAOP extends ActionSupport {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;
	private User user;

	public boolean checkLogined() throws ServletException, IOException {
		if (null == SysContext.getRequest()) {

			System.out.println("SysContext 没有拦截到request");
			request = ServletActionContext.getRequest();
		} else {
			System.out.println("SysContext 拦截到了request");
			request = SysContext.getRequest();
			response = SysContext.getResponse();
		}
		session = SysContext.getSession();

		System.out.println("登录状态检查");
		user = (User) session.getAttribute("user");
		if (null == user) {
			request.getRequestDispatcher("/jsp/warn.jsp").forward(request, response);
			System.out.println("未登录，已经响应了");
			return false;
		}
		return true;
	}
	public boolean checkPermission() throws Throwable {
        
		System.out.println("检查权限");
		Permission permissiom = user.getPermissionLevel();
		if (Integer.valueOf(permissiom.getPid()) > 1) {
			System.out.println("权限不足");
			request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
			return false;
		}
		return true;
	}
	
	@Around(value = "execution(* cn.itcast.action.CustomerAction.toAddPage(..))")
	public void toAddPage(ProceedingJoinPoint pp) throws Throwable{
	    if(checkLogined()){
	    	if(checkPermission()){
	    		pp.proceed();
	    	}
	    }	
	}
	
	@Around(value = "execution(* cn.itcast.action.CustomerAction.list(..))")
	public void toList(ProceedingJoinPoint pp) throws Throwable{
	    if(checkLogined()){
	    	if(checkPermission()){
	    		pp.proceed();
	    	}
	    }	
	}
	@Around(value = "execution(* cn.itcast.action.CustomerAction.listPage(..))")
	public void toListPage(ProceedingJoinPoint pp) throws Throwable{
	    if(checkLogined()){
	    	if(checkPermission()){
	    		pp.proceed();
	    	}
	    }	
	}

}
