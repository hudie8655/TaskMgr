package com.taskmgr.app;

import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast; 
import com.taskmgr.R;
import com.taskmgr.util.AppUtils;

public class AppItemListener implements OnItemClickListener {
	List<Application> apps;
	private Context context;

	public AppItemListener( Context context) {
		super();
		this.context = context;
	}

	public void setApps(List<Application> apps) {
		this.apps = apps;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final Application app = apps.get(arg2);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.manager_app);
		builder.setItems(R.array.app_dialog_item,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							// cancel
							break;
						case 1:
							// start
							if (app.getActivityInfo() == null) {
								Toast.makeText(context,
										R.string.notfound_program, 600).show();
								return;
							}
							Intent intent = new Intent();
							intent.setComponent(new ComponentName(app
									.getPackageName(),
									app.getActivityInfo().name));
							context.startActivity(intent);
							break;
						case 2:
							// detail
							AppUtils.showInstalledAppDetails(
									app.getPackageName(), context);
							break;
						}
					}
				});
		builder.create().show();
	}
}