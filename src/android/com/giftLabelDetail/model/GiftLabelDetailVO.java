package android.com.giftLabelDetail.model;
import java.io.Serializable;

@SuppressWarnings("serial")
public class GiftLabelDetailVO implements Serializable{
	private String gift_no;
	private String giftl_no;
	
	public GiftLabelDetailVO() {
		super();
	}

	public String getGift_no() {
		return gift_no;
	}

	public void setGift_no(String gift_no) {
		this.gift_no = gift_no;
	}

	public String getGiftl_no() {
		return giftl_no;
	}

	public void setGiftl_no(String giftl_no) {
		this.giftl_no = giftl_no;
	}
	
}

