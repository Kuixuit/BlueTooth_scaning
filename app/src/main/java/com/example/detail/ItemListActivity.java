package com.example.detail;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private BluetoothManager mBluetoothManager= null;
    private BluetoothAdapter mBluetoothAdapter =null;
    private BluetoothLeScanner mBluetoothLeScanner = null;

    private Switch mBluetoothcheck;
    public static final List<BlueItem> ITEMS = new ArrayList<BlueItem>();
    public static final Map<String, BlueItem> ITEM_MAP = new HashMap<String,BlueItem>();

    public  static  int position ;

    // setup and request bluetooth permission
    private static final int PERMISSION_REQUEST_CODE = 666;
    private final static String[] permissionWeNeed = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private void setupPremission(){
        boolean isGranted = true;
        for(String permission : permissionWeNeed){
            isGranted &= ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED;
        }
        if(!isGranted){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(permissionWeNeed,PERMISSION_REQUEST_CODE);
            }
            else {
                Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            }
        }
        else {
            initBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                boolean isGranted = grantResults.length >0;
                for (int grantResult : grantResults){
                    isGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
                }
                if ( isGranted){
                    initBluetooth();
                } else {
                    Toast.makeText(this,"no permission",Toast.LENGTH_SHORT).show();
                    finishAndRemoveTask();
                }
            }
        }
    }

    private void initBluetooth(){
        boolean success = false;
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(mBluetoothManager!= null){
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if(mBluetoothAdapter !=null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                Toast.makeText(this,"Bluetooth function started",Toast.LENGTH_SHORT).show();
                success =true;
            }
        }
        if (!success){
            Toast.makeText(this,"Can't start bluetooth function",Toast.LENGTH_SHORT).show();
        }
    }

    private final ScanCallback startScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            ScanRecord mScanRecord = result.getScanRecord();
            BlueItem Bdevice = new  BlueItem();

            String address = device.getAddress();
            byte[] content = mScanRecord.getBytes();
            int mRssi = result.getRssi();
            String Scontent = byteArrayToHexString(content);
            Bdevice.Devicename =address;
            Bdevice.RSSI = Integer.toString(mRssi);
            Bdevice.content=Scontent;
            Log.d("BLU:",Bdevice.Devicename);
            ITEMS.add(0,Bdevice);
            ///System.out.println("System.out.println"+ITEMS.get(0).Devicename);
            ITEM_MAP.put(Bdevice.Devicename, Bdevice);

            createBlueItem(Bdevice.Devicename);


        }
    };


    public static String byteArrayToHexString(byte b[]){
        int len = b.length;
        String data = new String();

        for (int i =0;i< len;i++){
            data += b[i];
        }
        return data;
    }


   // 放置藍芽裝置 blueitem
    private static BlueItem createBlueItem(String mac) {
        position+=1;
        return new BlueItem(String.valueOf(position), "Item " +position , makeDetails(position,mac));
    }

    private static String makeDetails(int position,String datail) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        builder.append("details information: ").append(datail);
        return builder.toString();
    }

    //blueitem's content object

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPremission();
        mBluetoothLeScanner.startScan(startScanCallback);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) { //畫面右下角的按鈕
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, ITEMS, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<BlueItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) { //點擊進入item詳細資訊 (透過 intent 傳送資料給 itemDetailActivity)
                BlueItem item = (BlueItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.Devicename);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.Devicename);

                    context.startActivity(intent);
                }
            }
        };

        // 列出items
        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<BlueItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).Devicename);
            holder.mContentView.setText(mValues.get(position).content);

//            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}