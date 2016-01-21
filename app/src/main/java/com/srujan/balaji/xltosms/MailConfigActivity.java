package com.srujan.balaji.xltosms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailConfigActivity extends AppCompatActivity {
    private String SHARED_PREFS = "BALAJI_PREFS";
    private String EMAIL_ID = "EMAIL_ID";
    private String PASSWORD = "PASSWORD";
    private String CUSTOMER_MESSAGE = "CUSTOMER_MESSAGE";
    private String CUSTOMER_MESSAGE_TEMPLATE = "Hi <name>, Please clear your due amount Rs.<amount> pending with Balaji Electricals, Rajamundry";
    TextView tvGoogleAppsettings, tvCustomerMessage;
    EditText etGmail,etPass, etMessage;
    Button btSave, btSaveMesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_config);
        tvGoogleAppsettings = (TextView) findViewById(R.id.tv_mailconfig_settings);
        tvGoogleAppsettings.setText(Html.fromHtml("First please go to your Google App security and enable access for 'Allow less secure apps:' -> <a href='https://myaccount.google.com/security'>Click here</a>"));
        tvGoogleAppsettings.setMovementMethod(LinkMovementMethod.getInstance());
        tvCustomerMessage = (TextView) findViewById(R.id.tv_mailconfig_mesg);
        etGmail = (EditText) findViewById(R.id.et_mailconfig_gmail);
        etPass = (EditText) findViewById(R.id.et_mailconfig_pass);
        etMessage = (EditText) findViewById(R.id.et_mailconfig_mesg);
        btSaveMesg = (Button) findViewById(R.id.bt_mailconfig_save_mesg);
        btSave = (Button) findViewById(R.id.bt_mailconfig_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(etGmail.getText().toString()).matches()){
                    getSharedPreferences(SHARED_PREFS,MODE_PRIVATE).edit().putString(EMAIL_ID, etGmail.getText().toString()).commit();
                }
                else{
                    Toast.makeText(MailConfigActivity.this, "Please enter valid email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etPass.length()>2){
                    getSharedPreferences(SHARED_PREFS,MODE_PRIVATE).edit().putString(PASSWORD, etPass.getText().toString()).commit();
                    Toast.makeText(MailConfigActivity.this, "Saved credentials", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MailConfigActivity.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        btSaveMesg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempMesg = etMessage.getText().toString();
                if(tempMesg.length()<1 || !tempMesg.contains("name".toLowerCase()) || !tempMesg.contains("amount".toLowerCase())){
                    Toast.makeText(MailConfigActivity.this, "Please use 'name' , 'amount' in your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    getSharedPreferences(SHARED_PREFS ,MODE_PRIVATE).edit().putString(CUSTOMER_MESSAGE, tempMesg);
                    tempMesg = tempMesg.replaceAll("name" , "Subbarao");
                    tempMesg = tempMesg.replaceAll("amount" , "3200");
                    tvCustomerMessage.setText("Sample: "+tempMesg);
                    Log.d("-----hello------", tempMesg);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mail_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
