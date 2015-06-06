package com.gaoyuan4122.download.app;

public class GlobalConfig {

    public static final int NETWORK_STATE_IDLE = -1;
    public static final int NETWORK_STATE_WIFI = 1;
    public static final int NETWORK_STATE_CMNET = 2;
    public static final int NETWORK_STATE_CMWAP = 3;
    public static final int NETWORK_STATE_CTWAP = 4;

    public static int CURRENT_NETWORK_STATE_TYPE = NETWORK_STATE_IDLE;

    public static int HTTP_CONNECTION_TIMEOUT = 50 * 1000;
    public static int HTTP_SO_TIMEOUT = 40 * 1000;
    public static int HTTP_SOCKET_BUFFER_SIZE = 8 * 1024;

}
