package com.taskmgr;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmgr.app.AppGridAdapter;
import com.taskmgr.app.AppItemListener;
import com.taskmgr.app.AppListAdapter;
import com.taskmgr.app.Application;
import com.taskmgr.task.TaskGridAdapter;
import com.taskmgr.task.TaskListAdapter;
import com.taskmgr.task.TaskPackageInfo;
import com.taskmgr.task.TaskProgram;
import com.taskmgr.util.AppUtils;
import com.taskmgr.util.ChangeViewListener;
import com.taskmgr.util.HorizontalSlide;
import com.taskmgr.util.SystemResource;

public class MainActivity extends Activity implements ChangeViewListener,
		SensorEventListener {
	private TextView tv_taskmgr;
	private TextView tv_appmgr;
	private TextView tv_sysres;
	private ImageView iv_taskmgr_line;
	private ImageView iv_appmgr_line;
	private ImageView iv_sysres_line;
	private ImageButton btn_change_view_mode;
	private List<TextView> text_titles;
	private List<ImageView> image_titles;
	private TouchTitleListener touchTitleListener = new TouchTitleListener();
	private ListView task_list;
	private GridView task_grid;
	private ListView app_list;
	private GridView app_grid;
	private ProgressBar progress_cpu;
	private ProgressBar progress_memory;
	private TextView tv_progress_cpu_rate;
	private TextView tv_progress_memory_rate;
	private TextView tv_memory_using;
	private TextView tv_memory_free;
	private TextView tv_memory_total;
	private TaskItemListener taskitemListener;
	private HorizontalSlide slideLayout;
	private boolean isListShow;
	private static final String CONFIG_INFO = "configure_info";
	private static final String SHOW_MODE_LABEL = "show_mode";
	private SharedPreferences shareData;
	private SharedPreferences.Editor shareEdit;
	private ActivityManager activityManager;
	private List<RunningAppProcessInfo> runs;
	private List<TaskProgram> taskApps;
	private int curScreen;
	private TaskListAdapter taskListAdapter;
	private TaskGridAdapter taskGridAdapter;
	private List<Application> allApp;
	private AppGridAdapter appGridAdapter;
	private AppListAdapter appListAdapter;
	private AppItemListener appItemListener;
	private SystemResource systemResource;
	private double totalMem;
	private static final int MENU_REFRESH_ID = 1;
	private static final int MENU_SEARCH_ID = 2;
	private static final int MENU_CLOSEALL_ID = 3;
	private static final int MENU_EXIT_ID = 4;
	private static MainActivity instance;
	private Sensor accelerometerSensor;
	private SensorManager sensorMgr;
	private Vibrator vibrator;
	private boolean isUpdateTask = false;

	public void setUpdateTask(boolean isUpdateTask) {
		this.isUpdateTask = isUpdateTask;
	}

	public static MainActivity getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		this.init();
	}

	private void init() {
		// initiation image and button
		instance = this;
		tv_taskmgr = (TextView) findViewById(R.id.task_mgr);
		tv_appmgr = (TextView) findViewById(R.id.app_mgr);
		tv_sysres = (TextView) findViewById(R.id.sys_res);
		if (text_titles == null)
			text_titles = new ArrayList<TextView>();
		tv_taskmgr.setOnClickListener(touchTitleListener);
		tv_appmgr.setOnClickListener(touchTitleListener);
		tv_sysres.setOnClickListener(touchTitleListener);
		text_titles.add(tv_taskmgr);
		text_titles.add(tv_appmgr);
		text_titles.add(tv_sysres);

		iv_taskmgr_line = (ImageView) findViewById(R.id.task_mgr_line);
		iv_appmgr_line = (ImageView) findViewById(R.id.app_mgr_line);
		iv_sysres_line = (ImageView) findViewById(R.id.sys_res_line);
		if (image_titles == null)
			image_titles = new ArrayList<ImageView>();
		image_titles.add(iv_taskmgr_line);
		image_titles.add(iv_appmgr_line);
		image_titles.add(iv_sysres_line);

		task_list = (ListView) findViewById(R.id.task_list);
		task_grid = (GridView) findViewById(R.id.task_grid);
		app_list = (ListView) findViewById(R.id.app_list);
		app_grid = (GridView) findViewById(R.id.app_grid);

		progress_cpu = (ProgressBar) findViewById(R.id.progress_cpu);
		progress_memory = (ProgressBar) findViewById(R.id.progress_memory);
		tv_progress_cpu_rate = (TextView) findViewById(R.id.tv_progress_cpu_rate);
		tv_progress_memory_rate = (TextView) findViewById(R.id.tv_progress_memory_rate);
		tv_memory_free = (TextView) findViewById(R.id.tv_memory_free_rate);
		tv_memory_using = (TextView) findViewById(R.id.tv_memory_using_rate);
		systemResource = new SystemResource(this);
		tv_memory_total = (TextView) findViewById(R.id.tv_memory_total_rate);
		totalMem = Double.parseDouble(systemResource.reservedPrecision(
				systemResource.getTotalMemory(), 2));
		tv_memory_total.setText(totalMem + "M");

		btn_change_view_mode = (ImageButton) findViewById(R.id.btn_change_view_mode);

		sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometerSensor = sensorMgr
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		shareData = getSharedPreferences(CONFIG_INFO,
				Context.MODE_WORLD_WRITEABLE);
		shareEdit = shareData.edit();
		isListShow = shareData.getBoolean(SHOW_MODE_LABEL, true);

		// seting change title listener
		HorizontalSlide.setChangeTitleListener(this);
		slideLayout = HorizontalSlide.getInstance();

		btn_change_view_mode.setOnClickListener(new ChangeShowModeListener());
		sysResHandler.post(sysResRun);
		if (isListShow) {
			this.loadTaskList(true);
			this.loadAppList(true);
		} else {
			this.loadTaskGrid(true);
			this.loadAppGrid(true);
		}

	}

	private Handler sysResHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			postDelayed(sysResRun, 2500);
			super.handleMessage(msg);
		}

	};
	private Runnable sysResRun = new Runnable() {
		public void run() {
			if (systemResource != null) {
				int cpuRate = (int) systemResource.getCpuUsage();
				progress_cpu.setProgress(cpuRate);
				tv_progress_cpu_rate.setText(cpuRate + "%");
				int memRate = systemResource.getMemoryUsageRate();
				progress_memory.setProgress(memRate);
				tv_progress_memory_rate.setText(memRate + "%");

				double freeMem = Double.parseDouble(systemResource
						.reservedPrecision(
								systemResource.getFreeMemory() / 1000000, 2));
				String usingMem = systemResource.reservedPrecision(totalMem
						- freeMem, 2);
				tv_memory_free.setText(freeMem + "M");
				tv_memory_using.setText(usingMem + "M");

				Message msg = sysResHandler.obtainMessage();
				sysResHandler.sendMessage(msg);
			}
		}
	};

	private void loadDialog() {
		final ProgressDialog initDialog = ProgressDialog.show(this, "",
				"loading");
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (initDialog != null && initDialog.isShowing())
					initDialog.dismiss();
			}
		}, 2000);
	}

	private class ChangeShowModeListener implements OnClickListener {

		public void onClick(final View v) {
			new Handler().post(new Runnable() {

				@Override
				public synchronized void run() {
					// TODO Auto-generated method stub
					if (isUpdateTask)
					{
						refreshTask();
						isUpdateTask=false;
					}
					if (isListShow) {
						isListShow = false;
						shareEdit.putBoolean(SHOW_MODE_LABEL, isListShow);
						btn_change_view_mode
								.setImageResource(R.drawable.gridview_press);
						Toast.makeText(MainActivity.this, R.string.grid_show,
								600).show();
						loadTaskData(false);
						loadAppData(false);
					} else {
						isListShow = true;
						shareEdit.putBoolean(SHOW_MODE_LABEL, isListShow);
						btn_change_view_mode
								.setImageResource(R.drawable.listview_press);
						Toast.makeText(MainActivity.this, R.string.list_show,
								600).show();
						loadTaskData(false);
						loadAppData(false);
					}
				}
			});
		}
	}

	public synchronized void loadAppData(boolean refresh) {

		if (isListShow) {
			loadAppList(refresh);
		} else {
			loadAppGrid(refresh);
		}
	}

	public synchronized void loadTaskData(boolean refresh) {

		if (isListShow) {
			loadTaskList(refresh);
		} else {
			loadTaskGrid(refresh);
		}
	}

	public void loadAppGrid(boolean refresh) {
		app_list.setVisibility(View.GONE);
		app_grid.setVisibility(View.VISIBLE);

		if (appGridAdapter == null || refresh) {
			this.loadDialog();
			allApp = getApps();
			if (appGridAdapter == null) {
				appGridAdapter = new AppGridAdapter(this);
			}
			if (appItemListener == null) {
				appItemListener = new AppItemListener(this);
			}
			appGridAdapter.setListApp(allApp);
			appItemListener.setApps(allApp);
		}
		app_grid.setLayoutAnimation(gridAnimlayout());
		app_grid.setAdapter(appGridAdapter);
		app_grid.setOnItemClickListener(appItemListener);
	}

	public void loadAppList(boolean refresh) {
		app_list.setVisibility(View.VISIBLE);
		app_grid.setVisibility(View.GONE);

		if (appListAdapter == null || refresh) {
			this.loadDialog();
			allApp = getApps();
			if (appListAdapter == null) {
				appListAdapter = new AppListAdapter(this);
			}
			if (appItemListener == null) {
				appItemListener = new AppItemListener(this);
			}
			appListAdapter.setListApp(allApp);
			appItemListener.setApps(allApp);
		}
		app_list.setLayoutAnimation(listAnimLayout());
		app_list.setAdapter(appListAdapter);
		app_list.setOnItemClickListener(appItemListener);
	}

	public void refreshTask() {
		// this.loadDialog();
		taskApps = getTask();
		if (taskListAdapter == null) {
			taskListAdapter = new TaskListAdapter(this);
		}
		if (taskGridAdapter == null) {
			taskGridAdapter = new TaskGridAdapter(this);
		}
		if (taskitemListener == null) {
			taskitemListener = new TaskItemListener(this);
		}
		taskListAdapter.setListApp(taskApps);
		taskGridAdapter.setListApp(taskApps);
		taskitemListener.setList(taskApps);
	}

	public void loadTaskList(boolean refresh) {
		task_grid.setVisibility(View.GONE);
		task_list.setVisibility(View.VISIBLE);
		if (taskListAdapter == null || refresh) {
			this.loadDialog();
			taskApps = getTask();
			if (taskListAdapter == null) {
				taskListAdapter = new TaskListAdapter(this);
			}
			if (taskitemListener == null) {
				taskitemListener = new TaskItemListener(this);
			}
			taskListAdapter.setListApp(taskApps);
			taskitemListener.setList(taskApps);
		}
		task_list.setLayoutAnimation(listAnimLayout());
		task_list.setAdapter(taskListAdapter);
		task_list.setOnItemClickListener(taskitemListener);
	}

	public void loadTaskGrid(boolean refresh) {
		task_list.setVisibility(View.GONE);
		task_grid.setVisibility(View.VISIBLE);

		if (taskGridAdapter == null || refresh) {
			this.loadDialog();
			taskApps = getTask();
			if (taskGridAdapter == null) {
				taskGridAdapter = new TaskGridAdapter(this);
			}
			if (taskitemListener == null) {
				taskitemListener = new TaskItemListener(this);
			}
			taskGridAdapter.setListApp(taskApps);
			taskitemListener.setList(taskApps);
		}
		task_grid.setLayoutAnimation(this.gridAnimlayout());
		task_grid.setAdapter(taskGridAdapter);
		task_grid.setOnItemClickListener(taskitemListener);
	}

	private LayoutAnimationController listAnimLayout() {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(400);
		alpha.setStartOffset(250);
		LayoutAnimationController layoutAnim = new LayoutAnimationController(
				alpha);
		return layoutAnim;
	}

	private LayoutAnimationController gridAnimlayout() {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(300);
		alpha.setStartOffset(400);
		alpha.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
		TranslateAnimation trans = new TranslateAnimation(100, 1, 120, 1);
		trans.setDuration(400);
		RotateAnimation rotate = new RotateAnimation(180, 0);
		rotate.setDuration(300);
		rotate.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(rotate);
		set.addAnimation(rotate);
		set.addAnimation(alpha);

		return new LayoutAnimationController(set);
	}

	private List<Application> getApps() {
		List<Application> listApps = new ArrayList<com.taskmgr.app.Application>();
		PackageManager pm = getPackageManager();
		List<PackageInfo> listPackage = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
						| PackageManager.GET_ACTIVITIES);

		for (int i = 0; i < listPackage.size(); i++) {
			PackageInfo pinfo = listPackage.get(i);
			Application app = new Application();

			app.setIcon(pinfo.applicationInfo.loadIcon(pm));
			app.setName(pinfo.applicationInfo.loadLabel(pm).toString());
			app.setPackageName(pinfo.packageName);
			app.setVersion(pinfo.versionName);
			if (pinfo.activities != null && pinfo.activities.length > 0)
				app.setActivityInfo(pinfo.activities[0]);
			listApps.add(app);
		}
		return listApps;
	}

	public List<TaskProgram> getTask() {
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		runs = activityManager.getRunningAppProcesses();
		TaskPackageInfo pcinfo = new TaskPackageInfo(this);
		PackageManager pm = getPackageManager();
		List<TaskProgram> tempApps = new ArrayList<TaskProgram>();

		for (int i = 0; i < runs.size(); i++) {
			if (pcinfo.getPackageInfo(runs.get(i).processName) != null) {
				TaskProgram p = new TaskProgram();
				PackageInfo packageInfo = pcinfo
						.getPackageInfo(runs.get(i).processName);

				p.setIcon(packageInfo.applicationInfo.loadIcon(pm));
				p.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
				p.setPackageName(packageInfo.packageName);
				// Log.e("", "packageinfo.activities sum  "
				// + (packageInfo.activities.length));
				if (packageInfo.activities != null)
					p.setActivityInfo(packageInfo.activities[0]);

				tempApps.add(p);
			}
		}
		return tempApps;

	}

	private class TaskItemListener implements OnItemClickListener {
		private Context context;
		private List<TaskProgram> list;
		private ActivityManager amgr;

		public TaskItemListener(Context context) {
			super();
			this.context = context;
			amgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}

		public void setList(List<TaskProgram> list) {
			this.list = list;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
				long arg3) {
			final TaskProgram program = list.get(arg2);

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(R.string.manager_task);
			builder.setItems(R.array.task_dialog_item,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								// cancel
								break;
							case 1:
								// start
								if (program.getActivityInfo() == null) {
									Toast.makeText(context,
											R.string.notfound_program, 600)
											.show();
									return;
								}
								Intent intent = new Intent();
								intent.setComponent(new ComponentName(program
										.getPackageName(), program
										.getActivityInfo().name));
								startActivity(intent);
								break;
							case 2:
								// kill
								amgr.killBackgroundProcesses(program
										.getPackageName());
								loadDialog();
								if (isListShow) {
									loadTaskList(true);
								} else {
									loadTaskGrid(true);
								}
								break;
							case 3:
								// detail
								AppUtils.showInstalledAppDetails(
										program.getPackageName(),
										MainActivity.this);
								break;
							}
						}
					});

			builder.create().show();
		}

	}

	private class TouchTitleListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			int id = v.getId();

			switch (id) {
			case R.id.task_mgr:
				slideLayout.gotoScreen(0);
				break;
			case R.id.app_mgr:
				slideLayout.gotoScreen(1);
				break;
			case R.id.sys_res:
				slideLayout.gotoScreen(2);
				break;
			}
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		slideLayout.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public boolean isListShow() {
		return isListShow;
	}

	@Override
	public synchronized void changeTitleState(int screen) {

		for (int i = 0; i < 3; i++) {
			if (i != screen) {
				text_titles.get(i).setTextColor(Color.argb(255, 0, 147, 221));
				image_titles.get(i).setVisibility(View.INVISIBLE);
			}
		}
		text_titles.get(screen).setTextColor(Color.argb(255, 255, 255, 255));
		image_titles.get(screen).setVisibility(View.VISIBLE);
		if (screen != curScreen) {
			this.curScreen = screen;
			if (curScreen == 0)
				loadTaskData(false);
			else if (curScreen == 1)
				loadAppData(false);
		}
	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		startSearch(null, false, null, false);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String word = intent.getStringExtra(SearchManager.QUERY);
		intent.putExtra("search_key", word);
		intent.setClass(this, SearchApplicationResult.class);
		SearchRecentSuggestions suggest = new SearchRecentSuggestions(this,
				MySearchSuggestion.AUTORITY, MySearchSuggestion.MODE);
		suggest.saveRecentQuery(word, null);
		startActivity(intent);
	}

	public List<Application> getSearchApps(String word) {
		List<Application> result = new ArrayList<Application>();
		Pattern p = Pattern.compile(".*" + word + ".*");
		for (int i = 0; i < allApp.size(); i++) {
			if (p.matcher(allApp.get(i).getName()).matches()) {
				result.add(allApp.get(i));
			}
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_REFRESH_ID, 1, R.string.refresh_app).setIcon(
				R.drawable.icon_menu_refresh);
		menu.add(0, MENU_SEARCH_ID, 2, R.string.search_app).setIcon(
				android.R.drawable.ic_menu_search);
		menu.add(0, MENU_CLOSEALL_ID, 3, R.string.close_all_app).setIcon(
				R.drawable.icon_menu_close_all);
		menu.add(0, MENU_EXIT_ID, 4, R.string.exit_program).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_REFRESH_ID:
			loadTaskData(true);
			loadAppData(true);
			break;
		case MENU_CLOSEALL_ID:
			killAllTask();
			break;
		case MENU_SEARCH_ID:
			onSearchRequested();
			break;
		case MENU_EXIT_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确定退出程序")
					.setCancelable(true)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									int ver = Build.VERSION.SDK_INT;
									if (ver > 8) {
										Intent intent = new Intent(
												Intent.ACTION_MAIN);
										intent.addCategory(Intent.CATEGORY_HOME);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										MainActivity.this.onPause();
										System.exit(0);
									} else {
										if (activityManager == null)
											activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
										activityManager
												.restartPackage(getPackageName());
									}
								}
							}).create().show();

			break;
		}
		return true;
	}

	private void killAllTask() {
		if (activityManager == null)
			activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (int i = 0; i < taskApps.size(); i++) {
			activityManager.killBackgroundProcesses(taskApps.get(i)
					.getPackageName());
		}
		isUpdateTask=true;
		refreshTask();
		if (isListShow) {
			loadTaskList(false);
		} else {
			loadTaskGrid(false);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		// Log.e("", "start");
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Log.e("", "resume");
		sensorMgr.registerListener(this, accelerometerSensor,
				SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("", "destroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// Log.e("", "pause");
		sensorMgr.unregisterListener(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.e("", "stop");
		super.onStop();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		doOnSensorChanged(event.sensor.getType(), event.values);
	}

	private float x, y, z, last_x, last_y, last_z;
	private static final int SHAKE_OFFSET = 2000;
	private long lastShakeTime;

	private void doOnSensorChanged(int type, float[] values) {
		// TODO Auto-generated method stub
		if (type == Sensor.TYPE_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			if (curTime - lastShakeTime > 100) {
				long diffTime = curTime - lastShakeTime;
				lastShakeTime = curTime;
				x = values[SensorManager.DATA_X];
				y = values[SensorManager.DATA_Y];
				z = values[SensorManager.DATA_Z];
				float speed = Math.abs(x + y + z - last_x - last_y - last_z)
						/ diffTime * 10000;
//				Log.e("","speed "+speed);
				if (speed > SHAKE_OFFSET) {
					killAllTask();
					vibrator.vibrate(1000);
					Toast.makeText(this, R.string.killall_app_prompt, 800)
							.show();
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
