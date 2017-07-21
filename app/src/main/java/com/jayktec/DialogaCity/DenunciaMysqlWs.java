
/**
 * Created by Usuario on 25-08-2015.
 */
package com.jayktec.DialogaCity;
        import android.app.Activity;
        import android.os.AsyncTask;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import com.android.volley.*;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.Volley;

        import java.util.Date;

/**y
 * Created by Usuario on 25-08-2015.
 */
public class DenunciaMysqlWs   extends AsyncTask<Integer,Integer,Boolean> {

    private Boolean resultado= false;
    private Integer idDenuncia=0;
    private Integer wowzer=0;
    public Activity laActividad;
    private RequestQueue mRequestQueue;


    public DenunciaMysqlWs(Activity act){
        this.laActividad = act;
    }

    public DenunciaMysqlWs(){

    }



    protected Boolean doInBackground(Integer... params) {

        idDenuncia=params[0];
        wowzer=params[1];
        BDControler BDTelefono = new BDControler(laActividad.getApplicationContext(),1);
        final String URL="http://jayktec.com.ve:8080/DialogaWeb/servicios/Denuncias";
        mRequestQueue =  Volley.newRequestQueue(laActividad.getApplicationContext());

        Log.i("ws",URL);

        int lista=0;


        Log.i("ws contDenuncia", "" + idDenuncia);

        Log.i("ws sin", "ya anadi las propiedades");

        try
        {
        JsonArrayRequest respuesta = new JsonArrayRequest(URL, new Response.Listener<JSONArray>(){
        @Override
        public void onResponse(JSONArray response) {
            Log.d("wsDenuncia", "Respuesta Volley:" + response.toString());
               int lista= response.length();
            BDControler BDTelefono = new BDControler(laActividad.getApplicationContext(),1);

            for (int i=0;i<lista;i++)
            {
                try {
                    JSONObject ic=(JSONObject) response.get(i);
                    //Log.d("Ws ic:",ic.toString());
                    Denuncia registro= new Denuncia();
                    Log.d("Ws ",ic.getString("denDescripcion"));
                    registro.setComentarios(ic.getString("denDescripcion"));
                    registro.setIdDenWow(ic.getInt("idDenuncia"));
                    Date fecha= new Date(ic.getInt("denFecha"));
                    Log.d("Ws fecha:",fecha.toString());
                    registro.setFechaDenuncia(fecha.toString());
                    registro.setTipoDenuncia((ic.getInt("denTipo")));
                    registro.setEstadoDenuncia(ic.getInt("denEstado"));
                    registro.setLongitud(Double.parseDouble(ic.getString("denLongitud")));
                    Log.i("ws lat", (ic.getString("denLongitud")));
                    registro.setLatitud(ic.getDouble("denLatitud"));
                    Log.i("ws lon", (ic.getString("denLatitud")));

                    registro.setRating(Double.parseDouble(ic.getString("denValoracion")));
                    //registro.setWowzer(ic.getString("loginUsuario"));
                    registro.setIdWowzer(ic.getInt("denWowzer"));
                    registro.setVersion(ic.getInt("denVersion"));
                    //registro.setUsuarioWeb(ic.getBoolean("usuarioWeb"));

                    //si la denuncia no existe en la bd la inserta
                    if (!BDTelefono.verificarDenuncia(ic.getInt("idDenuncia"))) {
                        BDTelefono.insertarDenuncia(registro);
                    }
                    if (i == lista -1){
                        BDTelefono.actualizarControlDenuncia(registro.getIdDenWow());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


    },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("wsDenuncia", "Error:" + error.getMessage());

                }
            }
        );
            Log.d("ws sin", "VOY A ANADIR");
            mRequestQueue.add(respuesta);
            //laActividad.getApplicationContext();
            // AppController.getInstance().addToRequestQueue(respuesta);
            Log.d("ws sin", "finalize");

      /*      //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
            //lista = resSoap.getPropertyCount();
            lista= result.size();
            Log.i("ws sin", "total de objetos:" + lista);


            for (int i = 0; i < lista; i++) {
             //   Log.i("ws sin", "objeto:" + i);
                SoapObject ic = (SoapObject) result.get(i);
                Denuncia registro= new Denuncia();
                registro.setComentarios(ic.getProperty("descripcion").toString());
                registro.setIdDenWow(Integer.parseInt(ic.getProperty("id").toString()));
                registro.setFechaDenuncia(ic.getProperty("fecha").toString());
                registro.setTipoDenuncia(Integer.parseInt(ic.getProperty("tipo").toString()));
                registro.setEstadoDenuncia(ic.getProperty("estado").toString());
                registro.setLongitud(Double.parseDouble(ic.getProperty("longitud").toString()));
               // Log.i("ws lat", (ic.getProperty("latitud").toString()));
                registro.setLatitud(Double.parseDouble(ic.getProperty("latitud").toString()));
               // Log.i("ws lon", (ic.getProperty("longitud").toString()));

                registro.setRating(Double.parseDouble(ic.getProperty("valoracion").toString()));
                registro.setWowzer(ic.getProperty("loginUsuario").toString());
                registro.setIdWowzer(Integer.parseInt(ic.getProperty("wowzer").toString()));
                registro.setVersion(Integer.parseInt(ic.getProperty("version").toString()));
                registro.setUsuarioWeb(Boolean.getBoolean(ic.getProperty("usuarioWeb").toString()));

                //si la denuncia no existe en la bd la inserta
                if (!BDTelefono.verificarDenuncia(Integer.parseInt(ic.getProperty("id").toString()))) {
                    BDTelefono.insertarDenuncia(registro);
                }
                    if (i == lista -1){
                    BDTelefono.actualizarControlDenuncia(registro.getIdDenWow());
                }
}*/
            resultado = true;
        if (wowzer==0)
        {
            BDTelefono.PrimeraVez(0);
        }

           // Log.i("ws", "exito");

        }
        catch (NullPointerException e)
        {

            Log.i("ws nulo",e.getMessage()+"");

            return resultado ;


        }

        catch (Exception e)
        {

            Log.i("ws E",e.getMessage()+"");

            return resultado ;


        }
        return resultado ;


    }


    protected void onPostExecute(boolean result) {

        if (result)
        {

        }
        else
        {
           Log.i("ws Denuncia","no trajo registros");
        }

    }






}
