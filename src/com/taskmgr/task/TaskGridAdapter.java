package com.taskmgr.task;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taskmgr.R;

class TaskGridHodler {
	ImageView icon;
	TextView name;
}

public class TaskGridAdapter extends BaseAdapter {
	private List<TaskProgram> listApp;
	private Context context;
	private LayoutInflater myInflater;

	public TaskGridAdapter(Context context) {
		super();
		this.context = context;
		myInflater = LayoutInflater.from(context);
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
		// TODO Auto-generated method stub
		TaskGridHodler grid;
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.taskgrid_item, null);
			grid = new TaskGridHodler();
			grid.icon = (ImageView) convertView
					.findViewById(R.id.task_grid_app_icon);
			grid.name = (TextView) convertView
					.findViewById(R.id.task_grid_app_name);
			convertView.setTag(grid);
		} else {
			grid = (TaskGridHodler) convertView.getTag();
		}
		TaskProgram p = listApp.get(position);
		grid.icon.setImageDrawable(p.getIcon());
		grid.name.setText(p.getName());
		return convertView;
	}

}
