package com.example.ledswitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 启动HTTP服务
        startService(new Intent(this, LightHttpService.class));
        
        // 显示服务状态
        TextView statusText = findViewById(R.id.statusText);
        statusText.setText("HTTP Service running on port 8080\n\n" +
                         "Available APIs:\n" +
                         "http://<device-ip>:8080/api/green\n" +
                         "http://<device-ip>:8080/api/red");
    }
} 