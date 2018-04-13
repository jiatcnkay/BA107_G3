package android.com.talk.ws;


import java.util.*;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import android.com.friends_list.model.FriendsListVO;
import android.com.friends_list.model.FriendsService;
import android.com.member.model.MemberVO;
import android.com.talk.model.TalkService;


@ServerEndpoint(value="/FriendWS/{mem_no}" , configurator=ServletAwareConfig.class)
public class FriendsWS {
	
	private EndpointConfig config;
	public static final Map<String , Session> onlineMem = new HashMap<String,Session>();
	private MemberVO self;

	@OnOpen
	public void onOpen(@PathParam("mem_no") String mem_no,Session session , EndpointConfig config) throws JSONException{
		this.config = config;
		HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
		
		self = (MemberVO)httpSession.getAttribute("memSelf");//�W�u�|��
		onlineMem.put(self.getMemNo(), session);
		System.out.println(self.getMemName()+"�W�u");
		
		sendFriends(session);
		getOnlineFri(session);
		
	}
	
	@OnMessage
	public void onMessage(Session session, String message) throws JSONException{
		System.out.println(message);
		JSONObject jsonObj = new JSONObject(message);
		String type = jsonObj.getString("type");
		Session friSession = null;
		
		//���o�P�n�ͪ����v�T��
		if("getOneTalk".equals(type)){
			
			String friNo = jsonObj.getString("friNo");
			System.out.println(friNo);
			String talk = getTalk(friNo);
			JSONObject result = null;
			result = new JSONObject();
			result.append("type","getMessage");
			result.append("message",talk);
			System.out.println(result);
			session.getAsyncRemote().sendText(result.toString());
		}

//		session.getAsyncRemote().sendText(message);
		
	}
	
	@OnError
	public void onError(Session session, Throwable e){
		
	}
	
	@OnClose
	public void onClose(Session session, CloseReason reason) throws Exception{
		FriendsService friSvc = new FriendsService();
		List<MemberVO>friends = friSvc.getMemFri(self);
		
		JSONObject result = null;
		result = new JSONObject();
		result.append("type","leave");
		result.append("memberNO",self.getMemNo());
		
		for(MemberVO friend : friends){
			if(onlineMem.containsKey(friend.getMemNo())){//���n�ͬO�_�b�W�u�W��
				
				Session friendsSession = onlineMem.get(friend.getMemNo());
				friendsSession.getAsyncRemote().sendText(result.toString());
			}
		}
		
		
		session.close();
		onlineMem.remove(self.getMemNo());
		
		
	}
	
	//�����n�ͤW�u�T��
	public void sendFriends(Session session) throws JSONException{
		FriendsService friSvc = new FriendsService();
		List<MemberVO> friends = friSvc.getMemFri(self);//�W�u�|���n�ͲM��
		
		JSONObject result =null;
		result = new JSONObject();
		result.append("type", "sendEveryFri");
		result.append("memberNO",self.getMemNo());
		
		for(MemberVO friend : friends){
			if(onlineMem.containsKey(friend.getMemNo())){//���n�ͬO�_�b�W�u�W��
				
				Session friendsSession = onlineMem.get(friend.getMemNo());
				friendsSession.getAsyncRemote().sendText(result.toString());
			}

		}
	}
	
	//���o�W�u�n�ͪ����A
	public void getOnlineFri(Session session) throws JSONException{
		FriendsService friSvc = new FriendsService();
		List<MemberVO>friends = friSvc.getMemFri(self);//�n���`�H��
		Set<String>onlineFri = new HashSet<String>();//�b�u���n��
		
		JSONObject result =null;
		result = new JSONObject();
		result.append("type", "sendSelf");
		result.append("onlineFri",onlineFri);
		
		for(MemberVO friend : friends){
			if(onlineMem.containsKey(friend.getMemNo())){//���n�ͬO�_�b�W�u�W��
				onlineFri.add(friend.getMemNo());
//				Session friendsSession = onlineMem.get(friend.getMem_no());
//				friendsSession.getAsyncRemote().sendText(result.toString());
			}
		}
		session.getAsyncRemote().sendText(result.toString());
	}
	
	//���o�P�n�ͪ����v�T��
	public String getTalk(String friNo){
		TalkService talkSvc = new TalkService();
		FriendsListVO friends = new FriendsListVO();
		friends.setMem_no_self(self.getMemNo());
		friends.setMem_no_other(friNo);
		String talk = talkSvc.getOneTalk(friends);
		
		return talk;
	}
	

}
