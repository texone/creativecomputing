/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.graphics.font.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;

/**
 * Simple lorem ipsum generator.
 * 
 * @author Christian Riekoff
 */
public class CCLoremIpsumGenerator {
	
	private static class CCLoremWord{
		private String _myWord;
		private double _myPropability;
		
		private CCLoremWord(String theWord, double thePropability) {
			_myWord = theWord;
			_myPropability = thePropability;
		}
	}
	
	private static CCLoremWord[] start_words = new CCLoremWord[] {
		new CCLoremWord("Maecenas", 0.030254778f),
		new CCLoremWord("Ut", 0.03343949f),
		new CCLoremWord("Nunc", 0.024681529f),
		new CCLoremWord("In", 0.035031848f),
		new CCLoremWord("Vivamus", 0.025477707f),
		new CCLoremWord("Aenean", 0.04299363f),
		new CCLoremWord("Nam", 0.031847134f),
		new CCLoremWord("Pellentesque", 0.039808918f),
		new CCLoremWord("Proin", 0.027070064f),
		new CCLoremWord("Aliquam", 0.046178345f),
		new CCLoremWord("Vestibulum", 0.035828024f),
		new CCLoremWord("Quisque", 0.034235667f),
		new CCLoremWord("Curae;", 0.007961784f),
		new CCLoremWord("Morbi", 0.039808918f),
		new CCLoremWord("Nulla", 0.045382164f),
		new CCLoremWord("Lorem", 0.013535032f),
		new CCLoremWord("Mauris", 0.027070064f),
		new CCLoremWord("Nullam", 0.027070064f),
		new CCLoremWord("Curabitur", 0.039808918f),
		new CCLoremWord("Praesent", 0.0294586f),
		new CCLoremWord("Sed", 0.06210191f),
		new CCLoremWord("Cras", 0.031847134f),
		new CCLoremWord("Suspendisse", 0.039808918f),
		new CCLoremWord("Duis", 0.022292994f),
		new CCLoremWord("Etiam", 0.031847134f),
		new CCLoremWord("Class", 0.003980892f),
		new CCLoremWord("Fusce", 0.027866242f),
		new CCLoremWord("Integer", 0.031847134f),
		new CCLoremWord("Phasellus", 0.03821656f),
		new CCLoremWord("Donec", 0.060509555f),
		new CCLoremWord("Cum", 0.012738854f)
	};
	
