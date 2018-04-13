package android.com.talk.model;

import java.util.Set;

import android.com.friends_list.model.FriendsListVO;

public interface TalkDAO_interface {
	public void insert(TalkVO talkvo);
	public void update(TalkVO talkvo);
	public void delete(FriendsListVO friends);
	public TalkVO findTalkByFriends(FriendsListVO friends);
	public Set<TalkVO> getAll();
}
