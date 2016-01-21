package com.srujan.balaji.xltosms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.srujan.balaji.xltosms.gmail2.Mail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LauncherActivity extends AppCompatActivity {
    private String SHARED_PREFS = "BALAJI_PREFS";
    private String EMAIL_ID = "EMAIL_ID";
    private String PASSWORD = "PASSWORD";
    private String DEFAULT = "DEFAULT";
    private String JSON_DATA = "JSON_DATA";

    private int PICK_FILE = 101;
    private String gmailId,gmailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //srujan
        setContentView(R.layout.activity_launcher);
        gmailId = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE).getString(EMAIL_ID, DEFAULT);
        gmailPassword = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(PASSWORD, DEFAULT);


        ((Button)findViewById(R.id.bt_load_json)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LauncherActivity.this, ListviewActivity.class));
                Intent intent = new Intent();
                intent.setType("*/*");
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent = Intent.createChooser(intent, "Select file");
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    String[] mimetypes = { "text/*" };
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                }
                startActivityForResult(intent, PICK_FILE);
            }
        });

        ((Button)findViewById(R.id.bt_mail_auth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, MailConfigActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    private void sendMessage(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                if(gmailId.equals(DEFAULT) || gmailPassword.equals(DEFAULT)){
                    gmailId = "srujanmaddula123@gmail.com";
                    gmailPassword = "9573181281";
                }
                Mail m = new Mail(gmailId, gmailPassword);

                String[] toArr = {"srujanmaddula@gmail.com", "srujanmaddula1234@gmail.com"};
                m.setTo(toArr);
                m.setFrom(gmailId);
                m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
                m.setBody("Email body.");

                try {
                    //m.addAttachment("/sdcard/filelocation");

                    if(m.send()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LauncherActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LauncherActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                } catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
                return null;
            }


            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);

    }

    public static boolean hasInternetConnection(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!hasInternetConnection(this))
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE && resultCode == Activity.RESULT_OK && data != null ){
            StringBuffer datax = new StringBuffer("");
            try {
                Uri uri = data.getData();
                //File file = new File(uri.getPath());
                InputStream fileInputStream = getContentResolver().openInputStream(uri);
                InputStreamReader isr = new InputStreamReader ( fileInputStream ) ;
                BufferedReader buffreader = new BufferedReader ( isr ) ;

                String readString = buffreader.readLine ( ) ;
                while ( readString != null ) {
                    datax.append(readString);
                    readString = buffreader.readLine ( ) ;
                }

                isr.close ( ) ;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String finalData = datax.toString();
            Log.d("------data----" , finalData);
            if(finalData!=null)
            {
                Intent intent = new Intent(this, ListviewActivity.class);
                intent.putExtra(JSON_DATA, finalData);
                startActivity(intent);
            }

        }
    }
}
