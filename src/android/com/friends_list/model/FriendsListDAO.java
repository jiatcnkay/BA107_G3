package android.com.friends_list.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FriendsListDAO implements FriendsListDAO_interface{
	
	private static DataSource ds = null;
	static{
		try{
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup("java:comp/env/jdbc/BA107G3");
		}catch(NamingException e){
			e.printStackTrace();
		}
	}
	
	private static final String INSERT_FRIENDS = "INSERT INTO FRIENDS_LIST (MEM_NO_SELF,MEM_NO_OTHER)VALUES(?,?)";
	private static final String UPDATE_FRIENDS = "UPDATE FRIENDS_LIST SET MEM_NO_SELF=?,MEM_NO_OTHER=?,FRILIST_MODIFY=?,FRILIST_TIME=?,FRILIST_NOTICE=? WHERE MEM_NO_SELF=? AND MEM_NO_OTHER=?";
	private static final String DELETE_FRIENDS = "DELETE FROM FRIENDS_LIST WHERE MEM_NO_SELF=? AND MEM_NO_OTHER=?";
	private static final String GET_ONE_LIST = "SELECT*FROM FRIENDS_LIST WHERE MEM_NO_SELF=? AND MEM_NO_OTHER=?";
	private static final String GET_ALL = "SELECT*FROM FRIENDS_LIST";
	private static final String GET_LIST_ByMEMEBERNO = "SELECT*FROM FRIENDS_LIST WHERE (MEM_NO_SELF=? OR MEM_NO_OTHER =?) AND FRILIST_MODIFY NOT IN('待審核')";

	@Override
	public void insert(FriendsListVO frilistVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_FRIENDS);
			
			pstmt.setString(1, frilistVO.getMem_no_self());
			pstmt.setString(2, frilistVO.getMem_no_other());
			
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
	public void update(FriendsListVO frilistVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_FRIENDS);
			
			pstmt.setString(1, frilistVO.getMem_no_self());
			pstmt.setString(2, frilistVO.getMem_no_other());
			pstmt.setString(3, frilistVO.getFrilist_modify());
			pstmt.setDate(4, (Date) frilistVO.getFrilist_time());
			pstmt.setString(5, frilistVO.getFrilist_notice());
			pstmt.setString(6, frilistVO.getMem_no_self());
			pstmt.setString(7, frilistVO.getMem_no_other());
			
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
	public void delete(String mem_no_self,String mem_no_other) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_FRIENDS);
			
			pstmt.setString(1, mem_no_self);
			pstmt.setString(2, mem_no_other);
			
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
	public FriendsListVO findByPrimaryKey(String mem_no_self, String mem_no_other) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FriendsListVO friendsVO = null;
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_LIST);
			
			pstmt.setString(1, mem_no_self);
			pstmt.setString(2, mem_no_other);
			rs = pstmt.executeQuery();
			rs.next();
			friendsVO = new FriendsListVO();
			friendsVO.setMem_no_self(rs.getString("mem_no_self"));
			friendsVO.setMem_no_other(rs.getString("mem_no_other"));
			friendsVO.setFrilist_modify(rs.getString("frilist_modify"));
			friendsVO.setFrilist_time(rs.getDate("frilist_time"));
			friendsVO.setFrilist_notice(rs.getString("frilist_notice"));
			
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
		return friendsVO;
	}

	@Override
	public List<FriendsListVO> getAll() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FriendsListVO friendsVO = null;
		List<FriendsListVO> list = new ArrayList<FriendsListVO>();
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				friendsVO = new FriendsListVO();
				friendsVO.setMem_no_self(rs.getString("mem_no_self"));
				friendsVO.setMem_no_other(rs.getString("mem_no_other"));
				friendsVO.setFrilist_modify(rs.getString("frilist_modify"));
				friendsVO.setFrilist_time(rs.getDate("frilist_time"));
				friendsVO.setFrilist_notice(rs.getString("frilist_notice"));
				list.add(friendsVO);
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
		return list;
	}

	@Override
	public List<FriendsListVO> getMemberFriends(String mem_no) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FriendsListVO friendsVO = null;
		List<FriendsListVO> list = new ArrayList<FriendsListVO>();
		
		try{
			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_LIST_ByMEMEBERNO);
			pstmt.setString(1, mem_no);
			pstmt.setString(2, mem_no);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				friendsVO = new FriendsListVO();
				friendsVO.setMem_no_self(rs.getString("mem_no_self"));
				friendsVO.setMem_no_other(rs.getString("mem_no_other"));
				friendsVO.setFrilist_modify(rs.getString("frilist_modify"));
				friendsVO.setFrilist_time(rs.getDate("frilist_time"));
				friendsVO.setFrilist_notice(rs.getString("frilist_notice"));
				list.add(friendsVO);
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
		return list;
	}

}
