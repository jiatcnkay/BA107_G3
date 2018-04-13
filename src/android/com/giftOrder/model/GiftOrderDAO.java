package android.com.giftOrder.model;

import java.sql.*;
import javax.sql.*;

import android.com.giftDiscount.model.GiftDiscountService;
import android.com.giftOrderDetail.model.GiftOrderDetailService;
import android.com.giftReceive.model.GiftReceiveVO;

import android.com.giftOrderDetail.model.GiftOrderDetailVO;
import android.com.member.model.MemberService;

import java.util.*;
import java.util.Map.Entry;

import javax.naming.*;

import android.com.giftOrderDetail.model.*;
import android.com.giftReceive.model.*;

public class GiftOrderDAO implements GiftOrderDAO_interface {

	private static DataSource ds = null;
	static {
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
		/*
		 * * * * * * * * * * * * * * * * * * * * * * * *
		 * ���s�W�|�̥H�U���Ƕi��A�Y�����ѫhrollback *
		 * ---------------------------------------------* x1. �����|���I��[MEMBER] *
		 * X2. ����������[COUPONS_RECORD](���) * 3. �s�W1���q��[GIFT_ORDER] * 4.
		 * �s�WN���q�����[GIFT_ORDER_DETAIL] * 5. �s�WN�����Ӫ�N���e§����[GIFT_RECEIVE] * 6.
		 * �ק�N*N�ӷ|����§�ƶq[MEMBER] * �H�W4~6�|�Hloop���覡�إߨC�����Ӫ�N���e§�����PN���ק�----- * * * *
		 * * * * * * * * * * * * * * * * * * * *
		 */
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			// 1. �����|���I��[MEMBER]
			MemberService memberSvc = new MemberService();
			int delDeposit = 0;
			for (GiftOrderDetailVO giftOrderDetailVO : giftOrderDetailMap.keySet()) {
				// �p���ʶR�`���B
				delDeposit += giftOrderDetailVO.getGiftod_money();
			}
			memberSvc.updateDeposit(giftOrderVO.getMem_no(), delDeposit, con);

			// 2. ����������[COUPONS_RECORD](���)

			// 3. �s�W1���q��[GIFT_ORDER]
			String giftoNoSeq = null;
			java.sql.Timestamp giftoTime = null;
			String cols[] = { "GIFTO_NO", "GIFTO_TIME" };// �q��ɶ���DB�۰ʲ���
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftOrderVO.getMem_no());
			pstmt.setString(2, giftOrderVO.getCoup_no());
			pstmt.setString(3, giftOrderVO.getGifto_remark());
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if (rs.next())
				giftoNoSeq = rs.getString(1);
			giftoTime = rs.getTimestamp(2);

			// 4.init �s�WN���q�����[GIFT_ORDER_DETAIL]
			GiftOrderDetailService giftOrderDetailSvc = new GiftOrderDetailService();
			Set<Entry<GiftOrderDetailVO, List<GiftReceiveVO>>> set = giftOrderDetailMap.entrySet();
			GiftOrderDetailVO giftOrderDetailVO = null;
			List<GiftReceiveVO> giftReceiveList = null;
			// 6.init �����U�ӭ����u�f§�����ʶR�ƶq
			Map<String, Integer> giftdMap = new HashMap<>();
			// 7.init �������즬§�|������§�ƶq
			Map<String, Integer> receiveMap = new HashMap<>();
			for (Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail : set) {
				// 4.set �b�q�����Table����J�q��s��
				giftOrderDetailVO = giftOrderDetail.getKey();
				giftOrderDetailVO.setGifto_no(giftoNoSeq);
				// 5.set �b����§Table����J����§�ɶ�
				giftReceiveList = giftOrderDetail.getValue();
				for (GiftReceiveVO giftReceiveVO : giftReceiveList) {
					giftReceiveVO.setGiftr_time(giftoTime);
					// 6.loop �O����§�H����§�ƶq
					String mem_no_other = giftReceiveVO.getMem_no_other();
					Integer giftr_amount = giftReceiveVO.getGiftr_amount();
					if (receiveMap.containsKey(mem_no_other)) {
						Integer oriAmount = receiveMap.get(mem_no_other);
						receiveMap.put(mem_no_other, oriAmount + giftr_amount);
					} else {
						receiveMap.put(mem_no_other, giftr_amount);
					}
				}
				// 5.�|�����giftOrderDetailDAO����
				giftOrderDetailSvc.insert(giftOrderDetail, con);

				// 7.loop �M��ө��Ӫ�§���O�_�ݩ󭭮��u�f�A�Y�O�h�O���ʶR�ƶq
				String giftd_no = giftOrderDetailVO.getGiftd_no();
				Integer giftod_amount = giftOrderDetailVO.getGiftod_amount();
				if (giftd_no != null && !"".equals(giftd_no)) {
					if (giftdMap.containsKey(giftd_no)) {
						Integer oriAmount = giftdMap.get(giftd_no);
						giftdMap.put(giftd_no, oriAmount + giftod_amount);
					} else {
						giftdMap.put(giftd_no, giftod_amount);
					}
				}
			}

			// 6. �Y�ݩ�h�����ӭ����u�f���ƶq
			GiftDiscountService giftDiscountSvc = new GiftDiscountService();
			for (String giftd_no : giftdMap.keySet()) {
				giftDiscountSvc.updateAmount(giftd_no, giftdMap.get(giftd_no), con);
			}

			// 7. �ק怜§�|�����`��§�ƶq[MEMBER]
			for (String mem_no_other : receiveMap.keySet()) {
				memberSvc.updateRecGift(mem_no_other, receiveMap.get(mem_no_other), con);
			}

			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if (con != null) {
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
			if (rs != null) {
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
