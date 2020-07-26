package br.com.kalffman.projetos.directconn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.TextView;

import br.com.kalffman.projetos.directconn.receiver.WifiP2PBroadcastReceiver;
import br.com.kalffman.projetos.directconn.receiver.WifiStatusBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements WifiStatusBroadcastReceiver.WifiStatusChangeListener,
                                                               WifiP2PBroadcastReceiver.WifiP2PChangeListener {
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;

    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadComponents();

        registerReceiver(new WifiStatusBroadcastReceiver(MainActivity.this), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

    }

    private void loadComponents() {
        tvInfo = findViewById(R.id.tv_info);
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

        registerReceiver(new WifiP2PBroadcastReceiver(p2pManager, p2pChannel, this), intentFilter);
    }

    @Override
    public void onChange(boolean enabled) {
        tvInfo.setText(enabled ? "Habilitado" : "Desligado");
    }

    @Override
    public void onChange(String action) {

    }
}