package com.betaminus.fitbitsample;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// Additions to Scribe taken from https://github.com/apakulov/scribe-java
	// General flow of Android oauth app taken from
	// http://schwiz.net/blog/2011/using-scribe-with-android/

	OAuthService service;
	Token requestToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final WebView wvAuthorise = (WebView) findViewById(R.id.wvAuthorise);
		final EditText etPIN = (EditText) findViewById(R.id.etPIN);

		// Replace these with your own api key and secret
		String apiKey = "apikey";
		String apiSecret = "apisecret";

		service = new ServiceBuilder().provider(FitbitApi.class).apiKey(apiKey)
				.apiSecret(apiSecret).build();

		// network operation shouldn't run on main thread
		new Thread(new Runnable() {
			public void run() {
				requestToken = service.getRequestToken();
				final String authURL = service
						.getAuthorizationUrl(requestToken);

				// Webview nagivation should run on main thread again...
				wvAuthorise.post(new Runnable() {
					@Override
					public void run() {
						wvAuthorise.loadUrl(authURL);
					}
				});
			}
		}).start();
	}

	public void btnRetrieveData(View view) {
		EditText etPIN = (EditText) findViewById(R.id.etPIN);
		String gotPIN = etPIN.getText().toString();

		final Verifier v = new Verifier(gotPIN);

		// network operation shouldn't run on main thread
		new Thread(new Runnable() {
			public void run() {
				Token accessToken = service.getAccessToken(requestToken, v);

				OAuthRequest request = new OAuthRequest(Verb.GET,
						"http://api.fitbit.com/1/user/-/profile.json");
				service.signRequest(accessToken, request); // the access token from step
															// 4
				final Response response = request.send();
				final TextView tvOutput = (TextView) findViewById(R.id.tvOutput);

				// Visual output should run on main thread again...
				tvOutput.post(new Runnable() {
					@Override
					public void run() {
						tvOutput.setText(response.getBody());
					}
				});
			}
		}).start();
	}
}