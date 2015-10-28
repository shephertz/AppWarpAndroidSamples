package app.shephertz.tictactoe;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	private char TYPE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		App42Handler.initialize(this);
	}

	public void onPlayGameClicked(View view){
		if(TYPE=='0' || TYPE=='X'){
			Intent intent = new Intent(this, GameActivity.class);
			intent.putExtra("TYPE", TYPE);
			startActivity(intent);
		}else{
			Util.showToastAlert(this, "Please select object to play");
		}
		
	}
	
	public void onLeaderBoardClicked(View view){
		Intent intent = new Intent(this, LeaderBoardActivity.class);
		startActivity(intent);
	}
	
	public void onMultiplayerClicked(View view){
		Intent intent = new Intent(this, MultiplayerGameActivity.class);
		startActivity(intent);
	}
	
	public void onExitClicked(View view){
		TYPE = 'X';
	}
	
	public void onZeroClicked(View view){
		TYPE = '0';
	}

}
