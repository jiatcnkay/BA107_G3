package android.com.friends_list.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import android.com.friends_list.model.FriendsListService;
import android.com.friends_list.model.FriendsListVO;
import android.com.main.ImageUtil;
import android.com.member.model.MemberService;
import android.com.member.model.MemberVO;

public class FriendsListServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		String outStr = "";
		String action = req.getParameter("action");
		System.out.println("½Ð¨D:"+action);
		FriendsListService fLSvc = new FriendsListService();
		MemberService memSvc = new MemberService();
		if ("getMemberFriends".equals(action)) {
			String mem_no = req.getParameter("mem_no");
			System.out.println("mem_no:"+mem_no);
			List<FriendsListVO> friendsList = fLSvc.getMemberFriends(mem_no);
			List<MemberVO> friendsDetailList = new ArrayList<>();
			MemberVO member = new MemberVO();
			for (int i = 0; i < friendsList.size(); i++) {
				System.out.println(friendsList.get(i).getMem_no_self());
				if (!friendsList.get(i).getMem_no_self().equals(mem_no)){
					System.out.println("1");
					member = memSvc.getOneByMemNo(friendsList.get(i).getMem_no_self());
				}else
					member = memSvc.getOneByMemNo(friendsList.get(i).getMem_no_other());
				int imageSize = Integer.parseInt(req.getParameter("imageSize"));
				member.setMemPhoto(ImageUtil.shrink(member.getMemPhoto(), imageSize));
				member.setMemAge(member.getMemBirthday().toString());
				member.setMemBirthday(null);
				friendsDetailList.add(member);
			}
			outStr = gson.toJson(friendsDetailList);
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
