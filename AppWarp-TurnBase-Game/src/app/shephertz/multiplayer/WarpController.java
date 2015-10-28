package app.shephertz.multiplayer;

import android.os.Handler;
import android.util.Log;
import app.shephertz.tictactoe.GameActivity;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

/**
 * @author Vishnu
 *
 */
public class WarpController{

	private static WarpController instance;

	private WarpClient warpClient;

	private String userName;
	private GameActivity activity;

	private static String roomId = "";

	private boolean isConnected = false;
	private boolean isNewRoomCreated = false;
	private boolean isGameStated = false;
	public static int currentState = -1;

	//Game State management for recovery checks
	private static final int StateJoiningRoom = 0;
	private static final int StateCreatingRoom = 1;
	private static final int StateSubscribingRoom = 2;
	private static final int StateWaitingForUser = 3;
	public static final int StatePlayingTurn = 4;
	public static final int StateWaitingTurn = 5;
	//Connection Recovery Time
    private final int RecoveryTime=30;
	private Handler handler = new Handler();

	/**
	 * 
	 */
	public WarpController() {
		initAppwarp();
		warpClient.addConnectionRequestListener(new ConnectionListener(this));
		warpClient.addZoneRequestListener(new ZoneListener(this));
		warpClient.addRoomRequestListener(new RoomListener(this));
		warpClient.addNotificationListener(new NotificationListener(this));
		warpClient.addTurnBasedRoomListener(new TurnBaseListener(this));
		warpClient.setRecoveryAllowance(RecoveryTime);
	}

	/**
	 * @return
	 */
	public static WarpController getInstance() {
		if (instance == null) {
			instance = new WarpController();
		}
		return instance;
	}

	/**
	 * @param activity
	 */
	public void setActivity(GameActivity activity) {
		this.activity = activity;
	}

	/**
	 * @param userName
	 * @param activity
	 */
	public void startApp(String userName, GameActivity activity) {
		this.userName = userName;
		warpClient.connectWithUserName(userName);
	}

	/**
	 * @param message
	 */
	public void sendMove(String message) {
		warpClient.sendMove(message);
	}

	/**
	 * 
	 */
	public void stopApp() {
		if (isConnected) {
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
			if (!isGameStated) {
				warpClient.deleteRoom(roomId);
			}
		}
		warpClient.disconnect();
		isConnected = false;
		isNewRoomCreated = false;
		isGameStated = false;
	}

	/**
	 * 
	 */
	private void initAppwarp() {
		try {
			WarpClient
					.initialize(WarpConstants.ApiKey, WarpConstants.SecretKey);
			warpClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param status
	 */
	public void onConnectDone(boolean status) {
		log("onConnectDone: " + status);
		if (status) {
			currentState = StateJoiningRoom;
			warpClient.joinRoomInRange(1, 1, false);
		} else {
			isConnected = false;
		}
	}

	/**
	 * @param event
	 */
	public void onJoinRoomDone(RoomEvent event) {
		if (event.getData() != null)
			roomId = event.getData().getId();
		log("onJoinRoomDone: " + event.getResult());
		if (event.getResult() == WarpResponseResultCode.SUCCESS) {
			currentState = StateSubscribingRoom;
			warpClient.subscribeRoom(event.getData().getId());
		} else if (event.getResult() == WarpResponseResultCode.RESOURCE_NOT_FOUND) {
			isNewRoomCreated = true;
			currentState = StateCreatingRoom;
			warpClient.createTurnRoom("TicTacToe", userName, 2, null, 30);
		} else {
			warpClient.disconnect();
		}
	}

	/**
	 * @param roomId
	 */
	public void onSubscribeRoomDone(String roomId) {
		log("onSubscribeRoomDone: " + roomId);
		if (roomId != null) {
			isConnected = true;
			if (isNewRoomCreated) {// he must be first user
				activity.setType('X');
				currentState = StateWaitingForUser;
				activity.updateNotification("Waiting for other user");
				Log.d("onSubscribeRoomDone", "Waiting for other user");
			} else {// he must be second user
				isGameStated = true;
				currentState = StateWaitingForUser;
				activity.setType('0');
				activity.updateNotification("Game Started");
				warpClient.startGame();
				Log.d("onSubscribeRoomDone", "Game Started");
			}

		} else {
			warpClient.disconnect();
		}
	}

	/**
	 * @param roomId
	 */
	public void onRoomCreated(String roomId) {
		if (roomId != null) {
			this.roomId = roomId;
			currentState = StateJoiningRoom;
			warpClient.joinRoom(roomId);
		} else {
			warpClient.disconnect();
		}
	}

	/**
	 * @param roomId
	 * @param userName
	 */
	public void onUserJoinedRoom(String roomId, String userName) {

		if (isNewRoomCreated) {
			isGameStated = true;
			warpClient.startGame();
		}
	}

	public void onUserLeftRoom(String roomId, String userName) {
		if (isGameStated) {
			isGameStated = false;
			activity.onEnemyLeft();
		}
	}

	public void onGameStarted(String sender, String roomId, String nextTurn) {
		activity.startGame(nextTurn);
	}

	public void onGameStopped(String roomId, String userName) {
		isGameStated = false;
	}

	public void onMoveCompleted(String moveData, String sender, String nextTurn) {
		activity.onMoveCompleted(moveData, sender, nextTurn);
	}

	private void log(String message) {
		System.out.println(message);
	}

	void RecoverConnection() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			public void run() {
				warpClient.RecoverConnection();
			}
		}, 5000);
	}

	/**
	 *This function used to check connection recovery as well as room properties 
	 */
	void onRecoverconnection() {
			if (currentState == StateCreatingRoom) {
			warpClient.createTurnRoom("TicTacToe", userName, 2, null, 30);
		} else if (currentState == StateJoiningRoom) {
			if (roomId != null)
				warpClient.joinRoom(roomId);
			else
				warpClient.joinRoomInRange(1, 1, false);
		} else if (currentState == StateSubscribingRoom) {
			if (roomId != null)
				warpClient.subscribeRoom(roomId);
		} else if (currentState == StateWaitingTurn||currentState == StatePlayingTurn) {
          warpClient.getMoveHistory();
		}
	}
/**
 * @param moves
 */
void onGetTurnHistory(MoveEvent[] moves){
	if(moves==null)
		return;
	for(MoveEvent event:moves){
		System.out.println(event.getMoveData()+event.getSender()+ event.getNextTurn());
		activity.validateMoveHistory(event.getMoveData(), event.getSender(), event.getNextTurn());
	}
}
}
