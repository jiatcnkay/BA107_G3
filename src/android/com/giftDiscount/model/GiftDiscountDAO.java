package android.com.giftDiscount.model;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import javax.management.RuntimeErrorException;
import javax.naming.*;
import javax.sql.DataSource;

import android.com.giftDiscount.model.GiftDiscountVO;

import android.com.gift.model.GiftVO;

public class GiftDiscountDAO implements GiftDiscountDAO_interface{
	
	private static DataSource ds = null;
	static{
		try{
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup("java:comp/env/jdbc/BA107G3");
		}catch(NamingException e){
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_DISCOUNT(GIFTD_NO,GIFT_NO,GIFTD_START,GIFTD_END,GIFTD_PERCENT,GIFTD_AMOUNT) VALUES ('GD'||LPAD(to_char(GIFT_DISCOUNT_SEQ.NEXTVAL),3,'0'),?,?,?,?,?)";
	private static final String UPDATE_STMT = "UPDATE GIFT_DISCOUNT SET GIFT_NO=?,GIFTD_START=?,GIFTD_END=?,GIFTD_PERCENT=?,GIFTD_AMOUNT=? WHERE GIFTD_NO=?";
	private static final String UPDATE_AMOUNT_STMT = "UPDATE GIFT_DISCOUNT SET GIFTD_AMOUNT=? WHERE GIFTD_NO=?";
	private static final String DELETE_STMT = "DELETE FROM GIFT_DISCOUNT WHERE GIFTD_NO=?";
	private static final String FIND_BY_PK_STMT = "SELECT * FROM GIFT_DISCOUNT WHERE GIFTD_NO=?";
	private static final String GET_ALL_STMT 	= "SELECT * FROM GIFT_DISCOUNT ORDER BY GIFTD_NO ";
	private static final String GET_ALL = "SELECT GD.GIFTD_NO,GD.GIFT_NO,GD.GIFTD_END,GD.GIFTD_PERCENT,GD.GIFTD_AMOUNT FROM GIFT_DISCOUNT GD JOIN GIFT G ON G.GIFT_NO = GD.GIFT_NO WHERE G.GIFT_IS_ON LIKE '%上架中%' ORDER BY GIFTD_NO DESC";
	private static final String GET_ALL2 = "SELECT GD.GIFTD_NO,GD.GIFT_NO,GD.GIFTD_END,GD.GIFTD_PERCENT,GD.GIFTD_AMOUNT FROM GIFT_DISCOUNT GD "
			+ "JOIN GIFT G ON G.GIFT_NO = GD.GIFT_NO WHERE G.GIFT_IS_ON LIKE '%上架中%' "
			+ "AND GD.GIFTD_END > TO_DATE(?,'YYYY/MM/DD HH24:MI:SS') ORDER BY GIFTD_NO";
	
	@Override
	public void insert(GiftDiscountVO giftDiscountVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);
			pstmt.setString(1, giftDiscountVO.getGift_no());
			pstmt.setTimestamp(2, giftDiscountVO.getGiftd_start());
			pstmt.setTimestamp(3, giftDiscountVO.getGiftd_end());
			pstmt.setDouble(4, giftDiscountVO.getGiftd_percent());
			pstmt.setInt(5, giftDiscountVO.getGiftd_amount());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	@Override
	public void update(GiftDiscountVO giftDiscountVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_STMT);
			pstmt.setString(1, giftDiscountVO.getGift_no());
			pstmt.setTimestamp(2, giftDiscountVO.getGiftd_start());
			pstmt.setTimestamp(3, giftDiscountVO.getGiftd_end());
			pstmt.setDouble(4, giftDiscountVO.getGiftd_percent());
			pstmt.setInt(5, giftDiscountVO.getGiftd_amount());
			pstmt.setString(6, giftDiscountVO.getGiftd_no());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	@Override
	public void updateAmount(String giftd_no, Integer buyAmount, Connection con) {
		/* con從GiftOrderDetailDAO的insert()傳遞過來  */
		PreparedStatement pstmt = null;
		GiftDiscountVO giftDiscountVO = getByPrimaryKey(giftd_no);
		Integer oriAmount = giftDiscountVO.getGiftd_amount();
		try {
			pstmt = con.prepareStatement(UPDATE_AMOUNT_STMT);
			pstmt.setInt(1, oriAmount-buyAmount);
			pstmt.setString(2, giftd_no);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDiscountDAO updateAmount時");
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
	
	@Override
	public void delete(String giftd_no) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setString(1, giftd_no);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	@Override
	public GiftDiscountVO getByPrimaryKey(String giftd_no) {
		GiftDiscountVO giftDiscountVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_PK_STMT);
			pstmt.setString(1, giftd_no);
			
			rs = pstmt.executeQuery();
			rs.next();
			giftDiscountVO = new GiftDiscountVO();
			giftDiscountVO.setGiftd_no(rs.getString("giftd_no"));
			giftDiscountVO.setGift_no(rs.getString("gift_no"));
			giftDiscountVO.setGiftd_start(rs.getTimestamp("giftd_start"));
			giftDiscountVO.setGiftd_end(rs.getTimestamp("giftd_end"));
			giftDiscountVO.setGiftd_percent(rs.getDouble("giftd_percent"));
			giftDiscountVO.setGiftd_amount(rs.getInt("giftd_amount"));
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return giftDiscountVO;
	}

	@Override
	public List<GiftDiscountVO> getAll() {
		List<GiftDiscountVO> list = new ArrayList<>();
		GiftDiscountVO giftDiscountVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL2);
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String strDate = sdFormat.format(date);
			//System.out.println(strDate);
			pstmt.setString(1, strDate);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftDiscountVO = new GiftDiscountVO();
				giftDiscountVO.setGiftd_no(rs.getString("giftd_no"));
				giftDiscountVO.setGift_no(rs.getString("gift_no"));
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
		GiftDiscountDAO dao = new GiftDiscountDAO();
		List<GiftDiscountVO> gift = dao.getAll();
		for(GiftDiscountVO g : gift){
			System.out.println(g.getGiftd_no());
			System.out.println(g.getGiftd_start());
			System.out.println(g.getGiftd_end());
		}	
	}
	
}
