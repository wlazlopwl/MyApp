package com.example.pawel.myapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {


    private static DataProduct data;
    private CartAdapter CartAdapter;
    public static ArrayList<DataProduct> dataCartArrayList;
    private RecyclerView recyclerView;
    SessionManager sessionManager;
    public static Context ctx;
    public static String userId;
    public Button btnNewOrder;
    public  TextView mTextEmptyCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        ctx = getApplicationContext();
         mTextEmptyCart = (TextView) findViewById(R.id.textEmptyCart);

        sessionManager = new SessionManager(this);
        btnNewOrder = (Button) findViewById(R.id.btn_order);


        userId = sessionManager.getUserInfo().get("id");
        getProduct(userId);


        btnNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkAddress()==true) {
                    sendNewOrder();
                }
                else{
                    alert();
                }



            }
        });


    }


    private void alert() {
        new AlertDialog.Builder(CartActivity.this).setTitle("Uwaga!.")
                .setMessage("Aby złożyć zamówienie, uzupełnij adres dostawy." +
                        " Czy chcesz to zrobić teraz?").setPositiveButton("Chcę uzupełnić", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CartActivity.this, AdminSettingChangeMyData.class);
                startActivity(intent);


            }
        }).setNegativeButton("Zrobię to później", null)
                .setIcon(android.R.drawable.ic_dialog_alert).show();


    }

    private Boolean checkAddress() {
        boolean enteredAddress;

        SessionManager sessionManager;
        sessionManager = new SessionManager(this);

        if      ((sessionManager.getUserInfo().get("street").isEmpty()) ||
                (sessionManager.getUserInfo().get("city").isEmpty()) ||
                (sessionManager.getUserInfo().get("phone").isEmpty()) ||
                (sessionManager.getUserInfo().get("postcode").isEmpty())) {

            enteredAddress=false;
        }
        else enteredAddress=true;




        return enteredAddress;

    }

    private void sendNewOrder() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_NEW_ORDER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.contains("Zaktualizowano")) {
                    Toast.makeText(ctx, "Złożono zamówienie", Toast.LENGTH_LONG).show();

                    dataCartArrayList.clear();

                } else {
                    Toast.makeText(ctx, "not" + response, Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();


                params.put("user_id", userId);


                return params;
            }
        };
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(ctx);

        requestQueue.add(stringRequest);


    }


    public void setupProductCartRecycler() {


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductCart);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setClickable(true);
        CartAdapter = new CartAdapter(dataCartArrayList);
        recyclerView.setAdapter(CartAdapter);
    }


    private void getProduct(final String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_GET_CART, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (!response.contains("Brak")) {
                    try {


                        dataCartArrayList = new ArrayList<>();
                        Log.i("tagconvertstr1", "[" + response + "]");
                        JSONArray dataArray = new JSONArray(response);

                        for (int i = 0; i < dataArray.length(); i++) {

                            DataProduct playerModel = new DataProduct();

                            JSONObject dataobj = dataArray.getJSONObject(i);

                            playerModel.setName(dataobj.getString("name"));
                            playerModel.setId(dataobj.getString("id"));
                            playerModel.setQuantity(dataobj.getString("quantity"));
                            playerModel.setDescription(dataobj.getString("description"));
                            playerModel.setImgUrl(dataobj.getString("img"));


                            dataCartArrayList.add(playerModel);

                        }

                        setupProductCartRecycler();


                    } catch (JSONException e) {
                        e.printStackTrace();
//                    DataProduct playerModell = new DataProduct();
//                    playerModell.setId("2");
//                    playerModell.setName("Test");
//                    dataCartArrayList.add(playerModell);
//                    setupProductCartRecycler();
                    }
                }
                else
                {
                    mTextEmptyCart.setVisibility(View.VISIBLE);
                    btnNewOrder.setEnabled(false);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);

    }

    public static void deleteProduct(final String idProduct) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_DELETE_CART_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(ctx, response, Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", userId);

                params.put("product_id", idProduct);


                return params;
            }
        };
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(ctx);

        requestQueue.add(stringRequest);

    }
    public static void updateCart(final String p, final String check, final String quantity){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_UPDATE_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(ctx, response, Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", userId);
                params.put("check", check);
                params.put("product_id", p);
                params.put("quantity", "3");
                if (check=="3") {
                    params.put("quantity", quantity);
                }



                return params;
            }
        };

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(ctx);

        requestQueue.add(stringRequest);


    }

}
