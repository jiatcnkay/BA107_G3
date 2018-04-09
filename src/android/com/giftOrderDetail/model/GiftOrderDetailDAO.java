package android.com.giftOrderDetail.model;

import java.sql.*;
import java.util.*;

import android.com.giftReceive.model.*;

public class GiftOrderDetailDAO implements GiftOrderDetailDAO_interface {
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_ORDER_DETAIL VALUES (to_char(sysdate,'yyyymmdd')||'-GOD'||LPAD(to_char(GIFT_ORDER_DETAIL_SEQ.NEXTVAL),3,'0'),?,?,?,?,?,?,?)";
	
	@Override
	public void insert(Map.Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail, Connection con) {
		/* con�qGiftOrderDAO��insert()�ǻ��L��  */
		GiftOrderDetailVO giftOrderDetailVO = giftOrderDetail.getKey();
		List<GiftReceiveVO> giftReceiveList = giftOrderDetail.getValue();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String giftodNoSeq = null;
			String cols[] = {"GIFTOD_NO"};
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftOrderDetailVO.getGift_no());
			pstmt.setString(2, giftOrderDetailVO.getGifto_no());
			pstmt.setString(3, giftOrderDetailVO.getGiftod_no());
			pstmt.setInt(4, giftOrderDetailVO.getGiftod_unit());
			pstmt.setInt(5, giftOrderDetailVO.getGiftod_amount());
			pstmt.setInt(6, giftOrderDetailVO.getGiftod_money());
			pstmt.setInt(7, giftOrderDetailVO.getGiftod_inventory());
			pstmt.executeUpdate();
			
			rs= pstmt.getGeneratedKeys();
			if(rs.next())
				giftodNoSeq = rs.getString(1);
			
			//5. �s�WN���e§����[GIFT_RECEIVE]	
			GiftReceiveService giftReceiveSvc = new GiftReceiveService();
			for(GiftReceiveVO giftReceiveVO: giftReceiveList){
				//�b����§Table����J�q����ӽs��
				giftReceiveVO.setGiftod_no(giftodNoSeq);
				//6.�|�����giftReceiveDAO����
				giftReceiveSvc.insert(giftReceiveVO, con);
			}
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-��-GiftReceiveDAO insert��");
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("rollback error occured." + excep.getMessage());
				}
			}
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

}

