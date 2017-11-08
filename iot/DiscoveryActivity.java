package com.oneproject.www.allinone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oneproject.www.allinone.NetWork.HostBean;
import com.oneproject.www.allinone.NetWork.NetInfo;
import com.oneproject.www.allinone.Utils.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.oneproject.www.allinone.DBHelper.TABLE_NAME3;
import static com.oneproject.www.allinone.DBHelper._IP;
import static com.oneproject.www.allinone.DBHelper._NIC;

public class DiscoveryActivity extends ActivityNet {
    private final String TAG = "ActivityDiscovery";
    public final static long VIBRATE = (long) 250;
    public final static int SCAN_PORT_RESULT = 1;
    public static final int MENU_SCAN_SINGLE = 0;
    public static final int MENU_OPTIONS = 1;
    public static final int MENU_HELP = 2;
    private static final int MENU_EXPORT = 3;
    private static LayoutInflater mInflater;
    private int currentNetwork = 0;
    private long network_ip = 0;
    private long network_start = 0;
    private long network_end = 0;
    private List<HostBean> hosts = null;
    private HostsAdapter adapter;
    private Button btn_discover;
    Button btnSubmit, btnCancel;
    private AbstractDiscovery mDiscoveryTask = null;

    SQLiteDatabase db;
    DBHelper mHelper;
    Cursor cursor;

    public ArrayList<DiscoveryResults> temp;
    ArrayList<String> temp_nic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        mInflater = LayoutInflater.from(ctxt);
        temp = new ArrayList<>();
        temp_nic = new ArrayList<>();
        setTitle(R.string.title_iot);
        try {
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
        } catch (Exception e) {

        }

