package com.taskmgr.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.ActivityManager;
import android.content.Context;

public class SystemResource {
	private ActivityManager activityManager;
	private ActivityManager.MemoryInfo memoryInfo;
	private Context context;
	private final double totalMemory;

	public SystemResource(Context context) {
		super();
		this.context = context;
		activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		memoryInfo = new ActivityManager.MemoryInfo();
		totalMemory = discoveryMemorySum();
	}

	public float getCpuUsage() {

		try {
			RandomAccessFile reade = new RandomAccessFile("/proc/stat", "r");
			String load = reade.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(360);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reade.seek(0);
			load = reade.readLine();
			reade.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public int getMemoryUsageRate() {
		double free = getFreeMemory();
		return (int) ((totalMemory - free) / totalMemory * 100);
	}

	public double getFreeMemory() {
		activityManager.getMemoryInfo(memoryInfo);
		double free = memoryInfo.availMem;
		return free;
	}

	public double getTotalMemory() {
		return totalMemory/1000000;
	}

	private double discoveryMemorySum() {
		String path = "/proc/meminfo";
		String sizeStr = "";
		double totalMem = 0;
		// str.split("[:+\\s]+",2);
		try {
			FileReader file = new FileReader(path);
			BufferedReader bufRead = new BufferedReader(file, 8192);
			if ((sizeStr = bufRead.readLine()) != null) {
				String sa[] = sizeStr.split("[:+\\s]+", 2);
				totalMem = Double.parseDouble(sa[1].substring(0,
						sa[1].length() - 2).trim());
			}
			bufRead.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		totalMem *= 1000;
		return totalMem;
	}

	public String reservedPrecision(double val, int reserved) {
		String str = "" + val;
		int index = -1;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') {
				index = i;
				break;
			}
			// System.out.println("-----123.456--- "+i);
		}
		if (index == -1 || index + reserved + 1 >= str.length())
			return str;
		else
			return str.substring(0, index + reserved + 1);
	}
}
