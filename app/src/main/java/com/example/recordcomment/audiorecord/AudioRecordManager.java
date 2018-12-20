package com.example.recordcomment.audiorecord;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import com.example.recordcomment.utils.FileUtils;
import com.example.recordcomment.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 使用AudioRecord来录制，得到 .wav 的格式，
 *  WAV 的格式可以通过 lamelib.jar, 转换为mp3 格式，最终兼容IOS
 *  具体代码 借鉴网上资源
 */

public class AudioRecordManager extends BaseRecordManager{
	private static volatile AudioRecordManager voiceManager;
	public final int						MSG_TIME_INTERVAL			= 100;
	// 多媒体例如声音的状态
	public final int						MEDIA_STATE_UNDEFINE		= 200;
	public final int						MEDIA_STATE_RECORD_STOP		= 210;
	public final int						MEDIA_STATE_RECORD_DOING	= 220;
	public final int						MEDIA_STATE_RECORD_PAUSE	= 230;
	
	// The interval in which the recorded samples are output to the file
	private static final int				TIMER_INTERVAL				= 120;
	
	private AudioRecord						mAudioRecorder				= null;
	private int								mSampleRate					= 16000;//44100;
	private short							bSamples;
	private short							nChannels;
	private int								framePeriod;
	// Stores current amplitude
	private int								cAmplitude					= 0;
	// File writer (only in uncompressed mode)
	private RandomAccessFile				randomAccessWriter;
	
	// Buffer for output
	private byte[]							buffer;
	// Number of bytes written to file after header(only in uncompressed mode)
	// after stop() is called, this size is written to the header/data chunk in
	// the wave file
	private int								payloadSize;
	
//	private Context							mContext					= null;
	private int								mDeviceState				= MEDIA_STATE_UNDEFINE;
//	private String							mRecTimePrev;
//	private long							mRecTimeSum					= 0;
private long mRecordTime = 0L;
	/**
	 * 录音文件存放的位置(文件夹)
	 */
	private String							recordFilePath				= "";
	
	/**
	 * 录音监听
	 */
	//	private VoiceRecordCallBack				voiceRecordCallBack			= null;
	// private ObtainDecibelThread mThread;

	private AcousticEchoCanceler mAcousticEchoCanceler = null;
	private NoiseSuppressor mNoiseSuppressor = null;
	private AutomaticGainControl mAutomaticGainControl = null;
	private int mAudioSessionId = 0;

	/**
	 * Recorder state; see State
 	 */
	private State							state						= State.STOPPED;
	
	/**
	 * INITIALIZING : recorder is initializing; READY : recorder has been
	 * initialized, recorder not yet started RECORDING : recording ERROR :
	 * reconstruction needed STOPPED: reset needed
	 */
	public enum State {
		INITIALIZING, READY, RECORDING, ERROR, STOPPED
	}
	
	public State getRecordState() {
		return state;
	}
	
	private AudioRecordManager() {
	}
	/**
	 * 获取单例
	 * @return
	 */
	public static AudioRecordManager getInstance() {
		if (voiceManager == null) {
			synchronized (AudioRecordManager.class) {
				if (voiceManager == null) {
					voiceManager = new AudioRecordManager();
				}
			}
		}
		return voiceManager;
	}

	@Override
	public File getRecordFile(){
		File file = null;
		if(FileUtils.fileIsExists(recordFilePath)) {
			file = new File(recordFilePath);
		}
		return file;
	}

	@Override
	public String getRecordPath(){
		return recordFilePath;
	}


	public long getRecordDuration(){
		return mRecordTime;
	}

	public void setRecordDuration(long recordTime){
		mRecordTime = recordTime;
	}

	@Override
	public void startRecord(String filePath) {
		if(StringUtil.isNullOrEmpty(filePath)) {
			return;
		}
		this.recordFilePath = filePath;
		startVoiceRecord(true);
	}
	
	@Override
	public void stopRecord() {
		try {
//			mHandler.removeMessages(MSG_TIME_INTERVAL);
			mDeviceState = MEDIA_STATE_RECORD_STOP;
			stopRec();
			release();
		} catch (Exception e) {
			state = State.STOPPED;
		}
	}

	@Override
	public void cancelRecord(){
		FileUtils.deleteFile(recordFilePath);
		mDeviceState = MEDIA_STATE_RECORD_STOP;
		stopRec();
		release();
	}


