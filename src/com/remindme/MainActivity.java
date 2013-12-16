package com.remindme;

import java.util.ArrayList;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.os.Bundle;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.util.Log;

public class MainActivity extends Activity {
	
	LiveCard liveCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		Card card = new Card(this);
		
		ArrayList<String> voiceResults = getIntent().getExtras()
		        .getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		
		card.setText(voiceResults.toString());
//		TimelineManager man = TimelineManager.from(this);
//		liveCard = man.getLiveCard("test");
//		liveCard.setViews(card.toRemoteViews());
//		
//		liveCard.publish();
		
		setContentView(card.toView());
	}//end of onCreate
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "onDestory is running.");
//		liveCard.unpublish();
		
	}



}//end of class
