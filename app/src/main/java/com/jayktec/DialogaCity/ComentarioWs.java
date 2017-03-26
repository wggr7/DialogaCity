package com.jayktec.DialogaCity;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Alexander on 01-Sep-15.
 */
public class ComentarioWs extends AsyncTask<Comentario,Void, Boolean> {


    private final Activity laActividad;
    private RequestQueue mRequestQueue;

    public ComentarioWs(Activity act){
        this.laActividad = act;
    }
    private int idComentario;

    public int getIdComentario() {
        return idComentario;
    }

    protected Boolean doInBackground(Comentario... params) {
        boolean result = false;
        final Comentario comentario = params[0];
        final String URL="http://jayktec.com.ve:8080/DialogaWeb/servicios/Comentar";

        Log.i("WS_COMENT", URL);
        int lista = 0;
        Log.i("WS_comentarios", "enviando parametros");
Log.i("WS_comentarios", comentario.get_comentario());

Log.i("WS_comentarios", comentario.getId_Wow().toString());

Log.i("WS_comentarios", comentario.getId_denuncia().toString());

Log.i("WS_comentarios", comentario.getLoginUsuario()+"/");


        ComentarioXml mensaje = new ComentarioXml(comentario);
        mRequestQueue =  Volley.newRequestQueue(laActividad.getApplicationContext());


       Log.i("WS_COMENT", "ya anadi las propiedades");

        try {
            JsonArrayRequest respuesta = new JsonArrayRequest(Request.Method.POST ,URL, null,new Response.Listener<JSONArray>(){
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("wsDenuncia", "Respuesta Volley:" + response.toString());
                    int lista= response.length();
                    BDControler BDTelefono = new BDControler(laActividad.getApplicationContext(),1);

                    for (int i=0;i<lista;i++)
                    {
                        try {
                            Log.d("Ws ic:","preparando para recibir");

                            JSONObject ic=(JSONObject) response.get(i);
                            Log.d("Ws ic:",ic.toString());
                            //Denuncia registro= new Denuncia();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            },


                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("wsComentario", "Error:" + error.getMessage());
                            Log.i("WS_comentarios", "eXPLOTE");

                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    //Log.i("WS_comentarios", "enviando parametros");

                    params.put("comComentarios", comentario.get_comentario());
                    //Log.i("WS_comentarios", comentario.get_comentario());

                    params.put("comWowzer", comentario.getId_Wow().toString());
                    //Log.i("WS_comentarios", comentario.getId_Wow().toString());

                    params.put("comDenuncia", comentario.getId_denuncia().toString());
                    //Log.i("WS_comentarios", comentario.getId_denuncia().toString());

                    params.put("comUsuario", "1");
                  //  params.put("comUsuario", comentario.getLoginUsuario());
                    //Log.i("WS_comentarios", comentario.getLoginUsuario());

                    Log.i("WS_comentarios", "enviando parametros");
                    return params;
                }

            };

            mRequestQueue.add(respuesta);

            Log.i("WS_comentarios", "exito");
            }

        catch (NullPointerException e)
        {
            Log.i("ws comentariows", e.getMessage().toString());
            result = false;
        }
        catch (Exception e) {

            Log.i("WS_COMENT EXPLOTO", e.getMessage().toString());
            result = false;
        }
        return result;
    }


    protected void onPostExecute(Boolean result) {

        if (result) {

        } else {
            idComentario = 0;

        }
    }


}