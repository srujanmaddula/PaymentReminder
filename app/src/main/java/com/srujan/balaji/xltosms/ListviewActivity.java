package com.srujan.balaji.xltosms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.srujan.balaji.xltosms.adapter.MyRecyclerViewAdapter;
import com.srujan.balaji.xltosms.gmail2.Mail;
import com.srujan.balaji.xltosms.models.BalajiSheet;
import com.srujan.balaji.xltosms.receivers.DeliverReceiver;
import com.srujan.balaji.xltosms.receivers.SentReceiver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListviewActivity extends AppCompatActivity {

    String jsonData = "[{\"account\":\"A d raghu kiran\",\"phone\":9963982299,\"debits\":\"14,836.00\",\"credits\":\"1234\",\"email\":\"srujanmaddula@gmail.com\"},{\"account\":\"Ramana\",\"phone\":null,\"debits\":\"45,000.00\",\"credits\":\"\",\"email\":\"\"},{\"account\":\"Solid construction\",\"phone\":9000177277,\"debits\":\"\",\"credits\":\"45,000.00\",\"email\":\"abc@abc.com\"},{\"account\":\"A d raghu kiran\",\"phone\":9963982299,\"debits\":\"14,836.00\",\"credits\":\"\",\"email\":\"abc@abc.com\"},{\"account\":\"Ramana\",\"phone\":null,\"debits\":\"45,000.00\",\"credits\":\"\",\"email\":\"\"},{\"account\":\"Solid construction\",\"phone\":9000177277,\"debits\":\"\",\"credits\":\"45,000.00\",\"email\":\"abc@abc.com\"},{\"account\":\"A d raghu kiran\",\"phone\":9963982299,\"debits\":\"14,836.00\",\"credits\":\"\",\"email\":\"abc@abc.com\"},{\"account\":\"Ramana\",\"phone\":null,\"debits\":\"45,000.00\",\"credits\":\"\",\"email\":\"\"},{\"account\":\"Solid construction\",\"phone\":9000177277,\"debits\":\"\",\"credits\":\"45,000.00\",\"email\":\"abc@abc.com\"},{\"account\":\"\",\"phone\":9000177277,\"debits\":\"\",\"credits\":\"45,000.00\",\"email\":\"abc@abc.com\"}]";
    List<BalajiSheet> mainData;
    RecyclerView mRecyclerView;
    MyRecyclerViewAdapter adapter;
    String gmailId;
    String gmailPass;
    private String SHARED_PREFS = "BALAJI_PREFS";
    private String EMAIL_ID = "EMAIL_ID";
    private String PASSWORD = "PASSWORD";
    private String CUSTOMER_MESSAGE = "CUSTOMER_MESSAGE";
    private String CUSTOMER_MESSAGE_TEMPLATE = "Hi name, Please clear your due Rs.amount pending with Balaji Electricals, Rajamundry";
    private String DEFAULT = "DEFAULT";
    private boolean haveGmailCredentials = false;
    private String customerMessage;
    short destinationPort = 80;
    private String JSON_DATA = "JSON_DATA";
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    public String SINGLE = "SINGLE";
    public String MULTIPLE = "MULTIPLE";
    private DeliverReceiver deliverReceiver;
    private SentReceiver sentReceiver;
    private String regex = "[0-9]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        if(getIntent().getExtras().get(JSON_DATA)!=null)
            jsonData = (String) getIntent().getExtras().get(JSON_DATA);
            Log.d("---input----", jsonData);
        initViews();
        checkGmailCredentials();
        checkMessageForCustomers();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<BalajiSheet>>() { }.getType();
        try{
            mainData = gson.fromJson(jsonData, listType);
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong with the json data", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(this, mainData.size()+"", Toast.LENGTH_SHORT).show();
        validateElements();

        adapter = new MyRecyclerViewAdapter(this, mainData, haveGmailCredentials);
        mRecyclerView.setAdapter(adapter);

        deliverReceiver = new DeliverReceiver();
        sentReceiver = new SentReceiver();

    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_listview_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_listview_smsall) {
            if(mainData == null )
                return true;
            for(BalajiSheet sheet:mainData){
                if(sheet.getPhone()!=null && sheet.getDebits()!=null){
                    sendMultipleSMS(sheet.getAccount(), sheet.getDebits(), sheet.getPhone());
                }
            }
            Toast.makeText(ListviewActivity.this, "Sent all sms", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.menu_listview_emailall){
            if(mainData == null )
                return true;
            for(BalajiSheet sheet:mainData){
                if(sheet.getEmail()!=null && sheet.getDebits()!=null){
                    sendEmailToCustomer(sheet.getAccount(), sheet.getDebits(), sheet.getEmail(), MULTIPLE);
                    Log.d("email:"+sheet.getEmail() , "debit"+sheet.getDebits());
                }
            }
            Toast.makeText(ListviewActivity.this, "Sent all emails", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void validateElements() {
        for(BalajiSheet sheet:mainData){
            if(sheet.getAccount()!=null && sheet.getAccount().length()<1)
                sheet.setAccount("No name");
            if(sheet.getPhone()!=null && !sheet.getPhone().matches(regex))
                sheet.setPhone(null);
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(sheet.getEmail()).matches())
                sheet.setEmail(null);
        }
    }

    private void checkGmailCredentials() {
        gmailId = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE).getString(EMAIL_ID, DEFAULT);
        gmailPass = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(PASSWORD, DEFAULT);
        if(!gmailId.equals(DEFAULT) && !gmailPass.equals(DEFAULT))
            haveGmailCredentials = true;
    }

    private void sendMessage(String message, String emailId, String isSingle){
        new AsyncTask<String, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(String... params) {
                if(gmailId.equals(DEFAULT) || gmailPass.equals(DEFAULT)){
                    return null;
                }
                Mail m = new Mail(gmailId, gmailPass);

                String[] toArr = {params[1]};
                m.setTo(toArr);
                m.setFrom(gmailId);
                m.setSubject("Payment reminder");
                m.setBody(params[0]);

                try {
                    //m.addAttachment("/sdcard/filelocation");

                    if(m.send() && params[2].equalsIgnoreCase(SINGLE)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ListviewActivity.this, "Email was sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ListviewActivity.this, "Email was not sent", Toast.LENGTH_SHORT).show();
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
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute(message,emailId);

    }

    private void checkMessageForCustomers() {
        customerMessage = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getString(CUSTOMER_MESSAGE, CUSTOMER_MESSAGE_TEMPLATE);
    }
    private String buildCustomerMessage(String name, String amount){
        String singleMessage = customerMessage;
        singleMessage = singleMessage.replaceAll("name" , name);
        singleMessage = singleMessage.replaceAll("amount", amount);
        return singleMessage;
    }
    public void sendEmailToCustomer(String name, String amount, String emailId, String isSingle){
        if(amount!=null)
        sendMessage(buildCustomerMessage(name,amount) , emailId, isSingle);
    }

    public void sendSingleSMS(String name ,String amount, String phoneno)
    {
        if (amount == null)
            return;
        String message = buildCustomerMessage(name, amount);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(deliverReceiver, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneno, null, message, sentPI, deliveredPI);
    }
    public void sendMultipleSMS(String name ,String amount, String phoneno){
        String message = buildCustomerMessage(name, amount);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneno, null, message, null, null);
        Log.d("phone:"+phoneno , "Message:"+message);
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
        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(deliverReceiver, new IntentFilter(DELIVERED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliverReceiver);
    }
}
