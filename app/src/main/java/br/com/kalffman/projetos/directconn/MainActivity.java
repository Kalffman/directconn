package br.com.kalffman.projetos.directconn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.kalffman.projetos.directconn.receiver.WifiP2PBroadcastReceiver;
import br.com.kalffman.projetos.directconn.receiver.WifiStatusBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements WifiStatusBroadcastReceiver.WifiStatusChangeListener,
                                                                WifiP2PBroadcastReceiver.WifiP2PChangeListener {
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    private BroadcastReceiver receiver;

    private TextView tvInfo;
    private Button btDiscover;
    private ListView lvP2pDevices;

    private ArrayAdapter adapter;

    private List<WifiP2pDevice> devices;
    private List<String> deviceNames;

    private final WifiP2pManager.ActionListener actionListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            tvInfo.setText("Procura inicada.");
        }

        @Override
        public void onFailure(int reason) {
            tvInfo.setText(String.format("Erro na procura: %d", reason));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponents();

        registerReceiver(new WifiStatusBroadcastReceiver(MainActivity.this), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

    }

    private void loadComponents() {
        tvInfo = findViewById(R.id.tv_info);
        btDiscover = findViewById(R.id.bt_discover);

        btDiscover.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

            p2pManager.discoverPeers(p2pChannel, actionListener);
        });

        lvP2pDevices = findViewById(R.id.lv_p2p_devices);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        lvP2pDevices.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadP2PContext();
    }

    private void loadP2PContext() {
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        p2pChannel = p2pManager.initialize(this, getMainLooper(), null);

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.receiver = new WifiP2PBroadcastReceiver(p2pManager, p2pChannel, this, this);

        registerReceiver(this.receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.receiver);
    }

    @Override
    public void onChange(boolean enabled) {
        tvInfo.setText(enabled ? "Habilitado" : "Desligado");
    }

    @Override
    public void onChange(String action, List<WifiP2pDevice> devices) {
        if (action != null)
            tvInfo.setText(action);

        updateList(devices);
    }

    private void updateList(List<WifiP2pDevice> list) {
        devices = list;

        if(devices != null && !devices.isEmpty()) {
            deviceNames = new ArrayList<>();

            for(WifiP2pDevice d : devices) deviceNames.add(d.deviceName);

            adapter.clear();
            adapter.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }
}