package com.taskmgr.task;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;

public class TaskProgram {
	private Drawable icon;
	private String name;
	private String packageName;
	private ActivityInfo activityInfo;

 
	public ActivityInfo getActivityInfo() {
		return activityInfo;
	}

	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
 

}