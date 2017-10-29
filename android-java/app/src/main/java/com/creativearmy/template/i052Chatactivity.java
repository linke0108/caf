package com.creativearmy.template;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creativearmy.sdk.APIConnection;
import com.creativearmy.sdk.JSONArray;
import com.creativearmy.sdk.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import com.creativearmy.template.i052.*;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Calendar.getInstance;

/**
 * Created by Administrator on 2016/3/1.
 */
public class i052Chatactivity extends Activity implements OnLayoutChangeListener, SwipeRefreshLayout.OnRefreshListener, SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, View.OnClickListener, TextWatcher {
    MessAdapter mMessAdapter;
    SwipeRefreshLayout mSwipeView;
    Button mBtnSendMsg;
    //ImageButton mBtnExpression;
    ImageButton mBtnAddFile;
    EditText mEditChatInput;
    TableLayout mMoreMenuTl;
    ImageView mBtnBack;
    LinearLayout mLinearChat;
    TextView mTvTitle;
    private boolean mIsShowMoreMenu = false;
    private boolean mMoreMenuVisible = false;
    private int mHieght;
    private String mNowId;
    private String mNextId;

    private String mode = "person"; // 聊天室：task, topic, person(私聊)
    public  String Title; // 聊天室标题

    public static String mTopicId; // 话题ID

    public static String mTaskId; // 任务 ID

    public static String mPersonId; // 私聊对方ID

    private String CREATOR_ID = ""; // task creator
    private String CREATOR_NAME = ""; // task creator name
    private ImageView guanzhu;
    private ImageView imgPhoto;
    private ImageView imgCamera;
    private ImageView tiaozhuan;
    private static int REQUEST_PHOTO = 1;
    private static int REQUEST_CAMERA = 2;
    private static int PHOTO_REQUEST_CUT_CIRCLE = 3;
    private Uri tempUri;

    private Uri imageUri;
    private File tempFile;

    private String currentHeader;

    private int imageMaxWidth;      //压缩上传IM图片宽度
    private int imageMaxSize;      //压缩上传IM图片大小
    private ImageView img_video;
    private ImageView img_card;
    private ImageView img_map;

    // chat list view
    private ListView msgListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //获取键盘高度
        mKeyboradHeigth = getisKeyboardHeight();
        APIConnection.registerHandler(handler);

        findViewById(R.id.relative_activity_chat).addOnLayoutChangeListener(this);
        mLinearChat = (LinearLayout) findViewById(R.id.send_msg_layout);
        mSwipeView = (SwipeRefreshLayout) findViewById(R.id.chat_refresh);
        mSwipeView.setEnabled(false);
        //mBtnExpression = (ImageButton) findViewById(R.id.expression_btn);
        mBtnAddFile = (ImageButton) findViewById(R.id.add_file_btn);
        mBtnBack = (ImageView) findViewById(R.id.i040_back);
        mBtnSendMsg = (Button) findViewById(R.id.send_msg_btn);
        mEditChatInput = (EditText) findViewById(R.id.chat_input_et);
        mMoreMenuTl = (TableLayout) findViewById(R.id.more_menu_tl);
        mTvTitle = (TextView) findViewById(R.id.tv_bar_title);
        //mBtnExpression.setOnClickListener(this);
        mBtnAddFile.setOnClickListener(this);
        mBtnSendMsg.setOnClickListener(this);
        mEditChatInput.addTextChangedListener(this);
        mEditChatInput.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    i052Chatactivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                } else {
                    // 此处为失去焦点时的处理内容
                }
            }
        });
        mBtnBack.setOnClickListener(this);
        imgPhoto = (ImageView) findViewById(R.id.img_photo);
        imgPhoto.setOnClickListener(this);
        imgCamera = (ImageView) findViewById(R.id.img_camera);
        imgCamera.setOnClickListener(this);
        //img_card = (ImageView) findViewById(R.id.img_card);
        //img_card.setOnClickListener(this);
        //img_map = (ImageView) findViewById(R.id.img_map);
        //img_map.setOnClickListener(this);
        //img_video = (ImageView) findViewById(R.id.img_video);
        //img_video.setOnClickListener(this);
        msgListview = (ListView) findViewById(R.id.chat_list);
        mMessAdapter = new MessAdapter(this);
        msgListview.setAdapter(mMessAdapter);

        CREATOR_ID = getIntent().getStringExtra("CREATOR_ID");
        CREATOR_NAME = getIntent().getStringExtra("CREATOR_NAME");
