package android.com.giftReceive.model;

import java.sql.Connection;

import android.com.giftReceive.model.GiftReceiveDAO;
import android.com.giftReceive.model.GiftReceiveDAO_interface;
import android.com.giftReceive.model.GiftReceiveVO;

public class GiftReceiveService {
	private GiftReceiveDAO_interface dao;

	public GiftReceiveService() {
		dao = new GiftReceiveDAO();
	}

	public void insert(GiftReceiveVO giftReceiveVO, Connection con){
		dao.insert(giftReceiveVO, con);
	}
}
	
