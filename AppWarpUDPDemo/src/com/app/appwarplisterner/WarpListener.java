package com.app.appwarplisterner;

import java.util.HashMap;
import java.util.Hashtable;

import android.util.Log;
import com.app.appwarpudpdemo.GameActivity;
import com.app.appwarpudpdemo.Utils;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.util.Util;

public class WarpListener implements RoomRequestListener, NotifyListener{

	private GameActivity gameScreen;
	
	public WarpListener(GameActivity gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	@Override
	public void onChatReceived(ChatEvent event) {
		
		
	}

	@Override
	public void onPrivateChatReceived(String arg0, String arg1) {
		
		
	}

	@Override
	public void onRoomCreated(RoomData arg0) {
		
		
	}

	@Override
	public void onRoomDestroyed(RoomData arg0) {
		
		
	}

	@Override
	public void onUpdatePeersReceived(UpdateEvent event) {
		if(event.isUDP()){
			String message = new String(event.getUpdate());
			if(message.startsWith("_")){// Result Message
				String sender = message.substring(1, message.indexOf('#')).trim();
				String data = message.substring(message.indexOf('#')+1, message.length());
				if(sender.equals(Util.userName)==false){
					gameScreen.handleGameResult(true, data);
				}
			}else{
				String sender = message.substring(0, message.indexOf('#')).trim();
				if(sender.equals(Util.userName)==false){// Update Message
					String xCordStr = message.substring(message.indexOf('#')+1, message.indexOf('@'));
					String yCordStr = message.substring(message.indexOf('@')+1, message.length());
					try{
						float xCord = Float.parseFloat(xCordStr);
						float yCord = Float.parseFloat(yCordStr);
						gameScreen.updateMove(sender, xCord, yCord);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		
		
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		
		
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		
		
	}

	@Override
	public void onUserJoinedRoom(RoomData roomData, String name) {
		if(name.equals(Util.userName)==false){
			gameScreen.addMorePlayer(false, name);
		}
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
		
	}

	@Override
	public void onUserLeftRoom(RoomData roomData, String name) {
		gameScreen.handleLeave(name);
		if(name.equals(Util.userName)==false){// remote player left
			gameScreen.handleGameResult(false, "LOOSE");
		}
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		String[] joinedUser = event.getJoinedUsers();
		if(joinedUser!=null){
			for(int i=0;i<joinedUser.length;i++){
				if(joinedUser[i].equals(Utils.userName)){
					gameScreen.addMorePlayer(true, joinedUser[i]);
				}else{
					gameScreen.addMorePlayer(false, joinedUser[i]);
				}
			}
		}else{
			Log.d("hello app", "joined users are null");
		}
	}

	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent event) {
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
		
	}

	@Override
	public void onMoveCompleted(MoveEvent arg0) {
		
		
	}

	@Override
	public void onLockPropertiesDone(byte arg0) {
		
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		
		
	}

	@Override
	public void onUserPaused(String arg0, boolean arg1, String arg2) {
		
		
	}

	@Override
	public void onUserResumed(String arg0, boolean arg1, String arg2) {
		
		
	}

	@Override
	public void onNextTurnRequest(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
}
