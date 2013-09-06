package visitor.rgpike.com;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.List;
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

public abstract class HttpPostThreadBase extends AsyncTask<String, String, String>
{
	private Context context;
	private String message;
	private String url;
	private boolean simulate;

	private String line;
	private ProgressDialog dialog;

	public HttpPostThreadBase(
		Activity activity,
		Context context,
		String message,
		String url,
		boolean simulate)
	{
		this.context = context;
		this.message = message;
		this.url = url;
		this.simulate = simulate;

		this.dialog = new ProgressDialog(this.context);
	}

	@Override
	protected void onPreExecute()
	{
		this.dialog.setMessage(message);
		this.dialog.show();
	}

	@Override
	protected String doInBackground(String... passcode)
	{
		if (simulate == false)
		{
			line = 
				postData();
		}
		else
		{
			line = "<HTML><HEAD><TITLE>Authentication Success</TITLE><meta http-equiv=\"Refresh\" content=\"1;URL=http://10.11.12.13:8080/wlan/greeting.html?timeout=-666\"></HEAD><BODY></BODY></HTML>";
		}

		line = extractTitle(line);

		return line;
	}

	@Override
	protected void onPostExecute(String s)
	{
		Log.d("Toast", "Complete");
		this.dialog.hide();
		Popup(line);
	}

	private String extractTitle(String s)
	{
		String title = "";

		Log.d("VISITOR", "Matching against: " + s);

		try
		{
			Pattern pattern = Pattern.compile("<TITLE>(.*)</TITLE>");
			Matcher matcher = pattern.matcher(s);

			if (matcher.find() == true)
			{
				title = matcher.group(1);

				Log.d("VISITOR", "Matched: " + title);
			}
		}
		catch (java.util.regex.PatternSyntaxException pse)
		{
			Log.e("VISITOR", "Illegal pattern");
		}

		return title;
	}

	protected abstract List<NameValuePair> populatePostData();

	private String postData()
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try
		{
			List<NameValuePair> nameValuePairs =
				populatePostData();

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.d("VISITOR", "Posting...");
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			InputStream is = entity.getContent();
			BufferedReader reader =
				new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();

			return line;
		}
		catch (ClientProtocolException e)
		{
		}
		catch (IOException e)
		{
		}

		return null;
	} 

	private void Popup(String s)
	{
		/*
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
			(ViewGroup) activity.findViewById(R.id.toast_layout_root));
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(s);

		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
		*/
	}
}
