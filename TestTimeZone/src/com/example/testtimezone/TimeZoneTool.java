package com.example.testtimezone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 根据教育机的方法创建
 * @author Administrator
 *
 */
public class TimeZoneTool {
    
    private String TAG = "TimeZoneTool";
    private String websizehead1 = "http://api.ipinfodb.com/v3/ip-city/?key=076b47ff1eefd89fde7cfc1409d808f48949e1eb0f2eb08e66786358d207e81d&ip=";
    private String ip;
    private Context mContext;

    
    public TimeZoneTool(Context c){
        mContext = c;
    }
    
    public void setCurrentTimezoneByNet(){
        new SyncTimezone().start();
    }
    
    public String  getTimezone(){
        Calendar now = Calendar.getInstance();
        String timezone = getTimeZoneText(now.getTimeZone());
        return timezone;
    }

    
    private class SyncTimezone extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
            getMyIP();
        }
    }
    
    
    /*
     * 获取公网IP
     * */
    private String getMyIP() {  
        Log.d(TAG, "------------getMyIP()------------");
        InputStream ins = null;  

        try {  
            URL url = new URL("http://1212.ip138.com/ic.asp");  
            URLConnection con = url.openConnection();  
            ins = con.getInputStream();  
            InputStreamReader isReader = new InputStreamReader(ins, "GB2312");  
            BufferedReader bReader = new BufferedReader(isReader);  
            StringBuffer webContent = new StringBuffer();  
            String str = null;  
            while ((str = bReader.readLine()) != null) {  
                webContent.append(str);  
            }  
            Log.v(TAG, "webContent=="+webContent);
            int start = webContent.indexOf("[") + 1;  
            int end = webContent.indexOf("]");  
            ip = webContent.substring(start, end);
            getTimeZone.sendEmptyMessage(0);
            /*
            Intent intent = new Intent();
            intent.setAction(GETTIMEZONE);
            mContext.sendBroadcast(intent);
            */
            return   ip;
        } catch (Exception e) {  
            e.printStackTrace(); 
          
        } finally {  
            if (ins != null) {  
                try {  
                    ins.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return "";  
    }
    
    private  void getTimezoneByNet(final String ip){
        String websize = websizehead1 + ip;
        Log.d(TAG, "websize = "+websize);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(websize, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // TODO Auto-generated method stub
                String responce = new String(bytes);
                Log.d(TAG, "+++++++++"+responce);
                String info[] = responce.split(";");
                Log.d(TAG, "+++++++++"+info[10]);
                Log.d(TAG, "---------------onSuccess------------------------");
                getGMTFormat1(info[10]);
                /*JSONObject object = null;
                try {
                    object = new JSONObject(responce);
                    String status = object.getString("timezone");
                    Log.d("TAG", "+++++++++"+status);
                    getGMTFormat(status);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable e) {
                // TODO Auto-generated method stub
                e.printStackTrace();
                getTimeZone.sendEmptyMessage(0);
                Log.d(TAG, "---------------failure------------------------");
            }
        });

    }
    
   private Handler getTimeZone = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getTimezoneByNet(ip);
        }
        
    };
    
    private void getGMTFormat1(String GMT){
        String str = "GMT"+GMT;
        Log.v(TAG, str);
        final AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(str);
        
        
//      alarm.setTimeZone(str);
        String s = getTimezone();
        Log.v(TAG, "relut--"+s);
    }

    
    private static String getTimeZoneText(TimeZone tz) {
        SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ");
        sdf.setTimeZone(tz);
        return sdf.format(new Date());
    }
    

}
