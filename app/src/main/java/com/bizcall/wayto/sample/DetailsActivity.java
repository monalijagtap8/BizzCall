package com.bizcall.wayto.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DetailsActivity extends AppCompatActivity {


    Button btnCancel;
    Spinner spinnerState1;
    ImageView imgBack,imgRefresh;
    int temp = 0;
    String clienturl,clientid;
    String mbl, parentno, name, course, sr_no, email, allocatedDate, adrs, city, state1, pincode, statusid, remark, status11,counselorid;
    SharedPreferences sp;
    UrlRequest urlRequest;
    ProgressDialog progressDialog;
    TextView txtSrno ,txtCourse,txtMobile,txtName, txtAddress,txtCity,txtState,txtPincode,txtEmail,txtParent;
    Button btnEdit,btnOk;
    ProgressDialog dialog;
    String url;
    RequestQueue requestQueue;
    Vibrator vibrator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detalis);
        try{
            vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        requestQueue=Volley.newRequestQueue(DetailsActivity.this);
        imgBack=findViewById(R.id.img_back);
        imgRefresh=findViewById(R.id.imgRefresh);
        txtSrno = findViewById(R.id.txtSrno11);
        txtCourse = findViewById(R.id.txtCourse11);
        txtMobile = findViewById(R.id.txtPhone11);
        txtName = findViewById(R.id.txtName11);
        txtAddress = findViewById(R.id.txtAddress11);
        txtCity = findViewById(R.id.txtCity11);
        txtState = findViewById(R.id.txtState11);
        txtPincode = findViewById(R.id.txtPinCode11);
        txtEmail = findViewById(R.id.txtEmail11);
        txtParent = findViewById(R.id.txtParentNo11);
        btnEdit = findViewById(R.id.btnEditDetails);
        btnOk = findViewById(R.id.btnOk11);

        sp = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        clientid=sp.getString("ClientId",null);
        clienturl=sp.getString("ClientUrl",null);
        sr_no = sp.getString("SelectedSrNo", null);
        counselorid = sp.getString("Id", null);
        counselorid=counselorid.replace(" ","");
        dialog = ProgressDialog.show(DetailsActivity.this, "", "Loading counselor information...", true);
        getCounselorData(sr_no,counselorid);
        //refreshWhenLoading();

            imgRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrator.vibrate(100);
                    Intent intent=new Intent(DetailsActivity.this,DetailsActivity.class);
                    //intent.putExtra("Activity",activityName);
                    startActivity(intent);
                }
            });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                vibrator.vibrate(100);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DetailsActivity.this, EditDetails.class);
                intent.putExtra("ActivityName","DetailsActivity");
                startActivity(intent);
                }
        });
        }catch (Exception e)
        {
            Toast.makeText(DetailsActivity.this,"Got exception",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            Log.d("Exception", String.valueOf(e));
        }
    }// oncreate closed

    public void refreshWhenLoading()
    {
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                if(dialog.isShowing()) {
                    Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
                   // intent.putExtra("Activity",strActivity);
                    startActivity(intent);// when the task active then close the dialog
                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            }
        }, 12000); // after 12 second (or 2000 miliseconds), the task will be active.

    }

    @Override
    public void onBackPressed()
    {
        try{
        Intent intent=new Intent(DetailsActivity.this,CounselorContactActivity.class);
        intent.putExtra("ActivityName","Details Activity");
        startActivity(intent);
      //  super.onBackPressed();
        }catch (Exception e)
        {
            Log.d("Exception", String.valueOf(e));
        }
    }

    public void getCounselorData(String serialno, String cid) {

        url=clienturl+"?clientid=" + clientid + "&caseid=32&nSrNo="+serialno+"&cCounselorID="+cid;
        Log.d("CounselorDetailsUrl",url);
        if(CheckInternet.checkInternet(DetailsActivity.this))
        {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.d("FetchedResponse", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("Json", jsonObject.toString());
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.d("Length", String.valueOf(jsonArray.length()));
                   /* if(jsonArray.length()==0)
                    {
                        startActivity(new Intent(DetailsActivity.this,CounsellorData.class));
                        Toast.makeText(DetailsActivity.this,"This candidate is allocated to someone else",Toast.LENGTH_SHORT).show();
                    }*/
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                name = jsonObject1.getString("cCandidateName");
                                course = jsonObject1.getString("cCourse");
                                Log.d("Course&*",course);
                                mbl = jsonObject1.getString("cMobile");
                                adrs = jsonObject1.getString("cAddressLine");
                                city = jsonObject1.getString("cCity");
                                state1 = jsonObject1.getString("cState");
                                pincode = jsonObject1.getString("cPinCode");
                                parentno = jsonObject1.getString("cParantNo");
                                email = jsonObject1.getString("cEmail");
                                //  fetchedDataFrom = jsonObject1.getString("cDataFrom");
                                // fetchedAllocatedTo = jsonObject1.getString("AllocatedTo");
                                allocatedDate = jsonObject1.getString("AllocationDate");
                                statusid = jsonObject1.getString("CurrentStatus");
                                remark = jsonObject1.getString("cRemarks");
                                //  fetchedCreatedDate = jsonObject1.getString("dtCreatedDate");
                                status11 = jsonObject1.getString("cStatus");

                            }

                            txtSrno.setText(sr_no);
                            txtCourse.setText(course);
                            txtMobile.setText(mbl);
                            txtName.setText(name);
                            txtAddress.setText(adrs);
                            if (txtAddress.getText().toString().length() == 0) {
                                txtAddress.setText("NA");
                            }
                            txtCity.setText(city);
                            if (txtCity.getText().toString().length() == 0) {
                                txtCity.setText("NA");
                            }
                            txtState.setText(state1);
                            if (txtState.getText().toString().contains("-Select State-")) {
                                txtState.setText("NA");
                            }
                            txtPincode.setText(pincode);
                            if (txtPincode.getText().toString().length() == 0) {
                                txtPincode.setText("NA");
                            }
                            txtEmail.setText(email);
                            if (txtEmail.getText().toString().length() == 0) {
                                txtEmail.setText("NA");
                            }
                            txtParent.setText(parentno);
                            if (txtParent.getText().toString().length() == 0) {
                                txtParent.setText("NA");
                            }

                        }catch (Exception e)
                        {
                            Toast.makeText(DetailsActivity.this,"Got exception",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.d("Exception", String.valueOf(e));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error == null || error.networkResponse == null)
                            return;
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if (error.networkResponse != null||error instanceof TimeoutError ||error instanceof NoConnectionError ||error instanceof AuthFailureError ||error instanceof ServerError ||error instanceof NetworkError ||error instanceof ParseError) {
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DetailsActivity.this);
                            alertDialogBuilder.setTitle("Server Error!!!")
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    }).show();
                            dialog.dismiss();
                            Toast.makeText(DetailsActivity.this,"Server Error",Toast.LENGTH_SHORT).show();
                            // showCustomPopupMenu();
                            Log.e("Volley", "Error.HTTP Status Code:" + error.networkResponse.statusCode);
                        }

                    }
                });
        requestQueue.add(stringRequest);
        } else {
            dialog.dismiss();
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DetailsActivity.this);
            alertDialogBuilder.setTitle("No Internet connection!!!")
                    .setMessage("Can't do further process")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }).show();
        }

      /*  urlRequest = UrlRequest.getObject();
        urlRequest.setContext(getApplicationContext());
        String url=clienturl+"?clientid=" + clientid + "&caseid=32&nSrNo="+serialno+"&cCounselorID="+cid;
        urlRequest.setUrl(url);
        Log.d("FetchedCounselorUrl", url);
        urlRequest.getResponse(new ServerCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                 dialog.dismiss();
                Log.d("FetchedResponse", response);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("Json", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.d("Length", String.valueOf(jsonArray.length()));
                   *//* if(jsonArray.length()==0)
                    {
                        startActivity(new Intent(DetailsActivity.this,CounsellorData.class));
                        Toast.makeText(DetailsActivity.this,"This candidate is allocated to someone else",Toast.LENGTH_SHORT).show();
                    }*//*
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                        name = jsonObject1.getString("cCandidateName");
                        course = jsonObject1.getString("cCourse");
                        Log.d("Course&*",course);
                        mbl = jsonObject1.getString("cMobile");
                        adrs = jsonObject1.getString("cAddressLine");
                        city = jsonObject1.getString("cCity");
                        state1 = jsonObject1.getString("cState");
                        pincode = jsonObject1.getString("cPinCode");
                        parentno = jsonObject1.getString("cParantNo");
                        email = jsonObject1.getString("cEmail");
                      //  fetchedDataFrom = jsonObject1.getString("cDataFrom");
                       // fetchedAllocatedTo = jsonObject1.getString("AllocatedTo");
                        allocatedDate = jsonObject1.getString("AllocationDate");
                        statusid = jsonObject1.getString("CurrentStatus");
                        remark = jsonObject1.getString("cRemarks");
                      //  fetchedCreatedDate = jsonObject1.getString("dtCreatedDate");
                        status11 = jsonObject1.getString("cStatus");

                       *//* editor.putString("SelectedMobile", mbl);
                        editor.putString("SelectedParentNo", parentno);
                        editor.putString("SelectedName", name);
                        editor.putString("SelectedCourse", course);
                        // editor.putString("SelectedSrNo", dataCounselor.getSr_no());
                        editor.putString("SelectedEmail", email);
                        editor.putString("AllocatedDate", allocatedDate);
                        editor.putString("SelectedAddress", adrs);
                        editor.putString("SelectedCity", city);
                        editor.putString("SelectedState", state1);
                        editor.putString("SelectedPinCode", pincode);
                        editor.putString("SelectedStatus", status11);
                        editor.putString("SelectedStatusId", statusid);
                        editor.putString("SelectedRemark", remark);
                        editor.commit();*//*
                    }

                    txtSrno.setText(sr_no);
                    txtCourse.setText(course);
                    txtMobile.setText(mbl);
                    txtName.setText(name);
                    txtAddress.setText(adrs);
                    if (txtAddress.getText().toString().length() == 0) {
                        txtAddress.setText("NA");
                    }
                    txtCity.setText(city);
                    if (txtCity.getText().toString().length() == 0) {
                        txtCity.setText("NA");
                    }
                    txtState.setText(state1);
                    if (txtState.getText().toString().contains("-Select State-")) {
                        txtState.setText("NA");
                    }
                    txtPincode.setText(pincode);
                    if (txtPincode.getText().toString().length() == 0) {
                        txtPincode.setText("NA");
                    }
                    txtEmail.setText(email);
                    if (txtEmail.getText().toString().length() == 0) {
                        txtEmail.setText("NA");
                    }
                    txtParent.setText(parentno);
                    if (txtParent.getText().toString().length() == 0) {
                        txtParent.setText("NA");
                    }

                }catch (Exception e)
                {
                    Log.d("Exception", String.valueOf(e));
                }
            }
        });*/
    }
}
