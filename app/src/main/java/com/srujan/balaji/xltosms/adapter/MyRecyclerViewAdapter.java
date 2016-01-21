package com.srujan.balaji.xltosms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.srujan.balaji.xltosms.ListviewActivity;
import com.srujan.balaji.xltosms.R;
import com.srujan.balaji.xltosms.models.BalajiSheet;

import java.util.List;

/**
 * Created by mobility on 31/12/15.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder>  {
    private List<BalajiSheet> feedItemList;
    private Context mContext;
    private boolean showGmail = false;
    private BalajiSheet item;

    public MyRecyclerViewAdapter(Context context, List<BalajiSheet> feedItemList, boolean haveGmailCredentials) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.showGmail = haveGmailCredentials;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_account, viewGroup, false);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);
/*
        item = feedItemList.get(i);

        viewHolder.sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListviewActivity)mContext).sendSingleSMS(item.getAccount(), item.getDebits(), item.getPhone());
                Toast.makeText(mContext, item.getAccount()+item.getPhone()+item.getEmail()+"",Toast.LENGTH_LONG).show();
                viewHolder.sms.setBackgroundColor(0xFF3AE17D);
                viewHolder.sms.invalidate();
            }
        });
        viewHolder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListviewActivity)mContext).sendEmailToCustomer(item.getAccount(), item.getDebits(), item.getEmail(), ((ListviewActivity)mContext).SINGLE);
                Toast.makeText(mContext, item.getAccount()+item.getPhone()+item.getEmail()+"",Toast.LENGTH_LONG).show();
            }
        });*/
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        BalajiSheet item = feedItemList.get(i);
        customViewHolder.name.setText(item.getAccount());
        if(!showGmail || item.getEmail()==null || !android.util.Patterns.EMAIL_ADDRESS.matcher(item.getEmail()).matches())
            customViewHolder.email.setVisibility(View.GONE);

        if(item.getPhone() == null || item.getPhone().length()<1)
            customViewHolder.sms.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected Button sms,email;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.tv_individual_account_name);
            this.sms = (Button) view.findViewById(R.id.bt_individual_account_sms);
            this.email = (Button) view.findViewById(R.id.bt_individual_account_email);
            this.sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BalajiSheet item = feedItemList.get(getPosition());
                    ((ListviewActivity)mContext).sendSingleSMS(item.getAccount(), item.getDebits(), item.getPhone());
                   // Toast.makeText(mContext, item.getAccount()+item.getPhone()+item.getEmail()+"",Toast.LENGTH_LONG).show();
                    sms.setBackgroundColor(0xFF3AE17D);
                    sms.invalidate();
                }
            });
            this.email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BalajiSheet item = feedItemList.get(getPosition());
                    ((ListviewActivity)mContext).sendEmailToCustomer(item.getAccount(), item.getDebits(), item.getEmail(), ((ListviewActivity)mContext).SINGLE);
                    //Toast.makeText(mContext, item.getAccount()+item.getPhone()+item.getEmail()+"",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
