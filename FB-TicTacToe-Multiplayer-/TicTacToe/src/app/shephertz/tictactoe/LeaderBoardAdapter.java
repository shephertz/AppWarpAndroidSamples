package app.shephertz.tictactoe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shephertz.app42.paas.sdk.android.game.Game.Score;
import com.shephertz.app42.paas.sdk.android.user.User;

public class LeaderBoardAdapter extends BaseAdapter{
	
	private Activity activity;
	private ArrayList<Score> userList;
	private LayoutInflater inflator;
	private boolean isGlobal;
	
	public LeaderBoardAdapter(Activity activity, ArrayList<Score> list, boolean isFriendList){
		this.activity = activity;
		this.userList = list;
		this.isGlobal = isFriendList;
		inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Score getItem(int index) {
		return userList.get(index);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		if(itemView==null){
			itemView = inflator.inflate(R.layout.leaderboard_list_item, null);
		}
		TextView rank = (TextView) itemView.findViewById(R.id.rank);
		TextView name = (TextView) itemView.findViewById(R.id.name);
		TextView score = (TextView) itemView.findViewById(R.id.score);
		rank.setText((position+1)+".");
		if(isGlobal){
			name.setText(getItem(position).getUserName());
		}else{
			name.setText(getItem(position).getFacebookProfile().getName());
		}
		score.setText(getItem(position).getValue()+"");
		return itemView;
	}

}
