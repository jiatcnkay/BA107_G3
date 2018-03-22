package android.com.member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.com.member.model.MemberDAO;
import android.com.member.model.MemberVO;

public class MemberServlet extends HttpServlet {
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonObject;
	private MemberDAO memberDAO = new MemberDAO();
	private MemberVO memberVO;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doPost(req,res);

	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		BufferedReader br = req.getReader();
		StringBuilder jsonIn = new StringBuilder();
		String line = null;
		while((line = br.readLine())!=null){
			jsonIn.append(line);
		}
		System.out.println(jsonIn);
		jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String param = jsonObject.get("param").getAsString();
		String outStr = "";
		switch(param){
			case "account":
				String account = jsonObject.get("account").getAsString();
				memberVO = memberDAO.memberAccount(account);
				outStr = gson.toJson(memberVO);
				break;
		}
		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		System.out.println(outStr);
		out.println(outStr);
	}
}
