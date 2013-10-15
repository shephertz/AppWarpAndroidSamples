package appwarp.example;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.R;
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
	protected void onStart(){
		super.onStart();
		theClient.addConnectionRequestListener(this); 
		theClient.addRoomRequestListener(this);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		theClient.removeConnectionRequestListener(this);  
		theClient.removeRoomRequestListener(this);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if(theClient!=null){
			theClient.disconnect();
		}
	}
	
	
	public void onConnectClicked(View view){
		String userName = nameEditText.getText().toString();
		if(userName.length()>0){
			Utils.USER_NAME  = userName;
			progressDialog = ProgressDialog.show(this, "", "Please wait...");
			progressDialog.setCancelable(true);
			theClient.connectWithUserName(userName);
		}else{
			Utils.showToast(this, "Please enter name");
		}
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
            theClient.joinRoom(Constants.roomId);
        }
        else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
        	showToastOnUIThread("Connection recovered");
        }
        else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(MainActivity.this, "", "Recoverable connection error. Recovering session after 5 seconds");
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
        	showToastOnUIThread("Non-recoverable connection error.");
        }
	}
	
	@Override
	public void onDisconnectDone(final ConnectEvent event) {
		
	}
	
	@Override
	public void onInitUDPDone(byte result) {
		
	}

	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
		
		
	}

	@Override
	public void onJoinRoomDone(RoomEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			theClient.subscribeRoom(event.getData().getId());
		}else{
			showToastOnUIThread("onJoinRoomDone with ErrorCode: "+event.getResult());
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
	public void onSubscribeRoomDone(RoomEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			Intent intent = new Intent(this, ChatActivity.class);
			startActivity(intent);
		}else{
			showToastOnUIThread("onSubscribeRoomDone Failed with ErrorCode: "+event.getResult());
		}
		
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
		
	}

	@Override
	public void onUnlockPropertiesDone(byte arg0) {
		
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
		
	}
	private void showToastOnUIThread(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
