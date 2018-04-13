package android.com.talk.model;

public class TalkMessageVO {
	private String memSend;
	private String memGet;
	private String time;
	private String message;
	
	public TalkMessageVO(){
		
	}
	
	public TalkMessageVO(String memSend,String memGet,String time ,String message){
		this.memSend = memSend;
		this.memGet = memGet;
		this.time = time;
		this.message = message;
	}

	public String getMemSend() {
		return memSend;
	}

	public void setMemSend(String memSend) {
		this.memSend = memSend;
	}

	public String getMemGet() {
		return memGet;
	}

	public void setMemGet(String memGet) {
		this.memGet = memGet;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	

}
