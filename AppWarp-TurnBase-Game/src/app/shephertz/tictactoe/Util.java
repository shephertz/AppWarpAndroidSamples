package app.shephertz.tictactoe;

import java.util.Random;
import android.content.Context;
import android.widget.Toast;

/**
 * @author Vishnu
 *
 */
public class Util {

	public static String UserName;
	
	/**
	 * @param ARRAY
	 * @return
	 */
	public static char checkForWin(char[][] ARRAY){
	  if(ARRAY[0][0]!='-' && ARRAY[0][0] == ARRAY[0][1] && ARRAY[0][0] == ARRAY[0][2]){
		  return ARRAY[0][0];
	  }else if(ARRAY[1][0]!='-' && ARRAY[1][0] == ARRAY[1][1] && ARRAY[1][0] == ARRAY[1][2]){
		  return ARRAY[1][0];  
	  }else if(ARRAY[2][0]!='-' && ARRAY[2][0] == ARRAY[2][1] && ARRAY[2][0] == ARRAY[2][2]){
		  return ARRAY[2][0];
	  }else if(ARRAY[0][0]!='-' && ARRAY[0][0] == ARRAY[1][0] && ARRAY[0][0] == ARRAY[2][0]){
		  return ARRAY[0][0];  
	  }else if(ARRAY[0][1]!='-' && ARRAY[0][1] == ARRAY[1][1] && ARRAY[0][1] == ARRAY[2][1]){
		  return ARRAY[0][1] ;
	  }else if(ARRAY[0][2]!='-' && ARRAY[0][2] == ARRAY[1][2] && ARRAY[0][2] == ARRAY[2][2]){
		  return ARRAY[0][2];
	  }else if(ARRAY[0][0]!='-' && ARRAY[0][0] == ARRAY[1][1] && ARRAY[0][0] == ARRAY[2][2]){
		  return ARRAY[0][0];
	  }else if(ARRAY[0][2]!='-' && ARRAY[0][2] == ARRAY[1][1] && ARRAY[0][2] == ARRAY[2][0]){
		  return ARRAY[0][2];
	  }  
	  return '-';
	}
	
	/**
	 * @param ARRAY
	 * @return
	 */
	public static boolean hasEmptyPlace(char[][] ARRAY){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(ARRAY[i][j] == '-'){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param ctx
	 * @param message
	 */
	public static void showToastAlert(Context ctx, String message){
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * @param numchars
	 * @return
	 */
	public static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        UserName = sb.toString().substring(0, numchars);
        return UserName;
    }
	

}
