package app.shephertz.multiplayer;

import java.util.HashMap;
import java.util.Hashtable;

import app.shephertz.tictactoe.Util;

import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;

/**
 * @author Vishnu
 *
 */
public class NotificationListener implements NotifyListener{

	
	private WarpController callBack;
	
	public NotificationListener(WarpController callBack) {
		this.callBack = callBack;
	}
	
	public void onChatReceived(ChatEvent event) {
		
	}

	public void onRoomCreated(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRoomDestroyed(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdatePeersReceived(UpdateEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			Hashtable arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onUserJoinedRoom(RoomData data, String userName) {
		if(userName!=Util.UserName){
			callBack.onUserJoinedRoom(data.getId(), userName);
		}		
	}

	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
	}

	public void onUserLeftRoom(RoomData data, String userName) {
		if(userName!=Util.UserName){
			callBack.onUserLeftRoom(data.getId(), userName);
		}	
	}

	@Override
	public void onGameStarted(String sender, String roomId, String nextTurn) {
		callBack.onGameStarted(sender, roomId, nextTurn);
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		//callBack.onGameStopped(roomId, userName)
		
	}

	@Override
	public void onMoveCompleted(MoveEvent event) {
		callBack.onMoveCompleted(event.getMoveData(), event.getSender(), event.getNextTurn());
		
	}

	@Override
	public void onPrivateChatReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener#onNextTurnRequest(java.lang.String)
	 */
	@Override
	public void onNextTurnRequest(String arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener#onPrivateUpdateReceived(java.lang.String, byte[], boolean)
	 */
	@Override
	public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
	
}
