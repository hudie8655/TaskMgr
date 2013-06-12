package com.taskmgr.app;

import java.util.List;

import com.taskmgr.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class ListHolder {
	ImageView icon;
	TextView name;
	TextView packageName;
	TextView version;
}

public class AppListAdapter extends BaseAdapter {
	private List<Application> listApp;
	private Context context;
	private ListHolder holder;
	private LayoutInflater layoutInflater;

	public AppListAdapter( Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}

	public void setListApp(List<Application> listApp) {
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
		if (convertView == null) {
			holder = new ListHolder();
			convertView = layoutInflater.inflate(R.layout.applist_item, null);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.app_list_app_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.app_list_app_name);
			holder.packageName = (TextView) convertView
					.findViewById(R.id.app_list_app_package_name);
			holder.version = (TextView) convertView
					.findViewById(R.id.app_list_app_version);

			convertView.setTag(holder);
		} else {
			holder = (ListHolder) convertView.getTag();
		}
		Application app = listApp.get(position);
		holder.icon.setImageDrawable(app.getIcon());
		holder.name.setText(app.getName());
		holder.packageName.setText(app.getPackageName());
		holder.version.getText();
		holder.version.setText(R.string.version);
		String ver = holder.version.getText().toString();
		holder.version.setText(ver + app.getVersion());
		return convertView;
	}

}