/*
        if (getIntent().getStringExtra("topic_id") != null && !getIntent().getStringExtra("topic_id").equals("")) {
            mode = "topic";
            mTopicId = getIntent().getStringExtra("topic_id");
            Title = getIntent().getStringExtra("topic_title");

        } else if (getIntent().getStringExtra("task_id") != null && !getIntent().getStringExtra("task_id").equals("")) {
            mode = "task";
            mTaskId = getIntent().getStringExtra("task_id");
            Title = getIntent().getStringExtra("task_title");

        } else if (getIntent().getStringExtra("person_id") != null && !getIntent().getStringExtra("person_id").equals("")) {
            mode = "person";
            mPersonId = getIntent().getStringExtra("person_id");
            Title = getIntent().getStringExtra("person_name");

        } else {
            // FIXME
        }
*/
        /*
        mode = "person";
        mPersonId = APIConnection.server_info.optString("admin_pid");

        if (APIConnection.user_info.optString("_id").equals(mPersonId)) {
            // for testint, admin chat with test1
            mPersonId = "o14692833186075780391";
        }
        */
        mode = "person";
        // 工具箱用的登录的用户ID
        mPersonId = "o14509039359136660099";

        Title = "私聊：i052Chatactivity.java mPersonId";

        mTvTitle.setText(Title);
        mTvTitle.setTextColor(Color.WHITE);

        HashMap req = new HashMap();

        if (mode.equals("topic")) {
            req.put("obj", "topic");
            req.put("act", "chat_get");
            req.put("topic_id", mTopicId);

        } else if (mode.equals("task")) {
            req.put("obj", "task");
            req.put("act", "chat_get");
            req.put("task_id", mTaskId);

        } else if (mode.equals("person")) {
            req.put("obj", "person");
            req.put("act", "chat_get");
		
            JSONArray ja = new JSONArray();
            // 这个ID 是我自己
            ja.put(APIConnection.user_info.optString("_id"));
            ja.put(mPersonId);

            req.put("users", ja);

        } else {
		// FIXME
	    }

        // 通常还有用户在界面输入的其他数据，一起发送好了
        req.put("person_id", APIConnection.user_info.optString("_id"));
        Log.e("I052", String.format("PID = %s\tTID = %s", "", mTopicId));
        APIConnection.send(req);

        mSwipeView.setOnRefreshListener(this);


        msgListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    mSwipeView.setEnabled(true);
                else
                    mSwipeView.setEnabled(false);
            }
        });

        // currentHeader= ( (MyApplication)getApplication()).getmDownPathHead() + fid;
        currentHeader= APIConnection.server_info.optString("download_path")+APIConnection.user_info.optString("headFid");

        imageMaxWidth = Integer.parseInt(getImageWidth());
        imageMaxSize = Integer.parseInt(getImageSize());

    }

    public String getImageWidth() {
        if (APIConnection.server_info == null ||
                APIConnection.server_info.optString("resize_upload_im_image_width_to").equals("")) return 800+"";

        return APIConnection.server_info.optString("resize_upload_im_image_width_to");
    }
    public String getImageSize() {
        if (APIConnection.server_info == null ||
                APIConnection.server_info.optString("compress_upload_im_image_max").equals("")) return 100000+"";

        return APIConnection.server_info.optString("compress_upload_im_image_max");
    }

    private boolean isfollowing = false;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//            TextView output = (TextView) findViewById(R.id.OUTPUT);
