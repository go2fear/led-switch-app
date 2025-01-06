package com.example.ledswitch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.ys.serialport.LightController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import androidx.core.app.NotificationCompat;

public class LightHttpService extends Service {
    private static final String TAG = "LightHttpService";
    private static final int PORT = 8080;
    private boolean isRunning = true;
    private ServerSocket serverSocket;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 创建通知渠道（Android 8.0及以上需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                "LED_SERVICE",
                "LED Control Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // 创建前台服务通知
        Notification notification = new NotificationCompat.Builder(this, "LED_SERVICE")
            .setContentTitle("LED Control Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build();

        // 启动前台服务
        startForeground(1, notification);

        // 初始化串口
        LightController.getInstance().openDevice(this, "/dev/ttyS3", 9600);
        
        // 启动HTTP服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                startHttpServer();
            }
        }).start();
    }

    private void startHttpServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            Log.i(TAG, "HTTP Server started on port " + PORT);
            
            while (isRunning) {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error starting server: " + e.getMessage());
        }
    }

    private void handleRequest(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream input = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead = input.read(buffer);
                    String request = new String(buffer, 0, bytesRead);
                    
                    // 解析请求
                    String firstLine = request.split("\n")[0];
                    String path = firstLine.split(" ")[1];
                    
                    // 处理请求
                    String response;
                    if (path.equals("/api/green")) {
                        setGreenColor();
                        response = "HTTP/1.1 200 OK\r\n\r\nGreen light set";
                    } else if (path.equals("/api/red")) {
                        setRedColor();
                        response = "HTTP/1.1 200 OK\r\n\r\nRed light set";
                    } else {
                        response = "HTTP/1.1 404 Not Found\r\n\r\nInvalid path";
                    }
                    
                    // 发送响应
                    OutputStream output = socket.getOutputStream();
                    output.write(response.getBytes());
                    
                    // 关闭连接
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error handling request: " + e.getMessage());
                }
            }
        }).start();
    }
    
    private void setGreenColor() {
        LightController.getInstance().keepMode(LightController.Led.GREEN, 0, 255);
        LightController.getInstance().keepMode(LightController.Led.RED, 0, 0);
        LightController.getInstance().keepMode(LightController.Led.BLUE, 0, 0);
    }
    
    private void setRedColor() {
        LightController.getInstance().keepMode(LightController.Led.RED, 0, 255);
        LightController.getInstance().keepMode(LightController.Led.GREEN, 0, 0);
        LightController.getInstance().keepMode(LightController.Led.BLUE, 0, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing server: " + e.getMessage());
        }
        LightController.getInstance().close();
    }
} 