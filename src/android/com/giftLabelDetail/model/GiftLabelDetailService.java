package android.com.giftLabelDetail.model;
import java.sql.Connection;
import java.util.List;

public class GiftLabelDetailService {
	private GiftLabelDetailDAO_interface dao;
	
	public GiftLabelDetailService(){
		dao = new GiftLabelDetailDAO();
	}
	
	public void insertGiftLabelDetail(GiftLabelDetailVO giftLabelDetailVO, Connection con){
		dao.insert(giftLabelDetailVO, con);
	}
	
	public void deleteOneGiftLabelDetail(GiftLabelDetailVO giftLabelDetailVO, Connection con){
		dao.deleteOne(giftLabelDetailVO, con);
	}
	
	public void deleteByGiftNo(String gift_no, Connection con){
		dao.deleteByGiftNo(gift_no, con);
	}
	
	public List<GiftLabelDetailVO> getByGiftNo(String gift_no){
		return dao.getByGiftNo(gift_no);
	}
	
	public List<GiftLabelDetailVO> getByGiftLabelNo(String giftl_no){
		return dao.getByGiftLabelNo(giftl_no);
	}
}

