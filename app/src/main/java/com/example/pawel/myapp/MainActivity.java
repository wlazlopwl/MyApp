package com.example.pawel.myapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener{

    private static String URL_GET_CATEGORY="http://s34787.s.pwste.edu.pl/app/getCategory.php";
    ArrayList<DataModel> dataModelArrayList;
    private Adapter Adapter;
    private RecyclerView recyclerView;



    private TextView menuName;
    private TextView menuEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);







        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent= getIntent();
        String extraName=intent.getStringExtra("name");
        String extraEmail=intent.getStringExtra("email");

        View headerView = navigationView.getHeaderView(0);
        TextView menuName = (TextView) headerView.findViewById(R.id.menu_name);
        TextView menuEmail = (TextView) headerView.findViewById(R.id.menu_email);
        menuName.setText(extraName);
        menuEmail.setText(extraEmail);


        recyclerView = findViewById(R.id.recyclerVievCategory);

        getCategory();


    }


private void getCategory(){
    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_CATEGORY, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d("strrrrr", ">>" + response);
            try {




                    dataModelArrayList = new ArrayList<>();
                    JSONArray dataArray  = new JSONArray(response);

                    for (int i = 0; i < dataArray.length(); i++) {

                        DataModel playerModel = new DataModel();
                        JSONObject dataobj = dataArray.getJSONObject(i);
                        playerModel.setId(dataobj.getString("id"));
                        playerModel.setName(dataobj.getString("name"));
                        playerModel.setImgUrl(dataobj.getString("img"));



                        dataModelArrayList.add(playerModel);

                    }

                    setupRecycler();



            } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //displaying the error in toast if occurrs
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);


    }

    public void setupRecycler(){


        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView=findViewById(R.id.recyclerVievCategory);
        recyclerView.setLayoutManager( layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setClickable(true);
        Adapter  = new Adapter(this, dataModelArrayList);
        recyclerView.setAdapter(Adapter);





    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }  else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_cart) {
            Intent i = new Intent(MainActivity.this,  CartActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {
            Intent i = new Intent(MainActivity.this,  SupportActivity.class);
            startActivity(i);


        }
        else if (id==R.id.nav_logout){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(int position) {
        Intent i = new Intent(MainActivity.this,  ProductListActivity.class);
        String id= dataModelArrayList.get(position).getId();
        i.putExtra("position", id);
        startActivity(i);
    }
}

