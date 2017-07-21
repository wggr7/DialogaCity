package com.jayktec.DialogaCity;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alexander on 01-Sep-15.
 */
public class ApoyarWs extends AsyncTask<String,Void, Boolean> {

    private int idDenuncia;
    public Activity actividad;

    /**
     * constructor que recibe la actividad para poder grabar e la Base de Datos
     * @param act
     */
    public ApoyarWs(Activity act){
        this.actividad = act;
    }

    /**
     * Constructor por Defecto
     */
    public ApoyarWs(){
    }

    /**
     * Metodo asincrono para trabajar el WS de modo Background
     * @param params Denuncia en modo paramtro
     * @return resultado del WS
     */
    protected Boolean doInBackground(String... params) {
        boolean result = false;

        String stridDen=params[0];
        String idWowzer=params[1];
        String rating=params[2];

        BDControler BDTelefono = new BDControler(actividad.getApplicationContext(),1);
            Denuncia den=BDTelefono.obtenerLaDenuncia(Integer.parseInt(stridDen));
        String idDen=String.valueOf(den.getIdDenWow());

        Log.i("ws den123344", idDen);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("apovarloracion", rating);
            jsonObj.put("apoDenuncia", idDen);
            jsonObj.put("apoWowzer", idWowzer);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("ws", jsonObj.toString());

        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/Apoyar");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("apovarloracion", rating);
            connection.setRequestProperty("apoDenuncia", idDen);
            connection.setRequestProperty("apoWowzer", idWowzer);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            //Send request
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("apovarloracion", rating));
            params1.add(new BasicNameValuePair("apoDenuncia", idDen));
            params1.add(new BasicNameValuePair("apoWowzer", idWowzer));



            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params1));
            writer.flush();
            writer.close();
            os.close();

            connection.connect();

            StringBuffer sb = new StringBuffer();

            int response = connection.getResponseCode();

            Log.i("ws", connection.getResponseMessage());

            if (response >= 200 && response <=399) {
                Log.i("ws", connection.getContent().toString());
                InputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                Log.i("ws",sb.toString());




                result=true;

            }
            else{
                Log.i("ws", "response incorrecta");
            }




        } catch (Exception e) {

            e.printStackTrace();
            result= false;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }


        return result;
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    protected void onPostExecute(Boolean result) {

        if (result)
        {

        }
        else
        {
            idDenuncia=0;

        }
    }


}


