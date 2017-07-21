package com.jayktec.DialogaCity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Usuario on 03-11-2015.
 */
public class BootServicio extends BroadcastReceiver
        {


            @Override
public void onReceive(Context context, Intent intent) {

                Log.i("service","inicio ");
                if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                    BDControler bdlocal= new BDControler(context,1);
                    Log.i("service","inicio2 ");
                    if(!bdlocal.primeraVez()) {
                        Log.i("service","inicio3 ");
                        Intent pushIntent = new Intent(context, ServicioSincronizacion.class);
                        context.startService(pushIntent);

                        Intent pushIntent1 = new Intent(context, ServicioNotificacion.class);
                        context.startService(pushIntent1);
                    }
                }


        }


        }
