package bmird.radboud.fieldtripserverservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Locale;

import bmird.radboud.fieldtripserverservice.monitor.BufferMonitor;
import nl.fcdonders.fieldtrip.bufferserver.BufferServer;

public class FieldTripServerService extends Service {

	private BufferServer buffer;
	private BufferMonitor monitor;
	private WakeLock wakeLock;
	private WifiLock wifiLock;

    IntentFilter intentFilter = new IntentFilter(C.FILTER);
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i(C.TAG, "Dealing with Flush request");
            switch (intent.getIntExtra(C.MESSAGE_TYPE, -1)) {
                case C.REQUEST_PUT_HEADER:
                    buffer.putHeader(0, 0, 0);
                    break;
                case C.REQUEST_FLUSH_HEADER:
                    buffer.flushHeader();
                    break;
                case C.REQUEST_FLUSH_SAMPLES:
                    buffer.flushSamples();
                    break;
                case C.REQUEST_FLUSH_EVENTS:
                    buffer.flushEvents();
                    break;
                default:
            }
        }
    };

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	/**
	 * Called when the service is stopped. Stops the buffer.
	 */
	@Override
	public void onDestroy() {
		Log.i(C.TAG, "Stopping Buffer Service.");
        this.unregisterReceiver(mMessageReceiver);
		if (buffer != null) {
			buffer.stopBuffer();
		}
		if (monitor != null) {
			monitor.stopMonitoring();
		}
		if (wakeLock != null) {
			wakeLock.release();
		}
		if (wifiLock != null) {
			wifiLock.release();
		}
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		Log.i(C.TAG, "Buffer Service Running");
		// If no buffer is running.
		if (buffer == null) {

			final int port = intent.getIntExtra("port", 1972);
			// Get Wakelocks

			final PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					C.WAKELOCKTAG);
			wakeLock.acquire();

			final WifiManager wifiMan = (WifiManager) getSystemService(WIFI_SERVICE);
			wifiLock = wifiMan.createWifiLock(C.WAKELOCKTAGWIFI);
			wifiLock.acquire();

			// Create Foreground Notification

			// Get the currently used ip-address
			final WifiInfo wifiInf = wifiMan.getConnectionInfo();
			final int ipAddress = wifiInf.getIpAddress();
			final String ip = String.format(Locale.ENGLISH, "%d.%d.%d.%d",
					ipAddress & 0xff, ipAddress >> 8 & 0xff,
					ipAddress >> 16 & 0xff, ipAddress >> 24 & 0xff);

			// Create notification text
			final Resources res = getResources();
			final String notification_text = String.format(
					res.getString(R.string.notification_text), ip + ":" + port);

			final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(res.getString(R.string.notification_title))
					.setContentText(notification_text);


			// Create a buffer and start it.
			buffer = new BufferServer(port, intent.getIntExtra("nSamples", 100),
					intent.getIntExtra("nEvents", 100));
			monitor = new BufferMonitor(this, ip + ":" + port,
					System.currentTimeMillis());
			buffer.addMonitor(monitor);

			// Start the buffer and Monitor
			buffer.start(); Log.i(C.TAG, "1");
			monitor.start();
			Log.i(C.TAG, "Buffer thread started.");

			// Turn this service into a foreground service
			startForeground(1, mBuilder.build());
			Log.i(C.TAG, "Fieldtrip Buffer Service moved to foreground.");


            if(buffer !=null) {
                this.registerReceiver(mMessageReceiver, intentFilter);
                Log.i(C.TAG, "Registered receiver with buffer:"+buffer.getName());
            }


		}
		return START_NOT_STICKY;
	}
}
