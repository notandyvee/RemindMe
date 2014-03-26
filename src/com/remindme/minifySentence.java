package com.remindme;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class minifySentence {
	
	public static String stripSentence(String sentence) {
		//TODO Something else. This probably is the worst way to do this.
		
		sentence = sentence.toLowerCase();
		
		sentence = sentence.replaceAll("\\bthe\\b", "");
		sentence = sentence.replaceAll("\\bthis\\b", "");
		sentence = sentence.replaceAll("\\bwas\\b", "");
		sentence = sentence.replaceAll("\\bput\\b", "");
		sentence = sentence.replaceAll("\\ba\\b", "");
		sentence = sentence.replaceAll("\\ban\\b", "");
		sentence = sentence.replaceAll("\\bas\\b", "");
		sentence = sentence.replaceAll("\\band\\b", "");
		sentence = sentence.replaceAll("\\bbe\\b", "");
		sentence = sentence.replaceAll("\\bhave\\b", "");
		sentence = sentence.replaceAll("\\bin\\b", "");
		sentence = sentence.replaceAll("\\bto\\b", "");
		sentence = sentence.replaceAll("\\bit\\b", "");
		sentence = sentence.replaceAll("\\bto\\b", "");
		sentence = sentence.replaceAll("\\bfor\\b", "");
		sentence = sentence.replaceAll("\\bi\\b", "");
		sentence = sentence.replaceAll("\\bthat\\b", "");
		sentence = sentence.replaceAll("\\byou\\b", "");
		sentence = sentence.replaceAll("\\bhe\\b", "");
		sentence = sentence.replaceAll("\\bshe\\b", "");
		sentence = sentence.replaceAll("\\bon\\b", "");
		sentence = sentence.replaceAll("\\bwith\\b", "");
		sentence = sentence.replaceAll("\\bdo\\b", "");
		sentence = sentence.replaceAll("\\bat\\b", "");
		sentence = sentence.replaceAll("\\bby\\b", "");
		sentence = sentence.replaceAll("\\bwhere\\b", "");
		sentence = sentence.replaceAll("\\bis\\b", "");
		sentence = sentence.replaceAll("\\bthere\\b", "");
		sentence = sentence.replaceAll("\\bhis\\b", "");
		sentence = sentence.replaceAll("\\bher\\b", "");
		sentence = sentence.replaceAll("\\bor\\b", "");
		sentence = sentence.replaceAll("\\bwhich\\b", "");
		sentence = sentence.replaceAll("\\bwe\\b", "");
		sentence = sentence.replaceAll("\\bsay\\b", "");
		sentence = sentence.replaceAll("\\bwill\\b", "");
		sentence = sentence.replaceAll("\\bwould\\b", "");
		sentence = sentence.replaceAll("\\bcan\\b", "");
		sentence = sentence.replaceAll("\\bif\\b", "");
		sentence = sentence.replaceAll("\\btheir\\b", "");
		sentence = sentence.replaceAll("\\bgo\\b", "");
		sentence = sentence.replaceAll("\\bwhat\\b", "");
		sentence = sentence.replaceAll("\\bat\\b", "");
		sentence = sentence.replaceAll("\\bsome\\b", "");
		sentence = sentence.replaceAll("\\bcould\\b", "");
		sentence = sentence.replaceAll("\\bdid\\b", "");
		sentence = sentence.replaceAll("\\bleave\\b", "");
		sentence = sentence.replaceAll("\\bleft\\b", "");
		sentence = sentence.replaceAll("\\bmy\\b", "");
		sentence = sentence.replaceAll("\\bthese\\b", "");
		sentence = sentence.replaceAll("\\bright\\b", "");
		sentence = sentence.replaceAll("\\blike\\b", "");
		sentence = sentence.replaceAll("\\bgood\\b", "");
		sentence = sentence.replaceAll("\\bgreat\\b", "");
		sentence = sentence.replaceAll("\\bany\\b", "");
		sentence = sentence.replaceAll("\\bnew\\b", "");
		sentence = sentence.replaceAll("\\bvery\\b", "");
		sentence = sentence.replaceAll("\\bhow\\b", "");
		sentence = sentence.replaceAll("\\bwhen\\b", "");
		sentence = sentence.replaceAll("\\bfind\\b", "");

		
		String newSentence = "";
		String[] arr = sentence.split(" ");
		for (String s : arr) {
			if (!s.isEmpty()) {
				newSentence += s + " ";
			}
		}
		
		return newSentence;
	}

}
