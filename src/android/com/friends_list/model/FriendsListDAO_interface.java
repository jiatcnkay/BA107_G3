package android.com.friends_list.model;

import java.util.List;
import java.util.Set;

public interface FriendsListDAO_interface {
	public void insert(FriendsListVO frilistVO);
	public void update(FriendsListVO frilistVO);
	public void delete(String mem_no_self,String mem_no_other); //解除好友
	public FriendsListVO findByPrimaryKey(String mem_no_self,String mem_no_other);
	public List<FriendsListVO> getAll();
	public List<FriendsListVO> getMemberFriends(String mem_no);
	
}