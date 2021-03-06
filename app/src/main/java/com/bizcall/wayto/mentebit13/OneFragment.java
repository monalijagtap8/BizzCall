package com.bizcall.wayto.mentebit13;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment {

    String sr_no,clientid,clienturl;
    UrlRequest urlRequest;
    SharedPreferences sp;
    ProgressDialog dialog;
    RecyclerView recyclerRemark;
    AdapterRemarks adapterRemarks;
    LinearLayout linearRemarkTitle;
    ArrayList<DataRemark> arrayListRemark;
    TextView txtNoRemark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_one, container, false);
        try{
        view.setBackgroundColor(getResources().getColor(R.color.white));
        sp = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        clienturl=sp.getString("ClientUrl",null);
        clientid=sp.getString("ClientId",null);
        recyclerRemark=view.findViewById(R.id.recyclerRemarks);
        linearRemarkTitle=view.findViewById(R.id.TitleRemarks);
        txtNoRemark=view.findViewById(R.id.txtNoRemark);
        arrayListRemark=new ArrayList<>();
        sr_no = sp.getString("SelectedSrNo", null);
        clientid = sp.getString("ClientId", null);
       dialog = ProgressDialog.show(getActivity(), "", "Loading...", false,true);
        getRemarkBackup();


        }catch (Exception e)
        {
            Log.d("Exception", String.valueOf(e));
        }
        return view;
    }

    public void getRemarkBackup()
    {
        urlRequest = UrlRequest.getObject();
        urlRequest.setContext(getActivity());
        urlRequest.setUrl(clienturl+"?clientid="+clientid+"&caseid=21&SrNo="+sr_no);
        Log.d("RemarkUrl1", clienturl+"?clientid="+clientid+"&caseid=21&SrNo="+sr_no);
        urlRequest.getResponse(new ServerCallback() {
            @Override
            public void onSuccess(String response) throws JSONException {
                dialog.dismiss();

                Log.d("StatusResponse1", response);
                try {
                    if (response.contains("[]"))
                    {
                        linearRemarkTitle.setVisibility(View.GONE);
                        txtNoRemark.setVisibility(View.VISIBLE);
                    } else {
                        linearRemarkTitle.setVisibility(View.VISIBLE);
                        txtNoRemark.setVisibility(View.GONE);
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("Json", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String remark = jsonObject1.getString("cRemarks");
                        String remarksChangeDate=jsonObject1.getString("Remarks Change Date");
                        // Log.d("Status11",statusid);
                        //StatusInfo statusInfo=new StatusInfo(status1,statusid);

                        // Log.d("Json33333",statusInfo.toString());
                        //arrayList.add(statusInfo);
                        DataRemark dataRemark=new DataRemark(remark,remarksChangeDate);
                        arrayListRemark.add(dataRemark);
                        // Log.d("Json11111",arrayList1.toString());
                    }
                  adapterRemarks=new AdapterRemarks(getActivity(),arrayListRemark);
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerRemark.setLayoutManager(linearLayoutManager);
                    recyclerRemark.setAdapter(adapterRemarks);
                    adapterRemarks.notifyDataSetChanged();


                    //Log.d("Size**", String.valueOf(arrayList1.size()));
                } catch (Exception e) {
                    Toast.makeText(getActivity(),"Got exception",Toast.LENGTH_SHORT).show();
                    Log.d("RemarkHistoryException", String.valueOf(e));
                }
            }
        });
    }

}
