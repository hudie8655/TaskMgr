package com.taskmgr.task;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class TaskPackageInfo {
	private List<PackageInfo> list;

	public TaskPackageInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		list = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES|PackageManager.GET_ACTIVITIES);
	}

	public PackageInfo getPackageInfo(String name) {
		if (name == null || "".equals(name.trim())) {
			return null;
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).packageName.equals(name))
				return list.get(i);
		}
		return null;
	}
}