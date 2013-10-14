package com.example;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class ChatActivity extends Activity implements RoomRequestListener, NotifyListener, ConnectionRequestListener{
	
	private ProgressDialog progressDialog;
	private WarpClient theClient;
	private TextView outputView;
	private EditText inputEditText;
	private ScrollView outputScrollView;
	private UserListAdapter userListAdapter;
	private ListView onlineUsersList;
	private Handler handler = new Handler();
	private String roomId;
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
		roomId = "";
		roomId = getIntent().getStringExtra("roomId");
		
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		progressDialog = ProgressDialog.show(this, "", "Please wait..");
		theClient.addConnectionRequestListener(this);
		theClient.addRoomRequestListener(this);
		theClient.subscribeRoom(roomId);
		theClient.addNotificationListener(this);
		theClient.getLiveRoomInfo(roomId);
		
	}
	public void onDestroy(){
		super.onDestroy();
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
			theClient.removeRoomRequestListener(this);
			theClient.removeNotificationListener(this);
			theClient.disconnect();
		}
	}
	public void onSendClicked(View view){
		outputScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		theClient.sendChat(inputEditText.getText().toString());
	}
	@Override
	public void onGetLiveRoomInfoDone(final LiveRoomInfoEvent event) {
		progressDialog.dismiss();
		if(event.getResult()==0){
			
			if(event.getJoinedUsers().length>1){// if more than one user is online
				final String onlineUser[] = Utils.removeUsernameFromArray(event.getJoinedUsers());
				for(int i=0;i<onlineUser.length;i++){
					User user = new User(onlineUser[i].toString(), true);
					Log.d(onlineUser[i].toString(), onlineUser[i].toString());
					onlineUserList.add(user);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						resetAdapter();
					}
				});
			}else{ // Alert for no online user found
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utils.showToast(ChatActivity.this, "No online user found");
					}
				});
				Log.d("No online user found", "No online user found");
			}
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Utils.showToast(ChatActivity.this, "Error in fetching data. Please try later");
				}
			});
			
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRoomDestroyed(RoomData arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpdatePeersReceived(UpdateEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserJoinedRoom(final RoomData roomData, final String userName) {
		if(userName.equals(Utils.USER_NAME)==false){
			onlineUserList.add(new User(userName, true));
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					resetAdapter();
				}
			});
		}
	}
	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserLeftRoom(final RoomData roomData, final String userName) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<onlineUserList.size();i++){
					User user = onlineUserList.get(i);
					if(user.getName().equals(userName)){
						onlineUserList.remove(user);
					}
				}
				resetAdapter();
			}
		});
		
	}
	
	@Override
	public void onMoveCompleted(MoveEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserChangeRoomProperty(RoomData arg0, String arg1,
			Hashtable<String, Object> arg2, Hashtable<String, String> arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUserPaused(RoomData arg0, String userName) {
		for(int i=0;i<onlineUserList.size();i++){
			User user = onlineUserList.get(i);
			if(user.getName().equals(userName)){
				user.setStatus(false);
			}
		}
		resetAdapter();
		
	}
	@Override
	public void onUserResumed(RoomData arg0, String userName) {
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectDone(final ConnectEvent event) {
		handler.post(new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
            if(event.getResult() == WarpResponseResultCode.SUCCESS){
                Toast.makeText(ChatActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
            }
            else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
                Toast.makeText(ChatActivity.this, "Connection recovered", Toast.LENGTH_SHORT).show();
            }
            else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
                Toast.makeText(ChatActivity.this, "Recoverable connection error. Recovering session in 5 seconds", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {                                          
                        progressDialog = ProgressDialog.show(ChatActivity.this, "", "Recovering...");
                        theClient.RecoverConnection();
                    }
                }, 5000);
            }
            else{
                Toast.makeText(ChatActivity.this, "non-recoverable connection error. Please connect again", Toast.LENGTH_SHORT).show();
                ChatActivity.this.finish();
            }
        }
    });	
	}
	@Override
	public void onDisconnectDone(ConnectEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
