package appwarp.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.shephertz.app42.gaming.multiplayer.client.ConnectionState;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ChatRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class ChatActivity extends Activity implements RoomRequestListener, NotifyListener, ConnectionRequestListener, ChatRequestListener{
	
	private ProgressDialog progressDialog;
	private WarpClient theClient;
	private TextView outputView;
	private EditText inputEditText;
	private ScrollView outputScrollView;
	private UserListAdapter userListAdapter;
	private ListView onlineUsersList;
	private Handler handler = new Handler();
	private ArrayList<User> onlineUserList = new ArrayList<User>();
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		userListAdapter = new UserListAdapter(this);
		setContentView(R.layout.activity_chat);
		outputView = (TextView)findViewById(R.id.outputTextView);
		inputEditText = (EditText)findViewById(R.id.inputEditText);
		outputScrollView = (ScrollView)findViewById(R.id.outputScrollView);
		onlineUsersList = (ListView)findViewById(R.id.onlineUserList);
		onlineUsersList.setAdapter(userListAdapter);
		
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		theClient.addConnectionRequestListener(this);
		theClient.addRoomRequestListener(this);
		theClient.addNotificationListener(this);
		theClient.addChatRequestListener(this);
		if(theClient.getConnectionState()==ConnectionState.CONNECTED){
			progressDialog = ProgressDialog.show(this, "", "Please wait..");
			theClient.getLiveRoomInfo(Constants.roomId);
		}else{
			theClient.RecoverConnection();
		}
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		theClient.removeConnectionRequestListener(this);
		theClient.removeRoomRequestListener(this);
		theClient.removeNotificationListener(this);
		theClient.removeChatRequestListener(this);
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
			theClient.removeRoomRequestListener(this);
			theClient.removeNotificationListener(this);
			theClient.removeChatRequestListener(this);
			handleLeaveRoom();
		}
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	
	private void handleLeaveRoom(){
		if(theClient!=null){
			theClient.unsubscribeRoom(Constants.roomId);
			theClient.leaveRoom(Constants.roomId);
			theClient.disconnect();
		}
	}
	
	public void onSendClicked(View view){
		outputScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		theClient.sendChat(inputEditText.getText().toString());
	}
	
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		if(progressDialog!=null){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
				}
			});
		}
		
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			onlineUserList.clear();
			if(event.getJoinedUsers().length>1){// if more than one user is online
				final String onlineUser[] = Utils.removeLocalUserNameFromArray(event.getJoinedUsers());
				for(int i=0;i<onlineUser.length;i++){
					User user = new User(onlineUser[i].toString(), true);
					Log.d(onlineUser[i].toString(), onlineUser[i].toString());
					onlineUserList.add(user);
				}
				resetAdapter();
			}else{ 
				showToastOnUIThread("No online user found");
			}
		}else{
			showToastOnUIThread("onGetLiveRoomInfoDone Failed with ErrorCode: "+event.getResult());
		}
	}
	
	private void resetAdapter(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(onlineUserList.size()>0){
					userListAdapter.setData(onlineUserList);
				}else{
					userListAdapter.clear();
				}
			}
		});
		
	}
	
	@Override
	public void onJoinRoomDone(RoomEvent arg0) {
		
	}
	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		
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
	public void onChatReceived(final ChatEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				outputView.append("\n"+event.getSender()+": "+event.getMessage());
			}
		});
	}
	
	@Override
	public void onPrivateChatReceived(final String userName, final String message) {
		
	}
	
	@Override
	public void onRoomCreated(RoomData arg0) {
		
	}
	
	@Override
	public void onRoomDestroyed(RoomData arg0) {
		
	}
	
	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		
	}
	
	@Override
	public void onUserJoinedRoom(final RoomData roomData, final String userName) {
		if(userName.equals(Utils.USER_NAME)==false){
			onlineUserList.add(new User(userName, true));
			resetAdapter();
		}
	}
	
	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
	}
	
	@Override
	public void onUserLeftRoom(final RoomData roomData, final String userName) {
		for(int i=0;i<onlineUserList.size();i++){
			User user = onlineUserList.get(i);
			if(user.getName().equals(userName)){
				onlineUserList.remove(user);
			}
		}
		resetAdapter();
	}
	
	@Override
	public void onMoveCompleted(MoveEvent arg0) {
		
	}
	
	
	
	@Override
	public void onUserPaused(String locid, boolean isLobby, String userName) {
		for(int i=0;i<onlineUserList.size();i++){
			User user = onlineUserList.get(i);
			if(user.getName().equals(userName)){
				user.setStatus(false);
			}
		}
		resetAdapter();
	}
	
	@Override
	public void onUserResumed(String locid, boolean isLobby, String userName) {
		for(int i=0;i<onlineUserList.size();i++){
			User user = onlineUserList.get(i);
			if(user.getName().equals(userName)){
				user.setStatus(true);
			}
		}
		resetAdapter();
	}
	
	@Override
	public void onLockPropertiesDone(byte arg0) {
		
	}
	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		
	}
	@Override
	public void onConnectDone(final ConnectEvent event) {
		if(progressDialog!=null){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.dismiss();
					
				}
			});
		}
		
		if(event.getResult() == WarpResponseResultCode.SUCCESS){
        	showToastOnUIThread("Connection success");
        }
        else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
        	showToastOnUIThread("Connection recovered");
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(progressDialog!=null){
		        		progressDialog.dismiss();
		        	}
		        	progressDialog = ProgressDialog.show(ChatActivity.this, "", "Please wait..");
				}
			});
        	
			theClient.getLiveRoomInfo(Constants.roomId);
        }
        else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(ChatActivity.this, "", "Recoverable connection error. Recovering session after 5 seconds");
				}
			});
        	handler.postDelayed(new Runnable() {
                @Override
                public void run() {                                          
                    progressDialog.setMessage("Recovering...");
                    theClient.RecoverConnection();
                }
            }, 5000);
        }
        else{
        	showToastOnUIThread("Non-recoverable connection error."+event.getResult());
        	handleLeaveRoom();
        	this.finish();
        }

	}
	@Override
	public void onDisconnectDone(ConnectEvent arg0) {
		
	}
	
	@Override
	public void onInitUDPDone(byte result) {
		
	}
	
	@Override
	public void onSendChatDone(byte result) {
		if(result!=WarpResponseResultCode.SUCCESS){
			showToastOnUIThread("onSendChatDone Failed with ErrorCode: "+result);
		}
	}

	@Override
	public void onSendPrivateChatDone(byte result) {
		
		
	}
	
	private void showToastOnUIThread(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onGameStarted(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameStopped(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			HashMap<String, Object> arg2, HashMap<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}

	
}