	private void startVoiceRecord(boolean init) {
//		if (!isSDCardAvailable())
//			return;
//		if (init) {
//			mRecTimeSum = 0;
//		}
		
		stopRec();
		mAudioRecorder = null;

		/**
		 * 注意这里的AudioFormat.ENCODING_PCM_16BIT 参数，与音量的获取是有关的
		 */
		prepareAudioRecorder(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		if (recordFilePath != null && state == State.INITIALIZING) {
			eliminateNoise();
			initWavHeader();
			startRec();
			// 开始录音回调
//			if (voiceRecordCallBack != null) {
//				voiceRecordCallBack.recStart(init);
//			}
			mDeviceState = MEDIA_STATE_RECORD_DOING;
		}
	}

	private void prepareAudioRecorder(int audioSource, int sampleRate, int channelConfig, int audioFormat) {
		
		try {
			
			if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
				bSamples = 16;
			} else {
				bSamples = 8;
			}
			
			if (channelConfig == AudioFormat.CHANNEL_CONFIGURATION_MONO) {
				nChannels = 1;
			} else {
				nChannels = 2;
			}
			
			mSampleRate = sampleRate;

			framePeriod = sampleRate * TIMER_INTERVAL / 1000;
			int bufferSize = framePeriod * 2 * bSamples * nChannels / 8;
			if (bufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
				// Check to make sure
				// buffer size is not
				// smaller than the
				// smallest allowed one
				bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
				// Set frame period and timer interval accordingly
				framePeriod = bufferSize / (2 * bSamples * nChannels / 8);
				
			}
			BUFFER_SIZE = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
			
			mAudioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
			
			if (mAudioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
				throw new Exception("AudioRecord initialization failed");
			}
			mAudioRecorder.setRecordPositionUpdateListener(updateListener);
			mAudioRecorder.setPositionNotificationPeriod(framePeriod);
			mAudioSessionId = mAudioRecorder.getAudioSessionId();
			cAmplitude = 0;
			state = State.INITIALIZING;
			
		} catch (Exception e) {
			state = State.STOPPED;
			
		}
		
	}

	private void eliminateNoise(){
		if (mAudioSessionId != 0 && android.os.Build.VERSION.SDK_INT >= 16)
		{
			//消除噪音
			if (NoiseSuppressor.isAvailable())
			{
				if (mNoiseSuppressor != null)
				{
					mNoiseSuppressor.release();
					mNoiseSuppressor = null;
				}

				mNoiseSuppressor = NoiseSuppressor.create(mAudioSessionId);
				if (mNoiseSuppressor != null)
				{
					mNoiseSuppressor.setEnabled(true);
				}
				else
				{
					Log.i("voice", "Failed to create NoiseSuppressor.");
				}
			}
			else
			{
				Log.i("voice", "Doesn't support NoiseSuppressor");
			}

			if (AcousticEchoCanceler.isAvailable())
			{
				if (mAcousticEchoCanceler != null)
				{
					mAcousticEchoCanceler.release();
					mAcousticEchoCanceler = null;
				}

				mAcousticEchoCanceler = AcousticEchoCanceler.create(mAudioSessionId);
				if (mAcousticEchoCanceler != null)
				{
					mAcousticEchoCanceler.setEnabled(true);
				}
				else
				{
					Log.i("voice", "Failed to initAEC.");
					mAcousticEchoCanceler = null;
				}
			}
			else
			{
				Log.i("voice", "Doesn't support AcousticEchoCanceler");
			}

			if (AutomaticGainControl.isAvailable())
			{
				if (mAutomaticGainControl != null)
				{
					mAutomaticGainControl.release();
					mAutomaticGainControl = null;
				}

				mAutomaticGainControl = AutomaticGainControl.create(mAudioSessionId);
				if (mAutomaticGainControl != null)
				{
					mAutomaticGainControl.setEnabled(true);
				}
				else
				{
					Log.i("voice", "Failed to create AutomaticGainControl.");
				}

			}
			else
			{
				Log.i("voice", "Doesn't support AutomaticGainControl");
			}
		}
		else
		{
			Log.i("voice", "No AcousticEchoCanceler !!!");
		}


	}
	
	public void initWavHeader() {
		try {
			if (mAudioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
				// write file header
				
				randomAccessWriter = new RandomAccessFile(recordFilePath, "rw");
				
				randomAccessWriter.setLength(0); // Set file length to
				// 0, to prevent
				// unexpected behavior
				// in case the file
				// already existed
				randomAccessWriter.writeBytes("RIFF");
				randomAccessWriter.writeInt(0); // Final file size not
				// known yet, write 0
				randomAccessWriter.writeBytes("WAVE");
				randomAccessWriter.writeBytes("fmt ");
				randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk
				// size,
				// 16
				// for
				// PCM
				randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat,
				// 1
				// for
				// PCM
				randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number
				// of
				// channels,
				// 1 for mono, 2 for
				// stereo
				randomAccessWriter.writeInt(Integer.reverseBytes(mSampleRate)); // Sample
				// rate
				randomAccessWriter.writeInt(Integer.reverseBytes(mSampleRate * bSamples * nChannels / 8)); // Byte
				// rate,
				// SampleRate*NumberOfChannels*BitsPerSample/8
				randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * bSamples / 8))); // Block
				// align,
				// NumberOfChannels*BitsPerSample/8
				randomAccessWriter.writeShort(Short.reverseBytes(bSamples)); // Bits
				// per
				// sample
				randomAccessWriter.writeBytes("data");
				randomAccessWriter.writeInt(0); // Data chunk size not
				// known yet, write 0
				
