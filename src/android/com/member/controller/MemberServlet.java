package android.com.member.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import android.com.main.ImageUtil;
import android.com.member.model.MemberService;
import android.com.member.model.MemberVO;

public class MemberServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		MemberService memSvc = new MemberService();
		String outStr = "";
		
		String action = req.getParameter("action");
		System.out.println(action);
		List<MemberVO> memList;
		
		if ("isMember".equals(action)) {
			String account = req.getParameter("account");
			String password = req.getParameter("password");
			outStr = String.valueOf(memSvc.isMember(account, password));
		} 
		
		else if ("getOneByAccount".equals(action)) {
			String account = req.getParameter("account");
			MemberVO memberVO = memSvc.getOneByAccount(account);
			int imageSize = Integer.parseInt(req.getParameter("imageSize"));
			memberVO.setMemPhoto(ImageUtil.shrink(memberVO.getMemPhoto(), imageSize));
			memberVO.setMemAge(memberVO.getMemBirthday().toString());
			memberVO.setMemBirthday(null);
			outStr = gson.toJson(memberVO);
		} 
		
		else if("getLike".equals(action)){
			String test = req.getParameter("s");
			String jsonIn = req.getParameter("map");
			System.out.println(jsonIn);
			Type mapType = new TypeToken<Map<String , String>>() {
			}.getType();
			Map<String , String> map = gson.fromJson(jsonIn.toString(), mapType);
			System.out.println(map);
			memList = memSvc.getLike(map);
			int imageSize = Integer.parseInt(req.getParameter("imageSize"));
			for (int i = 0; i < memList.size(); i++) {
				MemberVO member = memList.get(i);
				member.setMemPhoto(ImageUtil.shrink(member.getMemPhoto(),imageSize));
				member.setMemAge(member.getMemBirthday().toString());
				member.setMemBirthday(null);
			}
			outStr = gson.toJson(memList);	
		}
		
		else if ("getAllMember".equals(action)) {
			memList = memSvc.getAll();
			for (int i = 0; i < memList.size(); i++) {
				MemberVO member = memList.get(i);
				int imageSize = Integer.parseInt(req.getParameter("imageSize"));
				member.setMemPhoto(ImageUtil.shrink(member.getMemPhoto(),imageSize));
				member.setMemAge(member.getMemBirthday().toString());
				member.setMemBirthday(null);
			}
			outStr = gson.toJson(memList);
		}
		
		else if("getPopular".equals(action)){
			memList = memSvc.getPopular();
			for (int i = 0; i < memList.size(); i++) {
				MemberVO member = memList.get(i);
				int imageSize = Integer.parseInt(req.getParameter("imageSize"));
				member.setMemPhoto(ImageUtil.shrink(member.getMemPhoto(),imageSize));
				member.setMemAge(member.getMemBirthday().toString());
				member.setMemBirthday(null);
			}
			outStr = gson.toJson(memList);
		}
			
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		//System.out.println(outStr);
		out.print(outStr);
		out.close();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}
}
