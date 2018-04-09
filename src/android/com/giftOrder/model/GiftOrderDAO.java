package android.com.giftOrder.model;

import java.sql.*;
import javax.sql.*;
import java.util.*;
import java.util.Map.Entry;

import javax.naming.*;

import android.com.giftOrderDetail.model.*;
import android.com.giftReceive.model.*;

public class GiftOrderDAO implements GiftOrderDAO_interface {
	
	private static DataSource ds = null;
	static{
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/BA107G3");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_ORDER(GIFTO_NO,MEM_NO,COUP_NO,GIFTO_REMARK) VALUES (to_char(sysdate,'yyyymmdd')||'-GO'||LPAD(to_char(GIFT_ORDER_SEQ.NEXTVAL),3,'0'),?,?,?)";
	
	@Override
	public void insert(GiftOrderVO giftOrderVO, Map<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetailMap) {
		/* * * * * * * * * * * * * * * * * * * * * * * *
		 * ���s�W�|�̥H�U���Ƕi��A�Y�����ѫhrollback			 	* 
		 * ---------------------------------------------* 
		 *x1. �����|���I��[MEMBER]						 	* 
		 *X2. ����������[COUPONS_RECORD](���)		 	* 
		 * 3. �s�W1���q��[GIFT_ORDER]						*
		 * 4. �s�WN���q�����[GIFT_ORDER_DETAIL]			 	* 
		 * 5. �s�WN�����Ӫ�N���e§����[GIFT_RECEIVE]		 	* 
		 * 6. �ק�N*N�ӷ|����§�ƶq[MEMBER]				 	* 
		 * �H�W4~6�|�Hloop���覡�إߨC�����Ӫ�N���e§�����PN���ק�-----	* 
		 * * * * * * * * * * * * * * * * * * * * * * * */	
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			//1. �����|���I��[MEMBER]
			
			//2. ����������[COUPONS_RECORD](���)
			
			//3. �s�W1���q��[GIFT_ORDER]
			String giftoNoSeq = null;
			java.sql.Timestamp giftoTime = null;
			String cols[] = {"GIFTO_NO","GIFTO_TIME"};//�q��ɶ���DB�۰ʲ���
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftOrderVO.getMem_no());
			pstmt.setString(2, giftOrderVO.getCoup_no());
			pstmt.setString(3, giftOrderVO.getGifto_remark());
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if(rs.next())
				giftoNoSeq = rs.getString(1);
				giftoTime  = rs.getTimestamp(2);
			
			//4. �s�WN���q�����[GIFT_ORDER_DETAIL]
			GiftOrderDetailService giftOrderDetailSvc = new GiftOrderDetailService();
			Set<Entry<GiftOrderDetailVO, List<GiftReceiveVO>>> set = giftOrderDetailMap.entrySet();
			GiftOrderDetailVO giftOrderDetailVO = null;
			List<GiftReceiveVO> giftReceiveList = null;
			for(Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail: set){
				//�b�q�����Table����J�q��s��
				giftOrderDetailVO = giftOrderDetail.getKey();
				giftOrderDetailVO.setGifto_no(giftoNoSeq);
				//�b����§Table����J����§�ɶ�
				giftReceiveList = giftOrderDetail.getValue();
				for(GiftReceiveVO giftReceiveVO: giftReceiveList){
					giftReceiveVO.setGiftr_time(giftoTime);
				}
				//5.�P6.�|�����giftOrderDetailDAO����
				giftOrderDetailSvc.insert(giftOrderDetail, con);
			}
			
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-��-GiftOrderDAO insert��");
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("rollback error occured. " + excep.getMessage());
				}
			}
			throw new RuntimeException("A database error occured. " + e.getMessage());
		
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}			
		}
	}

}

