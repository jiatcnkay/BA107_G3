package android.com.giftReceive.model;

import java.sql.*;

import android.com.member.model.MemberService;

public class GiftReceiveDAO implements GiftReceiveDAO_interface{
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_RECEIVE(GIFTR_NO,MEM_NO_SELF,MEM_NO_OTHER,GIFTOD_NO,GIFTR_AMOUNT,GIFTR_TIME,GIFTR_MESSAGE) VALUES(to_char(sysdate,'yyyymmdd')||'-GR'||LPAD(to_char(GIFT_RECEIVE_SEQ.NEXTVAL),3,'0'),?,?,?,?,?,?)";
	
	@Override
	public void insert(GiftReceiveVO giftReceiveVO, Connection con) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * con從GiftOrderDetailDAO的insert()傳遞過來 	 *
		 * 且以下欄位在資料庫有預設值，故新增時不自行加入			 * 
		 * ------------------------------------------* 
		 * GIFTR_IS_FOUND default 等待中			 	 * 
		 * GIFTR_IS_OPEN  default 是				 	 * 
		 * GIFTR_NOTICE   default 未通知		 		 * 
		 * * * * * * * * * * * * * * * * * * * * * * */		
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(INSERT_STMT);
			pstmt.setString(1, giftReceiveVO.getMem_no_self());
			pstmt.setString(2, giftReceiveVO.getMem_no_other());
			pstmt.setString(3, giftReceiveVO.getGiftod_no());
			pstmt.setInt(4, giftReceiveVO.getGiftr_amount());
			pstmt.setTimestamp(5, giftReceiveVO.getGiftr_time());
			pstmt.setString(6, giftReceiveVO.getGiftr_message());
			pstmt.executeUpdate();
			
			//6. 修改收禮會員的總收禮數量[MEMBER]
			MemberService memberSvc = new MemberService();
//尚未			
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftReceiveDAO insert時");
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

