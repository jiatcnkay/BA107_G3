package android.com.member.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class MemberDAO implements MemberDAO_interface{
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private static final String USER = "Toast";
	private static final String PASS = "Toast";
	
	static final String INSERT = "INSERT INTO MEMBER(MEM_NO, MEM_ACCOUNT, MEM_PASSWORD, MEM_JOIN_TIME, MEM_NAME, MEM_GENDER, MEM_BIRTHDAY, MEM_COUNTY, MEM_DEPOSIT, MEM_CONTACT, MEM_EMOTION, MEM_BONUS, MEM_BLOODTYPE, MEM_HEIGHT, MEM_WEIGHT, MEM_INTEREST, MEM_INTRO, MEM_ONLINE, MEM_LONGITUDE, MEM_LATITUDE, MEM_PHONE, MEM_MAIL, MEM_PHOTO, MEM_PROHIBIT, MEM_SETNOTIFY, MEM_TIMENOTIFY) "
			+ "VALUES ('M'||LPAD(to_char(MEMBER_SEQ.NEXTVAL),3,'0'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	static final String UPDATE = "UPDATE MEMBER SET MEM_ACCOUNT = ?, MEM_PASSWORD = ? WHERE MEM_NO = ?";
	
	static final String SINGLE_SEARCH = "SELECT MEM_ACCOUNT,MEM_PASSWORD FROM MEMBER WHERE MEM_NO = ?";
	
	static final String GETALL = "SELECT * FROM MEMBER";
	
	static final String ACCOUNT_SEARCH = "SELECT MEM_ACCOUNT,MEM_PASSWORD FROM MEMBER WHERE MEM_ACCOUNT = ?";
	
	static{
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void memberAdd(MemberVO member) {
		Connection con = null;
		PreparedStatement ps = null;
		
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			ps = con.prepareStatement(INSERT);

			ps.setString(1, member.getMemAccount());
			ps.setString(2, member.getMemPassword());
			ps.setDate(3, member.getMemJoinTime());
			ps.setString(4, member.getMemName());
			ps.setString(5, member.getMemGender());
			ps.setDate(6, member.getMemBirthday());
			ps.setString(7, member.getMemCounty());
			ps.setInt(8, member.getMemDeposit());
			ps.setString(9, member.getMemContact());
			ps.setString(10, member.getMemEmotion());
			ps.setInt(11, member.getMemBonus());
			ps.setString(12, member.getMemBloodType());
			ps.setInt(13, member.getMemHeight());
			ps.setInt(14, member.getMemWeight());
			ps.setString(15, member.getMemInterest());
			ps.setString(16, member.getMemIntro());
			ps.setString(17, member.getMemOnline());
			ps.setDouble(18, member.getMemLongitude());
			ps.setDouble(19, member.getMemLatitude());
			ps.setString(20, member.getMemPhone());
			ps.setString(21, member.getMemMail());
			ps.setBytes(22, member.getMemPhoto());
			ps.setString(23, member.getMemProhibit());
			ps.setString(24, member.getMemSetNotify());
			ps.setDate(25, member.getMemTimeNotify());
	
			ps.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}
	

	@Override
	public void memberUpdate(MemberVO member) {
		Connection con = null;
		PreparedStatement ps = null;
		
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			ps = con.prepareStatement(UPDATE);

			ps.setString(1, member.getMemAccount());
			ps.setString(2, member.getMemPassword());
			ps.setString(3, member.getMemNo());
		
	
			ps.executeUpdate();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public MemberVO memberSelect(String name) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;
		
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			ps = con.prepareStatement(SINGLE_SEARCH);
			ps.setString(1, name);
			
			rs = ps.executeQuery();
			while(rs.next()){
				mvos = new MemberVO();
				mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
				mvos.setMemPassword(rs.getString("MEM_PASSWORD"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mvos;
	}

	@Override
	public List<MemberVO> getAll() {
		List<MemberVO> memberList = new ArrayList<MemberVO>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			ps = con.prepareStatement(GETALL);
			rs = ps.executeQuery();
			while(rs.next()){
				mvos = new MemberVO();
				mvos.setMemNo(rs.getString("MEM_NO"));
				mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
				mvos.setMemPassword(rs.getString("MEM_PASSWORD"));
				mvos.setMemJoinTime(rs.getDate("MEM_JOIN_TIME"));
				mvos.setMemName(rs.getString("MEM_NAME"));
				mvos.setMemGender(rs.getString("MEM_GENDER"));
				mvos.setMemBirthday(rs.getDate("MEM_BIRTHDAY"));
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
				mvos.setMemTimeNotify(rs.getDate("MEM_TIMENOTIFY"));
				memberList.add(mvos);
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
		return memberList;
	}


	@Override
	public MemberVO memberAccount(String account) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MemberVO mvos = null;
		
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			ps = con.prepareStatement(ACCOUNT_SEARCH);
			ps.setString(1, account);
			
			rs = ps.executeQuery();
			rs.next();
				mvos = new MemberVO();
				mvos.setMemAccount(rs.getString("MEM_ACCOUNT"));
				mvos.setMemPassword(rs.getString("MEM_PASSWORD"));
				
		} catch (SQLException e) {
			System.out.println("無此帳號");
		}
		return mvos;
	}		
}
