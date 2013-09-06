package visitor.rgpike.com;

import android.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.content.Context;

public class Config
{
	private Context context;

	public Config(Context context)
	{
		this.context = context;
	}

	/**
	 * Returns the URL for authorization connection
	 */
	public String getAuthUrl()
	{
		SharedPreferences settings =
			PreferenceManager.getDefaultSharedPreferences(context);
		String s = settings.getString(Prefs.AUTH_URL, "");
		s = s.trim();
		if (s.length() == 0)
		{
			s =	context.getString(R.string.pref_auth_url);
		}

		return s;
	}

	/**
	 * Returns the user for authorization
	 */
	public String getAuthUser()
	{
		SharedPreferences settings =
			PreferenceManager.getDefaultSharedPreferences(context);
		String s = settings.getString(Prefs.AUTH_USER, "");
		s = s.trim();
		if (s.length() == 0)
		{
			s =	context.getString(R.string.pref_auth_user);
		}

		return s;
	}

	/**
	 * Returns response simulation mode
	 */
	public Boolean getSimulate()
	{
		SharedPreferences settings =
			PreferenceManager.getDefaultSharedPreferences(context);
		boolean b = settings.getBoolean(Prefs.SIMULATE, false);

		return b;
	}
}
