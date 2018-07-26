package com.example.iervan.crudvolley;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iervan.crudvolley.getset.Data;
import com.example.iervan.crudvolley.url.Server;
import com.example.iervan.crudvolley.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    FloatingActionButton fab;
    ListView list;
    SwipeRefreshLayout swipe;
    List<Data> itemList = new ArrayList<Data>();
    com.example.iervan.crudvolley.adapter.Adapter adapter;
    int success;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    EditText txt_id, txt_name, txt_posisi, txt_gajih;
    String id, name, posisi, gajih;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static String url_read = Server.URL + "pegawai";
    private static String url_create = Server.URL + "pegawai/create";
    private static String url_edit = Server.URL + "pegawai/edit";
    private static String url_update = Server.URL + "pegawai/update";
    private static String url_delete = Server.URL + "pegawai/destroy";

    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_POSISI = "posisi";
    public static final String TAG_GAJIH = "gajih";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";

    String tag_json_obj = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        list = (ListView) findViewById(R.id.list);

        adapter = new com.example.iervan.crudvolley.adapter.Adapter(MainActivity.this, itemList);
        list.setAdapter(adapter);

        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
            @Override
            public void run() {
                swipe.setRefreshing(true);
                itemList.clear();
                adapter.notifyDataSetChanged();
                koneksi();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm("","","","","Simpan");
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String idx = itemList.get(position).getId();

                final CharSequence[] dialogitem = {"Edit","Delete"};
                dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which){
                            case 0:
                                edit(idx);
                                break;
                            case 1:
                                delete(idx);
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        koneksi();
    }

    private void kosong(){
        txt_id.setText(null);
        txt_name.setText(null);
        txt_posisi.setText(null);
        txt_gajih.setText(null);
    }

    private void DialogForm(String idx, String names, String posisis, String gajihs, String button){
        dialog = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.catatan,null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Pegawai");

        txt_id = (EditText) dialogView.findViewById(R.id.txt_id);
        txt_name = (EditText) dialogView.findViewById(R.id.txt_name);
        txt_posisi = (EditText) dialogView.findViewById(R.id.txt_posisi);
        txt_gajih = (EditText) dialogView.findViewById(R.id.txt_gajih);

        if (!idx.isEmpty()){
            txt_id.setText(idx);
            txt_name.setText(names);
            txt_posisi.setText(posisis);
            txt_gajih.setText(gajihs);
        }else{
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                id = txt_id.getText().toString();
                name = txt_name.getText().toString();
                posisi = txt_posisi.getText().toString();
                gajih = txt_gajih.getText().toString();

                simpan_update();
                dialogInterface.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                kosong();
            }
        });
        dialog.show();
    }

    private void koneksi(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        JsonArrayRequest jArr = new JsonArrayRequest(url_read, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Data item = new Data();

                        item.setId(obj.getString(TAG_ID));
                        item.setName(obj.getString(TAG_NAME));
                        item.setPosisi(obj.getString(TAG_POSISI));
                        item.setGajih(obj.getString(TAG_GAJIH));

                        itemList.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG,"Error : " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });
        Volley.newRequestQueue(this).add(jArr);
    }

    private void simpan_update(){
        String url;

        if (id.isEmpty()){
            url = url_create;
        }else{
            url = url_update;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("Add/Update", jObj.toString());

                        koneksi();
                        kosong();

                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error : " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();

                if (id.isEmpty()){
                    params.put("name", name);
                    params.put("posisi", posisi);
                    params.put("gajih", gajih);
                }else{
                    params.put("id", id);
                    params.put("name", name);
                    params.put("posisi", posisi);
                    params.put("gajih", gajih);
                }

                return params;
            }
        };
        Volley.newRequestQueue(this).add(strReq);
    }

    private void edit(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());

                        String idx = jObj.getString(TAG_ID);
                        String namex = jObj.getString(TAG_NAME);
                        String posisix = jObj.getString(TAG_POSISI);
                        String gajihx = jObj.getString(TAG_GAJIH);

                        DialogForm(idx, namex, posisix, gajihx, "Ubah");
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(strReq);
    }

    // fungsi untuk hapus
    private void delete(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("delete", jObj.toString());

                        koneksi();

                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };

        Volley.newRequestQueue(this).add(strReq);
    }
}
