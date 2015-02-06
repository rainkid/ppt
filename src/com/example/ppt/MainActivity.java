package com.example.ppt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	public static final String SERVER_HOST = "42.121.237.23";
	public static final int SERVER_PORT = 8008;

	private Handler handler;
	public MySyncTask dTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initHander();
		initView();
	}
	
	public void initView() {
		Button connBtn = (Button) findViewById(R.id.conn);
		connBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dTask = new MySyncTask(handler, SERVER_HOST, SERVER_PORT);
				dTask.execute();
			}
		});
		
		Button cancelBtn = (Button) findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("exit");
					dTask.cancel(true);
				}
			}
		});
		
		Button nextBtn = (Button) findViewById(R.id.next);
		nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("next");
					dTask.cancel(true);
				}
			}
		});
		
		Button preBtn = (Button) findViewById(R.id.pre);
		preBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("pre");
					dTask.cancel(true);
				}
			}
		});
		
		Button firstBtn = (Button) findViewById(R.id.first);
		firstBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("first");
					dTask.cancel(true);
				}
			}
		});
		
		Button d3Btn = (Button) findViewById(R.id.d3);
		d3Btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("3d");
					dTask.cancel(true);
				}
			}
		});
		
		Button endBtn = (Button) findViewById(R.id.end);
		endBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dTask != null) {
					dTask.SendDataToNetwork("end");
					dTask.cancel(true);
				}
			}
		});
		
		TextView tips = (TextView) findViewById(R.id.tips);
		tips.setMovementMethod(new ScrollingMovementMethod());
	}
	
	public void initHander() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					Bundle text = msg.getData();
					showMsg(text.getString("msg"));
					break;
				}
			}
		};
	}
	
	public void showMsg(String context) {
		TextView tips = (TextView) findViewById(R.id.tips);
		Bundle bundle = new Bundle();
		bundle.putString("tips_text", tips.getText().toString() + context + "\n");
		String text = bundle.getString("tips_text");
		tips.setText(text);
	}
	
	public void appendMsg(String content) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = 1;
		bundle.putString("msg", content);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
}
