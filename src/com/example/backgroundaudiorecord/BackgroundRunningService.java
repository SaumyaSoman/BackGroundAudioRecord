package com.example.backgroundaudiorecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class BackgroundRunningService extends Service {

	NotificationManager mNM;
	int notificationId = 1111;
	private MediaRecorder recorder;
	boolean isRecording = false;
	Runnable recordRunnable = new Runnable() {
		
		@Override
		public void run() {
			isRecording = true;
			startRecording();
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stopRecording();
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Notification notification=new Notification(R.drawable.ic_launcher,
                "Background Recording app",
                System.currentTimeMillis());
		Intent i=new Intent();
		
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
		Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pi=PendingIntent.getActivity(this, 0,
		                        i, 0);
		
		notification.setLatestEventInfo(this, "Background Recording app",
		    "Recording Enabled",
		    pi);
		notification.flags|=Notification.FLAG_NO_CLEAR;
		
		startForeground(1337, notification);
		scheduleRecord();
		return START_STICKY;	
	}
	
	public void scheduleRecord()
	{
		ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

		// This schedule a runnable task every 5 minutes
		Log.d("Check","Scheduling done");
		final ScheduledFuture scheduleHandler = scheduleTaskExecutor.scheduleAtFixedRate(recordRunnable, 0, 5, TimeUnit.MINUTES);
		
		scheduleTaskExecutor.schedule(new Runnable() {
		       public void run() { 
		    	   Log.d("Check","Scheduling stopped");
		    	   scheduleHandler.cancel(true); 
		    	   stopSelf();
		    	  }
		     }, 12, TimeUnit.HOURS);
		

	}
	
	private void startRecording(){

	    try {
	    Log.d("Check","Starting Recording");
	    recorder = new MediaRecorder();

	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);


	    Date today = Calendar.getInstance().getTime();    
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String reportDate = formatter.format(today);


	    File instanceRecordDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "sound");

	    if(!instanceRecordDirectory.exists()){
	        instanceRecordDirectory.mkdirs();
	    }

	    File instanceRecord = new File(instanceRecordDirectory.getAbsolutePath() + File.separator + reportDate + "_Recondsound.mp4");
	    if(!instanceRecord.exists()){
	        instanceRecord.createNewFile();
	    }
	    recorder.setOutputFile(instanceRecord.getAbsolutePath());



	        recorder.prepare();
	        recorder.start();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }catch (Exception e){
	          e.printStackTrace();
	    }
	}

	private void stopRecording() {
		Log.d("Check","Stopping Recording");
	    if (recorder != null) {       
	        recorder.stop();
	        recorder.release();
	        recorder = null;
	    }
	}
}
