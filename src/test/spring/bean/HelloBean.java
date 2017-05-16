package test.spring.bean;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/member/*")
public class HelloBean {

	@Autowired
	private SqlMapClientTemplate sqlMap;

	@RequestMapping("loginForm.do")
	public String hello() {
		System.out.println("loginForm");
		return "/0511/loginForm";
	}

	@RequestMapping("loginPro.do")
	public String loginPro(HttpSession session, TestDTO dto, Model model) {
		int check = (Integer) sqlMap.queryForObject("test.loginCheck", dto);
		if (check == 1) {
			session.setAttribute("memId", dto.getId());
		}
		model.addAttribute("check", check);
		return "/0511/loginPro";
	}

	@RequestMapping("upload.do")
	public String upload() {
		return "/0511/uploadForm";
	}

	@RequestMapping("uploadPro.do")
	public String uploadPro(MultipartHttpServletRequest request) {
		MultipartFile mf = request.getFile("save"); // 파라미터 이름
		String fileName = mf.getOriginalFilename();	// 파일 이름
		File copyFile = new File("e://save//"+fileName); // 복사할 위치 
		try {
			mf.transferTo(copyFile);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "/0511/uploadPro";
	}
}