//            TextView output2 = (TextView) findViewById(R.id.OUTPUT2);
            if (msg.what == APIConnection.responseProperty) {
                JSONObject jo = (JSONObject) msg.obj;
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                if ((jo.optString("obj").equals("topic") || jo.optString("obj").equals("task"))
  		            && jo.optString("act").equals("chat_get")) {

                    isfollowing = jo.optInt("following") == 0 ? false : true;
                    if (isfollowing) {
                        //guanzhu.setBackgroundResource(R.mipmap.icon_eye_gopen);
                    } else {
                        //guanzhu.setBackgroundResource(R.mipmap.icon_eye_gclose);
                    }
                    //拉取历史消息
                    if (null != jo.optJSONObject("chatRecord"))
                    {
                        JSONObject object =jo.optJSONObject("chatRecord");
                        mNowId = object.optString("_id");
                        mNextId = object.optString("next_id");

                        if(null != object.optJSONArray("records"))
                        {
                            JSONArray array =object.optJSONArray("records");
                            ArrayList<JSONObject> listMsges = new ArrayList<JSONObject>();
                            generateData(array, listMsges);

                            // for initial read, scroll to the bottom of the list view
                            boolean initial_read = (mMessAdapter.getCount() == 0);

                            if (null != mMessAdapter) {
                                if (array.length()==1){
                                    mMessAdapter.addNetMessage(listMsges);
                                }else{
                                    mMessAdapter.addHistory(listMsges);
                                }
                            }

                            if (initial_read) {
                                msgListview.setSelection(mMessAdapter.getCount() - 1);
                            }
                        }
                    }
                }

                if (jo.optString("obj").equals("person") && jo.optString("act").equals("chat_get"))
                {
                        JSONObject object =jo.optJSONObject("chatRecord");
                        mNowId = object.optString("_id");
                        mNextId = object.optString("next_id");

                        if(null != object.optJSONArray("records"))
                        {
                            JSONArray array =object.optJSONArray("records");
                            ArrayList<JSONObject> listMsges = new ArrayList<JSONObject>();
                            generateData(array, listMsges);

                            // for initial read, scroll to the bottom of the list view
                            boolean initial_read = (mMessAdapter.getCount() == 0);

                            if (null != mMessAdapter) {
                                if (array.length()==1){
                                    mMessAdapter.addNetMessage(listMsges);
                                }else{
                                    mMessAdapter.addHistory(listMsges);
                                }
                            }

                            if (initial_read) {
                                msgListview.setSelection(mMessAdapter.getCount() - 1);
                            }
			}
                }

                if (jo.optString("obj").equals("push") && 
                    (   jo.optString("act").equals("chat_topic") && mode.equals("topic") && jo.optString("topic_id").equals(mTopicId)
                     || jo.optString("act").equals("chat_task") && mode.equals("task") && jo.optString("task_id").equals(mTaskId)
                     || jo.optString("act").equals("chat_person") && mode.equals("person") && jo.optString("from_id").equals(mPersonId)
                    )) {
                    //群里新消息
                    ArrayList<JSONObject> listMsges = new ArrayList<JSONObject>();

                    String from_name = jo.optString("from_name");
                    int send_time = jo.optInt("chat_time");
                    String from_id = jo.optString("from_id");
                    String from_image = jo.optString("from_image");
//                            String state = jo.optString("topic_id");
                    String xtype = jo.optString("chat_type");

                    JSONObject netMessage1 = new JSONObject();
                    try {
                        if (from_id.equals(APIConnection.user_info.optString("_id"))) {//本人
                            netMessage1.put("type_tran", "SEND");
                        } else {
                            netMessage1.put("type_tran", "RECV");
                        }

                        if (xtype.equals("ximage")) {
                            netMessage1.put("content", jo.o("chat_content"));
                        } else {
                            netMessage1.put("content", jo.s("chat_content"));
                        }

                        netMessage1.put("from_name", from_name);
                        netMessage1.put("from_id", from_id);
                        netMessage1.put("from_image", from_image);
                        netMessage1.put("send_time", send_time);
                        netMessage1.put("xtype", xtype);
                        listMsges.add(netMessage1);
                    } catch (Exception e) {}

                    if (null != mMessAdapter) {
                        mMessAdapter.addNetMessage(listMsges);
                    }
//                    req.put("obj","topic");
//                    req.put("act","follow");
                } else if ((jo.optString("obj").equals("topic") || jo.optString("obj").equals("task")) && jo.optString("act").equals("follow")) {
                    ToastUtil.showShortToast(i052Chatactivity.this, "关注修改成功!");

                    if (!isfollowing) {
                        //guanzhu.setBackgroundResource(R.mipmap.icon_eye_gopen);
                    } else {
                        //guanzhu.setBackgroundResource(R.mipmap.icon_eye_gclose);
                    }

                    isfollowing = !isfollowing;
                }

                if ((jo.optString("obj").equals("topic") || jo.optString("obj").equals("task") || jo.optString("obj").equals("person")) 
		    && jo.optString("act").equals("chat_send") && jo.optString("status").equals("sucess")) {
                }


                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        }
    };

    private void generateData(JSONArray array, ArrayList<JSONObject> listMsges) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject jo = array.optJSONObject(i);

            String xtype = array.optJSONObject(i).optString("xtype");

            JSONObject netMessage1 = new JSONObject();
            try {
                if (jo.s("from_id").equals(APIConnection.user_info.optString("_id"))) {//本人
                    netMessage1.put("type_tran", "SEND");
                } else {
                    netMessage1.put("type_tran", "RECV");
                }

                if (xtype.equals("ximage")) {
                    netMessage1.put("content", jo.o("content"));
                } else {
                    netMessage1.put("content", jo.s("content"));
                }

                netMessage1.put("from_name", jo.s("from_name"));
                netMessage1.put("from_id", jo.s("from_id"));
                netMessage1.put("from_image", jo.s("from_image"));
                netMessage1.put("send_time", jo.i("send_time"));
                netMessage1.put("xtype", xtype);

                listMsges.add(netMessage1);
            } catch (Exception e) {}
        }
    }

    @Override
    public void onRefresh() {
        mSwipeView.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeView.setRefreshing(false);

                if (null == mNextId || "".equals(mNextId) || "0".equals(mNextId)) return;

                HashMap req = new HashMap();

                if (mode.equals("topic")) {
                    req.put("obj", "topic");
                }
                if (mode.equals("task")) {
                    req.put("obj", "task");
                }
                if (mode.equals("person")) {
                    req.put("obj", "person");
                }

                req.put("act", "chat_get");

                req.put("chatRecords_id", mNextId);

                APIConnection.send(req);
            }
        }, 1500);
    }

    @Override
    public void onSizeChanged(int height) {
        mHieght = height;
        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }
    }

    private Activity getActivity() {
        return this;
    }


    private boolean isRemoveMoreMenu = false;

    private void setVisibilityMoreMenu(boolean isShow) {
        mIsShowMoreMenu = isShow;
        if (isShow) {
            setMoreMenuHeight();
            mMoreMenuTl.setVisibility(View.VISIBLE);
            if (isRemoveMoreMenu) {
                mLinearChat.addView(mMoreMenuTl);
                isRemoveMoreMenu = false;
            }
        } else {
            mLinearChat.removeView(mMoreMenuTl);
            isRemoveMoreMenu = true;
        }
    }

    private boolean isKeyboardShow = false;
    private int mKeyboradHeigth;
    private boolean mIsNeedshowTab = false;

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom > bottom)) {
            isKeyboardShow = true;
            setVisibilityMoreMenu(false);
            mKeyboradHeigth = oldBottom - bottom;
        } else if (oldBottom != 0 && bottom != 0 && (bottom > oldBottom)) {
            isKeyboardShow = false;
            if (!mIsNeedshowTab) {
                setVisibilityMoreMenu(false);
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add_file_btn:
                //如果Tab已经打开，再次点击关闭
                mIsNeedshowTab = !mIsNeedshowTab;
                if (mIsShowMoreMenu) {
                    if (!isKeyboardShow) {
                        focusToInput(true);
                        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                } else {
                    setVisibilityMoreMenu(true);
                    if (isKeyboardShow) {
                        dismissSoftInput();
                        focusToInput(false);
                        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    }
                }

                /*if (mIsShowMoreMenu) {
                    if (mMoreMenuVisible) {
                        //focusToInput(true);
                        //showSoftInput();
                        mMoreMenuTl.setVisibility(View.GONE);
                        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        mMoreMenuVisible = false;
                    } else {
                        //dismissSoftInput();
                        //focusToInput(false);
                        mMoreMenuTl.setVisibility(View.VISIBLE);
                        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                        mMoreMenuVisible = true;
                    }
                } else {
                    //focusToInput(false);
                    //dismissSoftInput();
                    //setMoreMenuHeight();
                    mMoreMenuTl.setVisibility(View.VISIBLE);
                    //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    mIsShowMoreMenu = true;
                    mMoreMenuVisible = true;
                }*/
                break;
            case R.id.send_msg_btn:

                HashMap req = new HashMap();

                if (mode.equals("topic")) {
                    req.put("obj", "topic");
                    req.put("act", "chat_send");
                    req.put("topic_id", mTopicId);

                } else if (mode.equals("task")) {
                    req.put("obj", "task");
                    req.put("act", "chat_send");
                    req.put("task_id", mTaskId);

                } else if (mode.equals("person")) {
                    req.put("obj", "person");
                    req.put("act", "chat_send");
                    req.put("to_id", mPersonId);

                } else {
		}

                req.put("from_id", APIConnection.user_info.optString("_id"));
                // 通常还有用户在界面输入的其他数据，一起发送好了
                req.put("chat_type", "text");
                req.put("chat_content", mEditChatInput.getText().toString());
                APIConnection.send(req);

                JSONObject netMessage1 = new JSONObject();
                try {
                    netMessage1.put("type_tran", "SEND");
                    netMessage1.put("content", (mEditChatInput.getText().toString()));
                    netMessage1.put("send_time", ((int)(System.currentTimeMillis()/1000)));
                    netMessage1.put("xtype", "text");
                } catch (Exception e) {}

                if (null != mMessAdapter) {
                    mMessAdapter.addNetMessage(netMessage1);
                }
                mEditChatInput.setText("");
                break;
            case R.id.i040_back:
                finish();
                break;
            case R.id.img_photo:
                startActivityForResult(PhotoUtil.selectPhoto(), REQUEST_PHOTO);
                break;
            case R.id.img_camera:
//                Intent intent = new Intent(
//                        "android.media.action.IMAGE_CAPTURE");
//
//                tempFile = new File(getCacheDir()+"/"+getInstance().getTimeInMillis()
//                        + ".jpg");
//                imageUri = Uri.fromFile(tempFile);
                // 指定调用相机拍照后照片的储存路径
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                imageUri = PhotoUtil.getTempUri();
                startActivityForResult(PhotoUtil.takePicture(imageUri), REQUEST_CAMERA);
                //startActivityForResult(intent, REQUEST_CAMERA);
                break;
            //case R.id.img_card:
            //case R.id.img_map:
            //case R.id.img_video:
                //Toast.makeText(this,"该功能未开放",Toast.LENGTH_SHORT).show();
                //break;

            default:
                break;
        }
    }

    private int getisKeyboardHeight() {
        SharedPreferences share = getSharedPreferences("keyboardheight", Activity.MODE_PRIVATE);
        return share.getInt("height", 1000);
    }

    private void saveKeyboardHeight(int height) {
        SharedPreferences sharedPreferences = getSharedPreferences("keyboardheight", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("height", height);
        editor.apply();//提交修改
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveKeyboardHeight(mKeyboradHeigth);
    }

    CharSequence temp = "";

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (temp.length() > 0) {
            mBtnAddFile.setVisibility(View.GONE);
            mBtnSendMsg.setVisibility(View.VISIBLE);
        } else {
            mBtnAddFile.setVisibility(View.VISIBLE);
            mBtnSendMsg.setVisibility(View.GONE);
        }
    }

    public void setMoreMenuHeight() {
//        SharedPreferences sp   = this.getSharedPreferences("JPushDemo", 0);
//        int softKeyboardHeight = sp.getInt("SoftKeyboardHeight", 0);

//        Rect r = new Rect();
//        View.getWindowVisibleDisplayFrame(r);
//        int screenHeight = getRootView()
//                .getHeight();
//        int key=KeyboardView.EDGE_TOP-Keyboard.EDGE_BOTTOM;
//        int mm= InputMethodManager.RESULT_UNCHANGED_SHOWN;
//        Log.e("键盘高度", screenHeight + ":" + mm);
//        if(softKeyboardHeight > 0)
//        {
        // int hieght = dip2px(this, 270);
        mMoreMenuTl.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mKeyboradHeigth));
