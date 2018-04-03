package android.com.giftDiscount.model;

import java.util.List;

public class GiftDiscountService {
	private GiftDiscountDAO_interface dao;
	
	public GiftDiscountService(){
		dao = new GiftDiscountDAO();
	}
	
	public void addGiftDiscount(GiftDiscountVO giftDiscountVO){
		dao.insert(giftDiscountVO);
	}
	
	public void updateGiftDiscount(GiftDiscountVO giftDiscountVO){
		dao.update(giftDiscountVO);
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
}
