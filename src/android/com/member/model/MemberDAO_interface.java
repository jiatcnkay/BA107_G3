package android.com.member.model;

import java.util.List;
import java.util.Map;

public interface MemberDAO_interface {
	Boolean isMember(String account,String password);
	void memberUpdate(MemberVO member);
	MemberVO getOneByAccount(String account);
	MemberVO getOneByMemNo(String mem_no);
	List<MemberVO> getLike(Map<String, String> map);
	List<MemberVO> getAll();
}