        // Discover
        btn_discover = (Button) findViewById(R.id.btn_discover);
        btnCancel = (Button) findViewById(R.id.btnCancel_discovery);
        btnSubmit = (Button) findViewById(R.id.btnSubmit_discovery);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDiscovering();
            }
        });
        // Hosts list
        adapter = new HostsAdapter(ctxt);
        ListView list = (ListView) findViewById(R.id.output);
        list.setAdapter(adapter);
        list.setItemsCanFocus(false);
        list.setOnItemClickListener(mItemClickListener);
        list.setEmptyView(findViewById(R.id.list_empty));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscovering();
                DiscoveryResults discoveryResults;
                ArrayList<String> temp1 = new ArrayList<String>();
                ArrayList<String> temp2 = new ArrayList<String>();
                ArrayList<String> temp3 = new ArrayList<String>();
                ArrayList<String> temp4 = new ArrayList<String>();
                int t = 0;

                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getCheck() == 1) {
                        discoveryResults = temp.get(i);
                        temp1.add(t, discoveryResults.getHostname());
                        temp2.add(t, discoveryResults.getIpaddress());
                        temp3.add(t, discoveryResults.getHardwareaddress());
                        temp4.add(t, temp_nic.get(i));
                        t++;
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("hostname", temp1);
                intent.putStringArrayListExtra("ip", temp2);
                intent.putStringArrayListExtra("hardware", temp3);
                intent.putStringArrayListExtra("nic", temp4);
                intent.putExtra("count", t);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        cancelTasks();
        temp.clear();
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.setChecked(position);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void setInfo() {
        // Info
        ((TextView) findViewById(R.id.info_ip)).setText(info_ip_str);
        ((TextView) findViewById(R.id.info_in)).setText(info_in_str);
        ((TextView) findViewById(R.id.info_mo)).setText(info_mo_str);

        // Scan button state
        if (mDiscoveryTask != null) {
            setButton(btn_discover, R.drawable.cancel, false);
            btn_discover.setText(R.string.btn_cancel);
            btn_discover.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cancelTasks();
                }
            });
        }

        if (currentNetwork != net.hashCode()) {
            Log.i(TAG, "Network info has changed");
            currentNetwork = net.hashCode();

            // Cancel running tasks
            cancelTasks();
        } else {
            return;
        }

        // Get ip information
        network_ip = NetInfo.getUnsignedLongFromIp(net.ip);
        if (prefs.getBoolean(Prefs.KEY_IP_CUSTOM, Prefs.DEFAULT_IP_CUSTOM)) {
            // Custom IP
            network_start = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_START,
                    Prefs.DEFAULT_IP_START));
            network_end = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_END,
                    Prefs.DEFAULT_IP_END));
        } else {
            // Custom CIDR
            if (prefs.getBoolean(Prefs.KEY_CIDR_CUSTOM, Prefs.DEFAULT_CIDR_CUSTOM)) {
                net.cidr = Integer.parseInt(prefs.getString(Prefs.KEY_CIDR, Prefs.DEFAULT_CIDR));
            }
            // Detected IP
            int shift = (32 - net.cidr);
            if (net.cidr < 31) {
                network_start = (network_ip >> shift << shift) + 1;
                network_end = (network_start | ((1 << shift) - 1)) - 1;
            } else {
                network_start = (network_ip >> shift << shift);
                network_end = (network_start | ((1 << shift) - 1));
            }
            // Reset ip start-end (is it really convenient ?)
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Prefs.KEY_IP_START, NetInfo.getIpFromLongUnsigned(network_start));
            edit.putString(Prefs.KEY_IP_END, NetInfo.getIpFromLongUnsigned(network_end));
            edit.commit();
        }
    }

    protected void setButtons(boolean disable) {
        if (disable) {
            setButtonOff(btn_discover, R.drawable.disabled);
        } else {
            setButtonOn(btn_discover, R.drawable.discover);
        }
    }

    protected void cancelTasks() {
        if (mDiscoveryTask != null) {
            mDiscoveryTask.cancel(true);
            mDiscoveryTask = null;
        }
    }
    //이게 필요할까
    /*
    intent.putExtra(EXTRA_WIFI, NetInfo.isConnected(ctxt));
    intent.putExtra(HostBean.EXTRA, host);
    */

    static class ViewHolder {
        LinearLayout llList;
        TextView host;
        //TextView mac;
        TextView vendor;
        ImageView logo;
        CheckBox cb;
    }

    // Custom ArrayAdapter
    private class HostsAdapter extends ArrayAdapter<Void> {
        ArrayList<Boolean> isCheckedConfrim = new ArrayList<>();

        public HostsAdapter(Context ctxt) {
            super(ctxt, R.layout.list_host, R.id.list);
        }

        public void setChecked(int position) {
            isCheckedConfrim.set(position, !isCheckedConfrim.get(position));
            //Toast.makeText(getContext(),isCheckedConfrim.get(position)+"",Toast.LENGTH_SHORT).show();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DiscoveryResults discoveryResults = new DiscoveryResults();
            try {
                isCheckedConfrim.get(position);
            } catch (Exception e) {
                isCheckedConfrim.add(position, false);
            }
            String ip = "";
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_host, null);
                holder = new ViewHolder();
                holder.llList = (LinearLayout) convertView.findViewById(R.id.llList);
                holder.host = (TextView) convertView.findViewById(R.id.list);
                //holder.mac = (TextView) convertView.findViewById(R.id.mac);
                holder.vendor = (TextView) convertView.findViewById(R.id.vendor);
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
                holder.cb.setClickable(false);
                holder.cb.setFocusable(false);
                holder.cb.setChecked(isCheckedConfrim.get(position));
                if (isCheckedConfrim.get(position))
                    discoveryResults.check = 1;
                else
                    discoveryResults.check = 0;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.cb.setChecked(isCheckedConfrim.get(position));
                if (isCheckedConfrim.get(position))
                    discoveryResults.check = 1;
                else
                    discoveryResults.check = 0;
            }
            final HostBean host = hosts.get(position);

            //여기서 IOT아이콘띄우면좋을듯
            if (host.deviceType == HostBean.TYPE_GATEWAY) {
                holder.logo.setImageResource(R.drawable.router);
                isCheckedConfrim.set(position,false);
                holder.cb.setChecked(isCheckedConfrim.get(position));
            } else if (host.isAlive == 1 || !host.hardwareAddress.equals(NetInfo.NOMAC)) {
                holder.logo.setImageResource(R.drawable.computer);
            } else {
                holder.logo.setImageResource(R.drawable.computer_down);
            }
            if (host.hostname != null && !host.hostname.equals(host.ipAddress)) {
                holder.host.setText(host.hostname + " (" + host.ipAddress + ")");
                discoveryResults.hostname = host.hostname;
                discoveryResults.ipaddress = host.ipAddress;
            } else {
                holder.host.setText(host.ipAddress);
                discoveryResults.ipaddress = host.ipAddress;
            }
            String nic_tempp = "";
            try {
                cursor = db.rawQuery(String.format("select %s,%s from %s where %s='%s'", _IP, _NIC, TABLE_NAME3, _IP, host.ipAddress), null);
                while (cursor.moveToNext()) {
                    ip = cursor.getString(cursor.getColumnIndex(_IP));
                    nic_tempp = cursor.getString(cursor.getColumnIndex(_NIC));
                }
            } catch (Exception e) {
                ip = "";
            }
            if (ip.equals(host.ipAddress)) {
                //날리던지일단 임시
                db.execSQL(String.format("delete from %s where %s='%s'", TABLE_NAME3, _IP, host.ipAddress));
                if (discoveryResults.checked) {
                    holder.llList.setBackgroundResource(R.color.color_list_background);
                    //1
                    isCheckedConfrim.set(position, true);
                    holder.cb.setChecked(isCheckedConfrim.get(position));
                    temp_nic.add(position, nic_tempp);
                    discoveryResults.checked = !discoveryResults.checked;
                }
            }
            String nic_temp = "";
            //if (!host.hardwareAddress.equals(NetInfo.NOMAC)) {
            // holder.mac.setText(host.hardwareAddress);
            //discoveryResults.hardwareaddress = host.hardwareAddress;
            //if (host.nicVendor != null) {
            //holder.vendor.setText(host.nicVendor);
            //discoveryResults.nicVender = host.nicVendor;
            //} else {
            //if (discoveryResults.nicVender.equals("")) {


            try {
                holder.vendor.setText(temp_nic.get(position));
            } catch (Exception e) {
                if (!(host.deviceType == HostBean.TYPE_GATEWAY)) {
                    String temp[] = {"KT IoT", "android", "LG U+ IoT", "Gas Detecter IoT", "SKT IoT"};
                    Random r = new Random();
                    int a;
                    a = r.nextInt(5);
                    nic_temp = temp[a];
                    //}
                } else {
                    nic_temp="Router";
                }
                temp_nic.add(position, nic_temp);
                holder.vendor.setText(temp_nic.get(position));
            }finally {
                if(temp_nic.get(position).equals("android")){
                    isCheckedConfrim.set(position,false);
                    holder.cb.setChecked(isCheckedConfrim.get(position));
                }
            }

            holder.vendor.setVisibility(View.VISIBLE);
            //일단 임시
            //discoveryResults.check=0;
            try {
                temp.get(position);
            } catch (Exception e) {
                temp.add(position, discoveryResults);
            } finally {
                temp.set(position, discoveryResults);
            }

            return convertView;
        }
    }

    /**
     * Discover hosts
     */
    private void startDiscovering() {
        int method = 0;
        try {
            method = Integer.parseInt(prefs.getString(Prefs.KEY_METHOD_DISCOVER,
                    Prefs.DEFAULT_METHOD_DISCOVER));
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        switch (method) {
            case 1:
                mDiscoveryTask = new DnsDiscovery(DiscoveryActivity.this);
                break;
            case 2:
                // Root
                break;
            case 0:
            default:
                mDiscoveryTask = new DefaultDiscovery(DiscoveryActivity.this);
        }
        mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
        mDiscoveryTask.execute();
        btn_discover.setText(R.string.btn_cancel);
        setButton(btn_discover, R.drawable.cancel, false);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelTasks();
            }
        });
        makeToast(R.string.discover_start);
        //툴바가 있어야 적용된다함
        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);
        initList();
    }

    public void stopDiscovering() {
        Log.e(TAG, "stopDiscovering()");
        mDiscoveryTask = null;
        setButtonOn(btn_discover, R.drawable.discover);
        btn_discover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDiscovering();
            }
        });
        setProgressBarVisibility(false);
        setProgressBarIndeterminateVisibility(false);
        btn_discover.setText(R.string.btn_discover);
    }

    private void initList() {
        // setSelectedHosts(false);
        adapter.clear();
        hosts = new ArrayList<HostBean>();
    }

    public void addHost(HostBean host) {
        host.position = hosts.size();
        hosts.add(host);
        adapter.add(null);
    }

    //PortScan부분
