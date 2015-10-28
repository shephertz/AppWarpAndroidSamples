package app.shephertz.tictactoe;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnTouchListener{
	
	RelativeLayout grid_view;
	private char[][] ARRAY = new char[3][3];
	private Random random = new Random();
	
	private final int GRID_WIDTH = 256;
	private final int OBJECT_WIDTH = 64;
	private int GAP = 256/3;
	private char TYPE;
	
	
	private TextView notificationTextView;
	private TextView resultTextView;
	private long GameStartTime;
	
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
		TYPE = getIntent().getCharExtra("TYPE", 'X');
		notificationTextView.setText("Game Started");
		init();
		GameStartTime = System.currentTimeMillis();
		GAP = Util.dpToPx(getApplicationContext(), GAP);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		getIndex(event.getX(), event.getY());
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
					if(ARRAY[i][j] == '-' && TYPE=='0') {
		              ARRAY[i][j] = '0';
		              updateUI(i, j, TYPE);
		              completeComputerTurn();
		            }
		            else if(ARRAY[i][j] == '-' && TYPE=='X') {
		              ARRAY[i][j] = 'X' ; 
		              updateUI(i, j, TYPE);
		              completeComputerTurn();
		            }
					handleGameOver();
				}
			}
		}
	}
	
	private void completeComputerTurn(){
		// check empty space
		if(!Util.hasEmptyPlace(ARRAY)){
			showResultDialog("Match Draw");
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(ARRAY[i][j]=='-'){
					list.add(i+"-"+j);
				}
			}
		}
		String s = list.get(random.nextInt(list.size()));
		int i = Integer.parseInt(s.substring(0,s.indexOf('-')));
		int j = Integer.parseInt(s.substring(s.indexOf('-')+1,s.length()));
		if(TYPE=='X'){
			updateUI(i, j, '0');
		}else{
			updateUI(i, j, 'X');
		}
	}
	
	private void handleGameOver(){
		char r = Util.checkForWin(ARRAY);
		if(r!='-'){
			if(r==TYPE){
				showResultDialog("Congrats, You Win");
			}else{
				showResultDialog("Oops, You Loose");  
			}
		}
	}
	
	private void showResultDialog(String message){
		long timeTaken = System.currentTimeMillis()-GameStartTime;
		final long score = 60000 - timeTaken;
		
		if(message.indexOf("Win")!=-1 && score>0){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(message)
			       .setCancelable(false)
			       .setNegativeButton("Submit Score", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Intent intent = new Intent(GameActivity.this, LeaderBoardActivity.class);
						   intent.putExtra("score", score);
						   startActivity(intent);
						   GameActivity.this.finish();
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
	
}
