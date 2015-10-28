package app.shephertz.multiplayer;


import android.util.Log;
import app.shephertz.tictactoe.MultiplayerGameActivity;
import app.shephertz.tictactoe.Util;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

public class WarpController {

	private static WarpController instance;
	
	private WarpClient warpClient;
	
	private String userName;
	private MultiplayerGameActivity activity;
	
	private String roomId = "";
		
	private boolean isConnected = false;
	private boolean isNewRoomCreated = false;
	private boolean isGameStated = false;
	
	public WarpController() {
		initAppwarp();
		warpClient.addConnectionRequestListener(new ConnectionListener(this));
		warpClient.addZoneRequestListener(new ZoneListener(this));
		warpClient.addRoomRequestListener(new RoomListener(this));
		warpClient.addNotificationListener(new NotificationListener(this));
	}
	
	public static WarpController getInstance(){
		if(instance == null){
			instance = new WarpController();
		}
		return instance;
	}
	
	public void setActivity(MultiplayerGameActivity activity){
		this.activity = activity;
	}
	
	public void startApp(String userName, MultiplayerGameActivity activity) {
		this.userName = userName;
		warpClient.connectWithUserName(userName);
	}
	
	public void sendMove(String message) {
		warpClient.sendMove(message);
	}
	
	public void stopApp(){
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
			if(!isGameStated){
				warpClient.deleteRoom(roomId);
			}
		}
		warpClient.disconnect();
		isConnected = false;
		isNewRoomCreated = false;
		isGameStated = false;
	}
	
	private void initAppwarp(){
		try {
			WarpClient.initialize(WarpConstants.API_KEY, WarpConstants.SECRET_KEY);
			warpClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void onConnectDone(boolean status){
		log("onConnectDone: "+status);
		if(status){
			warpClient.joinRoomInRange(1, 1, false);
		}else{
			isConnected = false;
		}
	}
	
	public void onDisconnectDone(boolean status){
		
	}
	
	public void onJoinRoomDone(RoomEvent event){
		log("onJoinRoomDone: "+event.getResult());
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			warpClient.subscribeRoom(event.getData().getId());
		}else if(event.getResult()==WarpResponseResultCode.RESOURCE_NOT_FOUND){
			isNewRoomCreated = true;
			warpClient.createTurnRoom("TicTacToe", userName, 2, null, 30);
		}else{
			warpClient.disconnect();
		}
	}
	
	public void onSubscribeRoomDone(String roomId){
		log("onSubscribeRoomDone: "+roomId);
		if(roomId!=null){
			isConnected = true;
			if(isNewRoomCreated){// he must be first user
				activity.setType('X');
				activity.updateNotification("Waiting for other user");
				Log.d("onSubscribeRoomDone", "Waiting for other user");
			}else{// he must be second user
				isGameStated = true;
				activity.setType('0');
				activity.updateNotification("Game Started");
				warpClient.startGame();
				Log.d("onSubscribeRoomDone", "Game Started");
			}
			
		}else{
			warpClient.disconnect();
		}
	}
	
	public void onRoomCreated(String roomId){
		if(roomId!=null){
			this.roomId = roomId;
			warpClient.joinRoom(roomId);
		}else{
			warpClient.disconnect();
		}
	}
	
	public void onUserJoinedRoom(String roomId, String userName){
		if(isNewRoomCreated){
			isGameStated = true;
			warpClient.startGame();
		}
	}
	
	public void onUserLeftRoom(String roomId, String userName){
		if(isGameStated){
			isGameStated = false;
			activity.onEnemyLeft();
		}
	}
	
	public void onGameStarted(String sender, String roomId, String nextTurn){
		activity.startGame(nextTurn);
	}
	
	public void onGameStopped(String roomId, String userName){
		isGameStated = false;
	}

	public void onMoveCompleted(String moveData, String sender, String nextTurn){
		activity.onMoveCompleted(moveData, sender, nextTurn);
	}


	private void log(String message){
		System.out.println(message);
	}
	
}
