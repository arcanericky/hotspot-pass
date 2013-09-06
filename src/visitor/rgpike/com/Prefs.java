package visitor.rgpike.com;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Prefs extends PreferenceActivity
{
	public static final String AUTH_URL = "auth_url";
	public static final String AUTH_USER = "auth_user";
	public static final String AUTH_PASSCODE = "passcode";
	public static final String AUTH_SUBMIT = "submit";
	public static final String SIMULATE = "simulate";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);
	}
}
