//import visitor.rgpike.com.HttpPostThreadBase;
package visitor.rgpike.com;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

public class HttpPostData extends HttpPostThreadBase
{
	private String passcode;

	public HttpPostData(Activity activity, Context context, String message, String url, String passcode, boolean simulated)
	{
		super(activity, context, message, url, simulated);

		this.passcode = passcode;
	}

	@Override
	protected List<NameValuePair> populatePostData()
	{
		List<NameValuePair> nameValuePairs =
			new ArrayList<NameValuePair>(2);

		nameValuePairs.add(new BasicNameValuePair("f_user",
			"hotspotmachine\\hotspot"));
		nameValuePairs.add(new BasicNameValuePair("f_pass",
			passcode));
		nameValuePairs.add(new BasicNameValuePair("submit", "Log+In"));

		return nameValuePairs;
	}
}
