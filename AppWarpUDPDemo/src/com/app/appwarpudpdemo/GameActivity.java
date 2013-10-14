package com.app.appwarpudpdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;

import com.app.appwarplisterner.WarpListener;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.util.Util;

public class GameActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, SensorEventListener{

	public static int CAMERA_WIDTH = 480;
	public static int CAMERA_HEIGHT = 800;

	private Camera mCamera;
	private Scene mMainScene;

	private BitmapTextureAtlas coinBitmapTextureAtlas;
	
	private BitmapTextureAtlas mBitmapTextureAtlasPlayer;
	private BitmapTextureAtlas mBitmapTextureAtlasEnemy;
	
	private TiledTextureRegion mTiledTextureRegionPlayer;
	private TiledTextureRegion mTiledTextureRegionEnemy;
	
	private TiledTextureRegion mCoinTiledTextureRegion;
	private Sprite coin;
	
	private RepeatingSpriteBackground mGrassBackground;
	
	private WarpClient theClient;
	private WarpListener eventHandler = new WarpListener(this);
	private Random ramdom = new Random();

	private HashMap<String, User> userMap = new HashMap<String, User>();
	
	private String roomId = "";
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	private ArrayList<Line> lineArray = new ArrayList<Line>();
	
	private ProgressDialog progressDialog;
	
	private boolean isGameRunning = false;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		try{
			theClient = WarpClient.getInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		userMap.clear();
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
	}

