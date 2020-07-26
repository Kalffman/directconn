package br.com.kalffman.projetos.directconn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import br.com.kalffman.projetos.directconn.MainActivity;

public class WifiP2PBroadcastReceiver extends BroadcastReceiver {
    public interface WifiP2PChangeListener {
        public void onChange(String action);
    }

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    public WifiP2PBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action == null) return;

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
        }
    }
}
