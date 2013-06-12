package com.taskmgr;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmgr.app.AppItemListener;
import com.taskmgr.app.AppListAdapter;
import com.taskmgr.app.Application;

public class SearchApplicationResult extends Activity {
	private ListView result_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_search_result);

		result_list = (ListView) findViewById(R.id.search_result_list);
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchApplicationResult.this.finish();
			}
		});
		Intent intent = getIntent();
		String key = intent.getStringExtra("search_key");
		List<Application> listApp = MainActivity.getInstance().getSearchApps(
				key);
		if (listApp.size() > 1) {
			AppListAdapter adapter = new AppListAdapter(this);
			adapter.setListApp(listApp);
			// Toast.makeText(this, ""+(result_list==null), 1000).show();
			result_list.setAdapter(adapter);
			AppItemListener listener = new AppItemListener(this);
			listener.setApps(listApp);
			result_list.setOnItemClickListener(listener);
		} else {
			result_list.setVisibility(View.GONE);
			final TextView tv = (TextView) findViewById(R.id.not_search_result);
			tv.setVisibility(View.VISIBLE);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(SearchApplicationResult.this,
							"touch textview", 600).show();
					finish();
				}
			});
		}
		super.onCreate(savedInstanceState);
	}
}