/*
    public static void scanSingle(final Context ctxt, String ip) {
        // Alert dialog
        View v = LayoutInflater.from(ctxt).inflate(R.layout.scan_single, null);
        final EditText txt = (EditText) v.findViewById(R.id.ip);
        if (ip != null) {
            txt.setText(ip);
        }
        AlertDialog.Builder dialogIp = new AlertDialog.Builder(ctxt);
        dialogIp.setTitle(R.string.scan_single_title);
        dialogIp.setView(v);
        dialogIp.setPositiveButton(R.string.btn_scan, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                // start scanportactivity
                Intent intent = new Intent(ctxt, ActivityPortscan.class);
                intent.putExtra(HostBean.EXTRA_HOST, txt.getText().toString());
                try {
                    intent.putExtra(HostBean.EXTRA_HOSTNAME, (InetAddress.getByName(txt.getText()
                            .toString()).getHostName()));
                } catch (UnknownHostException e) {
                    intent.putExtra(HostBean.EXTRA_HOSTNAME, txt.getText().toString());
                }
                ctxt.startActivity(intent);
            }
        });
        dialogIp.setNegativeButton(R.string.btn_discover_cancel, null);
        dialogIp.show();
    }

    private void export() {
        final Export e = new Export(ctxt, hosts);
        final String file = e.getFileName();

        View v = mInflater.inflate(R.layout.dialog_edittext, null);
        final EditText txt = (EditText) v.findViewById(R.id.edittext);
        txt.setText(file);

        AlertDialog.Builder getFileName = new AlertDialog.Builder(this);
        getFileName.setTitle(R.string.export_choose);
        getFileName.setView(v);
        getFileName.setPositiveButton(R.string.export_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                final String fileEdit = txt.getText().toString();
                if (e.fileExists(fileEdit)) {
                    AlertDialog.Builder fileExists = new AlertDialog.Builder(ActivityDiscovery.this);
                    fileExists.setTitle(R.string.export_exists_title);
                    fileExists.setMessage(R.string.export_exists_msg);
                    fileExists.setPositiveButton(R.string.btn_yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (e.writeToSd(fileEdit)) {
                                        makeToast(R.string.export_finished);
                                    } else {
                                        export();
                                    }
                                }
                            });
                    fileExists.setNegativeButton(R.string.btn_no, null);
                    fileExists.show();
                } else {
                    if (e.writeToSd(fileEdit)) {
                        makeToast(R.string.export_finished);
                    } else {
                        export();
                    }
                }
            }
        });
        getFileName.setNegativeButton(R.string.btn_discover_cancel, null);
        getFileName.show();
    }
*/
    public void makeToast(int msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setButton(Button btn, int res, boolean disable) {
        if (disable) {
            setButtonOff(btn, res);
        } else {
            setButtonOn(btn, res);
        }
    }

    private void setButtonOff(Button b, int drawable) {
        b.setClickable(false);
        b.setEnabled(false);
        b.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    private void setButtonOn(Button b, int drawable) {
        b.setClickable(true);
        b.setEnabled(true);
        b.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }
}

