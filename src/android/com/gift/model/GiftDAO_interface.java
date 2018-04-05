package android.com.gift.model;
import java.sql.Connection;
import java.util.List;
import java.util.Set;

import android.com.giftLabelDetail.model.GiftLabelDetailVO;

public interface GiftDAO_interface {
	public void insert(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList);
	public void update(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList);
	public void updateTrackQty(String gift_no, Integer gift_track_qty, Connection con);
	public void updateBuyQty(GiftVO giftVO, Integer gift_buy_qty, Connection con);
	public void delete(String gift_no);
	public GiftVO getByPrimaryKey(String gift_no);
	public Set<String> getByKeyWord(String keyword);
	public List<GiftVO> getAll();
	public byte[] getPic(String gift_no);
	
//	//查詢某標籤的禮物(一對多)(回傳 Set)
//	public Set<GiftVO> getGiftsByLabel(String giftl_name);

//	//萬用複合查詢(傳入參數型態Map)(回傳 List)
//	public List<GiftVO> getAll(Map<String, String[]> map); 

}