	private static CCLoremWord[] words = new CCLoremWord[] {
		new CCLoremWord("habitant", 0.0013723696f),
		new CCLoremWord("mattis", 0.005146386f),
		new CCLoremWord("neque", 0.00800549f),
		new CCLoremWord("diam", 0.007662397f),
		new CCLoremWord("pharetra", 0.005946935f),
		new CCLoremWord("mauris", 0.0074336687f),
		new CCLoremWord("dolor", 0.009606588f),
		new CCLoremWord("urna", 0.007662397f),
		new CCLoremWord("facilisi", 0.0010292772f),
		new CCLoremWord("tellus", 0.0060612992f),
		new CCLoremWord("nibh", 0.0062900274f),
		new CCLoremWord("est", 0.007662397f),
		new CCLoremWord("nostra", 5.7182065E-4f),
		new CCLoremWord("rutrum", 0.0033165598f),
		new CCLoremWord("semper", 0.0056038424f),
		new CCLoremWord("elit", 0.010407137f),
		new CCLoremWord("ante", 0.00800549f),
		new CCLoremWord("dictum", 0.005832571f),
		new CCLoremWord("aptent", 5.7182065E-4f),
		new CCLoremWord("tristique", 0.0072049405f),
		new CCLoremWord("tempus", 0.005146386f),
		new CCLoremWord("mollis", 0.004574565f),
		new CCLoremWord("dapibus", 0.004002745f),
		new CCLoremWord("morbi", 0.0013723696f),
		new CCLoremWord("faucibus", 0.0065187556f),
		new CCLoremWord("erat", 0.008920402f),
		new CCLoremWord("adipiscing", 0.0062900274f),
		new CCLoremWord("sociosqu", 5.7182065E-4f),
		new CCLoremWord("vehicula", 0.005146386f),
		new CCLoremWord("non", 0.014181153f),
		new CCLoremWord("lacinia", 0.004231473f),
		new CCLoremWord("ipsum", 0.009263495f),
		new CCLoremWord("arcu", 0.0066331197f),
		new CCLoremWord("euismod", 0.0054894784f),
		new CCLoremWord("turpis", 0.009263495f),
		new CCLoremWord("imperdiet", 0.0066331197f),
		new CCLoremWord("habitasse", 5.7182065E-4f),
		new CCLoremWord("pulvinar", 0.0054894784f),
		new CCLoremWord("aliquet", 0.0053751143f),
		new CCLoremWord("cursus", 0.004803294f),
		new CCLoremWord("pretium", 0.004460201f),
		new CCLoremWord("primis", 0.0011436413f),
		new CCLoremWord("enim", 0.008691674f),
		new CCLoremWord("parturient", 0.0018298262f),
		new CCLoremWord("lobortis", 0.005718207f),
		new CCLoremWord("fringilla", 0.004803294f),
		new CCLoremWord("ultrices", 0.005718207f),
		new CCLoremWord("facilisis", 0.00526075f),
		new CCLoremWord("quis", 0.014295517f),
		new CCLoremWord("penatibus", 0.0018298262f),
		new CCLoremWord("consectetur", 0.0068618483f),
		new CCLoremWord("himenaeos", 5.7182065E-4f),
		new CCLoremWord("litora", 5.7182065E-4f),
		new CCLoremWord("varius", 0.005146386f),
		new CCLoremWord("inceptos", 5.7182065E-4f),
		new CCLoremWord("pellentesque", 0.005032022f),
		new CCLoremWord("tortor", 0.0074336687f),
		new CCLoremWord("dignissim", 0.004803294f),
		new CCLoremWord("fames", 0.0013723696f),
		new CCLoremWord("cubilia", 0.0011436413f),
		new CCLoremWord("nec", 0.01383806f),
		new CCLoremWord("mi", 0.00800549f),
		new CCLoremWord("convallis", 0.0069762124f),
		new CCLoremWord("porta", 0.004803294f),
		new CCLoremWord("eu", 0.014981702f),
		new CCLoremWord("lorem", 0.0070905765f),
		new CCLoremWord("platea", 5.7182065E-4f),
		new CCLoremWord("quam", 0.0068618483f),
		new CCLoremWord("nunc", 0.009263495f),
		new CCLoremWord("a", 0.016239706f),
		new CCLoremWord("leo", 0.0064043915f),
		new CCLoremWord("ut", 0.014867337f),
		new CCLoremWord("massa", 0.0070905765f),
		new CCLoremWord("potenti", 0.0017154621f),
		new CCLoremWord("scelerisque", 0.004574565f),
		new CCLoremWord("sit", 0.015096066f),
		new CCLoremWord("eget", 0.012351327f),
		new CCLoremWord("posuere", 0.006747484f),
		new CCLoremWord("sagittis", 0.005146386f),
		new CCLoremWord("per", 0.0011436413f),
		new CCLoremWord("vel", 0.012236962f),
		new CCLoremWord("nulla", 0.0073193046f),
		new CCLoremWord("orci", 0.008119853f),
		new CCLoremWord("magnis", 0.0018298262f),
		new CCLoremWord("conubia", 5.7182065E-4f),
		new CCLoremWord("et", 0.019899359f),
		new CCLoremWord("feugiat", 0.0064043915f),
		new CCLoremWord("netus", 0.0013723696f),
		new CCLoremWord("sodales", 0.0032021957f),
		new CCLoremWord("malesuada", 0.0069762124f),
		new CCLoremWord("metus", 0.0069762124f),
		new CCLoremWord("consequat", 0.005946935f),
		new CCLoremWord("molestie", 0.0056038424f),
		new CCLoremWord("taciti", 5.7182065E-4f),
		new CCLoremWord("ornare", 0.0037740164f),
		new CCLoremWord("congue", 0.004460201f),
		new CCLoremWord("amet", 0.015096066f),
		new CCLoremWord("montes", 0.0018298262f),
		new CCLoremWord("sapien", 0.005946935f),
		new CCLoremWord("gravida", 0.0053751143f),
		new CCLoremWord("condimentum", 0.0061756633f),
		new CCLoremWord("rhoncus", 0.0056038424f),
		new CCLoremWord("libero", 0.0066331197f),
		new CCLoremWord("hac", 5.7182065E-4f),
		new CCLoremWord("justo", 0.0072049405f),
		new CCLoremWord("velit", 0.007891125f),
		new CCLoremWord("ridiculus", 0.0018298262f),
		new CCLoremWord("volutpat", 0.0066331197f),
		new CCLoremWord("auctor", 0.0054894784f),
		new CCLoremWord("laoreet", 0.0060612992f),
		new CCLoremWord("vitae", 0.014638609f),
		new CCLoremWord("lacus", 0.0064043915f),
		new CCLoremWord("iaculis", 0.004117109f),
		new CCLoremWord("lectus", 0.008462946f),
		new CCLoremWord("dui", 0.0074336687f),
		new CCLoremWord("natoque", 0.0018298262f),
		new CCLoremWord("bibendum", 0.0053751143f),
		new CCLoremWord("fermentum", 0.004574565f),
		new CCLoremWord("tincidunt", 0.010407137f),
		new CCLoremWord("tempor", 0.0054894784f),
		new CCLoremWord("commodo", 0.004345837f),
		new CCLoremWord("blandit", 0.00526075f),
		new CCLoremWord("hendrerit", 0.0062900274f),
		new CCLoremWord("sollicitudin", 0.004460201f),
		new CCLoremWord("suscipit", 0.0054894784f),
		new CCLoremWord("dictumst", 5.7182065E-4f),
		new CCLoremWord("porttitor", 0.004917658f),
		new CCLoremWord("felis", 0.005718207f),
		new CCLoremWord("viverra", 0.0068618483f),
		new CCLoremWord("id", 0.014409881f),
		new CCLoremWord("ullamcorper", 0.005032022f),
		new CCLoremWord("aliquam", 0.0062900274f),
		new CCLoremWord("dis", 0.0018298262f),
		new CCLoremWord("elementum", 0.0053751143f),
		new CCLoremWord("vestibulum", 0.004917658f),
		new CCLoremWord("odio", 0.0077767614f),
		new CCLoremWord("eleifend", 0.004231473f),
		new CCLoremWord("ultricies", 0.0054894784f),
		new CCLoremWord("interdum", 0.005718207f),
		new CCLoremWord("vulputate", 0.004917658f),
		new CCLoremWord("magna", 0.0064043915f),
		new CCLoremWord("torquent", 5.7182065E-4f),
		new CCLoremWord("augue", 0.007891125f),
		new CCLoremWord("mus", 0.0018298262f),
		new CCLoremWord("at", 0.015324794f),
		new CCLoremWord("venenatis", 0.0046889298f),
		new CCLoremWord("accumsan", 0.004803294f),
		new CCLoremWord("in", 0.01578225f),
		new CCLoremWord("eros", 0.008462946f),
		new CCLoremWord("senectus", 0.0013723696f),
		new CCLoremWord("placerat", 0.0053751143f),
		new CCLoremWord("sed", 0.013609332f),
		new CCLoremWord("risus", 0.008119853f),
		new CCLoremWord("purus", 0.0073193046f),
		new CCLoremWord("ac", 0.014752974f),
		new CCLoremWord("ad", 5.7182065E-4f),
		new CCLoremWord("sociis", 0.0018298262f),
		new CCLoremWord("nisi", 0.009034767f),
		new CCLoremWord("ligula", 0.007662397f),
		new CCLoremWord("egestas", 0.0061756633f),
		new CCLoremWord("sem", 0.0074336687f),
		new CCLoremWord("nisl", 0.0062900274f),
		new CCLoremWord("luctus", 0.005718207f),
		new CCLoremWord("nascetur", 0.0018298262f)
	};
	
