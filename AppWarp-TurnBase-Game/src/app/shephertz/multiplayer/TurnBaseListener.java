package app.shephertz.multiplayer;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener;

/**
 * @author Vishnu
 *
 */
public class TurnBaseListener implements TurnBasedRoomListener{
private WarpController callBack;
	
	public TurnBaseListener(WarpController callBack) {
		this.callBack = callBack;
	}
	@Override
	public void onGetMoveHistoryDone(byte arg0, MoveEvent[] arg1) {
		// TODO Auto-generated method stub
		if(arg0==WarpResponseResultCode.SUCCESS){
			callBack.onGetTurnHistory(arg1);
		}
	}

	@Override
	public void onSendMoveDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartGameDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopGameDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener#onSetNextTurnDone(byte)
	 */
	@Override
	public void onSetNextTurnDone(byte arg0) {
		// TODO Auto-generated method stub
		
	}

}
