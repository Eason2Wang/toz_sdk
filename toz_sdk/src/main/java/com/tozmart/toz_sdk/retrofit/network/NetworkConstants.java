package com.tozmart.toz_sdk.retrofit.network;

import com.tozmart.toz_sdk.R;

/**
 * Created by wys on 17/5/12.
 */

public class NetworkConstants {
    public static final int NET_CODE_SUCCESS = 0;
    public static final int NET_CODE_ERROR = -1;

    public static final int NET_CODE_CONNECT = 400;
    public static final int NET_CODE_UNKNOWN_HOST = 401;
    public static final int NET_CODE_SOCKET_TIMEOUT = 402;

    public static final int CONNECT_EXCEPTION = R.string.connect_exception;
    public static final int SOCKET_TIMEOUT_EXCEPTION = R.string.socket_time_out_exception;
    public static final int UNKNOWN_HOST_EXCEPTION = R.string.unknow_host_exception;
}
