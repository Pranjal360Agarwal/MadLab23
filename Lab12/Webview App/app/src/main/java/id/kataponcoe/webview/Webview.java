package id.kataponcoe.webview;

/**
 * Created by Poncoe on 16/07/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Webview extends Activity {

    private WebView webview;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("Webview");
        setContentView(R.layout.webview);
        webview = (WebView) findViewById(R.id.webview);

        startWebView("https://www.linkedin.com/in/arief-wardhana-483271aa/");

    }

    private void startWebView(String url) {

        webview.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(Webview.this);
                    progressDialog.setMessage("Sedang Menghubungkan ke Jaringan...");
                    progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        // Javascript
        webview.getSettings().setJavaScriptEnabled(true);


        webview.loadUrl(url);


    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

}