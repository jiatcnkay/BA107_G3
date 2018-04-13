package android.com.member.model;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class MemberService {
	
	private MemberDAO_interface dao;
	
	public MemberService(){
		dao = new MemberDAO();
	}

	public Boolean isMember(String account, String password) {
		return dao.isMember(account, password);
	}
	
	public void memberUpdate(MemberVO member) {
		
	}
	
	public MemberVO getOneByAccount(String account) {
		return dao.getOneByAccount(account);
	}
	
	public MemberVO getOneByMemNo(String mem_no){
		return dao.getOneByMemNo(mem_no);
	}
	
	public List<MemberVO> getLike(Map<String, String> map) {
		return dao.getLike(map);
	}
	
	public List<MemberVO> getAll() {
		return dao.getAll();
	}
	
	public List<MemberVO> getPopular() {
		return dao.getPopular();
	}
	
	public void updateDeposit(String mem_no, Integer delDeposit, Connection con){
		dao.updateDeposit(mem_no, delDeposit, con);
	}
	
	public void updateRecGift(String mem_no, Integer addRecGift, Connection con) {
		dao.updateRecGift(mem_no, addRecGift, con);
	}
}
