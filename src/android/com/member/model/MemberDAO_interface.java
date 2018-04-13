package android.com.member.model;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface MemberDAO_interface {
	Boolean isMember(String account,String password);
	void memberUpdate(MemberVO member);
	MemberVO getOneByAccount(String account);
	MemberVO getOneByMemNo(String mem_no);
	List<MemberVO> getLike(Map<String, String> map);
	List<MemberVO> getAll();
	void updateDeposit(String mem_no, Integer delDeposit, Connection con);
	void updateRecGift(String mem_no, Integer addRecGift, Connection con);
	List<MemberVO> getPopular();
}