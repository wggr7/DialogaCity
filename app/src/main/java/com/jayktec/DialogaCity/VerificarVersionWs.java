package com.jayktec.DialogaCity;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

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
 * Created by wil on 22/06/17.
 */

public class VerificarVersionWs extends AsyncTask<Integer,Void, Boolean>{


    /**
     * Created by yisheng 28-Sep-15.
     */


        private int version;
        private int estado;
        private int creador;
        private boolean actualizado;
        private BDControler bdCelular;



        public VerificarVersionWs(BDControler bdCelular) {
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
            int idDenuncia = params[0];
            int versionCel = params[1];
            int wowzer = params[2];
            Denuncia registro= new Denuncia();
            int lista = 0;

            actualizado=false;

      //      Integer control = bdCelular.devolverIdUltimaDenuncia();
      //      Log.i("ws", "Control:" + control);

            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/DenunciaUsuario");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("denUsuario", String.valueOf(wowzer));
                connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


                //Send request
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("denUsuario", String.valueOf(wowzer)));



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

                    for (int i = 0; i < lista; i++) {

                        JSONObject denuncia= (JSONObject) jsonresp.get(i);
                        if( denuncia.getInt("idDenuncia")==idDenuncia){
                        registro.setComentarios(denuncia.getString("denDescripcion"));
                        registro.setEstadoDenuncia(denuncia.getInt("denEstado"));
                        registro.setFechaDenuncia(denuncia.getString("denFecha"));
                        registro.setIdDenWow(denuncia.getInt("idDenuncia"));
                        registro.setTipoDenuncia(denuncia.getInt("denTipo"));
                        registro.setLongitud(denuncia.getDouble("denLongitud"));
                        registro.setLatitud(denuncia.getDouble("denLatitud"));
                        registro.setRating(denuncia.getDouble("denValoracion"));
                             registro.setIdWowzer(denuncia.getJSONObject("denWowzer").getInt("idWowzer"));
                            registro.setWowzer(denuncia.getJSONObject("denWowzer").getString("wowLogin"));
                        registro.setVersion(denuncia.getInt("denVersion"));
                        version = denuncia.getInt("denVersion");
                        estado= denuncia.getInt("denEstado");
                        creador=denuncia.getJSONObject("denWowzer").getInt("idWowzer");

                        Log.i("wsID", denuncia.getInt("idDenuncia")+" - "+idDenuncia);
                        Log.i("wsID",  denuncia.getInt("denVersion")+" - "+versionCel);


                            if(version != versionCel &&creador== wowzer) {
                                bdCelular.actualizaDenunciaSinImagen(registro, 2);
                                bdCelular.insertarNotificacion(denuncia.getInt("idDenuncia"), denuncia.getString("denDescripcion"));
                                Log.i("ws", "tengo q notificar");
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
