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
		 * 此新增會依以下順序進行，若有失敗則rollback *
		 * ---------------------------------------------* x1. 扣除會員點數[MEMBER] *
		 * X2. 更改折價券紀錄[COUPONS_RECORD](選填) * 3. 新增1筆訂單[GIFT_ORDER] * 4.
		 * 新增N筆訂單明細[GIFT_ORDER_DETAIL] * 5. 新增N筆明細的N筆送禮紀錄[GIFT_RECEIVE] * 6.
		 * 修改N*N個會員收禮數量[MEMBER] * 以上4~6會以loop的方式建立每筆明細的N筆送禮紀錄與N筆修改----- * * * *
		 * * * * * * * * * * * * * * * * * * * *
		 */
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = ds.getConnection();
			con.setAutoCommit(false);
			// 1. 扣除會員點數[MEMBER]
			MemberService memberSvc = new MemberService();
			int delDeposit = 0;
			for (GiftOrderDetailVO giftOrderDetailVO : giftOrderDetailMap.keySet()) {
				// 計算購買總金額
				delDeposit += giftOrderDetailVO.getGiftod_money();
			}
			memberSvc.updateDeposit(giftOrderVO.getMem_no(), delDeposit, con);

			// 2. 更改折價券紀錄[COUPONS_RECORD](選填)

			// 3. 新增1筆訂單[GIFT_ORDER]
			String giftoNoSeq = null;
			java.sql.Timestamp giftoTime = null;
			String cols[] = { "GIFTO_NO", "GIFTO_TIME" };// 訂單時間由DB自動產生
			pstmt = con.prepareStatement(INSERT_STMT, cols);
			pstmt.setString(1, giftOrderVO.getMem_no());
			pstmt.setString(2, giftOrderVO.getCoup_no());
			pstmt.setString(3, giftOrderVO.getGifto_remark());
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			if (rs.next())
				giftoNoSeq = rs.getString(1);
			giftoTime = rs.getTimestamp(2);

			// 4.init 新增N筆訂單明細[GIFT_ORDER_DETAIL]
			GiftOrderDetailService giftOrderDetailSvc = new GiftOrderDetailService();
			Set<Entry<GiftOrderDetailVO, List<GiftReceiveVO>>> set = giftOrderDetailMap.entrySet();
			GiftOrderDetailVO giftOrderDetailVO = null;
			List<GiftReceiveVO> giftReceiveList = null;
			// 6.init 紀錄各個限時優惠禮物的購買數量
			Map<String, Integer> giftdMap = new HashMap<>();
			// 7.init 紀錄給位收禮會員的收禮數量
			Map<String, Integer> receiveMap = new HashMap<>();
			for (Entry<GiftOrderDetailVO, List<GiftReceiveVO>> giftOrderDetail : set) {
				// 4.set 在訂單明細Table中填入訂單編號
				giftOrderDetailVO = giftOrderDetail.getKey();
				giftOrderDetailVO.setGifto_no(giftoNoSeq);
				// 5.set 在收贈禮Table中填入收贈禮時間
				giftReceiveList = giftOrderDetail.getValue();
				for (GiftReceiveVO giftReceiveVO : giftReceiveList) {
					giftReceiveVO.setGiftr_time(giftoTime);
					// 6.loop 記錄收禮人的收禮數量
					String mem_no_other = giftReceiveVO.getMem_no_other();
					Integer giftr_amount = giftReceiveVO.getGiftr_amount();
					if (receiveMap.containsKey(mem_no_other)) {
						Integer oriAmount = receiveMap.get(mem_no_other);
						receiveMap.put(mem_no_other, oriAmount + giftr_amount);
					} else {
						receiveMap.put(mem_no_other, giftr_amount);
					}
				}
				// 5.會跳轉到giftOrderDetailDAO執行
				giftOrderDetailSvc.insert(giftOrderDetail, con);

				// 7.loop 尋找該明細的禮物是否屬於限時優惠，若是則記錄購買數量
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

			// 6. 若屬於則扣除該限時優惠的數量
			GiftDiscountService giftDiscountSvc = new GiftDiscountService();
			for (String giftd_no : giftdMap.keySet()) {
				giftDiscountSvc.updateAmount(giftd_no, giftdMap.get(giftd_no), con);
			}

			// 7. 修改收禮會員的總收禮數量[MEMBER]
			for (String mem_no_other : receiveMap.keySet()) {
				memberSvc.updateRecGift(mem_no_other, receiveMap.get(mem_no_other), con);
			}

			con.commit();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			if (con != null) {
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
