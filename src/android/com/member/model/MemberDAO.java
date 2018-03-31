package android.com.member.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import android.com.main.SqlUtil;

public class MemberDAO implements MemberDAO_interface {
	
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/BA107G3");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	static final String INSERT = "INSERT INTO MEMBER(MEM_NO, MEM_ACCOUNT, MEM_PASSWORD, MEM_JOIN_TIME, MEM_NAME, MEM_GENDER, MEM_BIRTHDAY, MEM_COUNTY, MEM_DEPOSIT, MEM_CONTACT, MEM_EMOTION, MEM_BONUS, MEM_BLOODTYPE, MEM_HEIGHT, MEM_WEIGHT, MEM_INTEREST, MEM_INTRO, MEM_ONLINE, MEM_LONGITUDE, MEM_LATITUDE, MEM_PHONE, MEM_MAIL, MEM_PHOTO, MEM_PROHIBIT, MEM_SETNOTIFY, MEM_TIMENOTIFY) "
			+ "VALUES ('M'||LPAD(to_char(MEMBER_SEQ.NEXTVAL),3,'0'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	static final String SEARCHBYACCOUNT = "SELECT * FROM MEMBER WHERE MEM_ACCOUNT = ?";
	
	static final String SEARCHBYMEM_NO = "SELECT * FROM MEMBER WHERE MEM_NO = ?";

	static final String UPDATE = "UPDATE MEMBER SET MEM_ACCOUNT = ?, MEM_PASSWORD = ? WHERE MEM_NO = ?";

	static final String ISMEMBER = "SELECT * FROM MEMBER WHERE MEM_ACCOUNT = ? AND MEM_PASSWORD = ?";

	static final String GETALL = "SELECT * FROM MEMBER";
	
	static String finalSQL;


	@Override
	public Boolean isMember(String account, String password) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean isMember = false;
		try {
			con = ds.getConnection();
			ps = con.prepareStatement(ISMEMBER);
			ps.setString(1, account);
			ps.setString(2, password);
			rs = ps.executeQuery();
			isMember = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return isMember;
	}

