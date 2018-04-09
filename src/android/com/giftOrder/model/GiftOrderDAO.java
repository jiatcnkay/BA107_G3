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
		 * 此新增會依以下順序進行，若有失敗則rollback			 	* 
		 * ---------------------------------------------* 
		 *x1. 扣除會員點數[MEMBER]						 	* 
		 *X2. 更改折價券紀錄[COUPONS_RECORD](選填)		 	* 
		 * 3. 新增1筆訂單[GIFT_ORDER]						*
		 * 4. 新增N筆訂單明細[GIFT_ORDER_DETAIL]			 	* 
		 * 5. 新增N筆明細的N筆送禮紀錄[GIFT_RECEIVE]		 	* 
		 * 6. 修改N*N個會員收禮數量[MEMBER]				 	* 
		 * 以上4~6會以loop的方式建立每筆明細的N筆送禮紀錄與N筆修改-----	* 
		 * * * * * * * * * * * * * * * * * * * * * * * */	
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			//1. 扣除會員點數[MEMBER]
			
			//2. 更改折價券紀錄[COUPONS_RECORD](選填)
			
			//3. 新增1筆訂單[GIFT_ORDER]
			String giftoNoSeq = null;
			java.sql.Timestamp giftoTime = null;
			String cols[] = {"GIFTO_NO","GIFTO_TIME"};//訂單時間由DB自動產生
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftOrderVO.getMem_no());
			pstmt.setString(2, giftOrderVO.getCoup_no());
			pstmt.setString(3, giftOrderVO.getGifto_remark());
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if(rs.next())
				giftoNoSeq = rs.getString(1);
				giftoTime  = rs.getTimestamp(2);
			
			//4. 新增N筆訂單明細[GIFT_ORDER_DETAIL]
			GiftOrderDetailService giftOrderDetailSvc = new GiftOrderDetailService();
			Set<Entry<GiftOrderDetailVO, List<GiftReceiveVO>>> set = giftOrderDetailMap.entrySet();
			GiftOrderDetailVO giftOrderDetailVO = null;
			List<GiftReceiveVO> giftReceiveList = null;
			for(Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail: set){
				//在訂單明細Table中填入訂單編號
				giftOrderDetailVO = giftOrderDetail.getKey();
				giftOrderDetailVO.setGifto_no(giftoNoSeq);
				//在收贈禮Table中填入收贈禮時間
				giftReceiveList = giftOrderDetail.getValue();
				for(GiftReceiveVO giftReceiveVO: giftReceiveList){
					giftReceiveVO.setGiftr_time(giftoTime);
				}
				//5.與6.會跳轉到giftOrderDetailDAO執行
				giftOrderDetailSvc.insert(giftOrderDetail, con);
			}
			
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftOrderDAO insert時");
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

