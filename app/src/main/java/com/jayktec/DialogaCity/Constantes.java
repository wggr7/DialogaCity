package com.jayktec.DialogaCity;

/**
 * Created by yisheng on 07-12-16.
 */
/**
 * Clase que contiene los códigos usados
 * mantener la integridad en las interacciones entre actividades
 * y fragmentos
 */
public class Constantes {
    /**
     * Transición Home -> Detalle
     */
    public static final int CODIGO_DETALLE = 100;

    /**
     * Transición Detalle -> Actualización
     */
    public static final int CODIGO_ACTUALIZACION = 101;

    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta carácteristica.
     */
    private static final String PUERTO_HOST = "8080";

    /**
     * Dirección IP de genymotion o AVD
     */
    private static final String IP = "http://www.jayktec.com.ve:";
    /**
     * URLs del Web Service
     */
    public static final String APLICACION = IP + PUERTO_HOST + "/DialogaWeb-1.0-SNAPSHOT/";
    public static final String WSDENUNCIAS = APLICACION + "/DenunciasAndroid";

    /**
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String EXTRA_ID = "IDEXTRA";

}