	@Override
	protected void onCreateResources() {
		/* Load all the textures this game needs. */
		
		this.mGrassBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(), "background_grass.png"), this.getVertexBufferObjectManager());
		
		this.mBitmapTextureAtlasPlayer = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		this.mBitmapTextureAtlasEnemy = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
		
		this.mTiledTextureRegionPlayer = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlasPlayer, this, "monster1.png", 0, 0, 1, 1);
		this.mTiledTextureRegionEnemy = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlasEnemy, this, "monster2.png", 0, 0, 1, 1);
		
		this.coinBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 128, 128);
		this.mCoinTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.coinBitmapTextureAtlas, this, "coin-shrimp-sprite.png", 0, 0, 1, 1);
		
		this.coinBitmapTextureAtlas.load();
		this.mBitmapTextureAtlasPlayer.load();
		this.mBitmapTextureAtlasEnemy.load();
		
		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		init(roomId);
		
	}

	@Override
	protected Scene onCreateScene() {
//		this.mEngine.registerUpdateHandler(new FPSLogger()); // logs the frame rate

		/* Create Scene and set background colour to (1, 1, 1) = white */
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(mGrassBackground);
		this.mMainScene.setOnSceneTouchListener(this);
		this.coin = new Sprite(CAMERA_WIDTH/2-mCoinTiledTextureRegion.getWidth()/2, CAMERA_HEIGHT/2-mCoinTiledTextureRegion.getHeight()/2, mCoinTiledTextureRegion, this.getVertexBufferObjectManager());
		this.mMainScene.attachChild(coin);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		createMaze();
		return this.mMainScene;
	}
	
	private void createMaze(){
		int constantXX = CAMERA_WIDTH/10;
		int constantYY = CAMERA_HEIGHT/10;
		createLaserLine(CAMERA_WIDTH/2-constantXX, CAMERA_HEIGHT/2-constantYY, CAMERA_WIDTH/2-constantXX, CAMERA_HEIGHT/2+constantYY, false);
		createLaserLine(CAMERA_WIDTH/2+constantXX, CAMERA_HEIGHT/2-constantYY, CAMERA_WIDTH/2+constantXX, CAMERA_HEIGHT/2+constantYY, false);
		createLaserLine(CAMERA_WIDTH/2-2*constantXX, CAMERA_HEIGHT/2-2*constantYY, CAMERA_WIDTH/2+3*constantXX, CAMERA_HEIGHT/2-2*constantYY, true);
		createLaserLine(CAMERA_WIDTH/2-3*constantXX, CAMERA_HEIGHT/2+2*constantYY, CAMERA_WIDTH/2+2*constantXX, CAMERA_HEIGHT/2+2*constantYY, true);
		createLaserLine(CAMERA_WIDTH/2-(3*constantXX+constantXX/2), CAMERA_HEIGHT/2+2*constantYY, CAMERA_WIDTH/2-(3*constantXX+constantXX/2), CAMERA_HEIGHT/2-3*constantYY, false);
		createLaserLine(CAMERA_WIDTH/2+3*constantXX+constantXX/2, CAMERA_HEIGHT/2-2*constantYY, CAMERA_WIDTH/2+3*constantXX+constantXX/2, CAMERA_HEIGHT/2+3*constantYY, false);
	}
	
	private void createLaserLine(int x1, int y1, int x2, int y2, boolean isHorizontal){
		Line line = new Line(x1, y1, x2, y2, 5, getVertexBufferObjectManager());
		line.setColor(43, 255, 24);
        lineArray.add(line);
        this.mMainScene.attachChild(line);
	}
	
	@Override
	public void onResume(){
		super.onResume();
//		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	public void onPause(){
		super.onPause();
//		mSensorManager.unregisterListener(this);
	}

	
	
	private void init(String roomId){
		if(theClient!=null){
			theClient.addRoomRequestListener(eventHandler);
			theClient.addNotificationListener(eventHandler);
			Log.d(this.getClass().toString(), "Room Id is: "+roomId);
			theClient.getLiveRoomInfo(roomId);
			theClient.subscribeRoom(roomId);
		}
	}
	public void addMorePlayer(boolean isMine, String userName){
		
		Log.d("addMorePlayer", "userName: "+userName + "  isMine: "+isMine);
		
		if(isMine){
			final Sprite face = new Sprite(10, 10, mTiledTextureRegionPlayer, this.getVertexBufferObjectManager());
			this.mMainScene.attachChild(face);
//			face.setScale(1.5f);
			this.mMainScene.setOnSceneTouchListener(this);
			User user = new User(face.getX(), face.getY(), face);
			userMap.put(userName, user);
			face.registerUpdateHandler(new IUpdateHandler() {
				
				@Override
				public void reset() {
					
					
				}
				
				@Override
				public void onUpdate(float time) {
					if(isGameRunning && face.collidesWith(coin)){
						handleGameResult(false, "WON");
					}else if(isGameRunning && lineArray!=null ){
						for(Line line:lineArray){
							if(face.collidesWith(line)){
								vibrate();
								face.setX(20);
								face.setY(20);
								break;
							}
						}
					}
				}
			});
			if(userMap.size()<2){
				showProgressBar();
			}
		}else {
			final Sprite face = new Sprite(40, 10, mTiledTextureRegionEnemy, this.getVertexBufferObjectManager());
//			face.setScale(1.5f);
			this.mMainScene.attachChild(face);
			User user = new User(face.getX(), face.getY(), face);
			userMap.put(userName, user);
		}
//		Log.d("addMorePlayer", userMap.size()+"");
//		Log.d("userMap.get("+userName, userMap.get(userName)+"");
		if(userMap.size()==2){
			isGameRunning = true;
			if(progressDialog!=null){
				progressDialog.dismiss();
			}
			
		}
	}
	
	private void showProgressBar(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog = ProgressDialog.show(GameActivity.this, "", "Waiting for other user...");
				timerDelayRemoveDialog(30000, progressDialog);
			}
		});
		
	}
	
	public void timerDelayRemoveDialog(long time, final Dialog d){
		new Handler().postDelayed(new Runnable() {
	        public void run() {  
	        	if(!isGameRunning ){
	        		d.dismiss();  
		            Utils.showToastAlert(getApplicationContext(), "No online user found");
		            mSensorManager.unregisterListener(GameActivity.this);
		    		if(theClient!=null){
		    			handleLeave(Utils.userName);
		    			removeListener(false);
		    		}
		            clearResources();
		            GameActivity.this.finish();
	    		}
	        }
	    }, time); 
	}
	
	public void handleGameResult(boolean isRemote, String state){
		if(!isGameRunning){
			return;
		}
		vibrate();
		mSensorManager.unregisterListener(this);
		Log.d("handleGameResult: ", state);
		String message = null;
		if(isRemote){
			if(state.equals("WON")){
				message = "oops! You Loose";
			}else if(state.equals("LOOSE")){
				message = "Congratulation! You Won";
			}
			removeListener(true);
		}else{
			if(state.equals("WON")){
				message = "Congratulation! You Won";
			}else if(state.equals("LOOSE")){
				message = "oops! You Loose";
			}
			sendGameResult(state);
			removeListener(false);
		}
		showResultDialog(message);
		isGameRunning = false;
	}
	
	private void showResultDialog(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder1 = new AlertDialog.Builder(GameActivity.this);
		        builder1.setMessage(message);
		        builder1.setCancelable(false);
		        builder1.setPositiveButton("Back",
		                new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int id) {
			                GameActivity.this.finish();
			            }
		        });
		        AlertDialog alert11 = builder1.create();
		        alert11.show();
			}
		});
	}
	
	private void sendGameResult(String result){
		String message = '_'+Util.userName +'#'+ result;
		theClient.sendUDPUpdatePeers(message.getBytes());
	}
	
	private void sendUpdateEvent(float xCord, float yCord){
		try{
			String message = Util.userName +'#'+ xCord + "@" + yCord;
			theClient.sendUDPUpdatePeers(message.getBytes());
		}catch(Exception e){
			Log.d("Exception: sendUpdateEvent", e.getMessage());
		}
	}
	
	public void handleLeave(String name) {
		if(name.length()>0 && userMap.get(name)!=null){
			Sprite sprite = userMap.get(name).getSprite();
			final EngineLock engineLock = this.mEngine.getEngineLock();
			engineLock.lock();
			this.mMainScene.detachChild(sprite);
			sprite.dispose();
			sprite = null;
			userMap.remove(name);
			engineLock.unlock();
		}
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		return false;
	}
	
	
	public void updateMove(final String userName, final float x, final float y){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(userMap.get(userName)!=null){
					Sprite sprite = userMap.get(userName).getSprite();
					if(userName.equals(Util.userName)){// for local player
						float newX = sprite.getX() + x;
						float newY = sprite.getY() + y;
						if(newX<0){
							newX = 0;
						}
						if(newY<0){
							newY = 0;
						}
						if(newX>CAMERA_WIDTH-mTiledTextureRegionPlayer.getWidth()){
							newX = CAMERA_WIDTH-mTiledTextureRegionPlayer.getWidth();
						}
						if(newY>CAMERA_HEIGHT-mTiledTextureRegionPlayer.getHeight()){
							newY = CAMERA_HEIGHT-mTiledTextureRegionPlayer.getHeight();
						}
						float perXX = Utils.getPercentFromValue(newX, CAMERA_WIDTH);
						float perYY = Utils.getPercentFromValue(newY, CAMERA_HEIGHT);
						sendUpdateEvent(perXX, perYY);
						sprite.setX(newX);
						sprite.setY(newY);
					}else{ // if it is from remote player
						float valueXX = Utils.getValueFromPercent(x, CAMERA_WIDTH);
						float valueYY = Utils.getValueFromPercent(y, CAMERA_HEIGHT);
						sprite.setX(valueXX);
						sprite.setY(valueYY);
					}
					
				}else{
//					Log.d("updateMove", ""+userName+ " is not in  map. map size is"+userMap.size());
				}
			}
		});
	}
	
	
	private void removeListener(boolean deleteRoom){
		theClient.leaveRoom(roomId);
		theClient.unsubscribeRoom(roomId);
		if(deleteRoom){
			theClient.deleteRoom(roomId);
		}
		theClient.removeRoomRequestListener(eventHandler);
		theClient.removeNotificationListener(eventHandler);
	}
	
	@Override
	public void onBackPressed() {
		isGameRunning = false;
		mSensorManager.unregisterListener(this);
		if(theClient!=null){
			handleLeave(Utils.userName);
			removeListener(false);
		}
		super.onBackPressed();
	}
	
	public void clearResources(){
		this.mMainScene.dispose();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(isGameRunning){
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				float x = -event.values[0];
				float y = event.values[1];
				updateMove(Utils.userName, x*10, y*10);
			}
		}
	}
	
	private void vibrate(){
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(500);
	}
	
}