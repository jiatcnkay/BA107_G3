package android.com.giftDiscount.model;

import java.sql.Connection;
import java.util.List;

import android.com.gift.model.GiftVO;

public interface GiftDiscountDAO_interface {
	public void insert(GiftDiscountVO giftDiscountVO);
	public void update(GiftDiscountVO giftDiscountVO);
	public void updateAmount(String giftd_no, Integer buyAmount, Connection con);
	public void delete(String giftd_no);
	public GiftDiscountVO getByPrimaryKey(String giftd_no);
	public List<GiftDiscountVO> getAll();
	
}
