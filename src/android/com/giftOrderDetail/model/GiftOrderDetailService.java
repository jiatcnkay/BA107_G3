package android.com.giftOrderDetail.model;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import android.com.giftReceive.model.GiftReceiveVO;

public class GiftOrderDetailService {
	private GiftOrderDetailDAO_interface dao;

	public GiftOrderDetailService() {
		dao = new GiftOrderDetailDAO();
	}
	
	public void insert(Map.Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail, Connection con){
		dao.insert(giftOrderDetail, con);
	}

}
