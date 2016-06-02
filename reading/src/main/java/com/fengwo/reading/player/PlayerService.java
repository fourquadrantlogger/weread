package com.fengwo.reading.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.main.comment.BpInfoBean;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.MLog;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 
 * @author lxq 播放器的服务
 * 
 */
public class PlayerService extends Service {

	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int BACK = 2;
	public static final int NEXT = 3;
	public static final int STOP = 4;
	public static boolean autoplaynext=false;
	public static int current_progress = 0;//当前进度

	public static MediaPlayer player = null;
	private AudioManager audioManager;
	OtherAudioListener audioListener;
	private boolean isStop;

	private boolean isPlay = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//播放进度
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.fengwo.reading.PROGRESS_ACTION");
		registerReceiver(new ProgressReceiver(), filter);

		//region 暂停
		//耳机插拔
		IntentFilter filter_erji = new IntentFilter();
		filter_erji.addAction(Intent.ACTION_HEADSET_PLUG);
 		registerReceiver(new erjiReceiver(), filter_erji);

		//播出电话
		IntentFilter filter_call = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(new CallPhoneReceiver(), filter_call);

		//接听电话
		ReceiveCallListener pcl=new ReceiveCallListener();
		TelephonyManager tm=(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		tm.listen(pcl, pcl.LISTEN_CALL_STATE);
		mediaclockthread.start();
		//第三方音频
		audioListener=new OtherAudioListener();
		//endregion
		if (player == null) {
			player = new MediaPlayer();
			// player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}
//        else{
//            player.reset();
//            player = null;
//            player = new MediaPlayer();
//        }
	}
	public static BpInfoBean media_now_IndexBean;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int msg = intent.getIntExtra("MUSIC", -1);
            //从拆书包中进入server时传入的是media_now_IndexBean
			media_now_IndexBean=(BpInfoBean)intent.getSerializableExtra("singleBpInfoBean");
			isStop = false;

			switch (msg) {
				case PLAY:
                    //移除runnable中的消息
					handler.removeCallbacks(runnable);
					if(media_now_IndexBean!=null){
						playsingle(media_now_IndexBean.media);
					}else {
						Playlist_Now.musicID = intent.getIntExtra("MusicID", -1);
						playlist();
					}
					break;
				case PAUSE:
					if (player.isPlaying()) {
						player.pause();
					} else {
						player.start();
					}
					break;
				case NEXT:
					handler.removeCallbacks(runnable);
					playlist();
					break;
				case BACK:
					handler.removeCallbacks(runnable);
					playlist();
					break;
				case STOP:
					isStop = true;
					player.stop();
					player.release();
					break;
				default:
					break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	String getDataSource() {
		long stardownload = new Date().getTime();
		String s = Playlist_Now.getLocalMediaPath(false);
		Log.v("moe", "下载用时" + (new Date().getTime() - stardownload) + "毫秒");
		return s;
	}

	private void playlist() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (Playlist_Now.musicFiles() == null || Playlist_Now.musicFiles().size() == 0 || Playlist_Now.musicID == null || Playlist_Now.musicID == -1) {
					return;
				}

				try {
					if (isPlay) {
						player.stop();
					}
					//region 测试
					player.reset();
//                    player = null;
//                    player = new MediaPlayer();
					player.setDataSource(getDataSource());
					player.prepare();// 进行缓冲
					player.start();
					isPlay = true;

					//endregion

					handler.postDelayed(runnable, 100);
					player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						@Override
						public void onBufferingUpdate(MediaPlayer mp, int percent) {
							// 设置 预加载 网络音乐的进度。
							sendPercent(percent);
						}
					});

					player.setOnCompletionListener(new OnCompletionListener() {// 播放结束，下一首
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							MLog.v("reading", "播放完改，自动播放下一首"+autoplaynext);
							if(autoplaynext) {//region  自动播放下一首的开关
                                //todo
								if (!playnowfinish_and_stop) {

									handler.removeCallbacks(runnable);
									sendNext();
                                    //todo
									try {
										if (Playlist_Now.musicFiles().size() != 0) {
//											Playlist_Now.musicID = (Playlist_Now.musicID + 1) % Playlist_Now.musicFiles().size();
//											sendNext();
//											playlist();
                                            download_yesOrNo_noNet(Playlist_Now.musicID);
										}
									} catch (ArithmeticException e) {
									} catch (Exception e) {
									}
									//endregion
								} else {
									MLog.v("reading", "播放完改首即可退出");
									player.stop();
									player.reset();

									return;
								}
							}
						}
					});
					//region 注册焦点
					audioListener.requestAudioFocus();
					//endregion

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    //todo
    public void download_yesOrNo_noNet(Integer id){
        if (!Fragment_MediaPlayer.isConNet){
            if (new File(Environment.getExternalStorageDirectory().getPath() +  GlobalParams.FolderPath_Media+Playlist_Now.media_now_IndexBean.book_title+"/"+Playlist_Now.media_now_IndexBean.title+".mp3").exists()){
                Playlist_Now.musicID = (id+ 1) % Playlist_Now.musicFiles().size();
                sendNext();
                playlist();
            }else{
                download_yesOrNo_noNet((id+ 1) % Playlist_Now.musicFiles().size());
            }
        }else{
            Playlist_Now.musicID = (id + 1) % Playlist_Now.musicFiles().size();
            sendNext();
            playlist();
        }
    }
    //播放音频
	private void playsingle(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					if (isPlay) {
						player.stop();
					}
					//region 测试
					player.reset();
					player.setDataSource(url);
					player.prepare();// 进行缓冲
					player.start();
					isPlay = true;

					//endregion

					handler.postDelayed(runnable, 1000);
					player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

						@Override
						public void onBufferingUpdate(MediaPlayer mp, int percent) {
							// 设置 预加载 网络音乐的进度。
							sendPercent(percent);
						}
					});

					//region 注册焦点
					audioListener.requestAudioFocus();
					//endregion

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO
			if (isPlay) {
                //发送当前播放进度的广播
				sendCurrent();
                //要做的事情，这里再次调用此Runnable对象，以实现每一秒实现一次的定时器操作
				handler.postDelayed(this, 1000);
			}
		}
	};
    //todo
	public static Integer getMediaclock_length() {
		return mediaclock_length;
	}
	public static long starttime = new Date().getTime();
	static Thread mediaclockthread=new Thread(new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				long span = new Date().getTime() - starttime;
				if (mediaclock_length == 0){
					playnowfinish_and_stop = true;
					MLog.v("reading","播放完改首即可退出");
				}
				else if (mediaclock_length >= 1)
				{
					if (span/60000 <= mediaclock_length){

						Intent intent = new Intent();
						intent.setAction("MediaClock");
						intent.putExtra("exist_second",60*mediaclock_length-(int)(span/1000));
						MyApplication.getContext().sendBroadcast(intent);

					} else {
						break;
					}
				}
			}
			player.stop();
			player.reset();
		}
	});
    //todo
	public static void setMediaclock_length(final Integer mediaclock_length) {
		PlayerService.mediaclock_length = mediaclock_length;

	}

	static Integer mediaclock_length=-1;

	static boolean playnowfinish_and_stop =false;

	private void sendNext() {
		Intent intent = new Intent();
		intent.setAction("NEXT_ACTION");
		sendBroadcast(intent);
	}
    //发送预加载广播
	private void sendPercent(int percent) {
		Intent intent = new Intent();
		intent.putExtra("PERCENT", percent);
		intent.setAction("PERCENT_ACTION");
		sendBroadcast(intent);
	}
    //当前播放进度，并发送广播
	private void sendCurrent() {
		Intent intent = new Intent();
		if (!isStop) {
			if (player == null) {
				return;
			}
			intent.putExtra("CURRENT", player.getCurrentPosition());
			intent.setAction("CURRENT_ACTION");
			sendOrderedBroadcast(intent, null);
			this.current_progress = player.getCurrentPosition();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		audioListener.abandonAudioFocus();
		player.release();
		player = null;
	}

	private class ProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getIntExtra("PROGRESS", -1);
			if (player != null) {
				player.seekTo(progress);
			}
			MLog.v("progress", "player.seekTo " + progress);
		}
	}

	private class erjiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra("state", 0);
			Log.v("moe", "耳机插入状态:" + state);
			if (player == null) return;
			switch (state) {
				case 0:
					if (player.isPlaying()) {
						player.pause();
					}
					break;
				case 1:
				case 2:
				default:
					if (!player.isPlaying()) {
						player.start();
					}
					break;
			}
		}
	}

	private class CallPhoneReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra("state", 0);
			MLog.v("moe", "播出电话:" + state);
			if (player == null) return;
			switch (state) {
				case 0:
					if (player.isPlaying()) {
						player.pause();
					}
					break;
				case 1:
				case 2:
				default:
					if (!player.isPlaying()) {
						//player.start();
					}
					break;
			}
		}
	}
    //第三方音频
	private class OtherAudioListener implements AudioManager.OnAudioFocusChangeListener{
		private void requestAudioFocus() {

			if (audioManager == null)
				audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager != null) {
				int ret = audioManager.requestAudioFocus(audioListener,
						AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
				if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					MLog.v("moe","音频焦点注册失败");
				}
				MLog.v("moe","音频焦点注册成功");
			}
		}
		private void abandonAudioFocus() {

			if (audioManager != null) {
				audioManager.abandonAudioFocus(audioListener);
				audioManager = null;
			}
		}
		@Override
		public void onAudioFocusChange(int focusChange) {
			Log.v("moe", "第三方音频:" + focusChange);
			if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
				//失去焦点之后的操作
				if(player.isPlaying()){
					player.pause();
				}
			}else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
				//获得焦点之后的操作
				if (!player.isPlaying()) {
					//player.start();
				}
			}
		}
	}

	private class ReceiveCallListener extends PhoneStateListener {
		public void onCallStateChanged(int state,String incomingNumber){ //需要重写onCallStateChanged方法
			Log.v("moe", "电话接听状态:" + state);
			switch(state){
				case TelephonyManager.CALL_STATE_IDLE:
					//挂断0
					if (!player.isPlaying()) {
						//player.start();
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					//接听2
					if (player.isPlaying()) {
						player.pause();
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING://来电
					//1
					if (player.isPlaying()) {
						player.pause();
					}
					break;
				default:
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
}
	// 1）如何获得MediaPlayer实例：
	// 可以使用直接new的方式：
	// MediaPlayer mp = new MediaPlayer();
	// 也可以使用create的方式，如：
	// MediaPlayer mp = MediaPlayer.create(this,
	// R.raw.test);//这时就不用调用setDataSource了
	//
	// 2) 如何设置要播放的文件：
	// MediaPlayer要播放的文件主要包括3个来源：
	// a. 用户在应用中事先自带的resource资源
	// 例如：MediaPlayer.create(this, R.raw.test);
	// b. 存储在SD卡或其他文件路径下的媒体文件
	// 例如：mp.setDataSource("/sdcard/test.mp3");
	// c. 网络上的媒体文件
	// 例如：mp.setDataSource("http://www.citynorth.cn/music/confucius.mp3");
	//
	// MediaPlayer的setDataSource一共四个方法：
	// setDataSource (String path)
	// setDataSource (FileDescriptor fd)
	// setDataSource (Context context, Uri uri)
	// setDataSource (FileDescriptor fd, long offset, long length)
	//
	// 3）对播放器的主要控制方法：
	// Android通过控制播放器的状态的方式来控制媒体文件的播放，其中：
	// prepare()和prepareAsync()
	// 提供了同步和异步两种方式设置播放器进入prepare状态，需要注意的是，如果MediaPlayer实例是由create方法创建的，那么第一次启动播放前不需要再调用prepare（）了，因为create方法里已经调用过了。
	// start()是真正启动文件播放的方法，
	// pause()和stop()比较简单，起到暂停和停止播放的作用，
	//
	// seekTo()是定位方法，可以让播放器从指定的位置开始播放，需要注意的是该方法是个异步方法，也就是说该方法返回时并不意味着定位完成，尤其是播放的网络文件，真正定位完成时会触发OnSeekComplete.onSeekComplete()，如果需要是可以调用setOnSeekCompleteListener(OnSeekCompleteListener)设置监听器来处理的。
	// release()可以释放播放器占用的资源，一旦确定不再使用播放器时应当尽早调用它释放资源。
	// reset()可以使播放器从Error状态中恢复过来，重新会到Idle状态。
	//
	//
	// 4）设置播放器的监听器：
	// MediaPlayer提供了一些设置不同监听器的方法来更好地对播放器的工作状态进行监听，以期及时处理各种情况，
	// 如： setOnCompletionListener(MediaPlayer.OnCompletionListener listener)、
	// setOnErrorListener(MediaPlayer.OnErrorListener
	// listener)等,设置播放器时需要考虑到播放器可能出现的情况设置好监听和处理逻辑，以保持播放器的健壮性。