				buffer = new byte[framePeriod * bSamples / 8 * nChannels];
				state = State.READY;
			} else {
				
				state = State.STOPPED;
			}
		} catch (Exception e) {
			state = State.STOPPED;
		}
	}

	public void startRec() {
		if (state == State.READY) {
			payloadSize = 0;
			mAudioRecorder.startRecording();
			mAudioRecorder.read(buffer, 0, buffer.length);
			state = State.RECORDING;
			getNoiseLevel();
		} else {
			
			state = State.STOPPED;
		}
	}
	
	public void stopRec() {
		isGetVoiceRun = false;
		if (state == State.RECORDING) {
			mAudioRecorder.stop();
			
			try {
				randomAccessWriter.seek(4); // Write size to RIFF header
				randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));
				
				randomAccessWriter.seek(40); // Write size to Subchunk2Size
				// field
				randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
				
				randomAccessWriter.close();
			} catch (IOException e) {
				
				state = State.STOPPED;
			}
			state = State.STOPPED;
		} else {
			state = State.STOPPED;
			
		}
	}
	
	public void release() {
		if (state == State.RECORDING) {
			stopRec();
		} else {
			if (state == State.READY) {
				try {
					randomAccessWriter.close(); // Remove prepared file
				} catch (IOException e) {
				}
				File file = new File(recordFilePath);
				if (file.exists()) {
					file.delete();
				}
				state = State.STOPPED;
			}
		}

		if (mAudioRecorder != null) {
			mAudioRecorder.release();
			mAudioRecorder = null;
		}

		if (mAcousticEchoCanceler != null) {
			mAcousticEchoCanceler.setEnabled(false);
			mAcousticEchoCanceler.release();
			mAcousticEchoCanceler = null;
		}

		if (mNoiseSuppressor != null) {
			mNoiseSuppressor.setEnabled(false);
			mNoiseSuppressor.release();
			mNoiseSuppressor = null;
		}

		if (mAutomaticGainControl != null) {
			mAutomaticGainControl.setEnabled(false);
			mAutomaticGainControl.release();
			mAutomaticGainControl = null;
		}
	}

	/*
	 *
	 * Method used for recording.
	 */
	private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
		@Override
		public void onPeriodicNotification(AudioRecord recorder) {
			if (mAudioRecorder != null && randomAccessWriter != null) {
				int r = mAudioRecorder.read(buffer, 0, buffer.length); // Fill buffer
				long v = 0;

				try {
					randomAccessWriter.write(buffer); // Write buffer to file
					payloadSize += buffer.length;
					if (bSamples == 16) {
						for (int i = 0; i < buffer.length/2; i++) { // 16bit
							// sample size
							short curSample = getShort(buffer[i * 2], buffer[i * 2 + 1]);
							if (curSample > cAmplitude) { // Check amplitude
								cAmplitude = curSample;
							}

						}
					} else { // 8bit sample size
						for (int i = 0; i < buffer.length; i++) {
							if (buffer[i] > cAmplitude) { // Check amplitude
								cAmplitude = buffer[i];
							}
						}
					}
				} catch (IOException e) {
				}
			}
		}
		
		@Override
		public void onMarkerReached(AudioRecord recorder) {
		}
	};



	/*
	 * Converts a byte[2] to a short, in LITTLE_ENDIAN format
	 */
	private short getShort(byte argB1, byte argB2) {
		return (short) (argB1 | (argB2 << 8));
	}

	
	/**
	 * 目前获取不够精确，待更新....
	 * 获得录音的音量
	 * @return
	 */
	@Override
	public int getVoiceLevel(int maxLevel) {
		return 3;
	}


	int BUFFER_SIZE;
	boolean isGetVoiceRun = false;


	public void getNoiseLevel() {

		new Thread(new Runnable() {
			@Override
			public void run() {
//				mAudioRecorder.startRecording();
				short[] buffer = new short[BUFFER_SIZE];
				isGetVoiceRun  = false;
				while (isGetVoiceRun) {
					// r是实际读取的数据长度，一般而言r会小于buffersize
					int r = mAudioRecorder.read(buffer, 0, BUFFER_SIZE);
					long v = 0;
					// 将 buffer 内容取出，进行平方和运算
					for (int i = 0; i < buffer.length; i++) {
						v += buffer[i] * buffer[i];
					}
					// 平方和除以数据总长度，得到音量大小。
					double mean = v / (double) r;
					double volume = 10 * Math.log10(mean);
					Log.d("debug2", "分贝值:" + volume);
					// 大概一秒十次
					synchronized (this) {
						try {
							wait(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

}
