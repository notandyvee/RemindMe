package com.photoglassic;

import android.annotation.SuppressLint;

public class minifySentence {
	
	
	@SuppressLint("DefaultLocale")
	public static String stripSentence(String sentence) {
		//TODO Something else. This probably is the worst way to do this.
		
		sentence = sentence.toLowerCase();
		
		sentence = sentence.replaceAll("\\bthe\\b" + 
		"|\\bthis\\b" + 
		"|\\bwas\\b" + 
		"|\\bare\\b" + 
		"|\\bput\\b" + 
		"|\\ba\\b" + 
		"|\\ban\\b" + 
		"|\\bas\\b" + 
		"|\\band\\b" + 
		"|\\bbe\\b" + 
		"|\\bhave\\b" + 
		"|\\bin\\b" + 
		"|\\bto\\b" + 
		"|\\bit\\b" + 
		"|\\bto\\b" + 
		"|\\bfor\\b" + 
		"|\\bi\\b" + 
		"|\\bthat\\b" + 
		"|\\byou\\b" + 
		"|\\bhe\\b" + 
		"|\\bshe\\b" + 
		"|\\bon\\b" + 
		"|\\bwith\\b" + 
		"|\\bdo\\b" + 
		"|\\bat\\b" + 
		"|\\bby\\b" + 
		"|\\bwhere\\b" + 
		"|\\bis\\b" + 
		"|\\bthere\\b" + 
		"|\\bhis\\b" + 
		"|\\bher\\b" + 
		"|\\bor\\b" + 
		"|\\bwhich\\b" + 
		"|\\bwe\\b" + 
		"|\\bsay\\b" + 
		"|\\bwill\\b" + 
		"|\\bwould\\b" + 
		"|\\bcan\\b" + 
		"|\\bif\\b" + 
		"|\\btheir\\b" + 
		"|\\bgo\\b" + 
		"|\\bwhat\\b" + 
		"|\\bat\\b" + 
		"|\\bsome\\b" + 
		"|\\bcould\\b" + 
		"|\\bdid\\b" + 
		"|\\bleave\\b" + 
		"|\\bleft\\b" + 
		"|\\bmy\\b" + 
		"|\\bthese\\b" + 
		"|\\bright\\b" + 
		"|\\blike\\b" + 
		"|\\bgood\\b" + 
		"|\\bgreat\\b" + 
		"|\\bany\\b" + 
		"|\\bnew\\b" + 
		"|\\bvery\\b" + 
		"|\\bhow\\b" + 
		"|\\bwhen\\b" +
		"|\\bwhere's\\b" +
		"|\\bfind\\b", "");

		
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
