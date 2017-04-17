package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yisheng on 20-Sep-15.
 */
public class RegistroWs  extends AsyncTask<Registro,Void, Boolean> {

    private int idRegistro;
    private Activity actividad;
    private boolean result = false;
    private boolean trabajando= true;

    public boolean isResult() {
        return result;
    }

    public RegistroWs(Activity actividad) {
        this.actividad = actividad;
    }

    public boolean isTrabajando() {
        return trabajando;
    }

    public RegistroWs() {
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    @Override
    protected Boolean doInBackground(Registro... params) {

        Registro registro = params[0];
        String pnombre=registro.getNombre();
        String papellido=registro.getApellido();
        String email=registro.getEmail();
        String clave=registro.getPassword();
        String login=registro.getId_wowzer();
        String estadosent=registro.getEstado_civil();
        String sexo=registro.getSexo();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("pnombre", pnombre);
            jsonObj.put("papellido", papellido);
            jsonObj.put("email", email);
            jsonObj.put("clave", clave);
            jsonObj.put("login", login);
            jsonObj.put("estadosent", estadosent);
            jsonObj.put("sexo", sexo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("ws", jsonObj.toString());


        URL url;
        HttpURLConnection connection = null;


        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/Wowzer_save");
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("pnombre", pnombre));
            params1.add(new BasicNameValuePair("papellido", papellido));
            params1.add(new BasicNameValuePair("email", email));
            params1.add(new BasicNameValuePair("clave", clave));
            params1.add(new BasicNameValuePair("login", login));
            params1.add(new BasicNameValuePair("estadosent", estadosent));
            params1.add(new BasicNameValuePair("sexo", sexo));


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

            if (response >= 200 && response <=399) {
                Log.i("ws", connection.getInputStream().toString());
                is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }

                JSONObject jsonObject = new JSONObject(sb.toString());

                BDControler bdlocal = new BDControler(actividad,1);
                idRegistro = jsonObject.getInt("idWowzer");

                if (idRegistro !=0)
                    {
                        Log.i("ws","entre a intentar ");
                        if(bdlocal.registrarUsuarioLogeado(idRegistro,registro.getId_wowzer().toString(),registro.getSexo().toString(),1) == "OK")
                            {
                                Log.i("ws", "voy a llamar al mapa");
                                result = true;
                                Intent miIntent = new Intent(actividad,com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
                                actividad.startActivity(miIntent);

                            }
                         else
                            {
                                Toast.makeText(actividad, "Problemas para Guardar los datos", Toast.LENGTH_LONG).show();
                                result = false;
                            }

                    }
                else
                    {
                        result = false;
                        Toast.makeText(actividad, "Problemas para Guardar el Registro", Toast.LENGTH_LONG).show();
                    }


            }
            else{
                Toast.makeText(actividad, "Problemas para comunicar", Toast.LENGTH_LONG).show();
                result = false;
            }





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        trabajando=false;
        return result;
    }






   /*     SoapObject resultsRequestSOAP;
        Registro registro = params[0];
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "CrearWowzer";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;
        trabajando=true;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        RegistroXml mensaje = new RegistroXml(registro);
        request.addProperty("wowzer", mensaje);


        Log.i("ws", "ya anadi las propiedades");
        Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        envelope.addMapping(SOAP_ACTION, "wowzer", new RegistroXml().getClass());

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

                    Log.i("ws wowzer id:", " "+Integer.parseInt(ic.getProperty("idGenerico").toString()));

                    BDControler bdlocal = new BDControler(actividad,1);

                    idRegistro= Integer.parseInt(ic.getProperty("idGenerico").toString());
                    Log.i("ws","ya cree la bd ");

                    if (idRegistro !=0){
                        Log.i("ws","entre a intentar ");
                        if(bdlocal.registrarUsuarioLogeado(idRegistro,registro.getEmail().toString(),registro.getSexo().toString(),1) == "OK"){
                            Log.i("ws", "voy a llamar al mapa");
                            Intent miIntent = new Intent(actividad,com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
                            actividad.startActivity(miIntent);

                        }
                        else{

                            Toast.makeText(actividad, "Problemas para Guardar los", Toast.LENGTH_LONG).show();
                             Log.i("ws", ic.getProperty("mensaje").toString());
                        }
                        result = true;
                    }
                    else
                    {
                        result = false;
                        Toast.makeText(actividad, "Problemas para Guardar el Registro", Toast.LENGTH_LONG).show();
                    }

                    result = true;
                }
            }
            Log.i("ws", "exito");
        }
        catch (Exception e)
        {

            trabajando=false;
            //Log.i("ws EXPLOTO",e.getMessage());
            result = false;
        }
        trabajando=false;*/



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
            //BDControler bdlocal=new BDControler(ActividadMapaCityWowzer.class,0);
            //bdlocal.actualizaDenunciaInsercion(denuncia.getIdDenuncia(),idDenuncia);

        }
        else
        {
            idRegistro=0;
            //    txtResultado.setText("Error!");
        }
    }

}
