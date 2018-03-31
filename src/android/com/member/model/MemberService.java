package android.com.member.model;

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
}
