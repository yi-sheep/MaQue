package com.gaoxianglong.maque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gaoxianglong.maque.adapter.TelephoneBookAdapter;
import com.gaoxianglong.maque.db.Contacts;
import com.gaoxianglong.maque.db.TelephoneBook;
import com.gaoxianglong.maque.utils.ConvertPictures;
import com.gaoxianglong.maque.utils.DrawableToUri;
import com.gaoxianglong.maque.utils.MyActivity;
import com.gaoxianglong.maque.utils.PinyinComparator;
import com.gaoxianglong.maque.utils.PinyinUtils;
import com.gaoxianglong.maque.utils.SortModel;
import com.gaoxianglong.maque.views.SearchEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends MyActivity {

    public static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private List<TelephoneBook> mTelephoneBookList = new ArrayList<>();
    private TelephoneBookAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_main);
        //运行时权限
        List<String> permissionList = new ArrayList<>();
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CONTACTS);
        }
        if (checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CALL_PHONE);
        }
        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_CALL_LOG);
        }
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
            requestPermissions(permissions, 1);
        } else {
            firstRun();  //判断程序是不是第一次使用
            init(); // 初始化和一系列简单操作
            initTelephoneBook();  // 向RecyclerView添加数据
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun", 0);
        Boolean first_run = sharedPreferences.getBoolean("First", true);
        if (first_run) {
            sharedPreferences.edit().putBoolean("First", false).commit();
            Toast.makeText(this, "第一次", Toast.LENGTH_LONG).show();
            readContacts();  // 获取系统联系人
        } else {
            Toast.makeText(this, "不是第一次", Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readContacts() {
        ConvertPictures convertPictures = new ConvertPictures();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s*", "");
                    Log.d(TAG, "readContacts: " + displayName);
                    Log.d(TAG, "readContacts: " + number);
                    TelephoneBook telephoneBook = new TelephoneBook();
                    Contacts contacts = new Contacts();
                    telephoneBook.setName(displayName);
                    //汉字转换成拼音
                    String pinyin = PinyinUtils.getPingYin(displayName);
                    String sortString = pinyin.substring(0, 1).toUpperCase();

                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        telephoneBook.setPinyin(sortString.toUpperCase());
                    } else {
                        telephoneBook.setPinyin("#");
                    }
                    telephoneBook.setNumber(number);
                    contacts.setNumber(number);
                    telephoneBook.setImgpath(DrawableToUri.toUri(MainActivity.this,R.drawable.img_blue).toString());
                    contacts.save();
                    telephoneBook.save();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public void initTelephoneBook() {
        Cursor cursor = LitePal.findBySQL("select DISTINCT number,name,imgpath from TelephoneBook ORDER BY pinyin ASC");
        mTelephoneBookList.clear();  // 在每次加载数据的时候都需要清空上一次链表里的数据
        if (cursor.moveToNext()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String imgpath = cursor.getString(cursor.getColumnIndex("imgpath"));

                mTelephoneBookList.add(new TelephoneBook(number, name, imgpath));
            }
            while (cursor.moveToNext());
        }
    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);  //获取toolbar实例
        setSupportActionBar(toolbar);  //设置标题栏

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.a);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        //悬浮按钮
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewContactActivity.class);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new TelephoneBookAdapter(mTelephoneBookList);
        recyclerView.setAdapter(mAdapter);

        // 下拉刷新
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });

        SearchEditText searchEditText = findViewById(R.id.search_et);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 更改文字之前
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 文字已更改
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 文字更改后
            }
        });
    }

    /**
     * 搜索处理逻辑
     *
     * @param toString
     */
    private void filterData(String toString) {
        List<TelephoneBook> telephoneBooks = new ArrayList<>();
        if (toString.isEmpty()) {
            telephoneBooks = mTelephoneBookList;
        } else {
            telephoneBooks.clear();
            for (TelephoneBook telephoneBook : mTelephoneBookList) {
                if (isNumeric(toString)) {
                    // 检索号码
                    String number = telephoneBook.getNumber();
                    if (number.contains(toString)) {
                        telephoneBooks.add(telephoneBook);
                    }
                } else {
                    String name = telephoneBook.getName();
                    // 检索姓名的首字母和包含的文字
                    if (name.indexOf(toString) != -1 || PinyinUtils.getFirstSpell(name).startsWith(toString) ||
                            PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(toString)
                            || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(toString)) {
                        telephoneBooks.add(telephoneBook);
                    }
                }
            }
        }
        // 根据a-z进行排序
        PinyinComparator pinyinComparator = new PinyinComparator();
        Collections.sort(telephoneBooks, pinyinComparator);
        mAdapter.updateList(telephoneBooks);
    }

    /**
     * 使用正则表达式判断字符串是不是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 使用正则表达式判断是否是字母
     *
     * @param str
     * @return
     */
    public static boolean isPinyin(String str) {
        boolean matches;
        Pattern pattern = Pattern.compile("[A-Z]*");
        if (!pattern.matcher(str).matches()) {
            Pattern pattern1 = Pattern.compile("[a-z]*");
            matches = pattern1.matcher(str).matches();
        } else {
            matches = pattern.matcher(str).matches();
        }
        Log.d(TAG, "isPinyin: " + matches);
        return matches;
    }

    /**
     * 下拉刷新的处理逻辑
     */
    public void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initTelephoneBook();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /**
     * 加载menu点击事件
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * menu点击事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:

        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.CERT_INPUT_RAW_X509) {
                            Toast.makeText(this, "必须同意哦!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    firstRun();  //判断程序是不是第一次使用
                    init(); // 初始化和一系列简单操作
                    initTelephoneBook();  // 向RecyclerView添加数据
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initTelephoneBook();
        mAdapter.notifyDataSetChanged();
    }

}
