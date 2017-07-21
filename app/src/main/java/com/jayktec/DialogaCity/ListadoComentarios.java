package com.jayktec.DialogaCity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yisheng  15-09-2015.
 *  Mod 08-Feb-2016: agregar el detalle de la denuncia como primer comentario
 *  para evitar valores NULL
 */
public class ListadoComentarios extends Activity {
    private ArrayList<ListadoComentarioItem> arregloListadoDeItem = new ArrayList<ListadoComentarioItem>();
    private ListView miListView;
    private Comentario[] arregloComentarios;
    private String denuncia;
    private int idDenuncia;
    private int idDenunciaWs;
    private int iDUsuarioWow;
    private String misComentarios;
    private String fechaComentario;
    private String usrWowzer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_comentarios);
        ComentarioMysqlWs listado = new ComentarioMysqlWs();
        Bundle datosEntrada = getIntent().getExtras();

        idDenuncia = datosEntrada.getInt("IDDenuncia");
        iDUsuarioWow = datosEntrada.getInt("wowzer");
        idDenunciaWs = datosEntrada.getInt("IDDenunciaWS");
        misComentarios = datosEntrada.getString("ComentDenuncia");
        usrWowzer=datosEntrada.getString("DenWowzer");
        String denFecha=null;
        long val2 = Long.valueOf(datosEntrada.getString("FechaDenuncia"));
        Date fech2 = new Date(val2);
        SimpleDateFormat newFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        denFecha = newFormat2.format(fech2);

        fechaComentario = denFecha;
        denuncia = String.valueOf(idDenunciaWs);

        ListadoComentarioItem itemEntradaListado = new ListadoComentarioItem();
        itemEntradaListado.TextoDenuncia = misComentarios;
        itemEntradaListado.Fecha = fechaComentario;
        itemEntradaListado.Usuario = usrWowzer; //usrWowzer;
         //ListadoComentarioItem(0,misComentarios,0,iDUsuarioWow,"Pepe",0,fechaComentario);
        arregloListadoDeItem.add(itemEntradaListado);

        String[] prueba = {denuncia};

        listado.execute(prueba);
        Log.i("lCom,fin denuncia:", denuncia);

        ProgressDialog pd =new ProgressDialog (ListadoComentarios.this);
        pd.setMessage("Cargando comentarios.....");
        pd.show();
        while (listado.getTrabajando() ) {
//        Log.i("lCom SUCIIO fin:",listado.getTrabajando().toString());
        //esperando que finalice el ws
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
        }
     }, 100);
     }
pd.dismiss();
        arregloComentarios = listado.getListaComentario();

       Log.i("Ls hgds ccom", "empezamos con los comentarios");
       Log.i("lCom SUCIIO fin:", listado.getTrabajando().toString());
        try {
            if (arregloComentarios.length > 0) {

                Date fecha =new Date();
                for (int i = 0; i < arregloComentarios.length; i++) {
                    Log.i("Ls hgds ccom", "empezamos con los comentarios");
                    ListadoComentarioItem listadoItem = new ListadoComentarioItem();
                    Comentario com = arregloComentarios[i];
                    listadoItem.TextoDenuncia = com.get_comentario();

                    String formatedDate=null;
/*
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyMMddHHmmss");

                    try {
                        Date date = originalFormat.parse(com.get_fecha());
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        formatedDate = newFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
*/ try {
                        long val = Long.valueOf(com.get_fecha());
                        Date fech = new Date(val);
                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                        formatedDate = newFormat.format(fech);
                        Log.i("ws", "correcto");
                    }
                    catch (Exception e){
                        Log.i("ws", "error"+e.toString());
                    }



                    listadoItem.Fecha = formatedDate;
                    listadoItem.Usuario = com.getLoginUsuario();
                    arregloListadoDeItem.add(listadoItem);
                }
            }
        } catch
                (NullPointerException e) {
            Log.i("ls comen", "no hay comentario");
        }


        AdaptadorListadoComentario miAdaptador = new AdaptadorListadoComentario(this, R.layout.listado_comentario_item, arregloListadoDeItem);

        miListView = (ListView) findViewById(R.id.ListaDeComentarios);
        View cabecera = (View) getLayoutInflater().inflate(R.layout.cabecera_listado_comentario, null);
        miListView.addHeaderView(cabecera);
        miListView.setAdapter(miAdaptador);
        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        Button botonMas= (Button)findViewById(R.id.nuevoComentario);

        botonMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  finish();
                  Intent miIntent = new Intent(getApplicationContext(),ComentarioActivity.class);
                  miIntent.putExtra("denuncia", denuncia);
                  miIntent.putExtra("UsuarioWowzer", iDUsuarioWow);


                Bundle datosComentarios = new Bundle();
                datosComentarios.putInt("IDDenuncia",idDenuncia);
                datosComentarios.putInt("IDWowzer", iDUsuarioWow);
                datosComentarios.putInt("IDDenunciaWS", idDenunciaWs);
                datosComentarios.putString("ComentDenuncia", misComentarios);
                miIntent.putExtras(datosComentarios);
                startActivity(miIntent);

            }
        });

        ImageButton botonCerrar = (ImageButton) findViewById(R.id.BotonCerrarListado);
        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Intent miIntent = new Intent(getApplicationContext(),laboratorios.mova.hereame.MainActivity.class);
                // startActivity(miIntent);
                finish();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_listado_denuncias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
