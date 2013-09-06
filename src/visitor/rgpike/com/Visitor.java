package visitor.rgpike.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.AsyncTask;

import android.preference.PreferenceManager;

import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;

import android.content.SharedPreferences;

import android.view.View;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Visitor extends Activity
{
	EditText passCode;
	SharedPreferences settings;

	private Config config;
	private static final String TAG = "Visitor";

	/**
	 * Initializes the application
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		passCode = (EditText) findViewById(R.id.txt_passcode);

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		passCode.setText(settings.getString(Prefs.AUTH_PASSCODE, ""));

		config = new Config(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.settings:
				startActivity(new Intent(this, Prefs.class));
				break;
		}

		//startActivity(new Intent(this, Prefs.class));
		return true;
	}

	/**
	 * Handles click on Authorize button
	 */
	public void onAuthorizeClick(View view)
	{
		final String passcode = passCode.getText().toString();

		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Prefs.AUTH_PASSCODE, passcode);
		editor.commit();

		PostPasscodeTask ppct = new PostPasscodeTask();
		ppct.execute(passcode);
	}

	/**
	 * Handles click on Authorize button
	 */
	public void onClearClick(View view)
	{
		passCode = (EditText) findViewById(R.id.txt_passcode);
		passCode.setText("");
	}

	private class PostPasscodeTask extends AsyncTask<String, String, String>
	{
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			this.dialog = new ProgressDialog(Visitor.this);
			this.dialog.setMessage(getString(R.string.txt_authorizing));
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... passcode)
		{
			String webResponse = null;

			if (config.getSimulate() == false)
			{
				webResponse = postData(
					config.getAuthUrl(),
					passcode[0]);
			}
			else
			{
				webResponse = "<HTML><HEAD><TITLE>Authentication Success</TITLE><meta http-equiv=\"Refresh\" content=\"1;URL=http://10.11.12.13:8080/wlan/greeting.html?timeout=-666\"></HEAD><BODY></BODY></HTML>";
			}

			String response = extractTitle(webResponse);

			if (response == null)
			{
				response = "Error";
			}

			return response;
		}

		@Override
		protected void onPostExecute(String response)
		{
			Log.d(TAG, "Complete " + response);
			this.dialog.hide();
			Popup(response);
		}

		/**
		 * Extracts title element from authorization web response
		 */
		private String extractTitle(String webResponse)
		{
			String title = null;

			if (webResponse != null)
			{
				Log.d(TAG, "Matching against: " + webResponse);

				try
				{
					Pattern pattern = Pattern.compile("<TITLE>(.*)</TITLE>");
					Matcher matcher = pattern.matcher(webResponse);

					if (matcher.find() == true)
					{
						title = matcher.group(1);

						Log.d(TAG, "Matched: " + title);
					}
				}
				catch (java.util.regex.PatternSyntaxException pse)
				{
					Log.e(TAG, "Illegal pattern");
				}
			} // if()

			return title;
		}

		/**
		 * Populates data pairs for authorization web post
		 */
		private List<NameValuePair> populatePostData(String passcode)
		{
			List<NameValuePair> nameValuePairs =
				new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("f_user",
				config.getAuthUser()));

			nameValuePairs.add(new BasicNameValuePair("f_pass",
				passcode));

			nameValuePairs.add(new BasicNameValuePair("submit",
				settings.getString("authsubmittxt",
				getString(R.string.pref_auth_submit))));

			return nameValuePairs;
		}

		/**
		 * Converts http response to a String
		 */
		private String httpResponseToString(HttpEntity entity)
		{
			String line = null;

			try
			{
				InputStream is = entity.getContent();
				BufferedReader reader =
					new BufferedReader(new InputStreamReader(is));
				line = reader.readLine();
			}
			catch (ClientProtocolException e)
			{
				Log.d(TAG, e.toString());
			}
			catch (IOException e)
			{
				Log.d(TAG, e.toString());
			}

			return line;
		}

		/**
		 * Posts data and retrieves response from authorization server
		 */
		private String postData(String address, String passcode)
		{
			String response = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(address);

			List<NameValuePair> nameValuePairs = populatePostData(passcode);
			try
			{
				UrlEncodedFormEntity uefe =
					new UrlEncodedFormEntity(nameValuePairs);
				httppost.setEntity(uefe);
			}
			catch (UnsupportedEncodingException e)
			{
				Log.d(TAG, e.toString());
			}

			Log.d(TAG, "Posting...");

			try
			{
				HttpResponse httpResponse = httpclient.execute(httppost);
				response = httpResponseToString(httpResponse.getEntity());
			}
			catch (IOException e)
			{
				Log.d(TAG, e.toString());
			}

			return response;
		} 

		/**
		 * Displays response result
		 */
		private void Popup(String s)
		{
			AlertDialog.Builder alertDialogBuilder = new
				AlertDialog.Builder(Visitor.this);

			TextView message = new TextView(Visitor.this);

			message.setText(s);
			message.setGravity(Gravity.CENTER_HORIZONTAL);
			alertDialogBuilder.setView(message);

			AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.setButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
				}
			});

			alertDialog.show();
		}
	}
}
