
/**
 * Created by Usuario on 25-08-2015.
 */
package com.jayktec.DialogaCity;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Vector;

/**
 * Created by Usuario on 25-08-2015.
 */
public class ComentarioMysqlWs extends AsyncTask<String,Integer,Comentario[]> {
    private     Comentario[] listaComentario;
    public     Boolean Trabajando =true ;


    public Boolean getTrabajando() {
        return Trabajando;
    }

    public Comentario[] getListaComentario() {
        return this.listaComentario;
    }

    protected Comentario[] doInBackground(String... params) {

        SoapObject resultsRequestSOAP;
int lista;
        String param = params[0];
        Log.i("ws",param);
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/ComentariosDenuncia");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("comDenuncia", param);
            connection.setRequestMethod("GET"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            //Send request
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("comDenuncia", param));



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

                JSONArray jsonresp = new JSONArray(sb.toString());

                if(!jsonresp.equals("[]")){

                    lista= jsonresp.length();
                    this.listaComentario= new Comentario[lista];
                    for (int i = 0; i < lista; i++) {
                        JSONObject comentario= (JSONObject) jsonresp.get(i);

                        Comentario registro= new Comentario();
                        registro.set_comentario(comentario.getString("comComentarios"));
                        registro.set_fecha(comentario.getString("comFechaCreacion"));
                  try {
                      registro.setLoginUsuario(comentario.getJSONObject("comWowzer").getString("wowLogin"));
                      Log.i("ws", "correctoWowzer");
                  }
                      catch (Exception e) {
                          Log.i("ws", "falloWowzer"+e.toString());
                      }

                        try {
                            registro.setLoginUsuario(comentario.getJSONObject("comUsuario").getString("usuLogin"));
                            Log.i("ws", "correctoUsuario");
                        }
                        catch (Exception e) {
                            Log.i("ws", "falloUsuario"+e.toString());
                        }




                        this.listaComentario[i]=registro;
                    }
                    this.Trabajando=false;

                }
                else{
                    Log.i("ws", "response vacia");
                    this.Trabajando= false;
                }






            }
            else{
                Log.i("ws", "response incorrecta");
                this.Trabajando= false;
            }

        } catch (Exception e) {

            e.printStackTrace();
            this.Trabajando= false;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }


        /*
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "ConsultarListaComentarios";

        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";

        //Log.i("ws",URL);
        int lista=0;


        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


        request.addProperty("idDenuncia", param);
        //Log.i("ls com ws idDe:",":"+param);

        //Log.v("ws com", request.toString());


        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);


        HttpTransportSE transporte = new HttpTransportSE(URL);


        try
        {
            //Log.i("WS_COMENT", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            //Log.i("WS_COMENT", "esperando");

            Vector<SoapObject> result = (Vector<SoapObject>) envelope.getResponse();
            //Log.i("WS_COMENT", "RECIBI ALGO");

            //Log.i("WS_COMENT", "trajo algo");


            //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
            //lista = resSoap.getPropertyCount();
            lista= result.size();
            //Log.i("ws", "total de objetos:" + lista);
            this.listaComentario= new Comentario[lista];

            for (int i = 0; i < lista; i++) {
              //  Log.i("ws", "objeto:" + i);
                SoapObject ic = (SoapObject) result.get(i);
                Comentario registro= new Comentario();
                registro.set_comentario(ic.getProperty("comentario").toString());
                registro.set_fecha(ic.getProperty("fechaCreacion").toString());
                registro.setId_denuncia(Integer.parseInt(ic.getProperty("idDenuncia").toString()));
                registro.setId_denuncia(Integer.parseInt(ic.getProperty("id").toString()));
                registro.setId_wow(Integer.parseInt(ic.getProperty("idWowzer").toString()));
                registro.setIdUsuario(Integer.parseInt(ic.getProperty("idUsuario").toString()));
                registro.setLoginUsuario(ic.getProperty("loginUsuario").toString());
                registro.setUsuarioWeb(Boolean.valueOf(ic.getProperty("usuarioWeb").toString()));

                this.listaComentario[i]=registro;
            }
            this.Trabajando=false;

           // Log.i("ws", "exito");
        }

    catch(java.lang.ClassCastException e) {

        try {
                       // Log.i("WS_COMENT", "enviando");
                        transporte.call(SOAP_ACTION, envelope);

                       // Log.i("WS_COMENT", "esperando");

                        SoapObject ic = (SoapObject) envelope.getResponse();
                        //Log.i("WS_COMENT", "RECIBI ALGO");

                        //Log.i("WS_COMENT", "trajo algo");


                        //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
                        //lista = resSoap.getPropertyCount();
                        this.listaComentario= new Comentario[1];

                            Comentario registro= new Comentario();
                            registro.set_comentario(ic.getProperty("comentario").toString());
                            registro.setId_denuncia(Integer.parseInt(ic.getProperty("idDenuncia").toString()));
                            registro.setId_denuncia(Integer.parseInt(ic.getProperty("id").toString()));
                            registro.setId_wow(Integer.parseInt(ic.getProperty("idWowzer").toString()));
                            registro.setIdUsuario(Integer.parseInt(ic.getProperty("idUsuario").toString()));
                            registro.setLoginUsuario(ic.getProperty("loginUsuario").toString());
                            registro.setUsuarioWeb(Boolean.valueOf(ic.getProperty("usuarioWeb").toString()));
                            // registro.set_fecha();
                            this.listaComentario[0]=registro;

                        this.Trabajando=false;

                        Log.i("ws", "exito");
            }
                            catch( Exception ex1)
                        {
                            Log.i("ws error 1", ex1.getMessage().toString());
                            Trabajando=false;
                        }
    }



        catch (org.xmlpull.v1.XmlPullParserException ex2)
        {
            Trabajando=false;

            Log.i("wsEXCEPTION: " , ex2.getMessage().toString());
        }
        catch (SoapFault e) {
            Trabajando=false;

            Log.i("wsSOAPFAULT====","fallo");//)//e.printStackTrace());
        }
        catch (IOException e) {
            Trabajando=false;

            Log.i("wsIOEXCEPTION====","fallo");
           // e.printStackTrace();
        }
        catch (Exception e)

        {
                //  Log.i("ws error",e.getMessage().toString());
                //  todo aca tengo un problema no se como manejar que simplemente no tenga
                //  ningún comentario da un error y como realmente no es un error no tiene un error que mostrar
                //  y explota .. debo investigar mas mientras  apagado el log de errores....

            Trabajando=false;
       }

       */
        Trabajando=false;
        return this.listaComentario;
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

    protected void onPostExecute(Comentario[] comentarios)
    {
        this.Trabajando = false;
    }






}