	private static class CCLoremLength{
		private int _myLength;
		private double _myPropability;
		
		private CCLoremLength(int theLength, double thePropability) {
			_myLength = theLength;
			_myPropability = thePropability;
		}
	}
	
	private static CCLoremLength[] phrase_lengths = new CCLoremLength[] {
		new CCLoremLength(2, 0.0014634146f),
		new CCLoremLength(3, 0.19317073f),
		new CCLoremLength(4, 0.26682928f),
		new CCLoremLength(5, 0.2512195f),
		new CCLoremLength(6, 0.057073172f),
		new CCLoremLength(7, 0.043902438f),
		new CCLoremLength(8, 0.06926829f),
		new CCLoremLength(9, 0.0068292683f),
		new CCLoremLength(10, 0.0121951215f),
		new CCLoremLength(11, 0.075121954f),
		new CCLoremLength(12, 0.011219512f),
		new CCLoremLength(13, 0.0014634146f),
		new CCLoremLength(14, 0.0058536585f),
		new CCLoremLength(17, 4.8780488E-4f),
		new CCLoremLength(19, 4.8780488E-4f),
		new CCLoremLength(18, 0.0019512195f),
		new CCLoremLength(21, 4.8780488E-4f),
		new CCLoremLength(24, 9.7560976E-4f)
	};
	
	private static CCLoremLength[] sentence_lengths = new CCLoremLength[] {
		new CCLoremLength(1, 0.6200485f),
		new CCLoremLength(2, 0.16814874f),
		new CCLoremLength(3, 0.14632174f),
		new CCLoremLength(4, 0.065481f)
	};
	
	@SuppressWarnings("unused")
	private static CCLoremLength[] paragraph_lengths = new CCLoremLength[] {
		new CCLoremLength(9, 0.036585364f),
		new CCLoremLength(10, 0.09756097f),
		new CCLoremLength(11, 0.07317073f),
		new CCLoremLength(12, 0.048780486f),
		new CCLoremLength(13, 0.09756097f),
		new CCLoremLength(14, 0.1097561f),
		new CCLoremLength(15, 0.06097561f),
		new CCLoremLength(16, 0.048780486f),
		new CCLoremLength(17, 0.1097561f),
		new CCLoremLength(18, 0.1097561f),
		new CCLoremLength(19, 0.09756097f),
		new CCLoremLength(20, 0.1097561f),
	};
	
