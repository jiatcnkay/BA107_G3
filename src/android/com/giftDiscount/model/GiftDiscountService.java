package android.com.giftDiscount.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import android.com.gift.model.GiftDAO_interface;
import android.com.gift.model.GiftService;
import android.com.gift.model.GiftVO;

public class GiftDiscountService {
	private GiftDiscountDAO_interface dao;
	private GiftService gSvc = new GiftService();
	
	public GiftDiscountService(){
		dao = new GiftDiscountDAO();
	}
	
	public void addGiftDiscount(GiftDiscountVO giftDiscountVO){
		dao.insert(giftDiscountVO);
	}
	
	public void updateGiftDiscount(GiftDiscountVO giftDiscountVO){
		dao.update(giftDiscountVO);
	}
	
	public void updateAmount(String giftd_no, Integer buyAmount, Connection con){
		dao.updateAmount(giftd_no, buyAmount, con);
	}
	
	public void deleteGiftDiscount(String giftd_no){
		dao.delete(giftd_no);
	}
	
	public GiftDiscountVO getOneGiftDiscount(String giftd_no){
		return dao.getByPrimaryKey(giftd_no);
	}
	
	public List<GiftDiscountVO> getAll(){
		return dao.getAll();
	}
	
	public List<GiftVO> getGiftD(){
		List<GiftDiscountVO> getAll = dao.getAll();
		List<GiftVO> getGiftD = new ArrayList<>();
		for(GiftDiscountVO list : getAll){
			//System.out.println(gSvc.getOneGift(list.getGift_no()));
			getGiftD.add(gSvc.getOneGift(list.getGift_no()));
		}
		return getGiftD;
	}
}
