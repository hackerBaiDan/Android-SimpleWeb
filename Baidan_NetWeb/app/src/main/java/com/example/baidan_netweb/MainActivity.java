package com.example.baidan_netweb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //初始化控件
    private WebView webView;
    private ProgressBar progressBar;
    private EditText textUrl;
    private ImageView webIcon,goBack,goForward,navSet,goHome,btnStart;
    private Context mContext;
    private InputMethodManager manager;


    //初始化HTTP
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreWork();
        ViewClick();
        NetWeb();
    }

    //Webview 基础功能
    private void NetWeb() {
        // 重写 WebViewClient
        webView.setWebViewClient(new MkWebViewClient());
        // 重写 WebChromeClient
        webView.setWebChromeClient(new MkWebChromeClient());

        WebSettings settings = webView.getSettings();
        // 启用 js 功能
        settings.setJavaScriptEnabled(true);
        // 将图片调整到适合 WebView 的大小
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        // 支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该 WebView 不可缩放
        settings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);
        // 缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 设置默认编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 本地存储
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        // 资源混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 加载首页
        webView.loadUrl(getResources().getString(R.string.home_url));
    }

    private void PreWork(){
        // 防止底部按钮上移
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    //按钮功能
    private void ViewClick() {
        // 寻找页面控件
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        textUrl = findViewById(R.id.textUrl);
        webIcon = findViewById(R.id.webIcon);
        goBack = findViewById(R.id.goBack);
        goForward = findViewById(R.id.goForward);
        navSet = findViewById(R.id.navSet);
        goHome = findViewById(R.id.goHome);
        btnStart = findViewById(R.id.btnStart);

        //点击退后
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.goBack();
            }
        });

        //点击主页
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //webView.loadUrl("https://www.baidu.com");
                webView.loadUrl(getResources().getString(R.string.home_url));
            }
        });

        //点击前进
        goForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.goForward();
            }
        });

        //点击设置
        navSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "功能开发中", Toast.LENGTH_SHORT).show();
            }
        });

        //点击刷新或跳转
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textUrl.hasFocus()) {
                    // 隐藏软键盘
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(textUrl.getApplicationWindowToken(), 0);
                    }

                    // 地址栏有焦点，是跳转
                    String input = textUrl.getText().toString();
                    if (!isHttpUrl(input)) {
                        // 不是网址，加载搜索引擎处理
                        try {
                            // URL 编码
                            input = URLEncoder.encode(input, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        input = "https://www.baidu.com/s?wd=" + input + "&ie=UTF-8";
                    }
                    webView.loadUrl(input);

                    // 取消掉地址栏的焦点
                    textUrl.clearFocus();
                } else {
                    // 地址栏没焦点，是刷新
                    webView.reload();
                }
            }
        });

        // 地址输入栏获取与失去焦点处理
        textUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 显示当前网址链接 TODO:搜索页面显示搜索词
                    textUrl.setText(webView.getUrl());
                    // 光标置于末尾
                    textUrl.setSelection(textUrl.getText().length());
                    // 显示因特网图标
                    webIcon.setImageResource(R.drawable.internet);
                    // 显示跳转按钮
                    btnStart.setImageResource(R.drawable.go);
                } else {
                    // 显示网站名
                    textUrl.setText(webView.getTitle());
                    // 显示网站图标
                    webIcon.setImageBitmap(webView.getFavicon());
                    // 显示刷新按钮
                    btnStart.setImageResource(R.drawable.refresh);
                }
            }
        });

        // 监听键盘回车搜索
        textUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行搜索
                    btnStart.callOnClick();
                    textUrl.clearFocus();
                }
                return false;
            }
        });
    }

    //重写 WebViewClient
    private class MkWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中

            if (url == null) {
                // 返回true自己处理，返回false不处理
                return true;
            }

            // 正常的内容，打开
            if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
                view.loadUrl(url);
                return true;
            }

            // 调用第三方应用，防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
            try {
                // TODO:弹窗提示用户，允许后再调用
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 网页开始加载，显示进度条
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);

            // 更新状态文字
            textUrl.setText("加载中...");

            // 切换默认网页图标
            webIcon.setImageResource(R.drawable.internet);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 网页加载完毕，隐藏进度条
            progressBar.setVisibility(View.INVISIBLE);

            // 改变标题
            setTitle(webView.getTitle());
            // 显示页面标题
            textUrl.setText(webView.getTitle());
        }
    }

    /**
     * 判断字符串是否为URL（https://blog.csdn.net/bronna/article/details/77529145）
     *
     * @param urls 要勘定的字符串
     * @return true:是URL、false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        boolean isUrl;
        // 判断是否是网址的正则表达式
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        isUrl = mat.matches();
        return isUrl;
    }

    //重写 WebChromeClient
    private class MkWebChromeClient extends WebChromeClient {
        private final static int WEB_PROGRESS_MAX = 100;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            // 加载进度变动，刷新进度条
            progressBar.setProgress(newProgress);
            if (newProgress > 0) {
                if (newProgress == WEB_PROGRESS_MAX) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);

            // 改变图标
            webIcon.setImageBitmap(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            // 改变标题
            setTitle(title);
            // 显示页面标题
            textUrl.setText(title);
        }
    }
}