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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yisheng on 20-Oct-15.
 */
public class LoginWs extends AsyncTask<String,Void, Boolean> {

    private int idRegistro;
    private int nivel;
    private boolean trabajando= true;
    private String sexo;
    private String alias;
    public Activity actividad;
    private RequestQueue mRequestQueue;



    public boolean isTrabajando() {
        return trabajando;
    }

    public int getNivel() {
        return nivel;
    }

    public String getAlias() {
        return alias;
    }

    public String getSexo() {
        return sexo;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result = false;
        final String login = params[0];
        final String clave = params[1];


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("user", login);
            jsonObj.put("pass", clave);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("ws", jsonObj.toString());


        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/autenticarWowzer");
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("user", login);
            connection.setRequestProperty("pass", clave);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            //Send request
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("user", login));
            params1.add(new BasicNameValuePair("pass", clave));


            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params1));
            writer.flush();
            writer.close();
            os.close();

            connection.connect();
            InputStream is;
            StringBuffer sb = new StringBuffer();

            int response = connection.getResponseCode();

            Log.i("ws", connection.getResponseMessage());

            if (response >= 200 && response <=399){
                Log.i("ws", connection.getInputStream().toString());
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }

                JSONObject  jsonObject = new JSONObject(sb.toString());

                idRegistro = jsonObject.getInt("idWowzer");
                alias = jsonObject.getString("wowLogin");
                nivel = jsonObject.getInt("wowPuntos");
                if(jsonObject.getString("wowSexo").equals("M"))

                {
                    sexo="M";
                }
                else
                {
                    sexo="F";
                }
                Log.i("ws", String.valueOf(idRegistro));
                Log.i("ws", alias);
                Log.i("ws", String.valueOf(nivel));
                Log.i("ws", sexo);
                trabajando=false;
                result= true;
            } else {
                //return is = connection.getErrorStream();
                trabajando=false;
                result= false;
            }


        } catch (Exception e) {
            trabajando=false;
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



/*// Create the POST object and add the parameters
        HttpPost httpPost = new HttpPost("http://jayktec.com.ve/DialogaWeb/servicios/autenticarWowzer");
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response=null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        trabajando=false;
        String respStr = null;
        try {
            respStr = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("ws", respStr);
        */
     /* HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost("http://jayktec.com.ve/DialogaWeb/servicios/autenticarWowzer");

        post.setHeader("content-type", "application/json");

        JSONObject dato = new JSONObject();

        try {

            dato.put("user", login);
            dato.put("pass", clave);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("ws", login);
        Log.i("ws", clave);
        Log.i("ws", dato.toString());
        StringEntity entity = null;
        try {
            entity = new StringEntity(dato.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType("application/json");
        post.setEntity(entity);

        Log.d("ws", String.valueOf(post.getParams()));

        try
        {
            HttpResponse resp = httpClient.execute(post);
            Log.i("ws", resp.toString());
            String respStr = EntityUtils.toString(resp.getEntity());
            Log.i("ws", respStr);
            if(!respStr.contains("false")){
                JSONArray respJSON = new JSONArray(respStr);


                idRegistro = respJSON.getInt(Integer.parseInt("idWowzer"));
                alias = respJSON.getString(Integer.parseInt("wowLogin")).toString();
                nivel = respJSON.getInt(Integer.parseInt("wowPuntos"));
                if(respJSON.getString(Integer.parseInt("wowSexo")).toString().equals("M"))

                {
                    sexo="M";
                }
                else
                {
                    sexo="F";
                }

                result=true;
                trabajando=false;
            }
            else{
                trabajando=false;
                result=false;
            }


        }
        catch(Exception ex)
        {
            trabajando=false;
            Log.e("ServicioRest","Error!", ex);
            result=false;
        }






       /* final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "VerificacionLogin";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("login", login);
        request.addProperty("clave", clave);


        Log.i("ws", "ya anadi las propiedades");
        Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);


        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            Log.i("ws", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            Log.i("ws", "esperando");
            SoapObject resSoap =(SoapObject)envelope.getResponse();
            Log.i("ws", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj==null)
            {
                Log.i("ws", "nulo");

            }
            else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                Log.i("ws", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
                Log.i("ws", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                    Log.i("ws", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);

                    Log.i("ws wowzer id:", " "+Integer.parseInt(ic.getProperty("id_usuario").toString()));

                    idRegistro= Integer.parseInt(ic.getProperty("id_usuario").toString());
                    alias=(ic.getProperty("mensaje").toString());
                    nivel= Integer.parseInt(ic.getProperty("idGenerico").toString());

                    if ( Integer.parseInt(ic.getProperty("version").toString())==1)
                    {
                        sexo="M";
                    }
                    else
                    {
                        sexo="F";
                    }

                    result = true;
                }
            }
            Log.i("ws", "exito");
            trabajando=false;
        }
        catch (Exception e)
        {
            trabajando=false;

            //Log.i("ws EXPLOTO",e.getMessage());
            result = false;
        }
        trabajando=false;
*/


    protected void onPostExecute(Boolean result) {

        if (result)
        {

        }
        else
        {
            idRegistro=0;
        }
    }

}
