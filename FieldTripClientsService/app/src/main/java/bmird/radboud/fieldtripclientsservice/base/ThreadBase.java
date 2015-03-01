package bmird.radboud.fieldtripclientsservice.base;

import android.util.Log;

import java.io.IOException;

import bmird.radboud.fieldtripclientsservice.C;
import nl.fcdonders.fieldtrip.bufferclient.BufferClient;
import nl.fcdonders.fieldtrip.bufferclient.Header;

public abstract class ThreadBase {
	protected boolean run = false;
	protected AndroidHandle android;
	public Argument[] arguments;

	protected boolean connect(final BufferClient client, final String adress,
			final int port) throws IOException, InterruptedException {
		if (!client.isConnected()) {
			client.connect(adress, port);
			android.updateStatus("Waiting for header.");
		} else {
			return false;
		}
		Header hdr = null;
		do {
			try {
				hdr = client.getHeader();
			} catch (IOException e) {
				if (!e.getMessage().contains("517")) {
					throw e;
				}
				Thread.sleep(1000);
			}
		} while (hdr == null);
		return true;
	}

	public abstract Argument[] getArguments();

	public abstract String getName();

	public abstract void mainloop();

	public void pause() {
		run = false;
	}

	public void setArguments(final Argument[] arguments) {
        this.arguments = arguments;
	}

    public void setArgument(final Argument argument){
        String argDescription = argument.getDescription();
        for(Argument arg : this.arguments){
            if(arg.getDescription() == argDescription){
                arg = argument;
                return;
            }
        }
        Log.i(C.TAG, "Argument "+argDescription+" does not exist.");
    }

	public void setHandle(final AndroidHandle android) {
		this.android = android;
	}

	public void stop() {
		run = false;
	};

	public abstract void validateArguments(final Argument[] arguments);

}
