package com.app.appwarpudpdemo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.app.appwarplisterner.ConnectionListener;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class MainActivity extends Activity {

	
	private EditText nameEditText;
	private WarpClient theClient;
    public ProgressDialog progressDialog;
    private ConnectionListener connectionListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		init();
	}
	
	public void onPlayGameClicked(View view){
		if(nameEditText.getText().length()==0){
			Utils.showToastAlert(this, getApplicationContext().getString(R.string.enterName));
			return;
		}
		String userName = nameEditText.getText().toString().trim();
		Utils.userName = userName;
		Log.d("Name to Join ", ""+userName);
		theClient.connectWithUserName(userName);
		progressDialog =  ProgressDialog.show(this, "", "connecting to appwarp");
		progressDialog.setCancelable(true);
		
	}
	
	private void init(){
		try {
			WarpClient.initialize(Constants.apiKey, Constants.secretKey);
			theClient = WarpClient.getInstance();
			connectionListener = new ConnectionListener(this);
			theClient.addConnectionRequestListener(connectionListener); 
		} catch (Exception ex) {
        	Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
	
	public void goToRoomList(){
		theClient.initUDP();
		Intent intent = new Intent(this, RoomlistActivity.class);
		startActivity(intent);
	}
	
}
