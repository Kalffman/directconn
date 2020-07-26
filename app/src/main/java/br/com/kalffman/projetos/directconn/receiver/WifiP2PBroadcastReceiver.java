package br.com.kalffman.projetos.directconn.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import br.com.kalffman.projetos.directconn.MainActivity;

public class WifiP2PBroadcastReceiver extends BroadcastReceiver {

    public interface WifiP2PChangeListener {
        public void onChange(String action, List<WifiP2pDevice> devices);
    }

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    private WifiP2PChangeListener listener;

    public WifiP2PBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity, WifiP2PChangeListener listener) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    this.listener.onChange("Conexão P2P habilitado", null);
                } else {
                    this.listener.onChange("Conexão P2P desabilitado", null);
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (activity != null) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

                    manager.requestPeers(channel, peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
        }
    }

    private final WifiP2pManager.PeerListListener peerListListener = peers -> listener.onChange(null, new ArrayList<>(peers.getDeviceList()));
}
