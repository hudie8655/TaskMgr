package com.taskmgr.app;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;

public class Application {
	private String name;
	private String packageName;
	private Drawable icon;
	private String version;
	private ActivityInfo activityInfo;
	
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String versionName) {
		this.version = versionName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ActivityInfo getActivityInfo() {
		return activityInfo;
	}

	public void setActivityInfo(ActivityInfo activityInfo) {
		this.activityInfo = activityInfo;
	}

}
