package com.betaminus.fitbitsample;

import org.scribe.builder.ServiceBuilder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class AuthenticationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		final WebView wvAuthorise = (WebView) findViewById(R.id.wvAuthorise);
		wvAuthorise.getSettings().setJavaScriptEnabled(true);
		wvAuthorise.addJavascriptInterface(new MyJavaScriptInterface(/*this*/), "HtmlViewer");
		wvAuthorise.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	wvAuthorise.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });

		// Replace these with your own api key and secret
		String apiKey = "5dbccac0710c4d2ca0814f232beac1b5";
		String apiSecret = "f7e6a726b0eb49829816f75fccd30a54";

		MainActivity.service = new ServiceBuilder().provider(FitbitApi.class).apiKey(apiKey)
				.apiSecret(apiSecret).build();

		// network operation shouldn't run on main thread
		new Thread(new Runnable() {
			public void run() {
				MainActivity.requestToken = MainActivity.service.getRequestToken();
				final String authURL = MainActivity.service
						.getAuthorizationUrl(MainActivity.requestToken);

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
	
	class MyJavaScriptInterface
	{
		boolean firstRun=true;
		//Context ctx;
		public MyJavaScriptInterface() {
		}
		/*public MyJavaScriptInterface(Context _ctx) {
	    	ctx = _ctx;
		}*/

		public void showHTML(final String html) {
			if(firstRun){
				firstRun=false;
				return;
			}
			
			try {
				
				String divStr = "gap20\">";
				int first = html.indexOf(divStr);
				int second = html.indexOf("</div>",first);
				
				if(first!=-1){
					final String pin = html.substring(first+divStr.length(),second);
					Intent intent = new Intent();
					intent.putExtra("PIN",pin);
					setResult(RESULT_OK,intent);
					finish();
				}
				else
				{
					/*new AlertDialog.Builder(ctx).setTitle("HTML").setMessage("first = "+first+" , second = "+second)
                	.setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();*/
				}

			} catch (Exception e) {
				/*new AlertDialog.Builder(ctx).setTitle("HTML").setMessage(e.getMessage())
				.setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();*/
			}
        }
	}
}
