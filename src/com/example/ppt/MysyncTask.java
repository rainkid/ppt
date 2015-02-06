package com.example.ppt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class MySyncTask extends AsyncTask<Void, byte[], Boolean> {
	String dstAddress;
	int dstPort;
	String response = "";
	Handler hander = null;

	Socket nsocket; // Network Socket
	InputStream nis; // Network Input Stream
	OutputStream nos; // Network Output Stream

	MySyncTask(Handler handler, String addr, int port) {
		hander = handler;
		dstAddress = addr;
		dstPort = port;
	}

	public void appendMsg(String content) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = 1;
		bundle.putString("msg", content);
		msg.setData(bundle);
		hander.sendMessage(msg);
	}

	@Override
    protected Boolean doInBackground(Void... params) {//This runs on a different thread
		boolean result = false;
		try {
			appendMsg("doInBackground: Creating socket");
			SocketAddress sockaddr = new InetSocketAddress(dstAddress, dstPort);
			nsocket = new Socket();
			nsocket.connect(sockaddr); // 10 second connection timeout
			if (nsocket.isConnected()) {
				nis = nsocket.getInputStream();
				nos = nsocket.getOutputStream();

				appendMsg("doInBackground: Socket created, streams assigned");
				
				byte[] buffer = new byte[4096];
				int read = nis.read(buffer, 0, 4096); // This is blocking
				while (read != -1) {
					byte[] tempdata = new byte[read];
					System.arraycopy(buffer, 0, tempdata, 0, read);
					publishProgress(tempdata);
					Log.i("AsyncTask", "doInBackground: Got some data");
					read = nis.read(buffer, 0, 4096); // This is blocking
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			appendMsg("doInBackground: IOException");
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			appendMsg("doInBackground: Exception");
			result = true;
		} finally {
			try {
				nis.close();
				nos.close();
				nsocket.close();
			} catch (IOException e) {
				appendMsg(e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				appendMsg(e.toString());
				e.printStackTrace();
			}
			appendMsg("doInBackground: Finished");
		}
		return result;
	}
	
	public void SendDataToNetwork(String cmd) { //You run this from the main thread.
		String mcmd = cmd+"\r\n";
        try {
            if (nsocket.isConnected()) {
            	appendMsg("SendDataToNetwork: Writing received message to socket");
                nos.write(mcmd.getBytes());
            } else {
            	appendMsg("SendDataToNetwork: Cannot send message. Socket is closed");
            }
        } catch (Exception e) {
        	appendMsg("SendDataToNetwork: Message send failed. Caught an exception");
        }
    }

	@Override
	protected void onProgressUpdate(byte[]... values) {
		if (values.length > 0) {
			appendMsg( "onProgressUpdate: " + values[0].length + " bytes received.");
			// textStatus.setText(new String(values[0]));
		}
	}

	@Override
	protected void onCancelled() {
		appendMsg("Cancelled.");
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			appendMsg("onPostExecute: Completed with an Error.");
		} else {
			appendMsg("onPostExecute: Completed.");
		}
	}
}