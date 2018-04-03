package android.com.gift.model;
import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import android.com.giftLabelDetail.model.GiftLabelDetailService;
import android.com.giftLabelDetail.model.GiftLabelDetailVO;

public class GiftDAO implements GiftDAO_interface{

	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/BA107G3");
		} catch (NamingException e) {
			e.printStackTrace(System.err);
		}
	}	
	
	private static final String INSERT_STMT = "INSERT INTO GIFT(GIFT_NO,GIFT_NAME,GIFT_CNT,GIFT_PRICE,GIFT_PIC) VALUES('G'||LPAD(to_char(GIFT_SEQ.NEXTVAL),3,'0'), ?, ?, ?, ?)";
	private static final String UPDATE_STMT = "UPDATE GIFT SET GIFT_NAME=?, GIFT_CNT=?, GIFT_PRICE=?, GIFT_PIC=?, GIFT_IS_ON=?, GIFT_TRACK_QTY=?, GIFT_BUY_QTY=? WHERE GIFT_NO=?";
	private static final String UPDATE_TRACK_QTY_STMT = "UPDATE GIFT SET GIFT_TRACK_QTY=? WHERE GIFT_NO=?";
	private static final String UPDATE_BUY_QTY_STMT = "UPDATE GIFT SET GIFT_BUY_QTY=? WHERE GIFT_NO=?";
	private static final String DELETE_STMT = "DELETE FROM GIFT WHERE GIFT_NO=?";
	private static final String FIND_BY_PK_STMT = "SELECT * FROM GIFT WHERE GIFT_NO=?";
	private static final String GET_ALL_STMT = "SELECT * FROM GIFT ORDER BY GIFT_NO";
	private static final String GET_PIC_STMT = "SELECT GIFT_PIC FROM　GIFT WHERE GIFT_NO=?";
	
	@Override
	public void insert(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * 此新增會依以下順序進行，若有失敗則rollback			 * 
		 * ------------------------------------------* 
		 * 1. 新增禮物[GIFT]							 * 
		 * 2. 新增禮物標籤明細[GIFT_LABEL_LIST]			 * 
		 * * * * * * * * * * * * * * * * * * * * * * */
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String giftNoSeq = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			//1. 新增禮物[GIFT]
			String cols[] = {"GIFT_NO"};
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftVO.getGift_name());
			pstmt.setString(2, giftVO.getGift_cnt());
			pstmt.setInt(3, giftVO.getGift_price());
			pstmt.setBytes(4, giftVO.getGift_pic());
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if(rs.next()){
			giftNoSeq = rs.getString(1);
			}
			
			//2. 新增禮物標籤明細[GIFT_LABEL_LIST]
			if(giftLabelDetailList != null){
				GiftLabelDetailService giftLabelDetailSvc = new GiftLabelDetailService();
				for(GiftLabelDetailVO giftLabelDetailVO: giftLabelDetailList){
					giftLabelDetailVO.setGift_no(giftNoSeq);
					giftLabelDetailSvc.insertGiftLabelDetail(giftLabelDetailVO, con);
				}
			}
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDAO insert時");
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

	@Override
	public void update(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * 此修改會依以下順序進行，若有失敗則rollback			 * 
		 * ------------------------------------------* 
		 * 1. 修改禮物內容[GIFT]						 * 
		 * 2. 修改禮物標籤明細[GIFT_LABEL_LIST]			 * 
		 * * * * * * * * * * * * * * * * * * * * * * */
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			/* 1. 修改禮物內容[GIFT] */	
			pstmt = con.prepareStatement(UPDATE_STMT);
			pstmt.setString(1, giftVO.getGift_name());
			pstmt.setString(2, giftVO.getGift_cnt());
			pstmt.setInt(3, giftVO.getGift_price());
			pstmt.setBytes(4, giftVO.getGift_pic());
			pstmt.setString(5, giftVO.getGift_is_on());
			pstmt.setInt(6, giftVO.getGift_track_qty());
			pstmt.setInt(7, giftVO.getGift_buy_qty());
			pstmt.setString(8, giftVO.getGift_no());
			pstmt.executeUpdate();
			/* 2. 修改禮物標籤明細[GIFT_LABEL_LIST] */
			GiftLabelDetailService giftLabelDetailSvc = new GiftLabelDetailService();
			String gift_no = giftVO.getGift_no();
			
			//oriList有依據giftl_no由小排到大
			List<GiftLabelDetailVO> oriList = giftLabelDetailSvc.getByGiftNo(gift_no);
			//來源=傳入的標籤　目的=資料庫現有標籤
			int newIndex = 0, oriIndex = 0;
			if(giftLabelDetailList.isEmpty() && oriList.isEmpty()){
				//如果該禮物修改後[沒有]任何標籤，且原資料庫[也沒有]任何標籤明細
				//什麼事情都不做
			}else if(giftLabelDetailList.isEmpty() && !oriList.isEmpty()){
				//如果該禮物修改後[沒有]任何標籤，且原資料庫[有]任一標籤明細
				//則將原資料庫對應該禮物的所有標籤明細移除
				giftLabelDetailSvc.deleteByGiftNo(gift_no, con);
			}else if(!giftLabelDetailList.isEmpty() && oriList.isEmpty()){
				//如果該禮物修改後[有]任何標籤，但原資料庫[沒有]任何標籤
				//則直接新增所有修改後的標籤
				for(GiftLabelDetailVO giftLabelDetailVO: giftLabelDetailList){
				giftLabelDetailSvc.insertGiftLabelDetail(giftLabelDetailVO, con);
				}
			}else{
				//如果該禮物修改後[有]任何標籤，且原資料庫[也有]任何標籤
				//則跑while迴圈進行比對進行刪除與新增
				while(newIndex<giftLabelDetailList.size() && oriIndex<oriList.size()){
					//修改後第newIndex的物件標籤
					GiftLabelDetailVO newVO  = giftLabelDetailList.get(newIndex);
					//原資料庫第oriIndex的物件標籤
					GiftLabelDetailVO oriVO = oriList.get(oriIndex);
					String newStr = giftLabelDetailList.get(newIndex).getGiftl_no();
					String oriStr = oriList.get(oriIndex).getGiftl_no();
					//兩個物件標籤利用標籤名稱(giftl_no)來比較大小
					int compareToAns = newStr.compareTo(oriStr);
					if(compareToAns > 0){
								//修該後的標籤編號  > 原資料庫標籤編號，則刪除原資料庫標籤
								giftLabelDetailSvc.deleteOneGiftLabelDetail(oriVO, con);
								oriIndex++;
					}else if(compareToAns == 0){
								//修該後的標籤編號  = 原資料庫標籤編號，則不變動
								newIndex++;
								oriIndex++;
					}else if(compareToAns < 0){
								//修改後的標籤編號  < 原資料庫標籤編號，則新增修改標籤
								giftLabelDetailSvc.insertGiftLabelDetail(newVO, con);
								newIndex++;
					}
				}
				
				//若還有剩餘的修改標籤編號，則全數新增
				while(newIndex<giftLabelDetailList.size()){
					//修改後第newIndex的物件標籤
					GiftLabelDetailVO newVO  = giftLabelDetailList.get(newIndex);
					giftLabelDetailSvc.insertGiftLabelDetail(newVO, con);
					newIndex++;
				}
				
				//若還有剩餘的原資料庫標籤，則全數刪除
				while(oriIndex<oriList.size()){
					//原資料庫第oriIndex的物件標籤
					GiftLabelDetailVO oriVO = oriList.get(oriIndex);
					giftLabelDetailSvc.deleteOneGiftLabelDetail(oriVO, con);
					oriIndex++;
				}
				
			}
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDAO update時");
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
	public void updateTrackQty(String gift_no, Integer gift_track_qty, Connection con) {
		GiftVO giftVO = null;
		PreparedStatement pstmt = null;
		
		try {
			giftVO = getByPrimaryKey(gift_no);
			pstmt = con.prepareStatement(UPDATE_TRACK_QTY_STMT);
			pstmt.setInt(1, giftVO.getGift_track_qty() + gift_track_qty);
			pstmt.setString(2, gift_no);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDAO updateTrackQty時");
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("rollback error occured. " + excep.getMessage());
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
	public void updateBuyQty(GiftVO giftVO, Integer gift_buy_qty, Connection con) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = con.prepareStatement(UPDATE_BUY_QTY_STMT);
			pstmt.setInt(1, gift_buy_qty);
			pstmt.setString(2, giftVO.getGift_no());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDAO updateTrackQty時");
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("rollback error occured. " + excep.getMessage());
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
	public void delete(String gift_no) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * 僅提供[從未上架]的禮物進行刪除						 *
		 * 此刪除會依以下順序進行，若有失敗則rollback			 * 
		 * ------------------------------------------* 
		 * 1. 刪除禮物標籤明細[GIFT_LABEL_LIST]			 * 
		 * 2. 刪除禮物[GIFT]							 * 
		 * * * * * * * * * * * * * * * * * * * * * * */		
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			/* 1. 刪除禮物標籤明細[GIFT_LABEL_LIST] */
			GiftLabelDetailService giftLabelDetailSvc = new GiftLabelDetailService();
			giftLabelDetailSvc.deleteByGiftNo(gift_no, con);
			
			/* 2. 刪除禮物[GIFT] */
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setString(1, gift_no);
			pstmt.executeUpdate();
			
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-由-GiftDAO delete時");
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
	public GiftVO getByPrimaryKey(String gift_no) {
		GiftVO giftVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_PK_STMT);
			pstmt.setString(1, gift_no);
			
			rs = pstmt.executeQuery();
			rs.next();
			giftVO = new GiftVO();
			giftVO.setGift_no(rs.getString("gift_no"));
			giftVO.setGift_name(rs.getString("gift_name"));
			giftVO.setGift_cnt(rs.getString("gift_cnt"));
			giftVO.setGift_price(rs.getInt("gift_price"));
			giftVO.setGift_pic(rs.getBytes("gift_pic"));
			giftVO.setGift_is_on(rs.getString("gift_is_on"));
			giftVO.setGift_track_qty(rs.getInt("gift_track_qty"));
			giftVO.setGift_buy_qty(rs.getInt("gift_buy_qty"));
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
		return giftVO;
	}

	@Override
	public List<GiftVO> getAll() {
		List<GiftVO> list = new ArrayList<>();
		GiftVO giftVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				giftVO = new GiftVO();
				giftVO.setGift_no(rs.getString("gift_no"));
				giftVO.setGift_name(rs.getString("gift_name"));
				giftVO.setGift_cnt(rs.getString("gift_cnt"));
				giftVO.setGift_price(rs.getInt("gift_price"));
				giftVO.setGift_pic(rs.getBytes("gift_pic"));
				giftVO.setGift_is_on(rs.getString("gift_is_on"));
				giftVO.setGift_track_qty(rs.getInt("gift_track_qty"));
				giftVO.setGift_buy_qty(rs.getInt("gift_buy_qty"));
				list.add(giftVO);
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
	public byte[] getPic(String gift_no) {
		byte[] pic = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_PIC_STMT);
			pstmt.setString(1, gift_no);
			rs = pstmt.executeQuery();
			
			rs.next();
			pic  = rs.getBytes("gift_pic");
			
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
		return pic;
	}



}
