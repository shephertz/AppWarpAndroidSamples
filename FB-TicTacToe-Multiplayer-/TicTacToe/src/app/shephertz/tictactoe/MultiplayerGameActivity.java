package app.shephertz.tictactoe;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import app.shephertz.multiplayer.WarpController;

public class MultiplayerGameActivity extends Activity implements OnTouchListener{
	
	RelativeLayout grid_view;
	private char[][] ARRAY = new char[3][3];
	private Random random = new Random();
	
	private final int GRID_WIDTH = 256;
	private final int OBJECT_WIDTH = 64;
	private int GAP = 256/3;
	private char TYPE = '0';
	private final int WinningScore=100;
	
	private boolean isGameStarted = false;
	private boolean isUserTurn = false;
	
	private TextView notificationTextView;
	private TextView resultTextView;
		
	private void init(){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				ARRAY[i][j] = '-';
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		grid_view = (RelativeLayout)findViewById(R.id.grid_layout);
		notificationTextView = (TextView)findViewById(R.id.notification_text_view);
		resultTextView = (TextView)findViewById(R.id.result_text_view);
		grid_view.setOnTouchListener(this);
		notificationTextView.setText("Please wait...");
		init();
		connectToAppWarp();
		GAP = Util.dpToPx(getApplicationContext(), GAP);
	}
	
	private void connectToAppWarp(){
		WarpController.getInstance().setActivity(this);
		Util.UserName = Util.getRandomHexString(10);
		WarpController.getInstance().startApp(Util.UserName, this);
	}
	
	public void setType(char t){
		this.TYPE = t;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(isGameStarted && isUserTurn){
			getIndex(event.getX(), event.getY());
		}
		return false;
	}
	
	private void updateUI(int i, int j, char type){ 
	  ARRAY[i][j] = type;
	  int GAP = 256/3;
	  int DRAW_X = (j*GAP) + GAP/2 - OBJECT_WIDTH/2;
	  int DRAW_Y = (i*GAP) + GAP/2 - OBJECT_WIDTH/2;
	  ImageView imgView = new ImageView(this);
	  
	  if( type == '0' ) {
		  imgView.setImageDrawable(getResources().getDrawable(R.drawable.button_ai));
	  }else if ( type == 'X' ){
		  imgView.setImageDrawable(getResources().getDrawable(R.drawable.button_exit));
	  }
	    
	  RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((LayoutParams.WRAP_CONTENT), (LayoutParams.WRAP_CONTENT));
	  DRAW_X = Util.dpToPx(getApplicationContext(), DRAW_X);
	  DRAW_Y = Util.dpToPx(getApplicationContext(), DRAW_Y);
	  lp.leftMargin = DRAW_X;
	  lp.topMargin = DRAW_Y;
	  imgView.setLayoutParams(lp);
	  grid_view.addView(imgView);
	  
	}
	
	private void getIndex(float touchX, float touchY){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if ( touchX>(j*GAP) && touchX<((j*GAP)+GAP) && touchY>((i*GAP)) && touchY<(i*GAP)+GAP ) {
					if(ARRAY[i][j] == '-' ){
					if(TYPE=='0') {
		              ARRAY[i][j] = '0';
		              updateUI(i, j, TYPE);
		            }
		            else if( TYPE=='X') {
		              ARRAY[i][j] = 'X' ; 
		              updateUI(i, j, TYPE);
		            }
					checkGameComplete(i, j);
					}
				}
			}
		}
	}
	
	private void checkGameComplete(int i, int j){
		char r = Util.checkForWin(ARRAY);
		if(r!='-'){
			if(r==TYPE){
				showResultDialog("Congratulation, You have won",true);
				WarpController.getInstance().sendMove(i+"#"+j+"/"+"Win");
				return;
			}else{
				showResultDialog("Oops, You have lost, try again",false);  
				WarpController.getInstance().sendMove(i+"#"+j+"/"+"Loose");
				return;
			}
		}
		WarpController.getInstance().sendMove(i+"#"+j+"/"+"");
	}
	

	private void showResultDialog(String message,boolean isWon){
		
		if(isWon){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message)
			       .setCancelable(false)
			       .setNegativeButton("Submit Score", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Intent intent = new Intent(MultiplayerGameActivity.this, LeaderBoardActivity.class);
						   intent.putExtra("score", WinningScore);
						   startActivity(intent);
						   MultiplayerGameActivity.this.finish();
			           }
			       })
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                onBackPressed();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message)
			       .setCancelable(false)
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                onBackPressed();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}
	public void updateNotification(final String text){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notificationTextView.setText(text);
			}
		});
	}
	
	public void startGame(final String turnUser){
		isGameStarted = true;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(turnUser.equals(Util.UserName)){
					isUserTurn = true;
					notificationTextView.setText("Your Turn");
				}else{
					isUserTurn = false;
					notificationTextView.setText("Enemy Turn");
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		isGameStarted = false;
		isUserTurn = false;
		WarpController.getInstance().stopApp();
		super.onBackPressed();
	}
	
	public void onMoveCompleted(final String moveData, final String sender, final String nextTurn){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!sender.equals(Util.UserName) && moveData.length()>0){
					final int i = Integer.parseInt(moveData.substring(0,moveData.indexOf('#')));
					final int j = Integer.parseInt(moveData.substring(moveData.indexOf('#')+1, moveData.indexOf('/')));
					final String data = moveData.substring(moveData.indexOf('#')+1, moveData.length());
					if(TYPE=='0'){
						updateUI(i, j, 'X');
					}else{
						updateUI(i, j, '0');
					}
					if(data.trim().length()>0){
						if(data.indexOf("Win")!=-1){
							isGameStarted = false;
							showResultDialog("Oops! You have lost,try again",false);
						}
						if(data.indexOf("Loose")!=-1){
							isGameStarted = false;
							showResultDialog("Congratulation! You have won",true);
						}
					}
						
				}else{
					// empty move
				}
				if(nextTurn.equals(Util.UserName)){
					isUserTurn = true;
					notificationTextView.setText("Your Turn");
				}else{
					isUserTurn = false;
					notificationTextView.setText("Enemy Turn");
				}
			}
		});
		
	}
	
	public void onEnemyLeft(){
		if(isGameStarted){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showResultDialog("Congrats! You Win\nEnemy Left",true);
				}
			});
		}
	}
	
}
