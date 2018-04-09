package android.com.giftLabelDetail.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class GiftLabelDetailDAO implements GiftLabelDetailDAO_interface{

	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/BA107G3");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_LABEL_DETAIL VALUES(?,?)";
	private static final String DELETE_STMT = "DELETE FROM GIFT_LABEL_DETAIL WHERE GIFT_NO=? AND GIFTL_NO=?";
	private static final String GET_BY_GIFTNO_STMT    = "SELECT * FROM GIFT_LABEL_DETAIL WHERE GIFT_NO =? ORDER BY GIFTL_NO";
	private static final String GET_BY_GIFTLABELNO_STMT = "SELECT * FROM GIFT_LABEL_DETAIL WHERE GIFTL_NO=? ORDER BY GIFT_NO";
	
	@Override
	public void insert(GiftLabelDetailVO giftLabelDetailVO, Connection con) {
		/* con從GiftDAO的insert()傳遞過來  */
		PreparedStatement pstmt = null;
		
		try {
			pstmt = con.prepareStatement(INSERT_STMT);
			pstmt.setString(1, giftLabelDetailVO.getGift_no());
			pstmt.setString(2, giftLabelDetailVO.getGiftl_no());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftLabelDetailDAO insert時");
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

//	@Override
//	public void delete(GiftLabelListVO giftLabelListVO) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void deleteOne(GiftLabelDetailVO giftLabelDetailVO, Connection con) {
		/* con從GiftDAO的insert()傳遞過來  */
		PreparedStatement pstmt = null;
		
		try {
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setString(1, giftLabelDetailVO.getGift_no());
			pstmt.setString(2, giftLabelDetailVO.getGiftl_no());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftLabelDetailDAO deleteOne時");
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
	public void deleteByGiftNo(String gift_no, Connection con) {
		//取得該禮物的所有標籤
		List<GiftLabelDetailVO> giftLabelDetailList = getByGiftNo(gift_no);
		PreparedStatement pstmt = null;
		try {
			for(GiftLabelDetailVO giftLabelDetailVO: giftLabelDetailList){
				pstmt = con.prepareStatement(DELETE_STMT);
				pstmt.setString(1, giftLabelDetailVO.getGift_no());
				pstmt.setString(2, giftLabelDetailVO.getGiftl_no());
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftLabelDetailDAO deleteByGiftNo時");
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
	public List<GiftLabelDetailVO> getByGiftNo(String gift_no) {
		List<GiftLabelDetailVO> list = new ArrayList<>();
		GiftLabelDetailVO giftLabelListVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_BY_GIFTNO_STMT);
			pstmt.setString(1, gift_no);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftLabelListVO = new GiftLabelDetailVO();
				giftLabelListVO.setGift_no(rs.getString("gift_no"));
				giftLabelListVO.setGiftl_no(rs.getString("giftl_no"));
				list.add(giftLabelListVO);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
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

	@Override
	public List<GiftLabelDetailVO> getByGiftLabelNo(String giftl_no) {
		List<GiftLabelDetailVO> list = new ArrayList<>();
		GiftLabelDetailVO giftLabelListVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_BY_GIFTLABELNO_STMT);
			pstmt.setString(1, giftl_no);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftLabelListVO = new GiftLabelDetailVO();
				giftLabelListVO.setGift_no(rs.getString("gift_no"));
				giftLabelListVO.setGiftl_no(rs.getString("giftl_no"));
				list.add(giftLabelListVO);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}
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

}

