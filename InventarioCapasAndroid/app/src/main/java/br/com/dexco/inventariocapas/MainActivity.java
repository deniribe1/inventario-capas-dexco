package br.com.dexco.inventariocapas;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.Toast;
import androidx.webkit.WebViewAssetLoader;
import java.io.OutputStream;
import android.util.Base64;
public class MainActivity extends Activity {
 private WebView web;
 @Override public void onCreate(Bundle state){super.onCreate(state);web=new WebView(this);setContentView(web);
 WebSettings cfg=web.getSettings();cfg.setJavaScriptEnabled(true);cfg.setDomStorageEnabled(true);cfg.setAllowFileAccess(false);cfg.setAllowContentAccess(false);
 WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
 web.setWebViewClient(new WebViewClient(){@Override public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view,android.webkit.WebResourceRequest request){return loader.shouldInterceptRequest(request.getUrl());}});
 web.addJavascriptInterface(new Object(){@JavascriptInterface public void saveCsv(String b64,String filename){try{
 byte[] data=Base64.decode(b64,Base64.DEFAULT);ContentValues v=new ContentValues();v.put(MediaStore.Downloads.DISPLAY_NAME,filename.replaceAll("[^a-zA-Z0-9_.-]","_"));v.put(MediaStore.Downloads.MIME_TYPE,"text/csv");v.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS);
 android.net.Uri uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,v);if(uri==null)throw new Exception("Falha ao criar arquivo");try(OutputStream out=getContentResolver().openOutputStream(uri)){out.write(data);}runOnUiThread(()->Toast.makeText(MainActivity.this,"CSV salvo em Downloads",Toast.LENGTH_LONG).show());
 }catch(Exception e){runOnUiThread(()->Toast.makeText(MainActivity.this,"Erro ao exportar: "+e.getMessage(),Toast.LENGTH_LONG).show());}}},"AndroidExport");
 web.loadUrl("https://appassets.androidplatform.net/assets/index.html");}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}
