package ca.psiphon;

import android.content.Context;
import java.util.List;

public class PsiphonTunnel {

    public static class Exception extends java.lang.Exception {
        public Exception(String message) {
            super(message);
        }
    }

    public interface HostService {
        String getPsiphonConfig();
        void bindToDevice(long fd) throws Exception;
        void onListeningSocksProxyPort(int port);
        void onListeningHttpProxyPort(int port);
        void onConnecting();
        void onConnected();
        void onExiting();
        void onClientAddress(String address);
        void onHomepage(String homepage);
        void onClientRegion(String region);
        void onAvailableEgressRegions(List<String> regions);
        void onConnectedServerRegion(String region);
        void onBytesTransferred(long sent, long received);
        void onDiagnosticMessage(String message);
        Context getContext();
    }

    private final HostService hostService;

    private PsiphonTunnel(HostService hostService) {
        this.hostService = hostService;
    }

    public static PsiphonTunnel newPsiphonTunnel(HostService hostService) {
        return new PsiphonTunnel(hostService);
    }

    public void setVpnMode(boolean vpnMode) {
    }

    public boolean startTunneling(String serverEntries) {
        return true;
    }

    public void stop() {
    }
}
