package appwarp.example;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class Utils {

	public static String myUserName = "";
	
	public static String[] removeLocalUserNameFromArray(String[] user){
		ArrayList<String> userList = new ArrayList<String>();
		for(int i=0;i<user.length;i++){
			Log.d("i="+i, user[i]);
			if(myUserName.equals(user[i])==false){
				userList.add(user[i]);
			}
		}
		String returnArray[] = new String[userList.size()];
		for(int i=0;i<returnArray.length;i++){
			returnArray[i] = userList.get(i).toString();
		}
		return returnArray;
	}
	
	public static void showToast(Context ctx,String message){
		Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
	}
	
	
}
