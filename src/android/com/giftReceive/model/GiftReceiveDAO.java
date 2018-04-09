package android.com.giftReceive.model;

import java.sql.*;

import android.com.member.model.MemberService;

public class GiftReceiveDAO implements GiftReceiveDAO_interface{
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_RECEIVE(GIFTR_NO,MEM_NO_SELF,MEM_NO_OTHER,GIFTOD_NO,GIFTR_AMOUNT,GIFTR_TIME,GIFTR_MESSAGE) VALUES(to_char(sysdate,'yyyymmdd')||'-GR'||LPAD(to_char(GIFT_RECEIVE_SEQ.NEXTVAL),3,'0'),?,?,?,?,?,?)";
	
	@Override
	public void insert(GiftReceiveVO giftReceiveVO, Connection con) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * con�qGiftOrderDetailDAO��insert()�ǻ��L�� 	 *
		 * �B�H�U���b��Ʈw���w�]�ȡA�G�s�W�ɤ��ۦ�[�J			 * 
		 * ------------------------------------------* 
		 * GIFTR_IS_FOUND default ���ݤ�			 	 * 
		 * GIFTR_IS_OPEN  default �O				 	 * 
		 * GIFTR_NOTICE   default ���q��		 		 * 
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
			
			//6. �ק怜§�|�����`��§�ƶq[MEMBER]
			MemberService memberSvc = new MemberService();
//�|��			
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

