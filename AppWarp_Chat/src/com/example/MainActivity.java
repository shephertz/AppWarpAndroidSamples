package com.example;


import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class MainActivity extends Activity implements ConnectionRequestListener, RoomRequestListener{

	
	private EditText nameEditText;
	private WarpClient theClient;
	private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameEditText = (EditText)findViewById(R.id.editTextName);
		init();
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(theClient!=null){
			theClient.removeConnectionRequestListener(this);
			theClient.disconnect();
		}
	}
	
	public void onConnectClicked(View view){
		String userName = nameEditText.getText().toString();
		if(userName.length()>0){
			Utils.USER_NAME  = userName;
			progressDialog = ProgressDialog.show(this, "", "Please wait...");
			progressDialog.setCancelable(true);
			theClient.addConnectionRequestListener(this);  
			theClient.connectWithUserName(userName);
		}else{
			Utils.showToast(this, "Please enter name");
		}
	}
	
	public void startApp(){
		Hashtable<String, Object> propertiesToMatch = new Hashtable<String, Object>();
		propertiesToMatch.put("topic", "sports");
		theClient.addRoomRequestListener(this);
		theClient.joinRoomWithProperties(propertiesToMatch);
		
	}
	private void init(){
		WarpClient.initialize(Constants.apiKey, Constants.secretKey);
		WarpClient.setRecoveryAllowance(120);
        try {
            theClient = WarpClient.getInstance();
        } catch (Exception ex) {
            Toast.makeText(this, "Exception in Initilization", Toast.LENGTH_LONG).show();
        }
        
	}
	@Override
	public void onConnectDone(final ConnectEvent event) {
		handler.post(new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
            if(event.getResult() == WarpResponseResultCode.SUCCESS){
                Toast.makeText(MainActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
                startApp();
            }
            else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
                Toast.makeText(MainActivity.this, "Connection recovered", Toast.LENGTH_SHORT).show();
            }
            else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
                Toast.makeText(MainActivity.this, "Recoverable connection error. Recovering session in 5 seconds", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {                                          
                        progressDialog = ProgressDialog.show(MainActivity.this, "", "Recovering...");
                        theClient.RecoverConnection();
                    }
                }, 5000);
            }
            else{
                Toast.makeText(MainActivity.this, "non-recoverable connection error. Reconnecting in 5 seconds", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show(MainActivity.this, "", "Reconnecting...");
                        theClient.connectWithUserName(Utils.USER_NAME);
                    }
                }, 5000);
            }
        }
    });	
	}
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
		
		
	}

	@Override
	public void onJoinRoomDone(RoomEvent event) {
		if(event.getResult()==0){
			theClient.removeRoomRequestListener(this);
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("roomId", event.getData().getId());
			startActivity(intent);
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, "Room Join Failed! Please try again", Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		
		
	}

	@Override
	public void onLockPropertiesDone(byte arg0) {
		
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
		
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
	public void onUnlockPropertiesDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
