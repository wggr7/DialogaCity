package com.jayktec.DialogaCity;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
 * Created by yisheng 28-Sep-15.
 */
public class ActualizaDenunciaWs extends AsyncTask<Integer,Void, Boolean>  {

    private int version;
    private int estado;
    private int creador;
    private boolean actualizado;
    private BDControler bdCelular;



    public ActualizaDenunciaWs(BDControler bdCelular) {
        this.bdCelular = bdCelular;
    }


    public boolean isActualizado() {
        return actualizado;
    }

    public int getVersion() {
        return version;
    }

    public int getEstado() {
        return estado;
    }

    protected Boolean doInBackground(Integer... params) {
        boolean result = false;

        Log.i("ws", "estoy en el ws denuncia");
        SoapObject resultsRequestSOAP;
        int idDenuncia = params[0]-1;
        int versionCel = params[1];
        int wowzer = params[2];
        int primeraVez = params[3];
        Denuncia registro= new Denuncia();
        int lista = 0;
        Log.i("ws", "Denuncia tope:" + idDenuncia);
        actualizado=false;

        Integer control = bdCelular.devolverIdUltimaDenuncia();
        Log.i("ws", "Control:" + control);

        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/ConsultaDen");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("idDen", String.valueOf(idDenuncia));
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            //Send request
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("idDen", String.valueOf(idDenuncia)));



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
                Log.i("ws response",sb.toString());

                version=0;
                estado=0;

                JSONArray jsonresp = new JSONArray(sb.toString());
                lista= jsonresp.length();


                Log.i("ws", "total de objetos:" + lista);
                idDenuncia=idDenuncia+1;
                for (int i = 0; i < lista; i++) {
                    //   Log.i("ws", "objeto:" + i);
                 //   String denun= (String) jsonresp.get(i);
                    JSONObject denuncia= (JSONObject) jsonresp.get(i);
                    //Log.i("ws JsonDen",denuncia.toString());
                    registro.setComentarios(denuncia.getString("denDescripcion"));
                    registro.setEstadoDenuncia(denuncia.getInt("denEstado"));
                    registro.setFechaDenuncia(denuncia.getString("denFecha"));
                    registro.setIdDenWow(denuncia.getInt("idDenuncia"));
                    registro.setTipoDenuncia(denuncia.getInt("denTipo"));
                    registro.setLongitud(denuncia.getDouble("denLongitud"));
                    registro.setLatitud(denuncia.getDouble("denLatitud"));
                    registro.setRating(denuncia.getDouble("denValoracion"));

                    registro.setIdWowzer(denuncia.getJSONObject("denWowzer").getInt("idWowzer"));
                    registro.setVersion(denuncia.getInt("denVersion"));
                    registro.setWowzer(denuncia.getJSONObject("denWowzer").getString("wowLogin"));
                    //Log.i("wsVersion", String.valueOf(denuncia.getInt("denVersion")));
                    version = denuncia.getInt("denVersion");
                    estado= denuncia.getInt("denEstado");
                    creador=denuncia.getJSONObject("denWowzer").getInt("idWowzer");
                   /* if (estado==4 || estado==5)
                    {
                        actualizado=true;
                        Log.i("ws sin", "eliminando denuncias");
                        bdCelular.eliminarDenuncia(idDenuncia);
                        //todo  eliminar los marcadores en el mapa
                    }
                    else*/

                    // Log.i("ws sin", "insertando");
                       // Log.i("ws sin", "Sincronizando...");
                        if (!bdCelular.verificarDenuncia(denuncia.getInt("idDenuncia"))) {

                            bdCelular.insertarDenuncia(registro);

                            if (i == lista -1){
                                bdCelular.actualizarControlDenuncia(registro.getIdDenWow());
                            }


                        }

                   // Log.i("wsID", denuncia.getInt("idDenuncia")+" - "+idDenuncia);
                    //Log.i("wsID",  denuncia.getInt("denVersion")+" - "+versionCel);
                        if( denuncia.getInt("idDenuncia")==idDenuncia){
                           // Log.i("wsID", String.valueOf(denuncia.getInt("idDenuncia")));
                                if(version != versionCel && primeraVez==1 &&creador== wowzer) {
                                    bdCelular.actualizaDenunciaSinImagen(registro, 2);
                                    //bdCelular.insertarNotificacion(denuncia.getInt("idDenuncia"), denuncia.getString("denDescripcion"));
                                    //Log.i("ws", "tengo q notificar");
                                }
                        }
                    result = true;
                }
                actualizado=true;


            }
            else{
                Log.i("ws", "response incorrecta");
                result = false;
            }


        } catch (Exception e) {

            Log.i("ws EXPLOTO", e.toString());
            result = false;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }











