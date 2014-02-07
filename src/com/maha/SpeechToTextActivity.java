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
	private final int GOOGLE_STT = 1000, MY_UI=1001;				//requestCode. 구글음성인식, 내가 만든 Activity
	private ArrayList<String> mResult;									//음성인식 결과 저장할 list
	private String mSelectedString;										//결과 list 중 사용자가 선택한 텍스트
	private TextView mResultTextView;									//최종 결과 출력하는 텍스트 뷰
	private TextToSpeech mTTS;											//TextToSpeech 객체
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.show).setOnClickListener(this);				//구글 음성인식 앱 이용.
		//findViewById(R.id.hide).setOnClickListener(this);				//내가 만든 activity 이용.
		
		mResultTextView = (TextView)findViewById(R.id.result);		//결과 출력 뷰
		
		initData();
	}
	
	private void initData() {
		mTTS = new TextToSpeech(this, this);		//tts 객체 생성
	}

	@Override
	public void onClick(View v) {
		int view = v.getId();
		
		if(view == R.id.show){		//구글 음성인식 앱 사용이면
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);			//intent 생성
			i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());		//음성인식을 호출한 패키지
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");						//음성인식 언어 설정
			i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me");						//사용자에게 보여 줄 글자
			
			startActivityForResult(i, GOOGLE_STT);										//구글 음성인식 실행
		}
		/*
		else if(view == R.id.hide){
			startActivityForResult(new Intent(this, CustomUIActivity.class), MY_UI);	//내가 만든 activity 실행
		}
		*/
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if( resultCode == RESULT_OK  && (requestCode == GOOGLE_STT || requestCode == MY_UI) ){		//결과가 있으면
			//showSelectDialog(requestCode, data);				//결과를 다이얼로그로 출력.
			isMaha(requestCode, data);
		}
		else{															//결과가 없으면 에러 메시지 출력
			String msg = null;
			
			//내가 만든 activity에서 넘어오는 오류 코드를 분류
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
			
			if(msg != null)		//오류 메시지가 null이 아니면 메시지 출력
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void isMaha(int requestCode, Intent data){
		String key = "";
		if(requestCode == GOOGLE_STT)						//구글음성인식이면
			key = RecognizerIntent.EXTRA_RESULTS;			//키값 설정
		else if(requestCode == MY_UI)						//내가 만든 activity 이면
			key = SpeechRecognizer.RESULTS_RECOGNITION;		//키값 설정
		
		mResult = data.getStringArrayListExtra(key);		//인식된 데이터 list 받아옴.
		String[] result = new String[mResult.size()];		//배열생성. 다이얼로그에서 출력하기 위해
		mResult.toArray(result);							//	list 배열로 변환
		
		boolean isMaha = false;
		
		for(String s : mResult){
			if(s.contains("maha")) isMaha = true;
		}
		
		if(isMaha){
			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);			//intent 생성
			i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());		//음성인식을 호출한 패키지
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");						//음성인식 언어 설정
			i.putExtra(RecognizerIntent.EXTRA_PROMPT, "help you?");						//사용자에게 보여 줄 글자
			
			startActivityForResult(i, GOOGLE_STT);										//구글 음성인식 실행
		} else {
			showSelectDialog(requestCode, data);
		}
	}
	
	//결과 list 출력하는 다이얼로그 생성 
	private void showSelectDialog(int requestCode, Intent data){
		String key = ""; 
		if(requestCode == GOOGLE_STT)						//구글음성인식이면
			key = RecognizerIntent.EXTRA_RESULTS;			//키값 설정
		else if(requestCode == MY_UI)						//내가 만든 activity 이면
			key = SpeechRecognizer.RESULTS_RECOGNITION;		//키값 설정
		
		mResult = data.getStringArrayListExtra(key);		//인식된 데이터 list 받아옴.
		String[] result = new String[mResult.size()];		//배열생성. 다이얼로그에서 출력하기 위해
		mResult.toArray(result);							//	list 배열로 변환
		
		//1개 선택하는 다이얼로그 생성
		AlertDialog ad = new AlertDialog.Builder(this).setTitle("선택하세요.")
							.setSingleChoiceItems(result, -1, new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
										mSelectedString = mResult.get(which);		//선택하면 해당 글자 저장
								}
							})
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
									//mResultTextView.setText("인식결과 : "+mSelectedString);		//확인 버튼 누르면 결과 출력

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
									
									mTTS.setLanguage(Locale.US);									//언어 설정.
									mTTS.setPitch((float)1);										//pitch 설정.
									mTTS.setSpeechRate((float)1);									//rate 설정.
									mTTS.speak(output.toString(), TextToSpeech.QUEUE_FLUSH, null);	//해당 언어로 텍스트 음성 출력
									//mTTS.speak("hahahahahah", TextToSpeech.QUEUE_FLUSH, null);	//해당 언어로 텍스트 음성 출력
									
									mResultTextView.setMovementMethod(new ScrollingMovementMethod());
									mResultTextView.setText(output);	
									
								}
							})
							.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which) {
									mResultTextView.setText("");		//취소버튼 누르면 초기화
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
		if(mTTS != null) {			//TextToSpeech 객체가 생성되었으면
			mTTS.stop();			//음성 출력 중지
			mTTS.shutdown();		//TextToSpeech 종료
		}
		super.finish();
	}
}