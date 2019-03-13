package com.example.wayto.sample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    public RecyclerView recyclerReminder;
    public static AdapterReminder myAdapter;
    static ArrayList<DataReminder> mArrayList;
    private TextView mtxtrecords;
    private ProgressDialog dialog;
    private DataReminder dataReminder;
    private Handler notifyHandler = new Handler();
    Thread t1, t2;
    String clientid,counsellorid,clienturl,totalcoins;
    int TotalReminderMatch = 0;
    int TotalReminderNotMatch = 0;
    AlertDialog.Builder builder;
    DialogInterface dialogInterface;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ImageView imgBack,imgCoin;
    TextView txtCoin,txtDiamond;
    UrlRequest urlRequest;



    class MyJSON implements Runnable {
        @Override
        public void run() {
            try {
                String url = "http://anilsahasrabuddhe.in/CRM/AnDe828500/cases.php?clientid=" + clientid + "&caseid=33&nCounsellorId="+counsellorid;
                Log.d("ReminderUrl",url);
                while (true) {
                    RequestQueue requestQueue = Volley.newRequestQueue(ReminderActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                            null, new success(), new fail());
                    requestQueue.add(jsonObjectRequest);
                    t1.sleep(10000);

                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        sp = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        editor=sp.edit();
        clientid=sp.getString("ClientId",null);
        counsellorid=sp.getString("Id",null);
        clienturl=sp.getString("ClientUrl",null);
        totalcoins=sp.getString("TotalCoin",null);
        recyclerReminder = findViewById(R.id.recycleReminder);
        imgBack=findViewById(R.id.img_back);
        txtCoin=findViewById(R.id.txtCoin);
        imgCoin=findViewById(R.id.imgCoin);
        txtDiamond=findViewById(R.id.txtDiamond);
        txtCoin.setText(totalcoins);
        imgCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReminderActivity.this,PointCollectionDetails.class));
            }
        });

      //  mtxtrecords = findViewById(R.id.total_rec);

            counsellorid=counsellorid.replace(" ","");
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setMessage("Wait while loading...");
        dialog.show();

        Runnable mthread1 = new MyJSON();
        //  Runnable mthread2 = new compareDate();

        t1 = new Thread(mthread1);
        //  t2 = new Thread(mthread2);

        t1.start();
      imgBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onBackPressed();
          }
      });
    }

    private class success implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            Log.e("Success", String.valueOf(response));
            try {
                mArrayList = new ArrayList<>();
                TotalReminderMatch=0;
                TotalReminderNotMatch=0;
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String cId = jsonObject.getString("SR NO");
                    String cName = jsonObject.getString("NAME");
                    String cCourse = jsonObject.getString("COURSE");
                    String cMobile1 = jsonObject.getString("MOBILE");
                    String cMobile2 = jsonObject.getString("MOBILE2");
                    String cRemarks = jsonObject.getString("REMARKS");

                    JSONObject mjsonCallDate = jsonObject.getJSONObject("CALL DATE");
                    String cDate = mjsonCallDate.getString("date");

                    String cTime = jsonObject.getString("CALL TIME");
                    String callId=jsonObject.getString("CallingId");

                    dataReminder = new DataReminder(cId, cName, cCourse, cMobile1, cMobile2, cRemarks, cDate, cTime,callId);
                    mArrayList.add(dataReminder);

                 //   mtxtrecords.setText(mArrayList.size() + "");
                    dialog.dismiss();
                }

                myAdapter = new AdapterReminder(ReminderActivity.this,mArrayList);
                LinearLayoutManager lm = new LinearLayoutManager(ReminderActivity.this);
                lm.setOrientation(LinearLayoutManager.VERTICAL);

                recyclerReminder.addItemDecoration(new DividerItemDecoration(ReminderActivity.this, DividerItemDecoration.VERTICAL));
                recyclerReminder.setLayoutManager(lm);
                recyclerReminder.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();


                int extramin = 30;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, extramin);
                String date1 = sdf.format(cal.getTime());

                for (int i = 0; i <mArrayList.size(); i++) {
                    String mdate = mArrayList.get(i).getmDate().substring(0, 10);
                    String mtime = mArrayList.get(i).getmTime();

                    if (mtime.length() == 9) {
                        mtime = "0" + mtime.replace(".", "");
                    } else {
                        mtime = mtime.replace(".", "");
                    }
                    String DBDateFormat = mdate + " " + mtime;
                    Log.d("DBDATE=====", DBDateFormat);
                    Log.d("current date=====", date1);

                    if (date1.compareTo(DBDateFormat) < 0) {
                        TotalReminderMatch++;
                        Log.d("match date========", TotalReminderMatch + "");
                    } else {
                        TotalReminderNotMatch++;
                        Log.d("not match date========", TotalReminderNotMatch + "");
                    }
                }

                notifyHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        //Define sound URI
                        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Call Details")
                                .setContentText("Calls Arriving / Pending => " + TotalReminderMatch +" / "+ TotalReminderNotMatch)
                                .setWhen(System.currentTimeMillis())
                                .setSound(soundUri)
                                .setVibrate(new long[]{1000,1000,1000,1000,1000}); //This sets the sound to play

                        Intent intent = new Intent(ReminderActivity.this, ReminderActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(ReminderActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent);

                        //Display notification
                        notificationManager.notify(0, mBuilder.build());
                    }
                });

               /* alertHandler.post(new Runnable() {
                    @Override
                    public void run() {
                         builder = new AlertDialog.Builder(ReminderActivity.this);
                        builder.setTitle("Time Compare")
                                .setMessage("Calls Arriving => " + TotalReminderMatch + "\n" +
                                        "Calls Pending =>" + TotalReminderNotMatch)
                                .setCancelable(false)
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       dialogInterface=dialog;
                                        dialog.dismiss();
                                    }
                                });
                        if(!((ReminderActivity.this).isFinishing())) {
                            //show dialog
                            builder.create().show();
                        }
                    }
                });*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class fail implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("Fail", String.valueOf(error));
        }
    }

   /* public void insertPointCollection(int eid)
    {
        urlRequest = UrlRequest.getObject();
        urlRequest.setContext(getApplicationContext());
        urlRequest.setUrl(clienturl+"?clientid=" + clientid + "&caseid=36&nCounsellorId=" + counsellorid + "&nEventId="+eid);
        Log.d("PointCollectionResponse", clienturl+"?clientid=" + clientid + "&caseid=36&nCounsellorId=" + counsellorid + "&nEventId="+eid);
        urlRequest.getResponse(new ServerCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.d("PointCollectionResponse", response);
                if (response.contains("Data inserted successfully")) {
                    Toast.makeText(ReminderActivity.this, "Added point for reminder",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReminderActivity.this, "Point not added for reminder", Toast.LENGTH_SHORT).show();
                }
                //   Log.d("Size**", String.valueOf(arrayList.size()));
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ReminderActivity.this,Home.class));
        finish();
    }


}

