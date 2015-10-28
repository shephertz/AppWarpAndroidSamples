package app.shephertz.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.shephertz.multiplayer.WarpController;

/**
 * @author Vishnu
 *
 */
public class GameActivity extends Activity {
	
	RelativeLayout grid_view;
	private char[][] ARRAY = new char[3][3];
	private char TYPE = '0';
	private boolean isGameStarted = false;
	private boolean isUserTurn = false;
	private TextView notificationTextView;
	
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
		setContentView(R.layout.game);

		notificationTextView = (TextView)findViewById(R.id.notification_text_view);
		notificationTextView.setText("Please wait...");
		init();
		connectToAppWarp();
	}
	
	private void connectToAppWarp(){
		WarpController.getInstance().setActivity(this);
		Util.UserName = Util.getRandomHexString(10);
		WarpController.getInstance().startApp(Util.UserName, this);
	}
	
	public void setType(char t){
		this.TYPE = t;
	}
	public void onCellClicked(View view) {
		
		ImageButton selectedButton = (ImageButton)view;
		int idClicked=selectedButton.getId();
	   int cellIndex = getCellIndexFromView(idClicked);		
	if(cellIndex>=0&&isGameStarted && isUserTurn){
		int indexI=cellIndex/3;
		int indexJ=cellIndex%3;
		updateGameAndUI(indexI,indexJ);
		}
	}
	
	private int getCellIndexFromView(int viewId )
	{
		switch (viewId)
		{
		case R.id.cell_00 :
			return 0;
		case R.id.cell_01 :
			return 1;
		case R.id.cell_02 :
			return 2;
		case R.id.cell_10 :
			return 3;
		case R.id.cell_11 :
			return 4;
		case R.id.cell_12 :
			return 5;
		case R.id.cell_20 :
			return 6;
		case R.id.cell_21 :
			return 7;
		case R.id.cell_22 :
			return 8;			
		}
		return -1;
	}
	private int getImageId(int i,int j )
	{
		int index=i*3+j;
		switch (index)
		{
		case  0:
			return R.id.cell_00;
		case  1:
			return R.id.cell_01;
		case  2:
			return R.id.cell_02;
		case  3:
			return R.id.cell_10;
		case  4:
			return R.id.cell_11;
		case  5:
			return R.id.cell_12;
		case 6:
			return R.id.cell_20 ;
		case  7:
			return R.id.cell_21;
		case 8:
			return R.id.cell_22 ;			
		}
		return -1;
	}
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		if(isGameStarted && isUserTurn){
//			getIndex(event.getX(), event.getY());
//		}
//		return false;
//	}
	
	private void updateUI(int i, int j, char type){ 
	  ARRAY[i][j] = type;
	  int imageId=getImageId(i, j);
	  if(imageId!=-1){
	  ImageButton imge=(ImageButton) findViewById(imageId);
	  if( type == '0' ) {
		  imge.setImageResource(R.drawable.circle_cell);
		  
	  }else if ( type == 'X' ){
		  imge.setImageResource(R.drawable.cross_cell);
	  }
	  }
	}
	
	private void updateGameAndUI(int i,int j){
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
					WarpController.currentState=WarpController.StatePlayingTurn;
					notificationTextView.setText("Your Turn");
				}else{
					WarpController.currentState=WarpController.StateWaitingTurn;
					isUserTurn = false;
					notificationTextView.setText("Enemy Turn");
				}
			}
		});
		
	}
	public void validateMoveHistory(final String moveData, final String sender, final String nextTurn){
		if(moveData.length()>0){
			final int i = Integer.parseInt(moveData.substring(0,moveData.indexOf('#')));
			final int j = Integer.parseInt(moveData.substring(moveData.indexOf('#')+1, moveData.indexOf('/')));
			if(ARRAY[i][j] == '-' ){
				System.out.println(""+sender+"--"+nextTurn);
				onMoveCompleted(moveData, sender, nextTurn);
				
			}
		}
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
