package android.com.giftLabel.model;
import java.util.List;


public class GiftLabelService {
	private GiftLabelDAO_interface dao;
	
	public GiftLabelService(){
		dao = new GiftLabelDAO();
	}
	
	public void addGiftLabel(GiftLabelVO giftLabelVO){
		dao.insert(giftLabelVO);
	}
	
	public void updateGiftLabel(GiftLabelVO giftLabelVO){
		dao.update(giftLabelVO);
	}
	
	public void deleteGiftLabel(String giftl_no){
		dao.delete(giftl_no);
	}
	
	public GiftLabelVO getOneGiftLabelByNo(String giftl_no){
		return dao.getByPrimaryKey(giftl_no);
	}
	
	public GiftLabelVO getOneGiftLabelByName(String giftl_name){
		return dao.getByLabelName(giftl_name);
	}	
	
	public List<GiftLabelVO> getAll(){
		return dao.getAll();
	}
	
}
