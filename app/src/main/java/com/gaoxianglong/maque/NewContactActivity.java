package com.gaoxianglong.maque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.gridlayout.widget.GridLayout;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gaoxianglong.maque.db.Contacts;
import com.gaoxianglong.maque.db.TelephoneBook;
import com.gaoxianglong.maque.utils.DrawableToUri;
import com.gaoxianglong.maque.utils.MyActivity;
import com.gaoxianglong.maque.utils.PinyinUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewContactActivity extends MyActivity {

    public static final String TAG = "NewContactActivity";
    List<Integer> imgid = new ArrayList<>();
    String[] img = new String[1];
    String[] information = new String[8];

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0; // 打开系统相册请求码
    private static final int CODE_CAMERA_REQUEST = 0xa1; // 打开系统相机请求码
    private static final int CODE_RESULT_REQUEST = 0xa2; // 剪切图片请求码

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    private ImageView headImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_new_contact);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
    }

    private void init() {
        final EditText et_name = findViewById(R.id.et_name);
        final EditText et_number = findViewById(R.id.et_number);
        final EditText et_remarks = findViewById(R.id.et_remarks);
        final EditText et_qq = findViewById(R.id.et_qq);
        final EditText et_birthday = findViewById(R.id.et_birthday);
        final EditText et_describe = findViewById(R.id.et_describe);
        headImage = findViewById(R.id.img_title_other);

        imgid.add(R.drawable.img_purple);
        imgid.add(R.drawable.img_pink);
        imgid.add(R.drawable.img_blue);
        imgid.add(R.drawable.img_red);
        imgid.add(R.drawable.img_orange);
        imgid.add(R.drawable.bj_no);
        information[0] = "男";


        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        //悬浮按钮
        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                information[1] = et_name.getText().toString().trim();
                information[2] = et_number.getText().toString().trim();
                information[3] = et_remarks.getText().toString();
                information[4] = et_qq.getText().toString().trim();
                information[5] = et_birthday.getText().toString();
                information[6] = et_describe.getText().toString();
                if (!information[1].isEmpty()) {
                    String pinyin = PinyinUtils.getPingYin(information[1]);
                    information[7] = pinyin.substring(0, 1).toUpperCase();
                }
                if (!information[1].isEmpty() && !information[2].isEmpty()) {
                    Toast.makeText(NewContactActivity.this, "完成,选择的图片为" + img[0] + "性别为:" + information[0] + "姓名为："
                            + information[1] + "号码为:" + information[2] + "备注为:" + information[3] + "QQ为:" + information[4]
                            + "生日为:" + information[5] + "描述为:" + information[6], Toast.LENGTH_SHORT).show();
                    Contacts contacts = new Contacts();
                    TelephoneBook telephoneBook = new TelephoneBook();
                    telephoneBook.setImgpath(img[0]);
                    contacts.setSex(information[0]);
                    telephoneBook.setName(information[1]);
                    telephoneBook.setPinyin(information[7].toUpperCase());
                    telephoneBook.setNumber(information[2]);
                    contacts.setNumber(information[2]);
                    contacts.setRemarks(information[3]);
                    contacts.setQq(information[4]);
                    contacts.setBirthday(information[5]);
                    contacts.setDescribe(information[6]);
                    telephoneBook.save();
                    contacts.save();
                    finish();
                } else {
                    Toast.makeText(NewContactActivity.this, "请检查姓名和号码是否填写成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //顶部颜色选择
        final GridLayout gridLayout = findViewById(R.id.gridlayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            final LinearLayout layout = (LinearLayout) gridLayout.getChildAt(i);
            final int I = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < gridLayout.getChildCount(); j++) {
                        LinearLayout layout1 = (LinearLayout) gridLayout.getChildAt(j);
                        layout1.setBackgroundResource(R.drawable.bj_no);
                        img[0] = null;
                    }
//                    layout.setBackgroundResource(imgid.get(I));
                    Log.d(TAG, "onClick: " + img[0]);
                    Uri uri = DrawableToUri.toUri(NewContactActivity.this, imgid.get(I));
                    img[0] = uri.toString();
                    Log.d(TAG, "uri ==>" + img[0]);
                    headImage.setImageURI(uri);
                }
            });

            // 性别选择
            RadioButton radio_nan = findViewById(R.id.radioButton);
            RadioButton radio_nv = findViewById(R.id.radioButton2);
            radio_nan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_name.setHint(R.string.nan_name);
                    et_number.setHint(R.string.nan_number);
                    et_remarks.setHint(R.string.nan_remarks);
                    et_qq.setHint(R.string.nan_qq);
                    et_birthday.setHint(R.string.nan_birthday);
                    et_describe.setHint(R.string.nan_describe);
                    information[0] = "男";
                }
            });
            radio_nv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_name.setHint(R.string.nv_name);
                    et_number.setHint(R.string.nv_number);
                    et_remarks.setHint(R.string.nv_remarks);
                    et_qq.setHint(R.string.nv_qq);
                    et_birthday.setHint(R.string.nv_birthday);
                    et_describe.setHint(R.string.nv_describe);
                    information[0] = "女";
                }
            });
        }
        headImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                runPermissions();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void runPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            requestPermissions(permissions, 2);
        } else {
            listDialog();
        }
    }

    /**
     * 询问对话框  1、使用相机拍取图片   2、使用相册选择图片
     */
    public void listDialog() {
        final String[] items = {"直接拍照", "打开相册"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NewContactActivity.this);
        builder.setTitle("选择相机还是相册");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                switch (items[arg1]) {
                    case "直接拍照":
                        choseHeadImageFromCameraCapture();
                        break;
                    case "打开相册":
                        choseHeadImageFromGallery();
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 打开系统相机
     */
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    /**
     * 打开系统相册
     */
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                String uriPath = getUriPath(data.getData());
                headImage.setImageURI(Uri.parse("file://"+uriPath));
                img[0] = Uri.parse("file://"+uriPath).toString();
                break;
            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    headImage.setImageURI(Uri.fromFile(tempFile));
                    img[0] = Uri.fromFile(tempFile).toString();
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG).show();
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Log.d(TAG, "cropRawPhoto: ==>" + uri);
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//
//        // 设置裁剪
//        intent.putExtra("crop", "true");
//
//        // aspectX , aspectY :宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//
//        // outputX , outputY : 裁剪图片宽高
//        intent.putExtra("outputX", output_X);
//        intent.putExtra("outputY", output_Y);
//        intent.putExtra("return-data", true);
//
//        startActivityForResult(intent, CODE_RESULT_REQUEST);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scaleUpIfNeeded", true);//去除黑边
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
//        Log.d(TAG, "setImageToHeadView: " + intent.getE);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Log.d(TAG, "setImageToHeadView: " + photo);
            headImage.setImageBitmap(photo);
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0) {
                    for (int resilt : grantResults) {
                        if (resilt != PackageManager.CERT_INPUT_RAW_X509) {
                            Toast.makeText(this, "此功能需要读取相机和相册的权限哦", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    listDialog();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
