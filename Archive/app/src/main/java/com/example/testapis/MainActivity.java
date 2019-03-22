package com.example.testapis;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public final static int DATA_NOMAL = 0;
    public final static int DATA_ERROR = -1;

    ListView listView;
    Spinner spinner;
    ProgressBar progressBar;

    private Gson gson;
    private MyAdapter adapter;
    private List<DataBean.SubjectsBean> list = new ArrayList<>();
    private DataBean dataBean;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            switch (msg.what){
                case DATA_NOMAL://数据正常
                    String jsonStr = (String) msg.obj;
                    dataBean = gson.fromJson(jsonStr,DataBean.class);
                    list.addAll(dataBean.getSubjects());
                    Set<String> strings = new HashSet<>();
                    for (int i = 0; i < list.size(); i++) {//去重
                        strings.add(list.get(i).getGenres().get(0));
                    }
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, new ArrayList<>(strings));
                    spinner.setAdapter(spinnerAdapter);
                    adapter = new MyAdapter(MainActivity.this,list);
                    listView.setAdapter(adapter);
                    break;
                case DATA_ERROR://异常数据
                    Toast.makeText(MainActivity.this,"error data,try agin",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        gson = new Gson();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) parent.getItemAtPosition(position);
                list.clear();
                for (int i = 0; i < dataBean.getSubjects().size(); i++) {
                    if(dataBean.getSubjects().get(i).getGenres().get(0).equals(key)){
                        list.add(dataBean.getSubjects().get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initView() {
        listView = findViewById(R.id.lisview);
        spinner = findViewById(R.id.spinner1);
        progressBar = findViewById(R.id.progressbar);
    }

    public void onRequestProducts(View view) {
        doingToThread("https://api.douban.com/v2/movie/in_theaters");//测试路径
    }

    /**
     * 在子线程中进行耗时的网络请求
     * @param url
     */
    private void doingToThread(final String url){
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNetData(url);
            }
        }).start();
    }

    /**
     * 根据url进行网络请求
     * @param urlStr
     */
    private void getNetData(String urlStr){
        Message message = new Message();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-api-version","2");
            connection.connect();
            int code = connection.getResponseCode();
            StringBuilder msg = new StringBuilder();
            if (code == 200) { // 正常响应
                BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) { // 循环从流中读取
//                    msg += line + "\n";
                    msg.append(line + "\n");
                }
                reader.close(); // 关闭流
            }
            connection.disconnect();//断开连接，释放资源
            //交给handle处理
            message.what = DATA_NOMAL;
            message.obj = msg.toString();
            handler.sendMessage(message);
        }catch (Exception e){
            message.what = DATA_ERROR;
            message.obj = e;
            handler.sendMessage(message);
        }

    }
}
