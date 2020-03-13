package com.gaoxianglong.maque.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaoxianglong.maque.ContactInformationActivity;
import com.gaoxianglong.maque.MainActivity;
import com.gaoxianglong.maque.R;
import com.gaoxianglong.maque.context.MyApplication;
import com.gaoxianglong.maque.db.Contacts;
import com.gaoxianglong.maque.db.TelephoneBook;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.List;

public class TelephoneBookAdapter extends RecyclerView.Adapter<TelephoneBookAdapter.ViewHolder>{
    public static final String TAG = "TelephoneBookAdapter";
    private Contacts mContacts = new Contacts();
    private Context mContext;
    private List<TelephoneBook> mTelephoneBookList;

    public TelephoneBookAdapter(List<TelephoneBook> telephoneBookList) {
        mTelephoneBookList = telephoneBookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.telephone_book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        holder.setIsRecyclable(false);
        final TelephoneBook telephoneBook = mTelephoneBookList.get(position);
        holder.contactsName.setText(telephoneBook.getName());
        holder.contactsNumber.setText(telephoneBook.getNumber());
        Uri imageUri = Uri.parse("tencent/QQ_Images/2c92974886d57f82.jpg");
        Log.d(TAG, "path ==>" + telephoneBook.getImgpath());
        Glide.with(mContext).load(telephoneBook.getImgpath()).into(holder.contactsImage);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.call(telephoneBook.getNumber());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+telephoneBook.getImgpath());
                Snackbar.make(v,"你将要删除 "+telephoneBook.getName()+" 联系人\n是否删除",Snackbar.LENGTH_SHORT).setAction("删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTelephoneBookList.remove(position);
                        LitePal.deleteAll(TelephoneBook.class,"name=? and number=?",telephoneBook.getName(),telephoneBook.getNumber());
                        LitePal.deleteAll(Contacts.class,"number=?",telephoneBook.getNumber());
                        notifyDataSetChanged();
                    }
                }).show();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ContactInformationActivity.class);
                intent.putExtra("number",holder.contactsNumber.getText().toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTelephoneBookList.size();
    }

    /**
     * 提供给Activity刷新数据
     * @param list
     */
    public void updateList(List<TelephoneBook> list){
        this.mTelephoneBookList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView contactsImage;
        TextView contactsName;
        TextView contactsNumber;
        ImageButton call,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            contactsImage = itemView.findViewById(R.id.contacts_image);
            contactsName = itemView.findViewById(R.id.contacts_name);
            contactsNumber = itemView.findViewById(R.id.contacts_number);
            call = itemView.findViewById(R.id.call_imgbtn);
            delete = itemView.findViewById(R.id.delete_imgbtn);
        }

        /**
         * 拨号逻辑
         */
        private void call(String number) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }
    }
}
