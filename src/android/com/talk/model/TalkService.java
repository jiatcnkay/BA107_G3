package android.com.talk.model;

import android.com.friends_list.model.FriendsListVO;
import android.com.talk.model.TalkDAO;
import android.com.talk.model.TalkDAO_interface;
import android.com.talk.model.TalkVO;

public class TalkService {
	
	private TalkDAO_interface dao;
	public TalkService(){
		dao = new TalkDAO();
		
	}
	
	public String getOneTalk(FriendsListVO friends){
		TalkVO talk = dao.findTalkByFriends(friends);
		String content = talk.getTalk_cnt();
		
		return content;
	}

}
