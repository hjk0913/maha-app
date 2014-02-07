package com.maha;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import com.maha.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class SpeechToTextActivity extends Activity implements OnClickListener, OnInitListener {
	private final int GOOGLE_STT = 1000, MY_UI=1001;				//requestCode. ���������ν�, ���� ���� Activity
	private ArrayList<String> mResult;									//�����ν� ��� ������ list
	private String mSelectedString;										//��� list �� ����ڰ� ������ �ؽ�Ʈ
	private TextView mResultTextView;									//���� ��� ����ϴ� �ؽ�Ʈ ��
	private TextToSpeech mTTS;											//TextToSpeech ��ü
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.show).setOnClickListener(this);				//���� �����ν� �� �̿�.
		//findViewById(R.id.hide).setOnClickListener(this);				//���� ���� activity �̿�.
		
		mResultTextView = (TextView)findViewById(R.id.result);		//��� ��� ��
		
		initData();
	}
	
	private void initData() {
		mTTS = new TextToSpeech(this, this);		//tts ��ü ����
	}

	@Override
	public void onClick(View v) {
		int view = v.getId();
		
		if(view == R.id.show){		//���� �����ν� �� ����̸�
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);			//intent ����
			i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());		//�����ν��� ȣ���� ��Ű��
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");						//�����ν� ��� ����
			i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me");						//����ڿ��� ���� �� ����
			
			startActivityForResult(i, GOOGLE_STT);										//���� �����ν� ����
		}
		/*
		else if(view == R.id.hide){
			startActivityForResult(new Intent(this, CustomUIActivity.class), MY_UI);	//���� ���� activity ����
		}
		*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if( resultCode == RESULT_OK  && (requestCode == GOOGLE_STT || requestCode == MY_UI) ){		//����� ������
			//showSelectDialog(requestCode, data);				//����� ���̾�α׷� ���.
			isMaha(requestCode, data);
		}
		else{															//����� ������ ���� �޽��� ���
			String msg = null;
			
			//���� ���� activity���� �Ѿ���� ���� �ڵ带 �з�
			switch(resultCode){
				case SpeechRecognizer.ERROR_AUDIO:
					msg = "Error occured from audio.";
					break;
				case SpeechRecognizer.ERROR_CLIENT:
					msg = "Error occured from your device.";
					break;
				case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
					msg = "You are not allowed to use this service.";
					break;
				case SpeechRecognizer.ERROR_NETWORK:
					msg = "Error occured from bad network.";
					break;
				case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
					msg = "Error occured from bad network.";
					break;
				case SpeechRecognizer.ERROR_NO_MATCH:
					msg = "I'm sorry. I can't understand what you told me right now.";
					break;
				case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
					msg = "I got too manay questions at the moment. Tell me later please.";
					break;
				case SpeechRecognizer.ERROR_SERVER:
					msg = "I got a problem from our server systems.";
					break;
				case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
					msg = "You said nothing. Say something.";
					break;
			}
			
			if(msg != null)		//���� �޽����� null�� �ƴϸ� �޽��� ���
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void isMaha(int requestCode, Intent data){
		String key = "";
		if(requestCode == GOOGLE_STT)						//���������ν��̸�
			key = RecognizerIntent.EXTRA_RESULTS;			//Ű�� ����
		else if(requestCode == MY_UI)						//���� ���� activity �̸�
			key = SpeechRecognizer.RESULTS_RECOGNITION;		//Ű�� ����
		
		mResult = data.getStringArrayListExtra(key);		//�νĵ� ������ list �޾ƿ�.
		String[] result = new String[mResult.size()];		//�迭����. ���̾�α׿��� ����ϱ� ����
		mResult.toArray(result);							//	list �迭�� ��ȯ
		
		boolean isMaha = false;
		
		for(String s : mResult){
			if(s.contains("maha")) isMaha = true;
		}
		
		if(isMaha){
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);			//intent ����
			i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());		//�����ν��� ȣ���� ��Ű��
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");						//�����ν� ��� ����
			i.putExtra(RecognizerIntent.EXTRA_PROMPT, "help you?");						//����ڿ��� ���� �� ����
			
			startActivityForResult(i, GOOGLE_STT);										//���� �����ν� ����
		} else {
			showSelectDialog(requestCode, data);
		}
	}
	
	//��� list ����ϴ� ���̾�α� ���� 
	private void showSelectDialog(int requestCode, Intent data){
		String key = ""; 
		if(requestCode == GOOGLE_STT)						//���������ν��̸�
			key = RecognizerIntent.EXTRA_RESULTS;			//Ű�� ����
		else if(requestCode == MY_UI)						//���� ���� activity �̸�
			key = SpeechRecognizer.RESULTS_RECOGNITION;		//Ű�� ����
		
		mResult = data.getStringArrayListExtra(key);		//�νĵ� ������ list �޾ƿ�.
		String[] result = new String[mResult.size()];		//�迭����. ���̾�α׿��� ����ϱ� ����
		mResult.toArray(result);							//	list �迭�� ��ȯ
		
		//1�� �����ϴ� ���̾�α� ����
		AlertDialog ad = new AlertDialog.Builder(this).setTitle("�����ϼ���.")
							.setSingleChoiceItems(result, -1, new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
										mSelectedString = mResult.get(which);		//�����ϸ� �ش� ���� ����
								}
							})
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
									//mResultTextView.setText("�νİ�� : "+mSelectedString);		//Ȯ�� ��ư ������ ��� ���

									StringBuilder output = new StringBuilder();
									try{
										URL url = null;
										url = new URL("http://api.wolframalpha.com/v2/query?input=" + mSelectedString + "&appid=6KY5AA-EL889YUY6A");
										//url = new URL("http://115.140.158.140:8080/test/index.jsp?param=" + mSelectedString);
										
										HttpURLConnection conn = (HttpURLConnection)url.openConnection();
										if(conn != null){
											conn.setConnectTimeout(5000);
											conn.setRequestMethod("GET");
											conn.setDoInput(true);
											conn.setDoOutput(true);
											
											int resCode = conn.getResponseCode();
											if(resCode == HttpURLConnection.HTTP_OK){
												BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
												String line = null;
												while(true){
													line = reader.readLine();
													if(line == null){
														break;
													}
													output.append(line + "\n");
												}
												reader.close();
												conn.disconnect();
											}
										}
									} catch(Exception ex) {}
									
									mTTS.setLanguage(Locale.US);									//��� ����.
									mTTS.setPitch((float)1);										//pitch ����.
									mTTS.setSpeechRate((float)1);									//rate ����.
									mTTS.speak(output.toString(), TextToSpeech.QUEUE_FLUSH, null);	//�ش� ���� �ؽ�Ʈ ���� ���
									//mTTS.speak("hahahahahah", TextToSpeech.QUEUE_FLUSH, null);	//�ش� ���� �ؽ�Ʈ ���� ���
									
									mResultTextView.setMovementMethod(new ScrollingMovementMethod());
									mResultTextView.setText(output);	
									
								}
							})
							.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
									mResultTextView.setText("");		//��ҹ�ư ������ �ʱ�ȭ
									mSelectedString = null;
								}
							}).create();
		
		ad.show();
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void finish() {
		if(mTTS != null) {			//TextToSpeech ��ü�� �����Ǿ�����
			mTTS.stop();			//���� ��� ����
			mTTS.shutdown();		//TextToSpeech ����
		}
		super.finish();
	}
}