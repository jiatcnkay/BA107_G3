package android.com.giftLabel.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class GiftLabelDAO implements GiftLabelDAO_interface {

	private static DataSource ds = null;
	static {
		Context ctx;
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/BA107G3");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}
	
	private static final String INSERT_STMT = "INSERT INTO GIFT_LABEL VALUES('GL'||LPAD(to_char(GIFT_LABEL_SEQ.NEXTVAL),3,'0'),?)";
	private static final String UPDATE_STMT = "UPDATE GIFT_LABEL SET GIFTL_NAME=? WHERE GIFTL_NO=?";
	private static final String DELETE_STMT = "DELETE FROM GIFT_LABEL WHERE GIFTL_NO=?";
	private static final String FIND_BY_LABELNAME_STMT = "SELECT * FROM GIFT_LABEL WHERE GIFTL_NAME=?";
	private static final String FIND_BY_PK_STMT = "SELECT * FROM GIFT_LABEL WHERE GIFTL_NO=?";
	private static final String GET_ALL_STMT    = "SELECT * FROM GIFT_LABEL ORDER BY GIFTL_NO";
	
	@Override
	public void insert(GiftLabelVO giftLabelVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);
			
			pstmt.setString(1, giftLabelVO.getGiftl_name());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally {
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
	
	}

	@Override
	public void update(GiftLabelVO giftLabelVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_STMT);
			
			pstmt.setString(1, giftLabelVO.getGiftl_name());
			pstmt.setString(2, giftLabelVO.getGiftl_no());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException("A database error occured. " + e.getMessage());
		} finally{
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
		
	}

	@Override
	public void delete(String giftl_no) {
		/* 僅提供[未被任何禮物使用]的標籤進行刪除  */	
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setString(1, giftl_no);
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
	public GiftLabelVO getByPrimaryKey(String giftl_no) {
		GiftLabelVO giftLabelVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_PK_STMT);
			pstmt.setString(1, giftl_no);
			rs = pstmt.executeQuery();
			rs.next();
			giftLabelVO = new GiftLabelVO();
			giftLabelVO.setGiftl_no(rs.getString("giftl_no"));
			giftLabelVO.setGiftl_name(rs.getString("giftl_name"));

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
		return giftLabelVO;
	}

	@Override
	public List<GiftLabelVO> getAll() {
		List<GiftLabelVO> list = new ArrayList<GiftLabelVO>();
		GiftLabelVO giftLabelVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con .prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftLabelVO = new GiftLabelVO();
				giftLabelVO.setGiftl_no(rs.getString("giftl_no"));
				giftLabelVO.setGiftl_name(rs.getString("giftl_name"));	
				list.add(giftLabelVO);
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

	@Override
	public GiftLabelVO getByLabelName(String giftl_name) {
		GiftLabelVO giftLabelVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_LABELNAME_STMT);
			pstmt.setString(1, giftl_name);
			rs = pstmt.executeQuery();
			rs.next();
			giftLabelVO = new GiftLabelVO();
			giftLabelVO.setGiftl_no(rs.getString("giftl_no"));
			giftLabelVO.setGiftl_name(rs.getString("giftl_name"));

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
		return giftLabelVO;
	}

}

