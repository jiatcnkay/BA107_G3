package android.com.giftOrder.model;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class GiftOrderVO implements Serializable {
	String gifto_no;//�q��s��
	String mem_no;//�|���s��
	String coup_no;//�����s��
	Timestamp gifto_time;//�q��ɶ�
	String gifto_remark;//�q��Ƶ�

	public GiftOrderVO() {
		super();
	}

	public String getGifto_no() {
		return gifto_no;
	}

	public void setGifto_no(String gifto_no) {
		this.gifto_no = gifto_no;
	}

	public String getMem_no() {
		return mem_no;
	}

	public void setMem_no(String mem_no) {
		this.mem_no = mem_no;
	}

	public String getCoup_no() {
		return coup_no;
	}

	public void setCoup_no(String coup_no) {
		this.coup_no = coup_no;
	}

	public Timestamp getGifto_time() {
		return gifto_time;
	}

	public void setGifto_time(Timestamp gifto_time) {
		this.gifto_time = gifto_time;
	}

	public String getGifto_remark() {
		return gifto_remark;
	}

	public void setGifto_remark(String gifto_remark) {
		this.gifto_remark = gifto_remark;
	}

}

