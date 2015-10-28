package app.shephertz.multiplayer;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

/**
 * @author Vishnu
 *
 */
public class ConnectionListener implements ConnectionRequestListener {

	WarpController callBack;
	
	/**
	 * @param callBack
	 */
	public ConnectionListener(WarpController callBack){
		this.callBack = callBack;
	}
	
	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener#onConnectDone(com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent)
	 */
	public void onConnectDone(ConnectEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			callBack.onConnectDone(true);
		}
		//Here we check for Connection recovery in Game
		else if(event.getResult() == WarpResponseResultCode.SUCCESS_RECOVERED){
			callBack.onRecoverconnection();
		}
	    else if(event.getResult() == WarpResponseResultCode.CONNECTION_ERROR_RECOVERABLE){
	    	callBack.RecoverConnection();
	    }
		else {
			callBack.onConnectDone(false);
		}
		
	}

	public void onDisconnectDone(ConnectEvent e) {
		
	}

	@Override
	public void onInitUDPDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

}
