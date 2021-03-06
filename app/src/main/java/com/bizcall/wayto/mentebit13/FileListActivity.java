package com.bizcall.wayto.mentebit13;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {

    RecyclerView recyclerFileList;
    RecyclerFiles adapterFile;
    ArrayList<DataFileList> arrayList;
    CommonMethods commonMethods;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button btnUpload;
    CheckBox chkboxSelectAll;
    ImageView imgBack;
    String filename, filePath, path, activityName;
    int pos;
    ArrayList<DataUploadedList> arr;
    DataUploadedList dataUploadedList;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        try{
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sp = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = sp.edit();
        btnUpload = findViewById(R.id.btnUpload);
        imgBack = findViewById(R.id.img_back);
        chkboxSelectAll = findViewById(R.id.chkboxSelectAll);
        arrayList = new ArrayList<>();
        commonMethods = new CommonMethods();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                onBackPressed();
            }
        });
        activityName = getIntent().getStringExtra("ActivityName");
        arr = new ArrayList<>();
        // arr=new ArrayList<>();
      /*  if(activityName.equals("SelectedFileList"))
        {
          // arr=getIntent().getStringArrayListExtra("UploadedList");
            editor.putString("ServerResponse","200");
            editor.commit();
        }*/
        /*else
        {
            //pos=0;
            editor.putString("ServerResponse","0");
          //  editor.putInt("Position",pos);
            editor.commit();
        }*/

        File file = new File(commonMethods.getPath());

        File[] files = file.listFiles();

        for (File file1 : files) {
            // String isUploaded=getIntent().getStringExtra("uploadSuccess");
            Log.d("File***", file1.toString());
            filename = file1.toString();

            DataFileList dataFileList = new DataFileList(filename);
            arrayList.add(dataFileList);
        }
        Log.d("Arraylist size:", String.valueOf(arrayList.size()));
        adapterFile = new RecyclerFiles(FileListActivity.this, arrayList);
        recyclerFileList = findViewById(R.id.recyclerFileList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerFileList.setLayoutManager(layoutManager);
        recyclerFileList.setAdapter(adapterFile);
        adapterFile.notifyDataSetChanged();

        chkboxSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkboxSelectAll.isChecked()) {
                    arrayList = getModel(true);
                    RecyclerFiles recyclerFiles = new RecyclerFiles(getApplicationContext(), arrayList);
                    recyclerFileList.setAdapter(recyclerFiles);
                } else {
                    arrayList = getModel(false);
                    RecyclerFiles recyclerFiles = new RecyclerFiles(getApplicationContext(), arrayList);
                    recyclerFileList.setAdapter(recyclerFiles);
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < RecyclerFiles.arrayList.size(); i++) {
                    if (RecyclerFiles.arrayList.get(i).getSelected()) {
                        String f1 = RecyclerFiles.arrayList.get(i).getFilename();
                        dataUploadedList = new DataUploadedList(f1);
                        arr.add(dataUploadedList);
                    }
                }
                if (arr.isEmpty()) {
                    Toast.makeText(FileListActivity.this, "Please select file to upload", Toast.LENGTH_SHORT).show();


                } else {
                    Intent intent = new Intent(FileListActivity.this, SelectedFileList.class);
                    startActivity(intent);
                    arr.clear();
                }
            }
        });
        }catch (Exception e)
        {
            Log.d("Exception", String.valueOf(e));
        }
    }

    @Override
    public void onBackPressed()
    {
        try{
        Intent intent=new Intent(FileListActivity.this, Home.class);
        intent.putExtra("Activity","FileListActivity");
        startActivity(intent);
      //  super.onBackPressed();
        }catch (Exception e)
        {
            Log.d("Exception", String.valueOf(e));
        }
    }

    private ArrayList<DataFileList> getModel(boolean isSelect) {
        ArrayList<DataFileList> list = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {

            DataFileList model = new DataFileList();
            model.setSelected(isSelect);
            model.setFilename(arrayList.get(i).getFilename());
            list.add(model);
        }
        return list;
    }
}
