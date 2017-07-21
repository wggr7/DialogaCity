package com.jayktec.DialogaCity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yisheng Leon Espinoza on 02-Sep-15.
 */
public class ServicioSincronizacion extends Service {
    private Timer timer = new Timer();
    private static final int tiempoRefresh=300;//segundos 100000
    private static final long UPDATE_INTERVAL = 1000*tiempoRefresh;
    public static final int APP_ID_NOTIFICATION = 0;
    private NotificationCompat.Builder mensaje;
    private boolean actualizado=false;
    public static final String REFRESH_DATA_INTENT="REFRESH_DATA_INTENT";

    public ServicioSincronizacion() {
    }

    @Override

    public void onCreate()
    {
        super.onCreate();
        //onStart();

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            // El servicio se ha reiniciado
            onStart();

        return Service.START_STICKY;
    }

    public void onStart() {

        startService();

    }


    private void startService() {
        Log.i("sv sin", "inicio ");
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {

        BDControler bdlocal = new BDControler(ServicioSincronizacion.this, 1);
                        if (!bdlocal.primeraVezBdDenuncia()) {
                            ArrayList<Denuncia> arregloDenuncias = bdlocal.obtenerTodasLasDenuncias();
                            int wowzer = bdlocal.devolverIDUsrLogueado();
                            Log.i("ws BDarreglo", String.valueOf(arregloDenuncias.size()));
                            if (arregloDenuncias.size() > 0) {
                                Integer control = bdlocal.devolverIdUltimaDenuncia();

                                for (int i = 0; i < arregloDenuncias.size(); i++)
                                {

                                    Denuncia registro = arregloDenuncias.get(i);
                                    if(registro.getIdDenWow()>control)
                                        {
                                            if (registro.getIdDenWow() != 0)
                                                {
                                                    Integer[] idDenWow = {registro.getIdDenWow(), registro.getVersion(), wowzer, 1};
                                                    ActualizaDenunciaWs actualiza = new ActualizaDenunciaWs(bdlocal);
                                                    actualiza.execute(idDenWow);
                                                }
                                        }
                                    else
                                    if (registro.getIdDenWow() == 0)
                                        {
                                            Log.i("ws", "intentando guardar denucias en MYSQL");
                                           DenunciaWs guardaDenunciaMysql = new DenunciaWs(bdlocal);
                                            Denuncia[] lista = {registro};
                                            guardaDenunciaMysql.execute(lista);
                                        }
                                    else
                                        {
                                            Log.i("ws", "sin denuncias nuevas: "+wowzer);
                                        }
                                    if(registro.getIdWowzer()==wowzer)
                                        {
                                            Integer[] param = {registro.getIdDenWow(), registro.getVersion(), wowzer};
                                            VerificarVersionWs verifica = new VerificarVersionWs(bdlocal);
                                            verifica.execute(param);
                                        }
                                }


                                actualizado = true;
                            }
                        }
          else
              {
                            Integer[] idDenWow;
                            if(bdlocal.devolverIDUsrLogueado()==0){
                                idDenWow = new Integer[]{0, 0, 0,0};

                            }
                            else{
                                int wow = bdlocal.devolverIDUsrLogueado();
                                idDenWow = new Integer[]{0, 0,wow,0};
                            }

                            ActualizaDenunciaWs actualiza = new ActualizaDenunciaWs(bdlocal);
                            actualiza.execute(idDenWow);
                            actualizado = true;

                        }


                        Intent broadcastIntent = new Intent();
        // broadcastIntent.putExtra("id", Integer.parseInt(ic.getProperty("id").toString());
        broadcastIntent.setAction(REFRESH_DATA_INTENT);
        sendBroadcast(broadcastIntent);
        bdlocal.close();


   /*     timer.scheduleAtFixedRate(
                new TimerTask() {
                      public void run() {
  //                        Log.i("sv sin","bucle");
                        BDControler bdlocal = new BDControler(ServicioSincronizacion.this, 1);
                      //  if (!bdlocal.primeraVez()) {
                            ArrayList<Denuncia> arregloDenuncias = bdlocal.obtenerTodasLasDenuncias();
                            int wowzer = bdlocal.devolverIDUsrLogueado();
                            if (arregloDenuncias.size() > 0) {
                                for (int i = 0; i < arregloDenuncias.size(); i++) {

                                    Denuncia registro = arregloDenuncias.get(i);
                                    Integer[] idDenWow = {registro.getIdDenWow(), registro.getVersion(), wowzer};
                                   // Log.i("sv sin", "emp:" + registro.getIdDenWow());
                                   // Log.i("sv sin", "ver:" + registro.getVersion());
                                   // Log.i("sv sin", "wow:" + wowzer);
                                    ActualizaDenunciaWs actualiza = new ActualizaDenunciaWs(bdlocal);
                                    actualiza.execute(idDenWow);
                                    actualizado = true;


                                }


                            }

                            Intent broadcastIntent = new Intent();
                           // broadcastIntent.putExtra("id", Integer.parseInt(ic.getProperty("id").toString());
                            broadcastIntent.setAction(REFRESH_DATA_INTENT);
                            sendBroadcast(broadcastIntent);
                            bdlocal.close();

                        //}*/
                    }
                },
                0,
                UPDATE_INTERVAL);
    }
}