//        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void showSoftInput() {
        if (this.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null) {
                InputMethodManager imm = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
                imm.showSoftInputFromInputMethod(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void dismissSoftInput() {
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null)
//                imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void focusToInput(boolean inputFocus) {
        if (inputFocus) {
            mEditChatInput.requestFocus();
            Log.i("ChatView", "show softInput");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            mBtnAddFile.requestFocusFromTouch();
        }
    }

    private void follow(boolean is) {
//        {
//            "obj":"topic",
//                "act":"follow",
//                "unfollow":"1", // 如果设置了就取消关注
//                "topic_id":"o14477397324317851066"   //话题的id
//        }

        HashMap req = new HashMap();

        if (mode.equals("topic")) {
            req.put("obj", "topic");
            req.put("act", "follow");
            req.put("topic_id", mTopicId);
            req.put("unfollow", is ? "0" : "1");

        } else if (mode.equals("task")) {
            req.put("obj", "task");
            req.put("act", "follow");
            req.put("task_id", mTaskId);
            req.put("unfollow", is ? "0" : "1");

        } else if (mode.equals("person")) { // 对于私聊，这个加号是添加联系人
            req.put("obj", "person");
            req.put("act", "contact_add");
            req.put("person_id", mPersonId);

	} else {
		// FIXME
	} 

        APIConnection.send(req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK &&requestCode == REQUEST_PHOTO) {
            if (data != null) {
                // 得到图片的全路径
                //Uri uri = data.getData();
                //crop(uri, requestCode);
                upLoadImg(data);
            }
        } else if (resultCode == RESULT_OK &&requestCode == REQUEST_CAMERA) {
            //cropImageUri(imageUri);
            if(imageUri!=null) {
                data = new Intent();
                data.setData(imageUri);
                upLoadImg(data);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT_CIRCLE) {
            upLoadImg(data);
        }
    }




    private void upLoadImg(Intent data) {
        Toast.makeText(i052Chatactivity.this, "图片上传中", Toast.LENGTH_LONG).show();
        RequestParams rp = new RequestParams();
        if (data != null) {
            File f = null;
            f = PhotoUtil.saveImg(this,PhotoUtil.setPicToView(this,data),imageMaxSize,imageMaxWidth);


            Log.i("Test", "File is Not :" + (f.isFile() ? "yes" : "No"));
            rp.addBodyParameter("local_file", f);
            rp.addBodyParameter("proj", APIConnection.server_info.optString("proj"));
            HttpUtils h = new HttpUtils();

            h.send(HttpRequest.HttpMethod.POST, APIConnection.server_info.optString("upload_to"), rp, new RequestCallBack<Object>() {
                @Override
                public void onSuccess(ResponseInfo<Object> responseInfo) {
                    try {
                        JSONObject jb = new JSONObject(responseInfo.result.toString());

                        String fid = jb.s("fid");
                        String thumb = jb.s("thumb");
                        String mime = jb.s("type");

                        if (jb.optString("fid") != null && !jb.optString("fid").equals("")) {
                            Toast.makeText(i052Chatactivity.this, "图片上传成功", Toast.LENGTH_LONG).show();
                            HashMap req = new HashMap();

                            if (mode.equals("topic")) {
                                req.put("obj", "topic");
                                req.put("act", "chat_send");
                                req.put("topic_id", mTopicId);

                            } else if (mode.equals("task")) {
                                req.put("obj", "task");
                                req.put("act", "chat_send");
                                req.put("task_id", mTaskId);

                            } else if (mode.equals("person")) { // 对于私聊
                                req.put("obj", "person");
                                req.put("act", "chat_send");
                                req.put("to_id", mPersonId);

                            } else {
                                //FIXME
                            }

                            JSONObject chat_content = new JSONObject();
                            chat_content.put("thumb", thumb);
                            chat_content.put("fid", fid);
                            chat_content.put("mime", mime);

                            req.put("from_id", APIConnection.user_info.optString("_id"));
                            req.put("chat_type", "ximage");
                            req.put("chat_content", chat_content);
                            APIConnection.send(req);

                            JSONObject netMessage1 = new JSONObject();
                            netMessage1.put("type_tran", "SEND");
                            netMessage1.put("content", chat_content);
                            netMessage1.put("send_time", (int) (System.currentTimeMillis() / 1000));
                            netMessage1.put("xtype", "ximage");
                            if (null != mMessAdapter) {
                                mMessAdapter.addNetMessage(netMessage1);
                            }
//                                updateInfo();
                        }
                        // 将临时文件删除
                        tempFile.delete();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("Test", s);
                    Toast.makeText(i052Chatactivity.this, "图片上传错误", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}