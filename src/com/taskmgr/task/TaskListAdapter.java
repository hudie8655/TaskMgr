package com.taskmgr.task;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmgr.MainActivity;
import com.taskmgr.R;

class TaskListHodler {
	ImageView icon;
	TextView name;
	ImageButton close;
}

public class TaskListAdapter extends BaseAdapter {
	private List<TaskProgram> listApp;
	private Context context;
	private ActivityManager amgr;
	private LayoutInflater mylayoutInflater;

	public TaskListAdapter( Context context) {
		this.context = context;
		amgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		mylayoutInflater = LayoutInflater.from(context);
	}

	public void setListApp(List<TaskProgram> listApp) {
		this.listApp = listApp;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listApp.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listApp.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TaskListHodler taskList;
		if (convertView == null) {
			convertView = mylayoutInflater
					.inflate(R.layout.tasklist_item, null);
			taskList = new TaskListHodler();
			taskList.icon = (ImageView) convertView
					.findViewById(R.id.task_list_app_icon);
			taskList.name = (TextView) convertView
					.findViewById(R.id.task_list_app_name);
			taskList.close = (ImageButton) convertView
					.findViewById(R.id.app_delete_button);

			convertView.setTag(taskList);
		} else {
			taskList = (TaskListHodler) convertView.getTag();
		}
		final TaskProgram p = listApp.get(position);
		taskList.close.setImageResource(R.drawable.delete_button_press);
		taskList.close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				amgr.killBackgroundProcesses(p.getPackageName());
				MainActivity main = (MainActivity) context;
				main.setUpdateTask(true);
				if (main.isListShow()) {
					main.loadTaskList(true);
				} else {
					main.loadTaskGrid(true);
				}
			}
		});
		taskList.icon.setImageDrawable(p.getIcon());
		taskList.name.setText(p.getName());
		return convertView;
	}

}