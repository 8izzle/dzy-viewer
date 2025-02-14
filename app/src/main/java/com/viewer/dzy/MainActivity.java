package com.viewer.dzy;
 
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());

		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setAllowContentAccess(true);
		webView.getSettings().setJavaScriptEnabled(true); // If needed by your HTML
      
		webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					String errorHtml = "<html><body><h2>File Not Found</h2><p>The requested file could not be loaded.</p></body></html>";
					view.loadData(errorHtml, "text/html", "UTF-8");
				}
			});
		
			
		Intent intent = getIntent();
		Uri fileUri = intent.getData();

		if (fileUri != null) {
			String path = fileUri.getPath();
			if (path != null && path.toLowerCase().endsWith(".dzy")) {
				String extractedPath = extractDzy(fileUri);
				if (extractedPath != null) {
					webView.loadUrl("file://" + extractedPath + "/index.html");
				} else {
					showErrorPage();
				}
			} else {
				showErrorPage();
			}
		} else {
			showErrorPage();
		}
    }
	private void showErrorPage() {
		String errorHtml = "<html><body><h2>Error</h2><p>Invalid file selected.</p></body></html>";
		webView.loadData(errorHtml, "text/html", "UTF-8");
	}
    private String extractDzy(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) return null;

            File outputDir = new File(getCacheDir(), "dzy_content");
            outputDir.mkdirs();

            ZipInputStream zis = new ZipInputStream(inputStream);
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(outFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zis.closeEntry();
            }
            zis.close();
            return outputDir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearAppData();
	}

	private void clearAppData() {
		File cacheDir = getCacheDir();
		File filesDir = getFilesDir();

		deleteRecursive(cacheDir);
		deleteRecursive(filesDir);
	}

	private void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}
		fileOrDirectory.delete();
	}
}