	@Override
	public void memberUpdate(MemberVO member) {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = ds.getConnection();
			ps = con.prepareStatement(UPDATE);
			con.setAutoCommit(false);
			ps.setString(1, member.getMemAccount());
			ps.setString(2, member.getMemPassword());
			ps.setString(3, member.getMemNo());
			ps.executeUpdate();
			con.commit();
		} catch (SQLException se) {
			try {
				con.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public MemberVO getOneByAccount(String account) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;

		try {
			con = ds.getConnection();
			ps = con.prepareStatement(SEARCHBYACCOUNT);
			ps.setString(1, account);

			rs = ps.executeQuery();
			rs.next();
			mvos = new MemberVO();
			mvos.setMemNo(rs.getString("MEM_NO"));
			mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
			mvos.setMemPassword(rs.getString("MEM_PASSWORD"));
			mvos.setMemName(rs.getString("MEM_NAME"));
			mvos.setMemGender(rs.getString("MEM_GENDER"));
		//	mvos.setMemBirthday(rs.getDate("MEM_BIRTHDAY"));
			mvos.setMemCounty(rs.getString("MEM_COUNTY"));
			mvos.setMemDeposit(rs.getInt("MEM_DEPOSIT"));
			mvos.setMemContact(rs.getString("MEM_CONTACT"));
			mvos.setMemEmotion(rs.getString("MEM_EMOTION"));
			mvos.setMemBonus(rs.getInt("MEM_BONUS"));
			mvos.setMemBloodType(rs.getString("MEM_BLOODTYPE"));
			mvos.setMemHeight(rs.getInt("MEM_HEIGHT"));
			mvos.setMemWeight(rs.getInt("MEM_WEIGHT"));
			mvos.setMemInterest(rs.getString("MEM_INTEREST"));
			mvos.setMemIntro(rs.getString("MEM_INTRO"));
			mvos.setMemOnline(rs.getString("MEM_ONLINE"));
			mvos.setMemLongitude(rs.getDouble("MEM_LONGITUDE"));
			mvos.setMemLatitude(rs.getDouble("MEM_LATITUDE"));
			mvos.setMemPhone(rs.getString("MEM_PHONE"));
			mvos.setMemMail(rs.getString("MEM_MAIL"));
			mvos.setMemPhoto(rs.getBytes("MEM_PHOTO"));
			mvos.setMemProhibit(rs.getString("MEM_PROHIBIT"));
			mvos.setMemProhibit(rs.getString("MEM_SETNOTIFY"));
	//		mvos.setMemTimeNotify(rs.getDate("MEM_TIMENOTIFY"));

		} catch (

		SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return mvos;
	}
	
	@Override
	public MemberVO getOneByMemNo(String mem_no) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;

		try {
			con = ds.getConnection();
			ps = con.prepareStatement(SEARCHBYMEM_NO);
			ps.setString(1, mem_no);

			rs = ps.executeQuery();
			rs.next();
			mvos = new MemberVO();
			mvos.setMemNo(rs.getString("MEM_NO"));
			mvos.setMemName(rs.getString("MEM_NAME"));
			mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
			mvos.setMemGender(rs.getString("MEM_GENDER"));
			mvos.setMemBirthday(rs.getDate("MEM_BIRTHDAY"));
			mvos.setMemCounty(rs.getString("MEM_COUNTY"));
			mvos.setMemContact(rs.getString("MEM_CONTACT"));
			mvos.setMemEmotion(rs.getString("MEM_EMOTION"));
			mvos.setMemBloodType(rs.getString("MEM_BLOODTYPE"));
			mvos.setMemHeight(rs.getInt("MEM_HEIGHT"));
			mvos.setMemWeight(rs.getInt("MEM_WEIGHT"));
			mvos.setMemInterest(rs.getString("MEM_INTEREST"));
			mvos.setMemIntro(rs.getString("MEM_INTRO"));
			mvos.setMemOnline(rs.getString("MEM_ONLINE"));
			mvos.setMemLongitude(rs.getDouble("MEM_LONGITUDE"));
			mvos.setMemLatitude(rs.getDouble("MEM_LATITUDE"));
			mvos.setMemPhoto(rs.getBytes("MEM_PHOTO"));
			mvos.setMemProhibit(rs.getString("MEM_PROHIBIT"));

		} catch (

		SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return mvos;
	}

	@Override
	public List<MemberVO> getLike(Map<String, String> map) {
		List<MemberVO> memberList = new ArrayList<MemberVO>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;
		try {
			con = ds.getConnection();
			finalSQL = "select * from Member " + SqlUtil.get_WhereCondition(map);
			System.out.println(finalSQL);
			ps = con.prepareStatement(finalSQL);
			rs = ps.executeQuery();
			while (rs.next()) {
				mvos = new MemberVO();
				mvos.setMemNo(rs.getString("MEM_NO"));
				mvos.setMemName(rs.getString("MEM_NAME"));
				mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
				mvos.setMemGender(rs.getString("MEM_GENDER"));
				mvos.setMemBirthday(rs.getDate("MEM_BIRTHDAY"));
				mvos.setMemCounty(rs.getString("MEM_COUNTY"));
				mvos.setMemContact(rs.getString("MEM_CONTACT"));
				mvos.setMemEmotion(rs.getString("MEM_EMOTION"));
				mvos.setMemBloodType(rs.getString("MEM_BLOODTYPE"));
				mvos.setMemHeight(rs.getInt("MEM_HEIGHT"));
				mvos.setMemWeight(rs.getInt("MEM_WEIGHT"));
				mvos.setMemInterest(rs.getString("MEM_INTEREST"));
				mvos.setMemIntro(rs.getString("MEM_INTRO"));
				mvos.setMemOnline(rs.getString("MEM_ONLINE"));
				mvos.setMemLongitude(rs.getDouble("MEM_LONGITUDE"));
				mvos.setMemLatitude(rs.getDouble("MEM_LATITUDE"));
				mvos.setMemPhoto(rs.getBytes("MEM_PHOTO"));
				mvos.setMemProhibit(rs.getString("MEM_PROHIBIT"));
				memberList.add(mvos);
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return memberList;
	}

	@Override
	public List<MemberVO> getAll() {
		List<MemberVO> memberList = new ArrayList<MemberVO>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;
		try {
			con = ds.getConnection();
			ps = con.prepareStatement(GETALL);
			rs = ps.executeQuery();
			while (rs.next()) {
				mvos = new MemberVO();
				mvos.setMemNo(rs.getString("MEM_NO"));
				mvos.setMemName(rs.getString("MEM_NAME"));
				mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
				mvos.setMemGender(rs.getString("MEM_GENDER"));
				mvos.setMemBirthday(rs.getDate("MEM_BIRTHDAY"));
				mvos.setMemCounty(rs.getString("MEM_COUNTY"));
				mvos.setMemContact(rs.getString("MEM_CONTACT"));
				mvos.setMemEmotion(rs.getString("MEM_EMOTION"));
				mvos.setMemBloodType(rs.getString("MEM_BLOODTYPE"));
				mvos.setMemHeight(rs.getInt("MEM_HEIGHT"));
				mvos.setMemWeight(rs.getInt("MEM_WEIGHT"));
				mvos.setMemInterest(rs.getString("MEM_INTEREST"));
				mvos.setMemIntro(rs.getString("MEM_INTRO"));
				mvos.setMemOnline(rs.getString("MEM_ONLINE"));
				mvos.setMemLongitude(rs.getDouble("MEM_LONGITUDE"));
				mvos.setMemLatitude(rs.getDouble("MEM_LATITUDE"));
				mvos.setMemPhoto(rs.getBytes("MEM_PHOTO"));
				mvos.setMemProhibit(rs.getString("MEM_PROHIBIT"));
				memberList.add(mvos);
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.setAutoCommit(true);
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return memberList;
	}
	

}
