package com.example.fakenamegenerator;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FloatingWindowService extends Service {
    private WindowManager windowManager;
    private ImageView floatingButton;
    private LinearLayout floatingMenu;
    private ImageView copyButton;
    private ImageView shareButton;
    private ImageView refreshButton;
    private ImageView closeButton;
    private boolean isMenuOpen = false;
    private String currentGeneratedName;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // প্রধান ফ্লোটিং বাটন তৈরি করা
        floatingButton = new ImageView(this);
        floatingButton.setImageResource(R.drawable.ic_add_icon);
        floatingButton.setTag("floating_button");

        // ফ্লোটিং মেনু কন্টেইনার তৈরি করা
        floatingMenu = new LinearLayout(this);
        floatingMenu.setOrientation(LinearLayout.VERTICAL);
        floatingMenu.setTag("floating_menu");

        // মেনু আইটেম বাটন তৈরি করা
        copyButton = createMenuButton("Copy");
        shareButton = createMenuButton("Share");
        refreshButton = createMenuButton("Refresh");
        closeButton = createMenuButton("Close");

        floatingMenu.addView(copyButton);
        floatingMenu.addView(shareButton);
        floatingMenu.addView(refreshButton);
        floatingMenu.addView(closeButton);

        // উইন্ডো ম্যানেজার সেটিংস
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // প্রধান বাটনের পজিশন
        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        params.x = 30;
        params.y = 200;

        // প্রধান বাটনে ক্লিক করলে মেনু খুলবে
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen) {
                    windowManager.removeView(floatingMenu);
                    isMenuOpen = false;
                } else {
                    // নাম জেনারেট করা
                    currentGeneratedName = generateUSName();
                    
                    // মেনু প্যারামিটার সেট করা
                    WindowManager.LayoutParams menuParams = new WindowManager.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            layoutFlag,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT
                    );
                    menuParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                    menuParams.x = 30;
                    menuParams.y = 300;
                    
                    windowManager.addView(floatingMenu, menuParams);
                    isMenuOpen = true;
                }
            }
        });

        // কপি বাটনে ক্লিক
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Name", currentGeneratedName);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(FloatingWindowService.this, "Copied: " + currentGeneratedName, Toast.LENGTH_SHORT).show();
                closeMenu();
            }
        });

        // শেয়ার বাটনে ক্লিক
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Generated Name: " + currentGeneratedName);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
                closeMenu();
            }
        });

        // রিফ্রেশ বাটনে ক্লিক
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGeneratedName = generateUSName();
                Toast.makeText(FloatingWindowService.this, "New Name: " + currentGeneratedName, Toast.LENGTH_SHORT).show();
            }
        });

        // ক্লোজ বাটনে ক্লিক
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
                stopSelf();
            }
        });

        // প্রধান বাটন যুক্ত করা
        windowManager.addView(floatingButton, params);
    }

    private ImageView createMenuButton(String label) {
        ImageView button = new ImageView(this);
        button.setContentDescription(label);
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        
        // বিভিন্ন বাটনের জন্য বিভিন্ন আইকন
        if (label.equals("Copy")) {
            button.setImageResource(R.drawable.ic_copy);
        } else if (label.equals("Share")) {
            button.setImageResource(R.drawable.ic_share);
        } else if (label.equals("Refresh")) {
            button.setImageResource(R.drawable.ic_refresh);
        } else if (label.equals("Close")) {
            button.setImageResource(R.drawable.ic_close);
        }
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                80, 80
        );
        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
        
        return button;
    }

    private void closeMenu() {
        if (isMenuOpen) {
            windowManager.removeView(floatingMenu);
            isMenuOpen = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingButton != null) windowManager.removeView(floatingButton);
        if (floatingMenu != null && isMenuOpen) windowManager.removeView(floatingMenu);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // নাম জেনারেট করার মেথড
    private String generateUSName() {
        String[] first = {"Emma", "Olivia", "Ava", "Sophia", "Mia", "James", "Robert", "Michael", "William", "David"};
        String[] last = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
        java.util.Random r = new java.util.Random();
        return first[r.nextInt(first.length)] + " " + last[r.nextInt(last.length)];
    }
}
