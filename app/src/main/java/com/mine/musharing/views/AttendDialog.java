package com.mine.musharing.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.models.MuTextCode;
import com.mine.musharing.models.User;
import com.mine.musharing.requestTasks.AttendTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.ClipboardUtil;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.UserUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * AttendDialog 处理添加朋友的对话框
 *
 * 二维码生成、扫一扫组件：https://github.com/yuzhiqiang1993/zxing
 */
public class AttendDialog extends Dialog {
    public final static int REQUEST_CODE_SCAN = 123;

    private Context context;

    private RoomFragment parentRoomFragment;

    private ClipboardManager clipboard;

    private User user;

    public AttendDialog(Context context, RoomFragment parent, User user) {
        super(context);

        parentRoomFragment = parent;

        this.user = user;

        clipboard = (ClipboardManager)
                parentRoomFragment.getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);

        this.setTitle("添加朋友");

        init(context);
    }

    private void init(Context context) {
        this.context = context;
        build();

        checkClipMuCode();
    }

    private void build() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.append_dialog, null);

        // 搜索
        SearchView friendSearchView = view.findViewById(R.id.friend_search_view);

        // SearchView 默认弹出软键盘，但想默认隐藏解决方法
        // From https://github.com/chufengma/android-skills/issues/23
        friendSearchView.post(new Runnable() {
            @Override
            public void run() {
                friendSearchView.clearFocus();
            }
        });

        friendSearchView.setIconified(false);
        friendSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // 我的Mu口令
        ((LinearLayout) view.findViewById(R.id.my_text_code)).setOnClickListener(v -> myTextCode());

        // 我的二维码
        ((LinearLayout) view.findViewById(R.id.my_qr)).setOnClickListener(v -> myQrCode());

        // 扫一扫
        ((RelativeLayout) view.findViewById(R.id.scan_qr)).setOnClickListener(v -> scanQr());

        // 添加机器人
        ((RelativeLayout) view.findViewById(R.id.attend_chatbot)).setOnClickListener(v -> attendChatbot());

        // 取消
        ((Button) view.findViewById(R.id.cancel_append_button)).setOnClickListener(v -> dismiss());

        setContentView(view);
    }

    /**
     * 检查是否复制了 Mu 口令
     */
    private void checkClipMuCode() {
        String s = ClipboardUtil.getFromClipboard(clipboard);
        String t = MuTextCode.parse(s);
        if (!t.isEmpty()) {
            Toast.makeText(context, "发现了剪贴板中的Mu口令\n在搜索栏中粘贴即可添加", Toast.LENGTH_SHORT).show();
            // attend(t);
        }
    }

    /**
     * 点击搜索，或处理扫描二维码响应事件
     *
     * @param s 搜索文本: Mu 口令 或 Musharing 用户名
     */
    public void search(String s) {
        String t = MuTextCode.parse(s);
        if (!t.isEmpty()) {     // 是 Mu 口令
            attend(t);
        } else {    // 不是 Mu 口令，搜索 Musharing 用户名
            attend(s);
        }
    }

    /**
     * 点击我的Mu口令
     */
    private void myTextCode() {
        String code = MuTextCode.make(user);

        if (ClipboardUtil.setToClipboard(clipboard, code)) {
            Toast.makeText(context, "我的Mu口令已复制到系统剪贴板", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "我的Mu口令复制失败", Toast.LENGTH_SHORT).show();
        }

        final TextView textView = new TextView(context);
        textView.setText(code);
        textView.setTextIsSelectable(true);
        textView.setPadding(16, 8, 16, 8);

        final AlertDialog.Builder muCodeDialog =
                new AlertDialog.Builder(context);
        muCodeDialog.setTitle("我的Mu口令")
                .setView(textView)
                .setIcon(R.drawable.ic_kou_ling)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do Nothing
                            }
                        });

        // 显示
        muCodeDialog.show();
        // dismiss();
    }

    /**
     * 点击我的二维码
     */
    private void myQrCode() {
        String code = MuTextCode.make(user);

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap = CodeCreator.createQRCode(code, 400, 400, logo);

        // TODO: Implement View in a layout xml.

        final ImageView qrImageView = new ImageView(context);
        qrImageView.setMinimumWidth(400);
        qrImageView.setMinimumHeight(400);
        qrImageView.setMaxWidth(400);
        qrImageView.setMaxHeight(400);
        Glide.with(context).load(bitmap).into(qrImageView);

        final TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setWidth(400);
        textView.setText(String.format("扫一扫，加入%s的房间", user.getName()));

        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(16, 16, 16, 16);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(qrImageView);
        linearLayout.addView(textView);

        final AlertDialog.Builder qrDialog =
                new AlertDialog.Builder(context);
        qrDialog.setTitle("我的二维码")
                .setView(linearLayout)
                .setIcon(R.drawable.qr_code_line)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do Nothing
                            }
                        });

        // 显示
        qrDialog.show();

        // dismiss();
    }


    /**
     * 点击扫一扫
     */
    private void scanQr() {
        Intent intent = new Intent(context, CaptureActivity.class);
        parentRoomFragment.startActivityForResult(intent, REQUEST_CODE_SCAN);
        // dismiss();
    }

    /**
     * 点击添加机器人
     */
    private void attendChatbot() {
        attend("chatbot");
    }


    /**
     * 弹出对话框，向用户确认是否添加，是则调用 attendRequest 完成添加动作，否则取消。
     *
     * @param targetName 目标用户名
     */
    private void attend(String targetName) {
        final AlertDialog.Builder attendDialog =
                new AlertDialog.Builder(context);
        attendDialog.setTitle("添加朋友");
        attendDialog.setMessage("确定添加 " + targetName + " 进入房间吗？");
        attendDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attendRequest(user.getUid(), targetName);
                    }
                });
        attendDialog.setNegativeButton("取消",
                (dialog, which) -> Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show());
        // 显示
        attendDialog.show();
    }

    /**
     * 发送添加人的请求
     *
     * @param uid        当前用户 Uid
     * @param targetName 目标用户名
     */
    private void attendRequest(String uid, String targetName) {
        String targetNameEncoded = UserUtil.encodeName(targetName);

        new AttendTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {
                // progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String s) {
                parentRoomFragment.refreshMemberList();
            }

            @Override
            public void onFailed(String error) {
                parentRoomFragment.getActivity().runOnUiThread(() -> {
                    String readableError;
                    switch (error) {
                        case ParseUtil.ResponseError.TARGET_IN_ROOM:
                            readableError = "你的朋友正在别的房间中，不可以打扰ta哦。";
                            break;
                        case ParseUtil.ResponseError.TARGET_NOT_LOGIN:
                            readableError = "你的朋友没有登录，联系不上ta。";
                            break;
                        case ParseUtil.ResponseError.TARGET_NOT_EXIST:
                            readableError = "在 Musharing 家族中没有这个人。。。";
                            break;
                        case ParseUtil.ResponseError.FROM_NOT_LOGIN:
                            readableError = "请先登录。";
                            break;
                        case ParseUtil.ResponseError.FROM_NOT_EXIST:
                            readableError = "错误！发起用户不存在。";
                            break;
                        default:
                            readableError = "出错啦，请稍后再试TAT";
                    }
                    Toast.makeText(context, readableError, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                dismiss();
            }
        }).execute(uid, targetNameEncoded);
    }
}
