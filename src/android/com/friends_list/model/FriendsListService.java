package android.com.friends_list.model;

import java.util.List;
import java.util.Set;

public class FriendsListService {
	
	private FriendsListDAO_interface dao;
	
	public FriendsListService(){
		dao = new FriendsListDAO();
	}
	
	public void insert(String mem_no_self,String mem_no_other){
		FriendsListVO frilistVO = new FriendsListVO();
		frilistVO.setMem_no_self(mem_no_self);
		frilistVO.setMem_no_other(mem_no_other);
		dao.insert(frilistVO);
	}
	
	public void update(FriendsListVO frilistVO){
		
	}
	
	public void delete(String mem_no_self,String mem_no_other){
		dao.delete(mem_no_self, mem_no_other);
	}
	
	public FriendsListVO findByPrimaryKey(String mem_no_self,String mem_no_other){
		return dao.findByPrimaryKey(mem_no_self, mem_no_other);
	}
	
	public List<FriendsListVO> getAll(){
		return dao.getAll();
	}
	
	public List<FriendsListVO> getMemberFriends(String mem_no){
		return dao.getMemberFriends(mem_no);
	}
	
	public Boolean havewait(String mem_no_self,String mem_no_other){
		return dao.havewait(mem_no_self, mem_no_other);
	}

}