/*
        final String NAMESPACE = "http://AppMobile/";
        final String URL = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "consultarDenunciaWowzerEspecifica";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        //Log.i("ws", URL);
        int lista = 0;

        Log.i("ws idDenuncia:", "" + idDenuncia);

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idDenuncia", idDenuncia);


        //Log.i("ws", "ya anadi las propiedades");
        //Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        version=0;
        estado=0;
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            //Log.i("ws", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            //Log.i("ws", "esperando");
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            //Log.i("ws", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj == null) {
                Log.i("ws", "nulo");

            } else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                //Log.i("ws", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
               // Log.i("ws", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                 //   Log.i("ws", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);
                    registro.setComentarios(ic.getProperty("descripcion").toString());
                    registro.setEstadoDenuncia(ic.getProperty("estado").toString());
                    registro.setFechaDenuncia(ic.getProperty("fecha").toString());
                    registro.setIdDenWow(Integer.parseInt(ic.getProperty("id").toString()));
                   //Log.i("ws sin den:", ic.getProperty("id").toString());

                    registro.setTipoDenuncia(Integer.parseInt(ic.getProperty("tipo").toString()));
                    registro.setLongitud(Double.parseDouble(ic.getProperty("longitud").toString()));
                    registro.setLatitud(Double.parseDouble(ic.getProperty("latitud").toString()));
                    registro.setRating(Double.parseDouble(ic.getProperty("valoracion").toString()));
                   //
                    registro.setWowzer(ic.getProperty("loginUsuario").toString());
                    registro.setIdWowzer(Integer.parseInt(ic.getProperty("wowzer").toString()));
                    registro.setVersion(Integer.parseInt(ic.getProperty("version").toString()));

                    version = Integer.parseInt(ic.getProperty("version").toString());
                    estado= Integer.parseInt(ic.getProperty("estado").toString());
                    creador=Integer.parseInt(ic.getProperty("wowzer").toString());
                    if (estado==4 || estado==5)
                    {
                        actualizado=true;
                        Log.i("ws sin", "eliminando denuncias");
                        bdCelular.eliminarDenuncia(idDenuncia);
                        //todo  eliminar los marcadores en el mapa
                    }
                    else
                    if(version != versionCel)
                    {
                        actualizado=true;
                        //Log.i("ws sin", "versiones diferentes");
                        bdCelular.actualizaDenunciaSinImagen(registro, 2);
                            if (creador== wowzer)
                            {
                                //Log.i("ws sin", "inserto notificacion");
                                //Log.i("ws not", ic.getProperty("descripcion").toString());
                                bdCelular.insertarNotificacion(Integer.parseInt(ic.getProperty("id").toString()),
                                ic.getProperty("descripcion").toString());
                            }

                    }
                    result = true;
                }
            }
          Log.i("ws", "exito");
        }
        catch (NullPointerException e)
        {
          //  Log.i("ws EXPLOTO", e.getMessage().toString());
            result = false;
        }
        catch (Exception e) {

            Log.i("ws EXPLOTO", e.getMessage().toString());
            result = false;
        }
*/
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

        if (result) {

        } else {

        }
    }


}