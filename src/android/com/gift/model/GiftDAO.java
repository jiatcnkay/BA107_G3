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
	private static final String GET_PIC_STMT = "SELECT GIFT_PIC FROM�@GIFT WHERE GIFT_NO=?";
	
	@Override
	public void insert(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList) {
		/* * * * * * * * * * * * * * * * * * * * * * *
		 * ���s�W�|�̥H�U���Ƕi��A�Y�����ѫhrollback			 * 
		 * ------------------------------------------* 
		 * 1. �s�W§��[GIFT]							 * 
		 * 2. �s�W§�����ҩ���[GIFT_LABEL_LIST]			 * 
		 * * * * * * * * * * * * * * * * * * * * * * */
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String giftNoSeq = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			//1. �s�W§��[GIFT]
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
			
			//2. �s�W§�����ҩ���[GIFT_LABEL_LIST]
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
					System.err.println("rolled back-��-GiftDAO insert��");
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
		 * ���ק�|�̥H�U���Ƕi��A�Y�����ѫhrollback			 * 
		 * ------------------------------------------* 
		 * 1. �ק�§�����e[GIFT]						 * 
		 * 2. �ק�§�����ҩ���[GIFT_LABEL_LIST]			 * 
		 * * * * * * * * * * * * * * * * * * * * * * */
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			/* 1. �ק�§�����e[GIFT] */	
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
			/* 2. �ק�§�����ҩ���[GIFT_LABEL_LIST] */
			GiftLabelDetailService giftLabelDetailSvc = new GiftLabelDetailService();
			String gift_no = giftVO.getGift_no();
			
			//oriList���̾�giftl_no�Ѥp�ƨ�j
			List<GiftLabelDetailVO> oriList = giftLabelDetailSvc.getByGiftNo(gift_no);
			//�ӷ�=�ǤJ�����ҡ@�ت�=��Ʈw�{������
			int newIndex = 0, oriIndex = 0;
			if(giftLabelDetailList.isEmpty() && oriList.isEmpty()){
				//�p�G��§���ק��[�S��]������ҡA�B���Ʈw[�]�S��]������ҩ���
				//����Ʊ�������
			}else if(giftLabelDetailList.isEmpty() && !oriList.isEmpty()){
				//�p�G��§���ק��[�S��]������ҡA�B���Ʈw[��]���@���ҩ���
				//�h�N���Ʈw������§�����Ҧ����ҩ��Ӳ���
				giftLabelDetailSvc.deleteByGiftNo(gift_no, con);
			}else if(!giftLabelDetailList.isEmpty() && oriList.isEmpty()){
				//�p�G��§���ק��[��]������ҡA�����Ʈw[�S��]�������
				//�h�����s�W�Ҧ��ק�᪺����
				for(GiftLabelDetailVO giftLabelDetailVO: giftLabelDetailList){
				giftLabelDetailSvc.insertGiftLabelDetail(giftLabelDetailVO, con);
				}
			}else{
				//�p�G��§���ק��[��]������ҡA�B���Ʈw[�]��]�������
				//�h�]while�j��i����i��R���P�s�W
				while(newIndex<giftLabelDetailList.size() && oriIndex<oriList.size()){
					//�ק���newIndex���������
					GiftLabelDetailVO newVO  = giftLabelDetailList.get(newIndex);
					//���Ʈw��oriIndex���������
					GiftLabelDetailVO oriVO = oriList.get(oriIndex);
					String newStr = giftLabelDetailList.get(newIndex).getGiftl_no();
					String oriStr = oriList.get(oriIndex).getGiftl_no();
					//��Ӫ�����ҧQ�μ��ҦW��(giftl_no)�Ӥ���j�p
					int compareToAns = newStr.compareTo(oriStr);
					if(compareToAns > 0){
								//�׸ӫ᪺���ҽs��  > ���Ʈw���ҽs���A�h�R�����Ʈw����
								giftLabelDetailSvc.deleteOneGiftLabelDetail(oriVO, con);
								oriIndex++;
					}else if(compareToAns == 0){
								//�׸ӫ᪺���ҽs��  = ���Ʈw���ҽs���A�h���ܰ�
								newIndex++;
								oriIndex++;
					}else if(compareToAns < 0){
								//�ק�᪺���ҽs��  < ���Ʈw���ҽs���A�h�s�W�ק����
								giftLabelDetailSvc.insertGiftLabelDetail(newVO, con);
								newIndex++;
					}
				}
				
				//�Y�٦��Ѿl���ק���ҽs���A�h���Ʒs�W
				while(newIndex<giftLabelDetailList.size()){
					//�ק���newIndex���������
					GiftLabelDetailVO newVO  = giftLabelDetailList.get(newIndex);
					giftLabelDetailSvc.insertGiftLabelDetail(newVO, con);
					newIndex++;
				}
				
				//�Y�٦��Ѿl�����Ʈw���ҡA�h���ƧR��
				while(oriIndex<oriList.size()){
					//���Ʈw��oriIndex���������
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
					System.err.println("rolled back-��-GiftDAO update��");
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
					System.err.println("rolled back-��-GiftDAO updateTrackQty��");
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
					System.err.println("rolled back-��-GiftDAO updateTrackQty��");
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
		 * �ȴ���[�q���W�[]��§���i��R��						 *
		 * ���R���|�̥H�U���Ƕi��A�Y�����ѫhrollback			 * 
		 * ------------------------------------------* 
		 * 1. �R��§�����ҩ���[GIFT_LABEL_LIST]			 * 
		 * 2. �R��§��[GIFT]							 * 
		 * * * * * * * * * * * * * * * * * * * * * * */		
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			
			/* 1. �R��§�����ҩ���[GIFT_LABEL_LIST] */
			GiftLabelDetailService giftLabelDetailSvc = new GiftLabelDetailService();
			giftLabelDetailSvc.deleteByGiftNo(gift_no, con);
			
			/* 2. �R��§��[GIFT] */
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setString(1, gift_no);
			pstmt.executeUpdate();
			
			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if(con != null){
				try {
					System.err.print("Transaction is being ");
					System.err.println("rolled back-��-GiftDAO delete��");
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
