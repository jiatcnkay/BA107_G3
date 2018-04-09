package android.com.giftReceive.model;

import java.sql.Connection;

import android.com.giftReceive.model.GiftReceiveVO;

public interface GiftReceiveDAO_interface {
	public void insert(GiftReceiveVO giftReceiveVO, Connection con);
}
