package android.com.friends_list.model;

import java.util.ArrayList;
import java.util.List;

import android.com.member.model.MemberService;
import android.com.member.model.MemberVO;

public class FriendsService {
	
	private FriendsListDAO_interface dao;
	public FriendsService(){
		dao = new FriendsListDAO();
	}
	
	
	public void deleteFri(String self , String other){
		dao.delete(self, other);
		dao.delete(other, self);
	}
	
	public List<MemberVO> getMemFri(MemberVO member){
		String mem_no = member.getMemNo();
		List<FriendsListVO> list = dao.getMemberFriends(mem_no);
		List<MemberVO> friends = new ArrayList<MemberVO>();
		MemberService memSvc = new MemberService();
		
		for(FriendsListVO fr : list){
			//等拿到以no取會員物件，以及我這邊修改假資料，再做修改
			friends.add(memSvc.getOneByMemNo(fr.getMem_no_other()));
		}
		
		
		return friends;
	}
	
	public FriendsListVO getOne(String self , String other){
		return dao.findByPrimaryKey(self, other);
	}

}
