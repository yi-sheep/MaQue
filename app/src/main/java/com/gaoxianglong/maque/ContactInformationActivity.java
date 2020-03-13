package com.gaoxianglong.maque;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gaoxianglong.maque.adapter.CallRecordAdapter;
import com.gaoxianglong.maque.db.Contacts;
import com.gaoxianglong.maque.db.TelephoneBook;
import com.gaoxianglong.maque.dialog.ImageDialog;
import com.gaoxianglong.maque.utils.BlurTransformation;
import com.gaoxianglong.maque.utils.GlideRoundTransform;
import com.gaoxianglong.maque.utils.MyActivity;
import com.gaoxianglong.maque.utils.PinyinUtils;
import com.gaoxianglong.maque.utils.StatusBarUtil;
import com.gaoxianglong.maque.utils.ToDate;
import com.gaoxianglong.maque.views.ChangedScrollView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ContactInformationActivity extends MyActivity {

    public static final String TAG = "ContactInformationActivity";
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0; // 打开系统相册请求码
    private static final int CODE_CAMERA_REQUEST = 0xa1; // 打开系统相机请求码
    private static final String CALL_LOG_URI = "content://call_log";
    private static final String PATH_CALLS = "/calls";
    private ChangedScrollView mChangedScrollView;
    private Toolbar mToolbar;
    private String mName = "姓名正在加载中~";
    private String mImgpath = null;
    private String mSex = "性别正在加载中~";
    private String mNumber = "号码正在加载中~";
    private String mRemarks = "备注正在加载中~";
    private String mQQ = "QQ正在加载中~";
    private String mBirthday = "生日正在加载中~";
    private String mDescribe = "描述正在加载中~";
    private TextView mIName;
    private TextView mINumber;
    private TextView mIBirthday;
    private TextView mISex;
    private TextView mIRemarks;
    private TextView mIQQ;
    private TextView mIDescribe;
    private CallRecordAdapter mCallRecordAdapter;
    private ImageDialog mImageDialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);

        //这里注意下 因为在评论区发现有网友调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
        //是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this,false);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {

        mToolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(mToolbar);

        mIName = findViewById(R.id.i_name);
        mINumber = findViewById(R.id.i_number);
        mIBirthday = findViewById(R.id.i_birthday);
        mISex = findViewById(R.id.i_sex);
        mIRemarks = findViewById(R.id.i_remarks);
        mIQQ = findViewById(R.id.i_qq);
        mIDescribe = findViewById(R.id.i_describe);

        Intent intent = getIntent();
        final String number = intent.getStringExtra("number");

        selectTelephoneBook("select name,imgpath from telephoneBook where number=" + number,"name","imgpath");
        selectContacts("select * from contacts where number="+ number);

        mIName.setText(mName);
        mINumber.setText(mNumber);
        mIBirthday.setText(mBirthday);
        mISex.setText(mSex);
        mIRemarks.setText(mRemarks);
        mIQQ.setText(mQQ);
        mIDescribe.setText(mDescribe);

        //设置ImageView背景
        final ImageView headImageBJ = findViewById(R.id.head_image_bj);
        Glide.with(this).load(mImgpath).apply(RequestOptions.bitmapTransform(
                new BlurTransformation(this,25))).into(headImageBJ);
        // 设置头像
        ImageView headImage = findViewById(R.id.head_image);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_launcher_foreground) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new GlideRoundTransform(5)); //圆角
        Glide.with(this).load(mImgpath).apply(options).into(headImage);

        ChangedScrollView changedScrollView = findViewById(R.id.scrollView);
        changedScrollView.setTranslucentListener(new ChangedScrollView.ScrollviewListener() {
            @Override
            public void onScrollchanged(ChangedScrollView scrollView, int x, int y, int oldx, int oldy) {
                findViewById(R.id.head_fragment).setTranslationY(-y);
            }
        });
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageDialog = new ImageDialog(ContactInformationActivity.this,mNumber);
                mImageDialog.show();
                mImageDialog.onok = new ImageDialog.Onok() {
                    @Override
                    public void upDate() {
                        update(mNumber);
                    }
                };
            }
        });
        // 获取通话记录
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse(CALL_LOG_URI+PATH_CALLS);
        Cursor callCursor = contentResolver.query(uri, new String[]{"date"}, "name=?", new String[]{mName}, "date desc");
        List<String> callRecords = new ArrayList<>();
        while (callCursor.moveToNext()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                callRecords.add(ToDate.stampToDate(callCursor.getString(callCursor.getColumnIndex("date"))));
            }
        }
        // 加载通话记录
        RecyclerView callRecordRecyclerView = findViewById(R.id.call_record_recyclerView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        callRecordRecyclerView.setLayoutManager(layout);
        mCallRecordAdapter = new CallRecordAdapter(callRecords);
        callRecordRecyclerView.setAdapter(mCallRecordAdapter);
    }

    /**
     * 在telephonebook表中做查询
     * @param sql  查询语句
     * @param index1   查询的字段 1
     * @param index2   查询的字段 2
     */
    public void selectTelephoneBook(String sql,String index1,String index2) {
        Cursor cursor = LitePal.findBySQL(sql);
        if (cursor.moveToNext()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(index1));
                String imgpath = cursor.getString(cursor.getColumnIndex(index2));
                mName = name;
                mImgpath = imgpath;
            }
            while (cursor.moveToNext());
        }
    }

    /**
     * 在contacts表中做查询
     * @param sql  查询语句
     */
    public void selectContacts(String sql) {
        Cursor cursor = LitePal.findBySQL(sql);
        if (cursor.moveToNext()) {
            do {
                String sex = cursor.getString(cursor.getColumnIndex("sex"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String remarks = cursor.getString(cursor.getColumnIndex("remarks"));
                String qq = cursor.getString(cursor.getColumnIndex("qq"));
                String birthday = cursor.getString(cursor.getColumnIndex("birthday"));
                String describe = cursor.getString(cursor.getColumnIndex("describe"));
                mSex = sex;
                mNumber = number;
                mRemarks = remarks;
                mQQ = qq;
                mBirthday = birthday;
                mDescribe = describe;
            }
            while (cursor.moveToNext());
        }
    }

    // 返回按钮
    public void back(View view) {
        finish();
    }

    // 编辑按钮
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void editTextButton(View view) {
        RadioGroup iRadioGroup = findViewById(R.id.i_radiogroup);
        RadioButton radioButtonA = findViewById(R.id.et_i_radioButton);
        RadioButton radioButtonB = findViewById(R.id.et_i_radioButton2);
        EditText etName = findViewById(R.id.et_i_name);
        EditText etNumber = findViewById(R.id.et_i_number);
        EditText etQQ = findViewById(R.id.et_i_qq);
        EditText etBirthday = findViewById(R.id.et_i_birthday);
        EditText etRemarks = findViewById(R.id.et_i_remarks);
        EditText etDescribe = findViewById(R.id.et_i_describe);
        Button etBtn = findViewById(R.id.et_btn);
        if (etBtn.getText().toString().equals("编辑")) {

            String sexText = mISex.getText().toString();
            mISex.setVisibility(View.GONE);
            iRadioGroup.setVisibility(View.VISIBLE);
            if (sexText.equals("男")) {
                radioButtonA.setChecked(true);
            } else if (sexText.equals("女")) {
                radioButtonB.setChecked(true);
            }

            String nameText = mIName.getText().toString();
            mIName.setVisibility(View.GONE);
            etName.setVisibility(View.VISIBLE);
            etName.setText(nameText);

            String numberText = mINumber.getText().toString();
            mINumber.setVisibility(View.GONE);
            etNumber.setVisibility(View.VISIBLE);
            etNumber.setText(numberText);

            String qqText = mIQQ.getText().toString();
            mIQQ.setVisibility(View.GONE);
            etQQ.setVisibility(View.VISIBLE);
            etQQ.setText(qqText);

            String birthdayText = mIBirthday.getText().toString();
            mIBirthday.setVisibility(View.GONE);
            etBirthday.setVisibility(View.VISIBLE);
            etBirthday.setText(birthdayText);

            String remarksText = mIRemarks.getText().toString();
            mIRemarks.setVisibility(View.GONE);
            etRemarks.setVisibility(View.VISIBLE);
            etRemarks.setText(remarksText);

            String describeText = mIDescribe.getText().toString();
            mIDescribe.setVisibility(View.GONE);
            etDescribe.setVisibility(View.VISIBLE);
            etDescribe.setText(describeText);

            etBtn.setText("完成");
        } else {
            String name = etName.getText().toString().trim();
            String number = etNumber.getText().toString();
            String sex = "";
            if (radioButtonA.isChecked()) {
                sex = "男";
            } else if (radioButtonB.isChecked()) {
                sex = "女";
            }
            String qq = etQQ.getText().toString();
            String birthday = etBirthday.getText().toString().trim();
            String remarks = etRemarks.getText().toString().trim();
            String describe = etDescribe.getText().toString().trim();
            String pinyin;
            //汉字转换成拼音
            String qPinYin = PinyinUtils.getPingYin(name);
            String sortString = qPinYin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                pinyin = sortString.toUpperCase();
            } else {
                pinyin = "#";
            }

            // 更新telephoneBook表
            TelephoneBook telephoneBook = new TelephoneBook();
            telephoneBook.setPinyin(pinyin);
            telephoneBook.setName(name);
            telephoneBook.setNumber(number);
            telephoneBook.updateAll("number=?",mINumber.getText().toString());

            // 更新contacts表
            Contacts contacts = new Contacts();
            contacts.setSex(sex);
            contacts.setNumber(number);
            contacts.setRemarks(remarks);
            contacts.setQq(qq);
            contacts.setBirthday(birthday);
            contacts.setDescribe(describe);
            contacts.updateAll("number=?",mINumber.getText().toString());

            mISex.setVisibility(View.VISIBLE);
            iRadioGroup.setVisibility(View.GONE);
            mIName.setVisibility(View.VISIBLE);
            etName.setVisibility(View.GONE);
            mINumber.setVisibility(View.VISIBLE);
            etNumber.setVisibility(View.GONE);
            mIQQ.setVisibility(View.VISIBLE);
            etQQ.setVisibility(View.GONE);
            mIBirthday.setVisibility(View.VISIBLE);
            etBirthday.setVisibility(View.GONE);
            mIRemarks.setVisibility(View.VISIBLE);
            etRemarks.setVisibility(View.GONE);
            mIDescribe.setVisibility(View.VISIBLE);
            etDescribe.setVisibility(View.GONE);
            update(number); // 更新数据
            etBtn.setText("编辑");
        }
    }

    // 更新数据
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void update(String number) {
        selectTelephoneBook("select name,imgpath from telephoneBook where number=" + number,"name","imgpath");
        selectContacts("select * from contacts where number="+ number);

        mIName.setText(mName);
        mINumber.setText(mNumber);
        mIBirthday.setText(mBirthday);
        mISex.setText(mSex);
        mIRemarks.setText(mRemarks);
        mIQQ.setText(mQQ);
        mIDescribe.setText(mDescribe);

        //设置ImageView背景
        final ImageView headImageBJ = findViewById(R.id.head_image_bj);
        Glide.with(this).load(mImgpath).apply(RequestOptions.bitmapTransform(
                new BlurTransformation(this,25))).into(headImageBJ);
        // 设置头像
        ImageView headImage = findViewById(R.id.head_image);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_launcher_foreground) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .diskCacheStrategy(DiskCacheStrategy.NONE) //缓存
                .transform(new GlideRoundTransform(5)); //圆角
        Glide.with(this).load(mImgpath).apply(options).into(headImage);

        ChangedScrollView changedScrollView = findViewById(R.id.scrollView);
        changedScrollView.setTranslucentListener(new ChangedScrollView.ScrollviewListener() {
            @Override
            public void onScrollchanged(ChangedScrollView scrollView, int x, int y, int oldx, int oldy) {
                findViewById(R.id.head_fragment).setTranslationY(-y);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                String uriPath = getUriPath(data.getData());
                TelephoneBook telephoneBook = new TelephoneBook();
                telephoneBook.setImgpath(uriPath);
                telephoneBook.updateAll("number=?",mNumber);
                mImageDialog.onok.upDate();
                mImageDialog.dismiss();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 通过打开系统相册选择的图片返回的URI获取到图片的真实地址
     * @param uri
     * @return
     */
    private String getUriPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0); // 获取到图片id 类似于 image:1176329
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1); // 将image 和数字分开 得到1176329
            cursor.close(); // 关闭当前使用的查询内容
            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ",
                    new String[]{document_id}, null); // 在外部内容URI中通过图片id查询出这张图片的真实地址
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }
}