	private static CCLoremIpsumGenerator instance;

	private CCLoremIpsumGenerator() {
		
	}
	
	public static String generate(int theNumberOfWords) {
		if(instance == null)instance = new CCLoremIpsumGenerator();
		return instance.generateLorem(theNumberOfWords);
	}

	/**
	 * Generates the given number of lorem ipsum words. Starting at the given index.
	 * 
	 * @param theNumberOfWords Amount of words
	 * @param theStartIndex index of lorem ipsum word to begin with
	 * @return generated Lorem ipsum text
	 */
	public String generateLorem(int theNumberOfWords) {
		List<Integer> myPhraseLengths = generatePhraseLength(theNumberOfWords);
		List<Integer> mySentenceLength = generateSentenceLength(myPhraseLengths.size());
		
		int myNumberOfSentences = mySentenceLength.size();
		
		StringBuilder myResult = new StringBuilder();
		
		for(int h = 0; h < myNumberOfSentences;h++) {
			int myNumberOfPhrases = mySentenceLength.remove((int)CCMath.random(mySentenceLength.size()));
			myResult.append(chooseStartWord());
			myResult.append(" ");
			for(int i = 0;i < myNumberOfPhrases; i++) {
				int myPhraseLength = myPhraseLengths.remove((int)CCMath.random(myPhraseLengths.size()));
				
				for(int j = i == 0 ? 1 : 0; j < myPhraseLength;j++) {
					myResult.append(chooseWord()+ " ");
				}
				myResult.deleteCharAt(myResult.length()-1);
				myResult.append(", ");
			}
			myResult.deleteCharAt(myResult.length()-1);
			myResult.deleteCharAt(myResult.length()-1);
			myResult.append(". ");
		}
		myResult.deleteCharAt(myResult.length()-1);
		
		return myResult.toString();
	}
	
	private List<Integer> generatePhraseLength(int theNumberOfWords){
		List<Integer> myResult = new ArrayList<Integer>(theNumberOfWords);
		int i = 0;
		int myPhraseLength = 0;
		while(i + myPhraseLength < theNumberOfWords - 4) {
			myPhraseLength = CCMath.min(choosePhraseLength(), theNumberOfWords - i);
			myResult.add(myPhraseLength);
			i += myPhraseLength;
		}
		int myLast = theNumberOfWords - i;
		if(myLast > 0)myResult.add(myLast);
		return myResult;
	}
	
	private List<Integer> generateSentenceLength(int theNumberOfPhrases){
		List<Integer> myResult = new ArrayList<Integer>(theNumberOfPhrases);
		int i = 0;
		while(i < theNumberOfPhrases) {
			int myPhraseLength = CCMath.min(chooseSentenceLength(), theNumberOfPhrases - i);
			myResult.add(myPhraseLength);
			i += myPhraseLength;
		}
		int myLast = theNumberOfPhrases - i;
		if(myLast > 0)myResult.add(myLast);
		return myResult;
	}
	
	@SuppressWarnings("unused")
	private List<String> generateWords(int theNumberOfWords){
		List<String> myResult = new ArrayList<String>(theNumberOfWords);
		for(int i = 0; i < theNumberOfWords;i++) {
			myResult.add(chooseWord());
		}
		return myResult;
	}
	
	private String chooseStartWord() {
		return chooseWord(start_words);
	}
	
	private String chooseWord() {
		return chooseWord(words);
	}
	
	private String chooseWord(CCLoremWord[] theWords) {
		double myValue = CCMath.random();
		double myAdd = 0;
		
		for(CCLoremWord myWord:theWords) {
			myAdd += myWord._myPropability;
			if(myValue < myAdd)return myWord._myWord;
		}
		return theWords[theWords.length - 1]._myWord;
	}
	
	private int choosePhraseLength() {
		return chooseLength(phrase_lengths);
	}
	
	private int chooseSentenceLength() {
		return chooseLength(sentence_lengths);
	}
	
	private int chooseLength(CCLoremLength[] theLength) {
		double myValue = CCMath.random();
		double myAdd = 0;
		
		for(CCLoremLength myLength:theLength) {
			myAdd += myLength._myPropability;
			if(myValue < myAdd)return myLength._myLength;
		}
		
		return theLength[theLength.length - 1]._myLength;
	}
	
	public static void main(String[] args) {
		CCLoremIpsumGenerator.generate(300);
	}
}
