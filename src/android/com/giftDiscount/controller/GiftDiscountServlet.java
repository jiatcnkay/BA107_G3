package android.com.giftDiscount.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import android.com.gift.model.GiftVO;
import android.com.giftDiscount.model.GiftDiscountService;
import android.com.giftDiscount.model.GiftDiscountVO;
import android.com.main.ImageUtil;

@WebServlet("/GiftDiscountServlet")
public class GiftDiscountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String CONTENT_TYPE = "text/html; charset=UTF-8";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		String outStr = "";
		GiftDiscountService gdSvc = new GiftDiscountService();
		List<GiftDiscountVO> giftDlist = new ArrayList<>();
		List<GiftVO> giftD = new ArrayList<>();
		String action = req.getParameter("action");
		System.out.println(action);

		if ("getAll".equals(action)) {
			giftDlist = gdSvc.getAll();
			System.out.println(giftDlist);
			outStr = gson.toJson(giftDlist);
		}

		else if ("getGiftD".equals(action)) {
			giftD = gdSvc.getGiftD();
			int imageSize = Integer.parseInt(req.getParameter("imageSize"));
			for(GiftVO gift : giftD){
				gift.setGift_pic(ImageUtil.shrink(gift.getGift_pic(), imageSize));
			}
			outStr = gson.toJson(giftD);
		}

		res.setContentType(CONTENT_TYPE);
		PrintWriter out = res.getWriter();
		//System.out.println(outStr);
		out.print(outStr);
		out.close();

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		doPost(req, res);
	}
}
