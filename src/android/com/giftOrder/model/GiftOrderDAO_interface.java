package android.com.giftOrder.model;
import java.util.*;

import android.com.giftOrderDetail.model.*;
import android.com.giftReceive.model.*;

public interface GiftOrderDAO_interface {
	public void insert(GiftOrderVO giftOrderVO, Map<GiftOrderDetailVO,List<GiftReceiveVO>> giftOrderDetailMap);

}