package app.shephertz.multiplayer;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

/**
 * @author Vishnu
 *
 */
public class ZoneListener implements ZoneRequestListener{

	WarpController callBack;
	
	public ZoneListener(WarpController callBack) {
		this.callBack = callBack;
	}
	
	@Override
	public void onCreateRoomDone(RoomEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			callBack.onRoomCreated(event.getData().getId());
		}else{
			callBack.onRoomCreated(null);
		}
		
	}

	@Override
	public void onDeleteRoomDone(RoomEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetAllRoomsDone(AllRoomsEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetMatchedRoomsDone(MatchedRoomsEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetOnlineUsersDone(AllUsersEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomUserDataDone(LiveUserInfoEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetRoomsCountDone(com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent)
	 */
	@Override
	public void onGetRoomsCountDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetUsersCountDone(com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent)
	 */
	@Override
	public void onGetUsersCountDone(AllUsersEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
