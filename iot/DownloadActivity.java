package com.oneproject.www.allinone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.oneproject.www.allinone.NetWork.NetInfo;
import com.oneproject.www.allinone.Utils.Db;
import com.oneproject.www.allinone.Utils.DbUpdate;
import com.oneproject.www.allinone.Utils.Prefs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class DownloadActivity extends AppCompatActivity {
    public final static String TAG = "DownloadActivity";
    //public static final String PKG = "info.lamatricexiste.network";//이건바뀌면안됨->바껴야됨 공부좀
    public static final String PKG = "com.oneproject.www.allinone";
    static final int REQUESTCODE_DOWNLOAD=1002;
    public static SharedPreferences prefs = null;
    TextView test;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        final Context ctxt = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
        test=(TextView)findViewById(R.id.tvtest);
        // Reset interface
         edit= prefs.edit();
        edit.putString(Prefs.KEY_INTF, Prefs.DEFAULT_INTF);

        phase2(ctxt);
    }

    private void phase2(final Context ctxt) {
        phase3(ctxt);
        /*
        class DbUpdateNic extends DbUpdate {
            public DbUpdateNic() {
                super(DownloadActivity.this, Db.DB_NIC, "oui", "mac", 253);
            }

            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                final Activity d = mActivity.get();
                phase3(d);
            }

            protected void onCancelled() {
                super.onCancelled();
                final Activity d = mActivity.get();
                phase3(d);
            }
        }

        // CheckNicDb
        try {
            if (prefs.getInt(Prefs.KEY_RESET_NICDB, Prefs.DEFAULT_RESET_NICDB) != getPackageManager()
                    .getPackageInfo(PKG, 0).versionCode) {
                new DbUpdateNic();
            } else {
                // There is a NIC Db installed
                phase3(ctxt);
            }
        } catch (PackageManager.NameNotFoundException e) {
            test.append(e+"\n");
            phase3(ctxt);
        } catch (ClassCastException e) {
            test.append(e+"\n");
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(Prefs.KEY_RESET_NICDB, 1);
            edit.commit();
            phase3(ctxt);
        }
    }*/
    }
    private void phase3(final Context ctxt) {
        // Install Services DB

        try {

            if(checkDB(this,"nic"))
                dumpDB(this,"nic");
            if(checkDB(this,"probes"))
                dumpDB(this,"probes");


                new CreateServicesDb(DownloadActivity.this).execute();

        } catch (Exception e) {

            test.append(e+"\n");
            //startDiscoverActivity(ctxt);
        }

    }

    private void startDiscoverActivity(final Context ctxt) {
        startActivityForResult(new Intent(ctxt, DiscoveryActivity.class),REQUESTCODE_DOWNLOAD);
        //finish();
    }

    public boolean checkDB(Context mContext,String dbname){
        String filePath = Db.PATH+dbname+".db";
        File file = new File(filePath);
        return file.exists();
    }

    // Dump DB
    public void dumpDB(Context mContext,String dbname){
        AssetManager manager = mContext.getAssets();
        String folderPath = Db.PATH;
        String filePath = Db.PATH+dbname+".db";

        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            InputStream is = manager.open("db/"+dbname+".db");
            BufferedInputStream bis = new BufferedInputStream(is);

            if (!folder.exists())
                folder.mkdirs();


            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();

        } catch (IOException e) {
            Toast.makeText(this,e+"",Toast.LENGTH_LONG).show();
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }

    static class CreateServicesDb extends AsyncTask<Void, String, Void> {
        private WeakReference<Activity> mActivity;
        private ProgressDialog progress;

        public CreateServicesDb(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        protected void onPreExecute() {
            final Activity d = mActivity.get();
            if (d != null) {
                try {
                    d.setProgressBarIndeterminateVisibility(true);
                    progress = ProgressDialog.show(d, "", d.getString(R.string.task_services));
                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Activity d = mActivity.get();
            if (d != null) {
                Db db = new Db(d.getApplicationContext());
                try {
                    // db.copyDbToDevice(R.raw.probes, Db.DB_PROBES);
                    db.copyDbToDevice(R.raw.services, Db.DB_SERVICES);
                    db.copyDbToDevice(R.raw.saves, Db.DB_SAVES);
                    // Save this device in db
                    NetInfo net = new NetInfo(d.getApplicationContext());
                    ContentValues values = new ContentValues();
                    values.put("_id", 0);
                    if (net.macAddress == null) {
                        net.macAddress = NetInfo.NOMAC;
                    }
                    values.put("mac", net.macAddress.replace(":", "").toUpperCase());
                    values.put("name", d.getString(R.string.discover_myphone_name));
                    SQLiteDatabase data = Db.openDb(Db.DB_SAVES);
                    data.insert("nic", null, values);
                    data.close();
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    if (e != null) {
                        if (e.getMessage() != null) {
                            Log.e(TAG, e.getMessage());
                        } else {
                            Log.e(TAG, "Unknown IOException");
                        }
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            final DownloadActivity d = (DownloadActivity) mActivity.get();
            if (d != null) {
                d.setProgressBarIndeterminateVisibility(true);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                try {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putInt(Prefs.KEY_RESET_SERVICESDB, d.getPackageManager().getPackageInfo(
                            PKG, 0).versionCode);
                    edit.commit();
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    //Toast.makeText(d.getApplicationContext(),"done",Toast.LENGTH_SHORT).show();
                    d.startDiscoverActivity(d);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            ArrayList<String> hostname=data.getStringArrayListExtra("hostname");
            ArrayList<String> ipaddress=data.getStringArrayListExtra("ip");
            ArrayList<String> hardware=data.getStringArrayListExtra("hardware");
            ArrayList<String> nic=data.getStringArrayListExtra("nic");
            int count=data.getIntExtra("count",0);

            Intent intent=new Intent();
            intent.putStringArrayListExtra("hostname",hostname);
            intent.putStringArrayListExtra("ip",ipaddress);
            intent.putStringArrayListExtra("hardware",hardware);
            intent.putStringArrayListExtra("nic",nic);
            intent.putExtra("count",count);

            setResult(RESULT_OK,intent);
            finish();
        }else if(resultCode==RESULT_CANCELED){
            finish();
        }
    }

}
