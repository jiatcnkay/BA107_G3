package android.com.member.model;

import java.util.List;

public interface MemberDAO_interface {
	void memberAdd(MemberVO member);
	void memberUpdate(MemberVO member);
	MemberVO memberSelect(String name);
	List<MemberVO> getAll();
	public MemberVO memberAccount(String account);
}