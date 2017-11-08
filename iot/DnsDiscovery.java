package com.oneproject.www.allinone;

import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.util.Log;

import com.oneproject.www.allinone.NetWork.HardwareAddress;
import com.oneproject.www.allinone.NetWork.HostBean;
import com.oneproject.www.allinone.NetWork.NetInfo;
import com.oneproject.www.allinone.Utils.Prefs;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by admin on 2017-07-15.
 */

public class DnsDiscovery extends AbstractDiscovery {

    private final String TAG = "DnsDiscovery";

    public DnsDiscovery(DiscoveryActivity discover) {
        super(discover);
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mDiscover != null) {
            final DiscoveryActivity discover = mDiscover.get();
            if (discover != null) {
                Log.i(TAG, "start=" + NetInfo.getIpFromLongUnsigned(start) + " (" + start
                        + "), end=" + NetInfo.getIpFromLongUnsigned(end) + " (" + end
                        + "), length=" + size);

                int timeout = Integer.parseInt(discover.prefs.getString(Prefs.KEY_TIMEOUT_DISCOVER,
                        Prefs.DEFAULT_TIMEOUT_DISCOVER));
                Log.i(TAG, "timeout=" + timeout + "ms");

                for (long i = start; i < end + 1; i++) {
                    hosts_done++;
                    HostBean host = new HostBean();
                    host.ipAddress = NetInfo.getIpFromLongUnsigned(i);
                    try {
                        InetAddress ia = InetAddress.getByName(host.ipAddress);
                        host.hostname = ia.getCanonicalHostName();
                        host.isAlive = ia.isReachable(timeout) ? 1 : 0;
                    } catch (java.net.UnknownHostException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    if (host.hostname != null && !host.hostname.equals(host.ipAddress)) {
                        // Is gateway ?
                        if (discover.net.gatewayIp.equals(host.ipAddress)) {
                            host.deviceType = 1;
                        }
                        // Mac Addr
                        host.hardwareAddress = HardwareAddress.getHardwareAddress(host.ipAddress);
                        // NIC vendor
                        try {
                            host.nicVendor = HardwareAddress.getNicVendor(host.hardwareAddress);
                        } catch (SQLiteDatabaseCorruptException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        publishProgress(host);
                    } else {
                        publishProgress((HostBean) null);
                    }
                }
            }
        }
        return null;
    }
}
