package com.app.appwarplisterner;

import android.util.Log;

import com.app.appwarpudpdemo.MainActivity;
import com.app.appwarpudpdemo.Utils;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionListener implements ConnectionRequestListener {

	private MainActivity container;

	public ConnectionListener(MainActivity owner) {
		this.container = owner;
	}

	@Override
	public void onConnectDone(final ConnectEvent event) {
		Log.d("onConnectDone", event.getResult()+" listener ");
		container.progressDialog.dismiss();
		container.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (event.getResult() == WarpResponseResultCode.SUCCESS) {
					container.goToRoomList();
				} else {
					Utils.showToastAlert(container.getApplicationContext(), "Connection Failed");
				}
			}
		});
	}

	@Override
	public void onDisconnectDone(ConnectEvent event) {
		if (event.getResult() == WarpResponseResultCode.SUCCESS) {
			
		} else {
			Utils.showToastAlert(container.getApplicationContext(), "Can't Disconnect");
		}
	}

	@Override
	public void onInitUDPDone(final byte resultCode) {
		Log.d("onInitUDPDone", resultCode+" listener ");
		container.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(resultCode == WarpResponseResultCode.SUCCESS){
					Utils.showToastAlert(container.getApplicationContext(), "onInitUDPDone: Full-Deplex UDP");
				}else if(resultCode == WarpResponseResultCode.BAD_REQUEST){
					Utils.showToastAlert(container.getApplicationContext(), "onInitUDPDone: Incomming Blocked UDP");
				}
			}
		});
	}

}
