package login.spring.bean;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member/*")
public class LogonBean {

	@Autowired
	private SqlMapClientTemplate sqlMap;
	
	@RequestMapping("confirmId.do") // 회원가입 아이디 체크
	public String idCheck(HttpServletRequest request,Model model){
		String id = request.getParameter("id");
		
		int check= (Integer)sqlMap.queryForObject("test.idCheck", id);
		
		model.addAttribute("check", check);
		model.addAttribute("id",id);
		return "login_form/confirmId";
	}
	@RequestMapping("loginForm.do")
	public String loginForm(){ // 회원가입 창
		return "login_form/loginForm";
	}
	
	@RequestMapping("loginPro.do") // 로그인 서비스
	public String loginPro(HttpServletRequest request,HttpSession session,LogonDTO dto,Model model){
		String id = request.getParameter("id");
		String passwd = request.getParameter("passwd");
		
		int check = (Integer)sqlMap.queryForObject("test.loginCheck", dto);
		
		if(check == 1){
			session.setAttribute("loginId", id);
		}
		
		model.addAttribute("check",check);	
		return "login_form/loginPro";
	}
	
	@RequestMapping("main.do") // 회원가입 전 후 화면
	public String main(){
		return "login_form/main";
	}
	
	@RequestMapping("inputForm.do") // 회원가입 폼 창
	public String inputForm(){
		return "login_form/inputForm";
	}
	
	@RequestMapping("inputPro.do") // 회원가입 서비스
	public String inputPro(LogonDTO dto){
		sqlMap.insert("test.inputPro", dto);
		return "login_form/inputPro";
	}
	
	@RequestMapping("logout.do") // 로그아웃 서비스
	public String logout(HttpSession session){
		session.invalidate();
		return "login_form/logout";
	}
	
	@RequestMapping("modify.do")
	public String modify(){
		return "login_form/modify";
	}
	
	@RequestMapping("modifyForm.do") // 회원정보 수정 창
	public String modifyForm(HttpSession session,Model model){
		String id = (String)session.getAttribute("loginId");
		LogonDTO dto = (LogonDTO)sqlMap.queryForObject("test.modify", id);
		model.addAttribute("dto",dto);
		return "login_form/modifyForm";
	}
	
	@RequestMapping("modifyPro.do") // 회원정보 수정 서비스 
	public String modifyPro(LogonDTO dto){
		sqlMap.update("test.modifyPro", dto);
		return "login_form/modifyPro";
	}
	
	@RequestMapping("deleteForm.do")  // 회원탈퇴 창
	public String deleteForm(){
		return "login_form/deleteForm";
	}
	
	@RequestMapping("deletePro.do") // 회원탈퇴 서비스
	public String deletePro(HttpSession session,HttpServletRequest request,HashMap map){
		String id = (String)session.getAttribute("loginId");
		String passwd = request.getParameter("passwd");
		
		map.put("id", id);
		map.put("passwd",passwd);
		int check = (Integer)sqlMap.queryForObject("test.deleteCheck", map);
		
		if(check==1){
			sqlMap.delete("test.deletePro", map);
			session.invalidate();
		}
		request.setAttribute("check", check);
				
		return "login_form/deletePro";
	}
}
