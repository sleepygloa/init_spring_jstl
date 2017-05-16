package test.board.bean;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board/*")
public class BoardBean {
	
	@Autowired
	private SqlMapClientTemplate sqlMap;
	
	@RequestMapping("list.do")
	public String list(String pageNum , HttpServletRequest request){
		if (pageNum == null) {
            pageNum = "1";
        }
        int pageSize = 10;//�� �������� ���� ����
        int currentPage = Integer.parseInt(pageNum);
        int startRow = (currentPage - 1) * pageSize + 1;//�� �������� ���۱� ��ȣ
        int endRow = currentPage * pageSize;//�� �������� ������ �۹�ȣ
        int count = 0;
        int number=0;

        List articleList = null;
        
        count = (Integer)sqlMap.queryForObject("board.boardCount", null);//��ü ���� �� 
        if (count > 0) {
        	HashMap map = new HashMap();
        	map.put("startRow", startRow);
        	map.put("endRow", endRow);
            articleList = sqlMap.queryForList("board.boardAll", map); //���� �������� �ش��ϴ� �� ���
        } else {
            articleList = Collections.EMPTY_LIST;
        }

		number=count-(currentPage-1)*pageSize;//�۸�Ͽ� ǥ���� �۹�ȣ
        //�ش� �信�� ����� �Ӽ�
        request.setAttribute("currentPage", new Integer(currentPage));
        request.setAttribute("startRow", new Integer(startRow));
        request.setAttribute("endRow", new Integer(endRow));
        request.setAttribute("count", new Integer(count));
        request.setAttribute("pageSize", new Integer(pageSize));
		request.setAttribute("number", new Integer(number));
        request.setAttribute("articleList", articleList);
		return  "/board/list";
	}

	@RequestMapping("writeForm.do")
	public String writeForm(HttpServletRequest request){
	       //제목글과 답변글의 구분*
        int num = 0, ref = 1, re_step = 0, re_level = 0;
        
        
        try{  
          if(request.getParameter("num") != null){
	         num=Integer.parseInt(request.getParameter("num"));
	         ref=Integer.parseInt(request.getParameter("ref"));
	         re_step=Integer.parseInt(request.getParameter("re_step"));
	         re_level=Integer.parseInt(request.getParameter("re_level"));
	      }
		}catch(Exception e){e.printStackTrace();}
        //해당 뷰에서 사용할 속성
		request.setAttribute("num", new Integer(num));
        request.setAttribute("ref", new Integer(ref));
        request.setAttribute("re_step", new Integer(re_step));
        request.setAttribute("re_level", new Integer(re_level));
		return "/board/writeForm";
	}
	
	@RequestMapping("writePro.do")
	public String writePro(HttpServletRequest request){

//        request.setCharacterEncoding("UTF-8");//한글 인코딩
        
		
		
        BoardDataBean article = new BoardDataBean();//데이터처리 빈
		article.setNum(Integer.parseInt(request.getParameter("num")));
        article.setWriter(request.getParameter("writer"));
        article.setEmail(request.getParameter("email"));
        article.setSubject(request.getParameter("subject"));
        article.setPasswd(request.getParameter("passwd"));
        article.setReg_date(new Timestamp(System.currentTimeMillis()));
		article.setRef(Integer.parseInt(request.getParameter("ref")));
		article.setRe_step(Integer.parseInt(request.getParameter("re_step")));
		article.setRe_level(Integer.parseInt(request.getParameter("re_level")));
		article.setContent(request.getParameter("content"));
		article.setIp(request.getRemoteAddr());

		
		
		
		//query 처리를 위해 변수지정.
		int num = article.getNum();
		int ref = article.getRef();
		int re_step = article.getRe_step();
		int re_level = article.getRe_level();
		int number = 0;

		if(sqlMap.queryForObject("board.maxNum", null) == null){
			
		}else{
	    	number = (Integer)sqlMap.queryForObject("board.maxNum", null);
	    	number += 1;		
		}

    	System.out.println(number);
    	if(num != 0){
    		sqlMap.update("board.board_re_step_up", article);
    		article.setRe_step(article.getRe_step()+1);
    		article.setRe_level(article.getRe_level()+1);
    	}else{
    		
    		article.setRef(number);
    		article.setRe_step(0);
    		article.setRe_level(0);
    	}
    	
        sqlMap.insert("board.writeArticle", article); //���� �������� �ش��ϴ� �� ���
		

		return "/board/writePro";
	}
	
	
	@RequestMapping("content.do")
	public String content(int num, int pageNum, Model model){
		
		
		
		sqlMap.update("board.board_readcount_up", num);
		
		
		BoardDataBean dto = (BoardDataBean)sqlMap.queryForObject("board.getArticle", num);

       
  
        //해당 뷰에서 사용할 속성
        model.addAttribute("num", new Integer(num));
        model.addAttribute("pageNum", new Integer(pageNum));
        model.addAttribute("article", dto);
		
		return "/board/content";
	}

	@RequestMapping("updateForm.do")
	public String updateForm(int num, int pageNum, Model model){

		BoardDataBean article = (BoardDataBean)sqlMap.queryForObject("board.getArticle", num);


		//해당 뷰에서 사용할 속성
		model.addAttribute("pageNum", new Integer(pageNum));
		model.addAttribute("article", article);
		
		return "/board/updateForm";
	}
	
	@RequestMapping("updatePro.do")
	public String updatePro(HttpServletRequest request, int pageNum, Model model){


			BoardDataBean article = new BoardDataBean();
	        article.setNum(Integer.parseInt(request.getParameter("num")));
	        article.setWriter(request.getParameter("writer"));
	        article.setEmail(request.getParameter("email"));
	        article.setSubject(request.getParameter("subject"));
	        article.setContent(request.getParameter("content"));
	        article.setPasswd(request.getParameter("passwd"));
		    
	        int check;
	        
	        try{
	        sqlMap.update("board.updateArticle",article);
	        check = 1;
	        }catch(Exception e){
	        check = 0;
	        }

	        model.addAttribute("pageNum", new Integer(pageNum));
	        model.addAttribute("check", new Integer(check));
		
		return "/board/updatePro";
	}
	
	@RequestMapping("deleteForm.do")
	public String deleteForm(int num, int pageNum, Model model){



		//해당 뷰에서 사용할 속성
		model.addAttribute("num", new Integer(num));
        model.addAttribute("pageNum", new Integer(pageNum));

		
		return "/board/deleteForm";
	}
	
	
	@RequestMapping("deletePro.do")
	public String deletePro(int num, int pageNum, String passwd, Model model){

		int check = 0;
		
		//비밀번호 찾기
		String checkpaswd = (String)sqlMap.queryForObject("findPasswd", num);
	    
		if(passwd.equals(checkpaswd)){
			sqlMap.delete("deleteArticle", num);
			check = 1;
		}else{
			check = 0;
		}


        //해당 뷰에서 사용할 속성
        model.addAttribute("pageNum", new Integer(pageNum));
        model.addAttribute("check", new Integer(check));

		
		return "/board/deletePro";
	}
	
}









