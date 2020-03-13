package com.gaoxianglong.maque.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.gridlayout.widget.GridLayout;

import com.gaoxianglong.maque.NewContactActivity;
import com.gaoxianglong.maque.R;
import com.gaoxianglong.maque.db.TelephoneBook;
import com.gaoxianglong.maque.utils.DrawableToUri;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageDialog extends Dialog {

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0; // 打开系统相册请求码
    private static final int CODE_CAMERA_REQUEST = 0xa1; // 打开系统相机请求码

    private Context mContext;
    private FragmentActivity mActivity = new FragmentActivity();
    public Onok onok;
    private CircleImageView mBlack;
    private CircleImageView mPink;
    private CircleImageView mBlue;
    private CircleImageView mRed;
    private CircleImageView mYellow;
    private Button mNo;
    private Button mOk;
    private String mNumber;
    private List<Integer> imgid = new ArrayList<>();
    String[] img = new String[1];

    public ImageDialog(@NonNull Context context,String number) {
        super(context);
        mContext = context;
        mNumber = number;
    }

    public ImageDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_dialog);
        init();
        setSize();
        // 确认按钮
        mOk.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View arg0) {
                TelephoneBook telephoneBook = new TelephoneBook();
                telephoneBook.setImgpath(img[0]);
                telephoneBook.updateAll("number=?",mNumber);
                onok.upDate();
                dismiss();
            }
        });
        // 取消按钮
        mNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
    }

    /**
     * 对话框大小
     */
    private void setSize() {
        Window window = getWindow();
        // Point point = new Point();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        int x = display.getWidth();
        int y = display.getHeight();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (x * 3) / 4;
//        layoutParams.height = 850;
        window.setAttributes(layoutParams);
    }

    private void init() {
        mBlack = findViewById(R.id.img_dialog_black);
        mPink = findViewById(R.id.img_dialog_pink);
        mBlue = findViewById(R.id.img_dialog_blue);
        mRed = findViewById(R.id.img_dialog_red);
        mYellow = findViewById(R.id.img_dialog_yellow);
        mNo = findViewById(R.id.no);
        mOk = findViewById(R.id.ok);

        imgid.add(R.drawable.img_purple);
        imgid.add(R.drawable.img_pink);
        imgid.add(R.drawable.img_blue);
        imgid.add(R.drawable.img_red);
        imgid.add(R.drawable.img_orange);
        imgid.add(R.drawable.bj_no);

        final GridLayout gridLayout = findViewById(R.id.gridlayout2);
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
                    layout.setBackgroundResource(imgid.get(I));
                    Uri uri = DrawableToUri.toUri(mContext, imgid.get(I));
                    img[0] = uri.toString();
                }
            });
        }
        ImageView headImage = findViewById(R.id.img_dialog_other);
        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseHeadImageFromGallery();
            }
        });
    }

    /**
     * 打开系统相册
     */
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity)mContext).startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }
    public interface Onok {
        void upDate();
    }

}
