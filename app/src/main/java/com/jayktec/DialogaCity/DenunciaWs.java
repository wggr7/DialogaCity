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
public class DenunciaWs extends AsyncTask<Denuncia,Void, Boolean> {

    private int idDenuncia;
    public Activity actividad;

    /**
     * constructor que recibe la actividad para poder grabar e la Base de Datos
     * @param act
     */
    public DenunciaWs(Activity act){
        this.actividad = act;
    }

    /**
     * Constructor por Defecto
     */
    public DenunciaWs(){
    }

    /**
     * Metodo asincrono para trabajar el WS de modo Background
     * @param params Denuncia en modo paramtro
     * @return resultado del WS
     */
    protected Boolean doInBackground(Denuncia... params) {
        boolean result = false;
        BDControler BDLocal = new BDControler(actividad.getApplicationContext(),1);
        SoapObject resultsRequestSOAP;
        Denuncia denuncia = params[0];


    /*    Log.i("ws",denuncia.getIdDenuncia().toString());
        Log.i("ws",denuncia.getIdWowzer().toString());
        Log.i("ws",String.valueOf(denuncia.getTipoDenuncia()));
        Log.i("ws",denuncia.getFechaDenuncia());
        Log.i("ws",denuncia.getComentarios() );
        Log.i("ws",denuncia.getEstadoDenuncia() );
        Log.i("ws",denuncia.getRating().toString() );

        Log.i("ws",String.valueOf(denuncia.getImagen()));


        Log.i("ws","aqui se parte");

*/


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("DenWowzer", denuncia.getIdWowzer());
            jsonObj.put("DenTipo", denuncia.getTipoDenuncia());
            jsonObj.put("DenDescripcion", denuncia.getComentarios());
            jsonObj.put("DenEstado", denuncia.getEstadoDenuncia());
            jsonObj.put("DenLongitud", denuncia.getEstadoDenuncia());
            jsonObj.put("DenLatitud", denuncia.getEstadoDenuncia());
            jsonObj.put("DenValoracion", denuncia.getRating().toString());
            if(denuncia.getImagen()==null) {
                jsonObj.put("DenImagen", "");
            }
            else{
                jsonObj.put("DenImagen", Base64.encode(denuncia.getImagen()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("ws", jsonObj.toString());

        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://jayktec.com.ve/DialogaWeb/servicios/Denunciar");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Denuncia", jsonObj.toString());
            connection.setRequestMethod("POST"); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`


            //Send request
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Denuncia", jsonObj.toString()));



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
       /*         JSONObject jsonresp = new JSONObject(sb.toString());
                idDenuncia= jsonresp.getInt("idDenuncia");

                BDLocal.actualizaDenunciaInsercion(denuncia,idDenuncia);
                result=true;
*/
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

/*



        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        //Prueba imagen
        // final String METHOD_NAME = "CopiarImagen";
       //  request.addProperty("imgBytes", Base64.encode(denuncia.getImagen()));

                  final String METHOD_NAME = "CrearDenuncia";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
       DenunciaXml mensaje = new DenunciaXml(denuncia);
       request.addProperty("denuncia", mensaje);
        //  request.addProperty("imgBytes", Base64.encode(denuncia.getImagen()));
        Log.i("ws","aqui se parte");
        Log.i("ws", (String) mensaje.getProperty(3));

        Log.i("WS_DENUNCIA", "ya anadi las propiedades");
        Log.i("WS_DENUNCIA", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        //Prueba imagen
        envelope.addMapping(SOAP_ACTION, "denuncia", new DenunciaXml().getClass());

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            Log.i("WS_DENUNCIA", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            Log.i("WS_DENUNCIA", "esperando");
            SoapObject resSoap =(SoapObject)envelope.getResponse();
            Log.i("WS_DENUNCIA", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj==null)
            {
                Log.i("WS_DENUNCIA", "nulo");

            }
            else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                Log.i("WS_DENUNCIA", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
                Log.i("WS_DENUNCIA", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                    Log.i("WS_DENUNCIA", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);

                    Log.i("WS_DENUNCIA id", ic.getProperty("idGenerico").toString());
                    idDenuncia= Integer.parseInt(ic.getProperty("idGenerico").toString());

                    BDLocal.actualizaDenunciaInsercion(denuncia,idDenuncia);
                    Log.i("WS_DENUNCIA fin act", ic.getProperty("idGenerico").toString());
                    Log.i("WS_DENUNCIA error", ic.getProperty("mensaje").toString());
                    //BDLocal.actualizarControlDenuncia(idDenuncia);

                    result = true;
                }
            }
            Log.i("WS_DENUNCIA", "exito");
        }
        catch (org.xmlpull.v1.XmlPullParserException ex2)
        {
            Log.i(" wsEXCEPTION: " , ex2.getMessage().toString());
            result = false;
        }
        catch (SoapFault e) {

            Log.i("wsSOAPFAULT", "aca fallo");//(String) e.printStackTrace());
            result = false;
        }
        catch (IOException e) {
            Log.i("wsSOAPFAULT", "acax fallo :"+e);//(String) e.printStackTrace());

            //System.out.println("IOEXCEPTION====");
            //e.printStackTrace();
            result = false;
        }

        catch (java.lang.NullPointerException ex1)
        {
           // Log.i("ws error:", ex1.getMessage().toString().substring(1,15));//e.getMessage().toString());
            result = false;
        }
        catch (Exception e)
        {
          //  Log.i("ws error:",e.getCause().toString());//e.getMessage().toString());
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

        if (result)
        {

        }
        else
        {
            idDenuncia=0;

        }
    }



    public Integer idDenuncia()
    {
        return     idDenuncia;
    }


}