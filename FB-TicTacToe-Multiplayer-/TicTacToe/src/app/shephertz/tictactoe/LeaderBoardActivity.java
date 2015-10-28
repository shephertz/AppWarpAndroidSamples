package app.shephertz.tictactoe;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import app.shephertz.social.FacebookProfileRequesterActivity;
import app.shephertz.social.FacebookService;
import app.shephertz.social.UserContext;

import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.MetaResponse.JSONDocument;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.Game.Score;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.Social.PublicProfile;
import com.shephertz.app42.paas.sdk.android.user.User;

public class LeaderBoardActivity extends FacebookProfileRequesterActivity implements App42CallBack{
	
	TextView notificationText;
	ListView userList;
	LeaderBoardAdapter adapter;
	long score;
	boolean isGlobal;
	ProgressDialog progressDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_list);
        userList = (ListView) findViewById(R.id.users_list);
        notificationText = (TextView) findViewById(R.id.user_list_text_view);
        score = getIntent().getLongExtra("score", 0);
        FacebookService.instance().setContext(getApplicationContext());
        
        if(App42Handler.isAuthenticated()){
        	progressDialog = ProgressDialog.show(this, "", "Please Wait...");
	        if(score>0){
	        	App42Handler.instance().submitScore(UserContext.MyUserName, score, this);
	        }else{
	        	isGlobal = true;
	    		App42Handler.instance().getLeaderBoard(this);
	        }
        }else{
        	FacebookService.instance().fetchFacebookProfile(this);
        }
        
    }
	
	public void onFriendsClicked(View view){
		if(isGlobal){
			isGlobal = false;
			progressDialog = ProgressDialog.show(this, "", "Please Wait..");
			App42Handler.instance().getSocialLeaderBoard(UserContext.MyAccessToken, this);
		}
	}
	
	public void onGlobalClicked(View view){
		if(!isGlobal){
			isGlobal = true;
			progressDialog = ProgressDialog.show(this, "", "Please Wait..");
			App42Handler.instance().getLeaderBoard(this);
		}
	}
	
	@Override
	public void onException(Exception exception) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(progressDialog!=null){
					progressDialog.dismiss();
					progressDialog = null;
				}
				notificationText.setText("Result Not Found");
			}
		});
	}

	@Override
	public void onSuccess(final Object object) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(score>0){
					score = 0;
					isGlobal = true;
					App42Handler.instance().getLeaderBoard(LeaderBoardActivity.this);
					return;
				}
				
				Game game = (Game)object;
				Log.d("Game", game.toString());
				ArrayList<Score> scoreList = game.getScoreList();
				if(isGlobal){
					ArrayList<String> idList = new ArrayList<String>();
					for(int i=0;i<scoreList.size();i++){
						Score score = scoreList.get(i);
						if(score.getJsonDocList().size()>0){
							for(int j=0;j<score.getJsonDocList().size();j++){
								String doc = score.getJsonDocList().get(j).getJsonDoc();
								try {
									JSONObject object = new JSONObject(doc);
									if(object.get("id").equals(score.getUserName())){
										score.setUserName(object.getString("name"));
									}
								} catch (JSONException e) {
									// json exception found
								}
								
							}
						}
					}
				}
				if(progressDialog!=null){
					progressDialog.dismiss();
					progressDialog = null;
				}
				if(scoreList.size()>0){
					adapter = new LeaderBoardAdapter(LeaderBoardActivity.this, scoreList, isGlobal);
					userList.setAdapter(adapter);
				}
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        FacebookService.instance().authorizeCallback(requestCode, resultCode, data);
    }
	
	public void onFacebookProfileRetreived(boolean success) {
		App42Handler.instance().setUserCredentials();
		if(success){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog = ProgressDialog.show(LeaderBoardActivity.this, "", "Please Wait...");
				}
			});
			
    		if(score>0){
	        	App42Handler.instance().submitScore(UserContext.MyUserName, score, this);
	        }else{
	        	isGlobal = true;
	    		App42Handler.instance().getLeaderBoard(this);
	        }
		}
	}

}
