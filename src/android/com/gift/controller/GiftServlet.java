package android.com.gift.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;

import android.com.gift.model.GiftService;
import android.com.gift.model.GiftVO;
import android.com.giftLabel.model.GiftLabelVO;
import android.com.giftLabelDetail.model.GiftLabelDetailVO;
import android.com.main.ImageUtil;

@WebServlet("/GiftServlet")
public class GiftServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		String outStr = "";
		GiftService gSvc = new GiftService();
		List<GiftVO> list = new ArrayList<>();
		String action = req.getParameter("action");
		System.out.println(action);

		if ("getALL".equals(action)) {
			list = gSvc.getAll();
			outStr = gson.toJson(list);
		}

		else if ("getByKeyWord".equals(action)) {
			String keyword = req.getParameter("keyword");
			Set<String> giftnolist = gSvc.getByKeyWord(keyword);
			Iterator<String> it = giftnolist.iterator();
			int imageSize = Integer.parseInt(req.getParameter("imageSize"));
			while (it.hasNext()) {
				GiftVO gift = gSvc.getOneGift(it.next());
				if (gift != null) {
					gift.setGift_pic(ImageUtil.shrink(gift.getGift_pic(), imageSize));
					System.out.println(gift);
					list.add(gift);
				}
			}
			outStr = gson.toJson(list);
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
