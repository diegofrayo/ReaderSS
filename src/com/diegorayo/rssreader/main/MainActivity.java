package com.diegorayo.rssreader.main;

import com.diegorayo.rssreader.R;
import com.diegorayo.rssreader.api.RSSReaderAPI;
import com.diegorayo.rssreader.sqlite.DatabaseConnection;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private RSSReaderAPI API;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		API = new RSSReaderAPI(this);

		Button b = (Button) findViewById(R.id.prueba1);

		// dbConnection = new DatabaseConnection(this, "DatabaseApp.db", null,
		// 1);
		// b.setOnClickListener(this);
		// if (b != null) {
		// b.setText("d");
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// if (v.getId() == R.id.prueba1) {
		// SQLiteDatabase db = dbConnection.getReadableDatabase();
		// String[] a = new String[] { "0" };
		// Cursor c = db.rawQuery(
		// "select name from configuration where state = ?", a);
		// System.out.println("s");
		// if (c.moveToFirst()) {
		// System.out.println("mi");
		// TextView t = (TextView) findViewById(R.id.text);
		// t.setText(c.getString(0));
		// }
		//
		// }

	}

}
