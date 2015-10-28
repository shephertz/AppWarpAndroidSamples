package app.shephertz.social;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.BaseAdapter;
import app.shephertz.tictactoe.App42Handler;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class FacebookService {
    	
	private Handler mUIThreadHandler = null;
	private Facebook facebook = new Facebook(App42Handler.FB_APP_ID);
	public static AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;    
	private Context appContext = null;
    private static FacebookService _instance = null;
    
    public static FacebookService instance(){
    	if(_instance == null){
    		_instance = new FacebookService();
    	}
    	return _instance;
    }
    
    public void setContext(Context context){
    	_instance.appContext = context;
    	_instance.refreshFromContext();
    }
    
    public void signout() throws MalformedURLException, IOException{
    	facebook.logout(appContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("display_name", "");
        editor.putString("warp_join_id", "");
        editor.putString("profile_url", "");
        editor.putString("access_token", null);
        editor.putLong("access_expires", 0);
        editor.commit();
    }
    
    private void refreshFromContext(){
    	mPrefs = appContext.getSharedPreferences("MyGamePreferences", android.content.Context.MODE_PRIVATE);
        /*
         * Get existing access_token if any
         */
        
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }   
        if(facebook.isSessionValid()){
        	UserContext.MyAccessToken = access_token;
        	UserContext.MyDisplayName = mPrefs.getString("display_name", "");
        	UserContext.MyUserName = mPrefs.getString("warp_join_id", "");
        	UserContext.MyPicUrl = mPrefs.getString("profile_url", "");
        }else{
        	UserContext.MyDisplayName = "";
        	UserContext.MyUserName = "";
        	UserContext.MyPicUrl = "";
        	UserContext.MyAccessToken = "";
        }
    }
    
	private FacebookService(){
        mAsyncRunner = new AsyncFacebookRunner(facebook);                
	}
	
	public void authorizeCallback(int requestCode, int resultCode, Intent data)
	{
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	public boolean isFacebookSessionValid(){
		return facebook.isSessionValid();
	}
    
    public void fetchFacebookProfile(final FacebookProfileRequesterActivity hostActivity)
    {
    	if(mUIThreadHandler == null){
    		mUIThreadHandler = new Handler();
    	}
        facebook.authorize(hostActivity, new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
            	System.out.println("authorize on complete");
            	if(mPrefs == null){
            		mPrefs = appContext.getSharedPreferences("MyGamePreferences", android.content.Context.MODE_PRIVATE);
            	}
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("access_token", facebook.getAccessToken());
                editor.putLong("access_expires", facebook.getAccessExpires());
                editor.commit();
                FacebookService.this.getFacebookProfile(hostActivity);
            }

            @Override
            public void onFacebookError(FacebookError error) {
            	System.err.println("Facebook onFacebookError");
            }

            @Override
            public void onError(DialogError e) {
            	System.err.println("Facebook DialogError");
            }

            @Override
            public void onCancel() {
            	System.err.println("Facebook onCancel");
            }
        });	
    }
    
    private void getFacebookProfile(FacebookProfileRequesterActivity callingActivity)
    {
        Bundle params = new Bundle();
        params.putString("fields", "name, picture");    	
    	mAsyncRunner.request("me", params, new FacebookRequestListener(callingActivity));
    }
    
    public void getFacebookFriends(FacebookFriendListRequester caller){

    	if(mUIThreadHandler == null){
    		mUIThreadHandler = new Handler();
    	}
    	
        Bundle params = new Bundle();
        params.putString("fields", "name, picture, installed");    	
    	mAsyncRunner.request("me/friends", params, new FacebookFriendListRequest(caller));
    }
    
    private class FacebookFriendListRequest implements RequestListener{

    	FacebookFriendListRequester callBack;
		public FacebookFriendListRequest(FacebookFriendListRequester caller){
			this.callBack = caller;
		}
		
		@Override
		public void onComplete(String response, Object state) {
			JSONObject jsonObject;
			final ArrayList<JSONObject> gameFriends = new ArrayList<JSONObject>();
			final ArrayList<JSONObject> nonGameFriends = new ArrayList<JSONObject>();
			try {
				jsonObject = new JSONObject(response);
				JSONArray friends = jsonObject.getJSONArray("data");
				for(int i=0; i<friends.length(); i++){
					JSONObject friend = friends.getJSONObject(i);
					if(friend.optBoolean("installed")){
						gameFriends.add(friend);
					}
					else{
						nonGameFriends.add(friend);
					}
				}
	            mUIThreadHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	callBack.onListFetched(gameFriends, nonGameFriends);
	                }
	            });
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
	private class FacebookRequestListener implements RequestListener
	{
		FacebookProfileRequesterActivity callBack;
		public FacebookRequestListener(FacebookProfileRequesterActivity callingActivity){
			this.callBack = callingActivity;
		}
		
		@Override
		public void onComplete(String response, Object state) {
	        JSONObject jsonObject;
	        try {
	            jsonObject = new JSONObject(response);
	            JSONObject picObj = jsonObject.getJSONObject("picture");
	            JSONObject dataObj = picObj.getJSONObject("data");
	            
	            UserContext.MyDisplayName = jsonObject.getString("name");
	            UserContext.MyUserName = jsonObject.getString("id");
	            UserContext.MyPicUrl = dataObj.getString("url");
	            
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("display_name", UserContext.MyDisplayName);
                editor.putString("warp_join_id", UserContext.MyUserName);
                editor.putString("profile_url", UserContext.MyPicUrl);
                editor.commit();
                
	            mUIThreadHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	callBack.onFacebookProfileRetreived(true);
	                }
	            });

	        } catch (JSONException e) {
	            mUIThreadHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	callBack.onFacebookProfileRetreived(false);
	                }
	            });
	            e.printStackTrace();
	        }
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public interface FacebookFriendListRequester{
		public void onListFetched(ArrayList<JSONObject> gameFriends, ArrayList<JSONObject> nonGameFriends);
	}
}



