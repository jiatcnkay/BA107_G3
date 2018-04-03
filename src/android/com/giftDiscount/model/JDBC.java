package android.com.giftDiscount.model;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.sql.DataSource;

public class JDBC implements GiftDiscountDAO_interface{
	
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:XE";
	String userid = "BA107G3";
	String passwd = "BA107G3";
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_DISCOUNT(GIFTD_NO,GIFT_NO,GIFTD_START,GIFTD_END,GIFTD_PERCENT,GIFTD_AMOUNT) VALUES ('GD'||LPAD(to_char(GIFT_DISCOUNT_SEQ.NEXTVAL),3,'0'),?,?,?,?,?)";
	private static final String UPDATE_STMT = "UPDATE GIFT_DISCOUNT SET GIFT_NO=?,GIFTD_START=?,GIFTD_END=?,GIFTD_PERCENT=?,GIFTD_AMOUNT=? WHERE GIFTD_NO=?";
	private static final String DELETE_STMT = "DELETE FROM GIFT_DISCOUNT WHERE GIFTD_NO=?";
	private static final String FIND_BY_PK_STMT = "SELECT * FROM GIFT_DISCOUNT WHERE GIFTD_NO=?";
	private static final String GET_ALL_STMT 	= "SELECT * FROM GIFT_DISCOUNT ORDER BY GIFTD_NO DESC";
	
	
	@Override
	public List<GiftDiscountVO> getAll() {
		List<GiftDiscountVO> list = new ArrayList<>();
		GiftDiscountVO giftDiscountVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url,userid,passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftDiscountVO = new GiftDiscountVO();
				giftDiscountVO.setGiftd_no(rs.getString("giftd_no"));
				giftDiscountVO.setGift_no(rs.getString("gift_no"));
				giftDiscountVO.setGiftd_start(rs.getTimestamp("giftd_start"));
				giftDiscountVO.setGiftd_end(rs.getTimestamp("giftd_end"));
				giftDiscountVO.setGiftd_percent(rs.getDouble("giftd_percent"));
				giftDiscountVO.setGiftd_amount(rs.getInt("giftd_amount"));
				list.add(giftDiscountVO);
			}
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(rs != null){
				try {
						rs.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				};
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
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
		return list;
	}
	
	public static void main (String[] args){
		JDBC dao = new JDBC();
		Timestamp s = new Timestamp(System.currentTimeMillis());
		List<GiftDiscountVO> gift = dao.getAll();
		for(GiftDiscountVO g : gift){
			System.out.println(g.getGiftd_no());
			System.out.println(g.getGiftd_start());
			System.out.println((g.getGiftd_start().getTime()));
			System.out.println(g.getGiftd_end());
		}	
	}

	@Override
	public void insert(GiftDiscountVO giftDiscountVO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GiftDiscountVO giftDiscountVO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String giftd_no) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GiftDiscountVO getByPrimaryKey(String giftd_no) {
		// TODO Auto-generated method stub
		return null;
	}

	
}