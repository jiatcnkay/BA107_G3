package android.com.talk.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import android.com.friends_list.model.FriendsListVO;

public class TalkDAO implements TalkDAO_interface{
	
	private static DataSource ds = null;
	static {
		try{
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup("java:comp/env/jdbc/BA107G3");
		}catch(NamingException e){
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_TALK = "INSERT INTO TALK(TALK_NO,MEM_NO_SEND,MEM_NO_GET,TALK_CNT)VALUES('T'||LPAD(to_char(TALK_SEQ.NEXTVAL),3,'0'),?,?,?)";
	private static final String UPDATE_TALK = "UPDATE TALK SET MEM_NO_SEND=?,MEM_NO_GET=?,TALK_TIME=?,TALK_CNT=? WHERE MEM_NO_SEND=? AND MEM_NO_GET=?";
	private static final String DELETE_TALK = "DELETE TALK WHERE MEM_NO_SEND=? AND MEM_NO_GET=?";
	private static final String FIND_ONE_TALK = "SELECT*FROM TALK WHERE (MEM_NO_SEND=? AND MEM_NO_GET=?) OR (MEM_NO_SEND=? AND MEM_NO_GET=?)";
	private static final String GET_ALL = "SELECT*FROM TALK";

	@Override
	public void insert(TalkVO talkvo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_TALK);
			
			pstmt.setString(1, talkvo.getMem_no_send());
			pstmt.setString(2,talkvo.getMem_no_get());
			pstmt.setString(3,talkvo.getTalk_cnt());
			
			pstmt.executeUpdate();
			System.out.println("新增成功");
			
		}catch(SQLException se){
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
			if(con!=null){
				try{
					con.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public void update(TalkVO talkvo) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_TALK);
			
			pstmt.setString(1, talkvo.getMem_no_send());
			pstmt.setString(2,talkvo.getMem_no_get());
			pstmt.setTimestamp(3,talkvo.getTalk_time());
			pstmt.setString(4,talkvo.getTalk_cnt());
			pstmt.setString(5, talkvo.getMem_no_send());
			pstmt.setString(6,talkvo.getMem_no_get());
			
			pstmt.executeUpdate();
			System.out.println("修改成功");
			
		}catch(SQLException se){
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
			if(con!=null){
				try{
					con.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public void delete(FriendsListVO friends) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_TALK);
			
			pstmt.setString(1, friends.getMem_no_self());
			pstmt.setString(2, friends.getMem_no_other());
			
			pstmt.executeUpdate();
			System.out.println("刪除成功");
			
		}catch(SQLException se){
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
			if(con!=null){
				try{
					con.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public TalkVO findTalkByFriends(FriendsListVO friends) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		TalkVO talk = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_ONE_TALK);
			
			pstmt.setString(1, friends.getMem_no_self());
			pstmt.setString(2, friends.getMem_no_other());
			pstmt.setString(3, friends.getMem_no_other());
			pstmt.setString(4, friends.getMem_no_self());
			rs = pstmt.executeQuery();
			rs.next();
			talk = new TalkVO();
			talk.setTalk_no(rs.getString("talk_no"));
			talk.setMem_no_send(rs.getString("mem_no_send"));
			talk.setMem_no_get(rs.getString("mem_no_get"));
			talk.setTalk_time(rs.getTimestamp("talk_time"));
			talk.setTalk_cnt(rs.getString("talk_cnt"));
			
			
			
		}catch(SQLException se){
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
			if(con!=null){
				try{
					con.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
		}
		return talk;
	}

	@Override
	public Set<TalkVO> getAll() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Set<TalkVO>set = new LinkedHashSet<TalkVO>();
		TalkVO talk = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL);
			rs = pstmt.executeQuery();

			while(rs.next()){
				talk = new TalkVO();
				talk.setTalk_no(rs.getString("talk_no"));
				talk.setMem_no_send(rs.getString("mem_no_send"));
				talk.setMem_no_get(rs.getString("mem_no_get"));
				talk.setTalk_time(rs.getTimestamp("talk_time"));
				talk.setTalk_cnt(rs.getString("talk_cnt"));
				set.add(talk);
			}
			
			
		}catch(SQLException se){
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
			if(con!=null){
				try{
					con.close();
				}catch(SQLException se){
					se.printStackTrace(System.err);
				}
			}
		}
		return set;
	}

}
