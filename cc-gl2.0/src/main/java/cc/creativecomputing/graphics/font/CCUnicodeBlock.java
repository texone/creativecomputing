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
package cc.creativecomputing.graphics.font;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A family of character subsets representing the character blocks in the Unicode specification. Character blocks
 * generally define characters used for a specific script or purpose. A character is contained by at most one Unicode
 * block.
 * 
 * @author info
 * 
 */
public class CCUnicodeBlock {
	private String _myName;
	private int _myStart;
	private int _myEnd;
	
	private static Map<String, CCUnicodeBlock> map = new HashMap<String, CCUnicodeBlock>();

	/**
	 * Create a CCUnicodeBlock with the given identifier name. This name must be the same as the block identifier.
	 */
	private CCUnicodeBlock(final String theName, final int theStart, final int theEnd) {
		_myName = theName;
		_myStart = theStart;
		_myEnd = theEnd;
		
		map.put(theName.toUpperCase(Locale.US), this);
	}

	/**
	 * Create a CCUnicodeBlock with the given identifier name and alias name.
	 */
	private CCUnicodeBlock(final String theName, final int theStart, final int theEnd, final String theAlias) {
		this(theName, theStart, theEnd);
		map.put(theAlias.toUpperCase(Locale.US), this);
	}

	/**
	 * Create a CCUnicodeBlock with the given identifier name and alias names.
	 */
	private CCUnicodeBlock(final String theName, final int theStart, final int theEnd, final String[] theAliasName) {
		this(theName, theStart, theEnd);
		if (theAliasName != null) {
			for (int x = 0; x < theAliasName.length; ++x) {
				map.put(theAliasName[x].toUpperCase(Locale.US), this);
			}
		}
	}
	
	public int start() {
		return _myStart;
	}
	
	public int end() {
		return _myEnd;
	}
	
	public String name() {
		return _myName;
	}
	
	public char[] chars() {
		char[] myResult = new char[_myEnd - _myStart + 1];
		
		for(int i = _myStart; i <= _myEnd; i++) {
			myResult[i - _myStart] = (char)i;
		}
		
		return myResult;
	}
	
	@Override
	public String toString() {
		return _myName;
	}

	/**
	 * @shortdesc Constant for the "Basic Latin" Unicode character block.
	 * The first 128 code points in Unicode are identical with ASCII 
	 * (American Standard Code for Information Interchange), a standard 
	 * from 1967. ASCII is a 7-bit code originally designed for paper tape, 
	 * containing 33 control characters and 95 printing characters. It is adequate 
	 * for writing basic English, Hawaiian, and Swahili, but hardly anything else. 
	 * Some English words, such as resume, are properly written using non-ASCII 
	 * characters, but often written without them.
	 */
	public static final CCUnicodeBlock BASIC_LATIN = new CCUnicodeBlock(
		"BASIC_LATIN", 
		0, 127, 
		new String[] { "Basic Latin", "BasicLatin" }
	);

	/**
	 * @shortdesc Constant for the "Latin-1 Supplement" Unicode character block.
	 * The 128 characters in this Unicode block are identical to the upper eight-bit 
	 * characters in the ISO 8859-1 standard, which was derived from the DEC VT220 
	 * terminal\'s \"Multinational Character Set\". The accented characters in this 
	 * block can be used to supplement the ASCII block in the representation of many 
	 * Western and Northern European languages, such as French, German, Dutch, Spanish, 
	 * Italian, Portuguese, Swedish, Norwegian, Danish, Finnish and Icelandic. 
	 * One flaw is that the french character ? and ? is not included in the ISO 8859-1. 
	 * It was said that writing oe and OE was acceptable, but it is not always good enough.
	 */
	public static final CCUnicodeBlock LATIN_1_SUPPLEMENT = new CCUnicodeBlock(
		"LATIN_1_SUPPLEMENT", 
		128, 255, 
		new String[] { "Latin-1 Supplement", "Latin-1Supplement" }
	);

	/**
	 * @shortdesc Constant for the "Latin Extended-A" Unicode character block.
	 * Languages Afrikaans, Azerbaijani, Bosnian, Catalan, Croatian, Czech, Dutch, 
	 * Estonian, Finnish, French, Gaelic (old orthography), Hungarian, Igbo, Irish, 
	 * Latin, Latvian, Lithuanian, Maltese, Polish, Romanian,  Sami, Serbian (Latin), 
	 * Sorbian, Slovak, Slovenian, Turkish, Welsh, also Esperanto
	 */
	public static final CCUnicodeBlock LATIN_EXTENDED_A = new CCUnicodeBlock(
		"LATIN_EXTENDED_A", 
		256, 383, 
		new String[] { "Latin Extended-A", "LatinExtended-A" }
	);

	/**
	 * Constant for the "Latin Extended-B" Unicode character block.
	 */
	public static final CCUnicodeBlock LATIN_EXTENDED_B = new CCUnicodeBlock(
		"LATIN_EXTENDED_B", 
		384, 591, 
		new String[] { "Latin Extended-B", "LatinExtended-B" }
	);

	/**
	 * @shortdesc Constant for the "IPA Extensions" Unicode character block.
	 * The IPA is a notational standard for the phonetic representation of all languages.
	 * It was established in 1886 by the International Phonetic Association (IPA also) and 
	 * was based on the latin script. In the course of its history it went through a lot of 
	 * changes and today, it is the world standard. The latest Version of the IPA Alphabet 
	 * was published in 1993, updated 1996.
	 */
	public static final CCUnicodeBlock IPA_EXTENSIONS = new CCUnicodeBlock(
		"IPA_EXTENSIONS", 
		592, 687, 
		new String[] { "IPA Extensions", "IPAExtensions" }
	);

	/**
	 * @shortdesc Constant for the "Spacing Modifier Letters" Unicode character block.
	 * Accents and modifier letters The characters in this block are mainly accents, 
	 * modifier letters, diacritics and tone marks for the International Phonetic 
	 * Alphabet (IPA, previous block) and other phonetic transcription systems.
	 */
	public static final CCUnicodeBlock SPACING_MODIFIER_LETTERS = new CCUnicodeBlock(
		"SPACING_MODIFIER_LETTERS", 
		688, 767, 
		new String[] { "Spacing Modifier Letters","SpacingModifierLetters" }
	);

	/**
	 * @shortdesc Constant for the "Combining Diacritical Marks" Unicode character block.
	 * <p>
	 * Combining characters are made to be combined with characters preceding them and 
	 * are said to "apply" to those characters. They don't appear on their own and the 
	 * dotted circle indicates the position of the character they're combined with.
	 * </p>
	 * <p>
	 * In the text file, the diacritical character is stored after the base character it applies to.
	 * In this block you find ordinary diacritics (U+0300 - U+0338), additions for Greek (U+0342-0345) 
	 * and Vietnamese tone marks, additions for the IPA and IPA diacritics for disordered speech 
	 * (U+0345 - U+034E), additions for the Uralic Phonetic Alphabet (U+0350 - U+0357) and Medieval 
	 * Superscript Letter Diacritics (U+ 0363 - U+036F).
	 * </p>
	 * <p>
	 * The codepoint U+034F is a grapheme joiner, it has no visible glyph but indicates to 
	 * the software that adjoining characters are to be treated as a graphemic unit.
	 * </p>
	 */
	public static final CCUnicodeBlock COMBINING_DIACRITICAL_MARKS = new CCUnicodeBlock(
		"COMBINING_DIACRITICAL_MARKS", 
		768, 879,
		new String[] { "Combining Diacritical Marks","CombiningDiacriticalMarks" }
	);

	/**
	 * Constant for the "Greek and Coptic" Unicode character block.
	 */
	public static final CCUnicodeBlock GREEK = new CCUnicodeBlock(
		"GREEK", 
		880, 1023,
		new String[] { "Greek and Coptic", "GreekandCoptic" }
	);

	/**
	 * Constant for the "Cyrillic" Unicode character block.
	 */
	public static final CCUnicodeBlock CYRILLIC = new CCUnicodeBlock("CYRILLIC",1024,1279);

	/**
	 * Constant for the "Cyrillic Supplementary" Unicode character block.
	 */
	public static final CCUnicodeBlock CYRILLIC_SUPPLEMENTARY = new CCUnicodeBlock(
		"CYRILLIC_SUPPLEMENTARY", 
		1280, 1327,
		new String[] { "Cyrillic Supplementary", "CyrillicSupplementary" }
	);

	/**
	 * Constant for the "Armenian" Unicode character block.
	 */
	public static final CCUnicodeBlock ARMENIAN = new CCUnicodeBlock("ARMENIAN",1328,1423);

	/**
	 * Constant for the "Hebrew" Unicode character block.
	 */
	public static final CCUnicodeBlock HEBREW = new CCUnicodeBlock("HEBREW",1424,1535);

	/**
	 * Constant for the "Arabic" Unicode character block.
	 */
	public static final CCUnicodeBlock ARABIC = new CCUnicodeBlock("ARABIC",1536,1791);

	/**
	 * Constant for the "Syriac" Unicode character block.
	 */
	public static final CCUnicodeBlock SYRIAC = new CCUnicodeBlock("SYRIAC",1792,1871);

	/**
	 * Constant for the "Thaana" Unicode character block.
	 */
	public static final CCUnicodeBlock THAANA = new CCUnicodeBlock("THAANA", 1920, 1983);

	/**
	 * Constant for the "Devanagari" Unicode character block.
	 */
	public static final CCUnicodeBlock DEVANAGARI = new CCUnicodeBlock("DEVANAGARI", 2304, 2431);

	/**
	 * Constant for the "Bengali" Unicode character block.
	 */
	public static final CCUnicodeBlock BENGALI = new CCUnicodeBlock("BENGALI", 2432, 2559);

	/**
	 * Constant for the "Gurmukhi" Unicode character block.
	 */
	public static final CCUnicodeBlock GURMUKHI = new CCUnicodeBlock("GURMUKHI", 2560, 2687);

	/**
	 * Constant for the "Gujarati" Unicode character block.
	 */
	public static final CCUnicodeBlock GUJARATI = new CCUnicodeBlock("GUJARATI", 2688, 2815);

	/**
	 * Constant for the "Oriya" Unicode character block.
	 */
	public static final CCUnicodeBlock ORIYA = new CCUnicodeBlock("ORIYA", 2816, 2943);

	/**
	 * Constant for the "Tamil" Unicode character block.
	 */
	public static final CCUnicodeBlock TAMIL = new CCUnicodeBlock("TAMIL", 2944, 3071);

	/**
	 * Constant for the "Telugu" Unicode character block.
	 */
	public static final CCUnicodeBlock TELUGU = new CCUnicodeBlock("TELUGU", 3072, 3199);

	/**
	 * Constant for the "Kannada" Unicode character block.
	 */
	public static final CCUnicodeBlock KANNADA = new CCUnicodeBlock("KANNADA", 3200, 3327);

	/**
	 * Constant for the "Malayalam" Unicode character block.
	 */
	public static final CCUnicodeBlock MALAYALAM = new CCUnicodeBlock("MALAYALAM", 3328, 3455);

	/**
	 * Constant for the "Sinhala" Unicode character block.
	 */
	public static final CCUnicodeBlock SINHALA = new CCUnicodeBlock("SINHALA", 3456, 3583);

	/**
	 * Constant for the "Thai" Unicode character block.
	 */
	public static final CCUnicodeBlock THAI = new CCUnicodeBlock("THAI", 3584, 3711);

	/**
	 * Constant for the "Lao" Unicode character block.
	 */
	public static final CCUnicodeBlock LAO = new CCUnicodeBlock("LAO", 3712, 3839);

	/**
	 * Constant for the "Tibetan" Unicode character block.
	 */
	public static final CCUnicodeBlock TIBETAN = new CCUnicodeBlock("TIBETAN", 3840, 4095);

	/**
	 * Constant for the "Myanmar" Unicode character block.
	 */
	public static final CCUnicodeBlock MYANMAR = new CCUnicodeBlock("MYANMAR", 4096, 4255);

	/**
	 * Constant for the "Georgian" Unicode character block.
	 */
	public static final CCUnicodeBlock GEORGIAN = new CCUnicodeBlock("GEORGIAN", 4256, 4351);

	/**
	 * Constant for the "Hangul Jamo" Unicode character block.
	 */
	public static final CCUnicodeBlock HANGUL_JAMO = new CCUnicodeBlock(
		"HANGUL_JAMO", 
		4352, 4607,
		new String[] { "Hangul Jamo", "HangulJamo" }
	);

	/**
	 * Constant for the "Ethiopic" Unicode character block.
	 */
	public static final CCUnicodeBlock ETHIOPIC = new CCUnicodeBlock("ETHIOPIC", 4608, 4991);

	/**
	 * Constant for the "Cherokee" Unicode character block.
	 */
	public static final CCUnicodeBlock CHEROKEE = new CCUnicodeBlock("CHEROKEE", 5024, 5119);

	/**
	 * Constant for the "Unified Canadian Aboriginal Syllabics" Unicode character block.
	 */
	public static final CCUnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new CCUnicodeBlock(
		"UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", 
		5120, 5759,
		new String[] {"Unified Canadian Aboriginal Syllabics", "UnifiedCanadianAboriginalSyllabics" }
	);

	/**
	 * Constant for the "Ogham" Unicode character block.
	 */
	public static final CCUnicodeBlock OGHAM = new CCUnicodeBlock("OGHAM", 5760, 5791);

	/**
	 * Constant for the "Runic" Unicode character block.
	 */
	public static final CCUnicodeBlock RUNIC = new CCUnicodeBlock("RUNIC", 5792, 5887);

	/**
	 * Constant for the "Tagalog" Unicode character block.
	 */
	public static final CCUnicodeBlock TAGALOG = new CCUnicodeBlock("TAGALOG", 5888, 5919);

	/**
	 * Constant for the "Hanunoo" Unicode character block.
	 */
	public static final CCUnicodeBlock HANUNOO = new CCUnicodeBlock("HANUNOO", 5920, 5951);

	/**
	 * Constant for the "Buhid" Unicode character block.
	 */
	public static final CCUnicodeBlock BUHID = new CCUnicodeBlock("BUHID", 5952, 5983);

	/**
	 * Constant for the "Tagbanwa" Unicode character block.
	 */
	public static final CCUnicodeBlock TAGBANWA = new CCUnicodeBlock("TAGBANWA", 5984, 6015);

	/**
	 * Constant for the "Khmer" Unicode character block.
	 */
	public static final CCUnicodeBlock KHMER = new CCUnicodeBlock("KHMER", 6016, 6143);

	/**
	 * Constant for the "Mongolian" Unicode character block.
	 */
	public static final CCUnicodeBlock MONGOLIAN = new CCUnicodeBlock("MONGOLIAN", 6144, 6319);

	/**
	 * Constant for the "Limbu" Unicode character block.
	 */
	public static final CCUnicodeBlock LIMBU = new CCUnicodeBlock("LIMBU", 6400, 6479);

	/**
	 * Constant for the "Tai Le" Unicode character block.
	 */
	public static final CCUnicodeBlock TAI_LE = new CCUnicodeBlock(
		"TAI_LE", 
		6480, 6527,
		new String[] { "Tai Le", "TaiLe" }
	);

	/**
	 * Constant for the "Khmer Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock KHMER_SYMBOLS = new CCUnicodeBlock(
		"KHMER_SYMBOLS", 
		6624, 6655,
		new String[] { "Khmer Symbols", "KhmerSymbols" }
	);

	/**
	 * Constant for the "Phonetic Extensions" Unicode character block.
	 */
	public static final CCUnicodeBlock PHONETIC_EXTENSIONS = new CCUnicodeBlock(
		"PHONETIC_EXTENSIONS", 
		7424, 7551,
		new String[] { "Phonetic Extensions", "PhoneticExtensions" }
	);

	/**
	 * Constant for the "Latin Extended Additional" Unicode character block.
	 */
	public static final CCUnicodeBlock LATIN_EXTENDED_ADDITIONAL = new CCUnicodeBlock(
		"LATIN_EXTENDED_ADDITIONAL", 
		7680, 7935,
		new String[] { "Latin Extended Additional", "LatinExtendedAdditional" }
	);

	/**
	 * Constant for the "Greek Extended" Unicode character block.
	 */
	public static final CCUnicodeBlock GREEK_EXTENDED = new CCUnicodeBlock(
		"GREEK_EXTENDED", 
		7936, 8191, 
		new String[] { "Greek Extended", "GreekExtended" }
	);

	/**
	 * @shortdesc Constant for the "General Punctuation" Unicode character block.
	 * Position Variations Some characters in this block (?) are positioned 
	 * in the area x80 to x9F in the widespread Windows-codepage 1252. In Unicode, though, 
	 * they are encoded in a different area, since the area U+0000 to U+00FF is kept 
	 * conform with ISO-8859-1.
	 * Like the characters ? and ? (U+2122 and U+20AC, which was added to CP1252 only later on) 
	 * all these characters need not just two bytes (like umlauts do) but three bytes to be 
	 * encoded in UTF-8.
	 */
	public static final CCUnicodeBlock GENERAL_PUNCTUATION = new CCUnicodeBlock(
		"GENERAL_PUNCTUATION", 
		8192, 8303,
		new String[] { "General Punctuation", "GeneralPunctuation" }
	);

	/**
	 * @shortdesc Constant for the "Superscripts and Subscripts" Unicode character block.
	 * Superscripts are numbers or letters of a smaller size set above the normal line of type.
	 * Subscripts are also smaller in size and set at or below the baseline.In a thoroughly 
	 * designed font, typeface designers provide these characters with individual shapes 
	 * since numbers simply reduced in size will not display the same grey values as the 
	 * basic fonts will.
	 */
	public static final CCUnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = new CCUnicodeBlock(
		"SUPERSCRIPTS_AND_SUBSCRIPTS", 
		8304, 8351,
		new String[] { "Superscripts and Subscripts","SuperscriptsandSubscripts" });

	/**
	 * Constant for the "Currency Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock CURRENCY_SYMBOLS = new CCUnicodeBlock(
		"CURRENCY_SYMBOLS", 
		8352, 8399, 
		new String[] { "Currency Symbols", "CurrencySymbols" }
	);

	/**
	 * Constant for the "Combining Diacritical Marks for Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock COMBINING_MARKS_FOR_SYMBOLS = new CCUnicodeBlock(
		"COMBINING_MARKS_FOR_SYMBOLS", 
		8400, 8447,
		new String[] { "Combining Diacritical Marks for Symbols", "CombiningDiacriticalMarksforSymbols", "Combining Marks for Symbols", "CombiningMarksforSymbols" });

	/**
	 * Constant for the "Letterlike Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock LETTERLIKE_SYMBOLS = new CCUnicodeBlock(
		"LETTERLIKE_SYMBOLS", 
		8448, 8527,
		new String[] { "Letterlike Symbols", "LetterlikeSymbols" }
	);

	/**
	 * Constant for the "Number Forms" Unicode character block.
	 */
	public static final CCUnicodeBlock NUMBER_FORMS = new CCUnicodeBlock(
		"NUMBER_FORMS", 
		8528, 8591,
		new String[] { "Number Forms", "NumberForms" }
	);

	/**
	 * Constant for the "Arrows" Unicode character block.
	 */
	public static final CCUnicodeBlock ARROWS = new CCUnicodeBlock("ARROWS", 8592, 8703);

	/**
	 * Constant for the "Mathematical Operators" Unicode character block.
	 */
	public static final CCUnicodeBlock MATHEMATICAL_OPERATORS = new CCUnicodeBlock(
		"MATHEMATICAL_OPERATORS", 
		8704, 8959,
		new String[] { "Mathematical Operators", "MathematicalOperators" }
	);

	/**
	 * Constant for the "Miscellaneous Technical" Unicode character block.
	 */
	public static final CCUnicodeBlock MISCELLANEOUS_TECHNICAL = new CCUnicodeBlock(
		"MISCELLANEOUS_TECHNICAL", 
		8960, 9215,
		new String[] { "Miscellaneous Technical", "MiscellaneousTechnical" }
	);

	/**
	 * Constant for the "Control Pictures" Unicode character block.
	 */
	public static final CCUnicodeBlock CONTROL_PICTURES = new CCUnicodeBlock(
		"CONTROL_PICTURES", 
		9216, 9279,
		new String[] { "Control Pictures", "ControlPictures" }
	);

	/**
	 * Constant for the "Optical Character Recognition" Unicode character block.
	 */
	public static final CCUnicodeBlock OPTICAL_CHARACTER_RECOGNITION = new CCUnicodeBlock(
		"OPTICAL_CHARACTER_RECOGNITION", 
		9280, 9311,
		new String[] { "Optical Character Recognition", "OpticalCharacterRecognition" });

	/**
	 * Constant for the "Enclosed Alphanumerics" Unicode character block.
	 */
	public static final CCUnicodeBlock ENCLOSED_ALPHANUMERICS = new CCUnicodeBlock(
		"ENCLOSED_ALPHANUMERICS", 
		9312, 9471,
		new String[] { "Enclosed Alphanumerics", "EnclosedAlphanumerics" }
	);

	/**
	 * Constant for the "Box Drawing" Unicode character block.
	 */
	public static final CCUnicodeBlock BOX_DRAWING = new CCUnicodeBlock(
		"BOX_DRAWING", 
		9472, 9599,
		new String[] { "Box Drawing", "BoxDrawing" }
	);

	/**
	 * Constant for the "Block Elements" Unicode character block.
	 */
	public static final CCUnicodeBlock BLOCK_ELEMENTS = new CCUnicodeBlock(
		"BLOCK_ELEMENTS", 
		9600, 9631,
		new String[] { "Block Elements", "BlockElements" }
	);

	/**
	 * Constant for the "Geometric Shapes" Unicode character block.
	 */
	public static final CCUnicodeBlock GEOMETRIC_SHAPES = new CCUnicodeBlock(
		"GEOMETRIC_SHAPES", 
		9632, 9727,
		new String[] { "Geometric Shapes", "GeometricShapes" }
	);

	/**
	 * Constant for the "Miscellaneous Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock MISCELLANEOUS_SYMBOLS = new CCUnicodeBlock(
		"MISCELLANEOUS_SYMBOLS", 
		9728, 9983,
		new String[] { "Miscellaneous Symbols", "MiscellaneousSymbols" }
	);

	/**
	 * Constant for the "Dingbats" Unicode character block.
	 */
	public static final CCUnicodeBlock DINGBATS = new CCUnicodeBlock("DINGBATS", 9984, 10175);

	/**
	 * Constant for the "Miscellaneous Mathematical Symbols-A" Unicode character block.
	 */
	public static final CCUnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new CCUnicodeBlock(
		"MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", 
		10176, 10223,
		new String[] {"Miscellaneous Mathematical Symbols-A", "MiscellaneousMathematicalSymbols-A" }
	);

	/**
	 * Constant for the "Supplemental Arrows-A" Unicode character block.
	 */
	public static final CCUnicodeBlock SUPPLEMENTAL_ARROWS_A = new CCUnicodeBlock(
		"SUPPLEMENTAL_ARROWS_A", 
		10224, 10239,
		new String[] { "Supplemental Arrows-A", "SupplementalArrows-A" }
	);

	/**
	 * Constant for the "Braille Patterns" Unicode character block.
	 */
	public static final CCUnicodeBlock BRAILLE_PATTERNS = new CCUnicodeBlock(
		"BRAILLE_PATTERNS", 
		10240, 10495,
		new String[] { "Braille Patterns", "BraillePatterns" }
	);

	/**
	 * Constant for the "Supplemental Arrows-B" Unicode character block.
	 */
	public static final CCUnicodeBlock SUPPLEMENTAL_ARROWS_B = new CCUnicodeBlock(
		"SUPPLEMENTAL_ARROWS_B", 
		10496, 10623,
		new String[] { "Supplemental Arrows-B", "SupplementalArrows-B" }
	);

	/**
	 * Constant for the "Miscellaneous Mathematical Symbols-B" Unicode character block.
	 */
	public static final CCUnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new CCUnicodeBlock(
		"MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B",
		10624, 10751,
		new String[] {"Miscellaneous Mathematical Symbols-B", "MiscellaneousMathematicalSymbols-B" }
	);

	/**
	 * Constant for the "Supplemental Mathematical Operators" Unicode character block.
	 */
	public static final CCUnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new CCUnicodeBlock(
		"SUPPLEMENTAL_MATHEMATICAL_OPERATORS", 
		10752, 11007,
		new String[] {"Supplemental Mathematical Operators", "SupplementalMathematicalOperators" }
	);

	/**
	 * Constant for the "Miscellaneous Symbols and Arrows" Unicode character block.
	 */
	public static final CCUnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = new CCUnicodeBlock(
		"MISCELLANEOUS_SYMBOLS_AND_ARROWS", 
		11008, 11263,
		new String[] { "Miscellaneous Symbols and Arrows", "MiscellaneousSymbolsandArrows" }
	);

	/**
	 * Constant for the "CJK Radicals Supplement" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_RADICALS_SUPPLEMENT = new CCUnicodeBlock(
		"CJK_RADICALS_SUPPLEMENT", 
		11904, 12031,
		new String[] { "CJK Radicals Supplement", "CJKRadicalsSupplement" }
	);

	/**
	 * Constant for the "Kangxi Radicals" Unicode character block.
	 */
	public static final CCUnicodeBlock KANGXI_RADICALS = new CCUnicodeBlock(
		"KANGXI_RADICALS", 
		12032, 12255,
		new String[] { "Kangxi Radicals", "KangxiRadicals" }
	);

	/**
	 * Constant for the "Ideographic Description Characters" Unicode character block.
	 */
	public static final CCUnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new CCUnicodeBlock(
		"IDEOGRAPHIC_DESCRIPTION_CHARACTERS", 
		12272, 12287,
		new String[] {"Ideographic Description Characters", "IdeographicDescriptionCharacters" }
	);

	/**
	 * Constant for the "CJK Symbols and Punctuation" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = new CCUnicodeBlock(
		"CJK_SYMBOLS_AND_PUNCTUATION", 
		12288, 12351,
		new String[] { "CJK Symbols and Punctuation", "CJKSymbolsandPunctuation" }
	);

	/**
	 * Constant for the "Hiragana" Unicode character block.
	 */
	public static final CCUnicodeBlock HIRAGANA = new CCUnicodeBlock("HIRAGANA",12352,12447);

	/**
	 * Constant for the "Katakana" Unicode character block.
	 */
	public static final CCUnicodeBlock KATAKANA = new CCUnicodeBlock("KATAKANA", 12448, 12543);

	/**
	 * Constant for the "Bopomofo" Unicode character block.
	 */
	public static final CCUnicodeBlock BOPOMOFO = new CCUnicodeBlock("BOPOMOFO", 12544, 12591);

	/**
	 * Constant for the "Hangul Compatibility Jamo" Unicode character block.
	 */
	public static final CCUnicodeBlock HANGUL_COMPATIBILITY_JAMO = new CCUnicodeBlock(
		"HANGUL_COMPATIBILITY_JAMO", 
		12592, 12687,
		new String[] { "Hangul Compatibility Jamo", "HangulCompatibilityJamo" }
	);

	/**
	 * Constant for the "Kanbun" Unicode character block.
	 */
	public static final CCUnicodeBlock KANBUN = new CCUnicodeBlock("KANBUN", 12688, 12703);

	/**
	 * Constant for the "Bopomofo Extended" Unicode character block.
	 */
	public static final CCUnicodeBlock BOPOMOFO_EXTENDED = new CCUnicodeBlock(
		"BOPOMOFO_EXTENDED", 
		12704, 12735,
		new String[] { "Bopomofo Extended", "BopomofoExtended" }
	);

	/**
	 * Constant for the "Katakana Phonetic Extensions" Unicode character block.
	 */
	public static final CCUnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = new CCUnicodeBlock(
		"KATAKANA_PHONETIC_EXTENSIONS", 
		12784, 12799,
		new String[] { "Katakana Phonetic Extensions", "KatakanaPhoneticExtensions" }
	);

	/**
	 * Constant for the "Enclosed CJK Letters and Months" Unicode character block.
	 */
	public static final CCUnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = new CCUnicodeBlock(
		"ENCLOSED_CJK_LETTERS_AND_MONTHS", 
		12800, 13055,
		new String[] { "Enclosed CJK Letters and Months", "EnclosedCJKLettersandMonths" }
	);

	/**
	 * Constant for the "CJK Compatibility" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_COMPATIBILITY = new CCUnicodeBlock(
		"CJK_COMPATIBILITY", 
		13056, 13311,
		new String[] { "CJK Compatibility", "CJKCompatibility" }
	);

	/**
	 * Constant for the "CJK Unified Ideographs Extension A" Unicode character block.
	 * 
	 * @since 1.4
	 */
	public static final CCUnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new CCUnicodeBlock(
		"CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", 
		13312, 19903,
		new String[] {"CJK Unified Ideographs Extension A", "CJKUnifiedIdeographsExtensionA" });

	/**
	 * Constant for the "Yijing Hexagram Symbols" Unicode character block.
	 */
	public static final CCUnicodeBlock YIJING_HEXAGRAM_SYMBOLS = new CCUnicodeBlock(
		"YIJING_HEXAGRAM_SYMBOLS", 
		19904, 19967,
		new String[] { "Yijing Hexagram Symbols", "YijingHexagramSymbols" }
	);

	/**
	 * Constant for the "CJK Unified Ideographs" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_UNIFIED_IDEOGRAPHS = new CCUnicodeBlock(
		"CJK_UNIFIED_IDEOGRAPHS", 
		19968, 40959,
		new String[] { "CJK Unified Ideographs", "CJKUnifiedIdeographs" }
	);

	/**
	 * Constant for the "Yi Syllables" Unicode character block.
	 */
	public static final CCUnicodeBlock YI_SYLLABLES = new CCUnicodeBlock(
		"YI_SYLLABLES", 
		40960, 42127,
		new String[] { "Yi Syllables", "YiSyllables" }
	);

	/**
	 * Constant for the "Yi Radicals" Unicode character block.
	 */
	public static final CCUnicodeBlock YI_RADICALS = new CCUnicodeBlock(
		"YI_RADICALS",
		42128, 42191,
		new String[] { "Yi Radicals", "YiRadicals" }
	);

	/**
	 * Constant for the "Hangul Syllables" Unicode character block.
	 */
	public static final CCUnicodeBlock HANGUL_SYLLABLES = new CCUnicodeBlock(
		"HANGUL_SYLLABLES", 
		44032, 55215,
		new String[] { "Hangul Syllables", "HangulSyllables" }
	);

	/**
	 * Constant for the "High Surrogates" Unicode character block. This block represents codepoint values in the high
	 * surrogate range: 0xD800 through 0xDB7F
	 */
	public static final CCUnicodeBlock HIGH_SURROGATES = new CCUnicodeBlock(
		"HIGH_SURROGATES", 
		55296, 56191,
		new String[] { "High Surrogates", "HighSurrogates" }
	);

	/**
	 * Constant for the "High Private Use Surrogates" Unicode character block. This block represents codepoint values in
	 * the high surrogate range: 0xDB80 through 0xDBFF
	 */
	public static final CCUnicodeBlock HIGH_PRIVATE_USE_SURROGATES = new CCUnicodeBlock(
		"HIGH_PRIVATE_USE_SURROGATES", 
		56192, 56319,
		new String[] { "High Private Use Surrogates", "HighPrivateUseSurrogates" }
	);

	/**
	 * Constant for the "Low Surrogates" Unicode character block. This block represents codepoint values in the high
	 * surrogate range: 0xDC00 through 0xDFFF
	 */
	public static final CCUnicodeBlock LOW_SURROGATES = new CCUnicodeBlock(
		"LOW_SURROGATES", 
		56320, 57343,
		new String[] { "Low Surrogates", "LowSurrogates" }
	);
	
	/**
	 * Constant for the "Private Use Area" Unicode character block.
	 */
	public static final CCUnicodeBlock PRIVATE_USE_AREA = new CCUnicodeBlock(
		"PRIVATE_USE_AREA", 
		57344, 63743,
		new String[] { "Private Use Area", "PrivateUseArea" }
	);

	/**
	 * Constant for the "CJK Compatibility Ideographs" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = new CCUnicodeBlock(
		"CJK_COMPATIBILITY_IDEOGRAPHS", 
		63744, 64255,
		new String[] { "CJK Compatibility Ideographs", "CJKCompatibilityIdeographs" }
	);

	/**
	 * Constant for the "Alphabetic Presentation Forms" Unicode character block.
	 */
	public static final CCUnicodeBlock ALPHABETIC_PRESENTATION_FORMS = new CCUnicodeBlock(
		"ALPHABETIC_PRESENTATION_FORMS", 
		64256, 64335,
		new String[] { "Alphabetic Presentation Forms", "AlphabeticPresentationForms" });

	/**
	 * Constant for the "Arabic Presentation Forms-A" Unicode character block.
	 */
	public static final CCUnicodeBlock ARABIC_PRESENTATION_FORMS_A = new CCUnicodeBlock(
		"ARABIC_PRESENTATION_FORMS_A", 
		64336, 65023,
		new String[] { "Arabic Presentation Forms-A", "ArabicPresentationForms-A" }
	);

	/**
	 * Constant for the "Variation Selectors" Unicode character block.
	 */
	public static final CCUnicodeBlock VARIATION_SELECTORS = new CCUnicodeBlock(
		"VARIATION_SELECTORS", 
		65024, 65039,
		new String[] { "Variation Selectors", "VariationSelectors" }
	);

	/**
	 * Constant for the "Combining Half Marks" Unicode character block.
	 */
	public static final CCUnicodeBlock COMBINING_HALF_MARKS = new CCUnicodeBlock(
		"COMBINING_HALF_MARKS",
		65056, 65071,
		new String[] { "Combining Half Marks", "CombiningHalfMarks" });

	/**
	 * Constant for the "CJK Compatibility Forms" Unicode character block.
	 */
	public static final CCUnicodeBlock CJK_COMPATIBILITY_FORMS = new CCUnicodeBlock(
		"CJK_COMPATIBILITY_FORMS", 
		65072, 65103,
		new String[] { "CJK Compatibility Forms", "CJKCompatibilityForms" }
	);

	/**
	 * Constant for the "Small Form Variants" Unicode character block.
	 */
	public static final CCUnicodeBlock SMALL_FORM_VARIANTS = new CCUnicodeBlock(
		"SMALL_FORM_VARIANTS", 
		65104, 65135,
		new String[] { "Small Form Variants", "SmallFormVariants" }
	);

	/**
	 * Constant for the "Arabic Presentation Forms-B" Unicode character block.
	 */
	public static final CCUnicodeBlock ARABIC_PRESENTATION_FORMS_B = new CCUnicodeBlock(
		"ARABIC_PRESENTATION_FORMS_B", 
		65136, 65279,
		new String[] { "Arabic Presentation Forms-B", "ArabicPresentationForms-B" }
	);

	/**
	 * Constant for the "Halfwidth and Fullwidth Forms" Unicode character block.
	 */
	public static final CCUnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = new CCUnicodeBlock(
		"HALFWIDTH_AND_FULLWIDTH_FORMS", 
		65280, 65519,
		new String[] { "Halfwidth and Fullwidth Forms", "HalfwidthandFullwidthForms" }
	);

	/**
	 * Constant for the "Specials" Unicode character block.
	 * 
	 * @since 1.2
	 */
	public static final CCUnicodeBlock SPECIALS = new CCUnicodeBlock("SPECIALS",65520, 65535);

	
	private static final int blockStarts[] = { 0x0000, // Basic Latin
			0x0080, // Latin-1 Supplement
			0x0100, // Latin Extended-A
			0x0180, // Latin Extended-B
			0x0250, // IPA Extensions
			0x02B0, // Spacing Modifier Letters
			0x0300, // Combining Diacritical Marks
			0x0370, // Greek and Coptic
			0x0400, // Cyrillic
			0x0500, // Cyrillic Supplementary
			0x0530, // Armenian
			0x0590, // Hebrew
			0x0600, // Arabic
			0x0700, // Syriac
			0x0750, // unassigned
			0x0780, // Thaana
			0x07C0, // unassigned
			0x0900, // Devanagari
			0x0980, // Bengali
			0x0A00, // Gurmukhi
			0x0A80, // Gujarati
			0x0B00, // Oriya
			0x0B80, // Tamil
			0x0C00, // Telugu
			0x0C80, // Kannada
			0x0D00, // Malayalam
			0x0D80, // Sinhala
			0x0E00, // Thai
			0x0E80, // Lao
			0x0F00, // Tibetan
			0x1000, // Myanmar
			0x10A0, // Georgian
			0x1100, // Hangul Jamo
			0x1200, // Ethiopic
			0x1380, // unassigned
			0x13A0, // Cherokee
			0x1400, // Unified Canadian Aboriginal Syllabics
			0x1680, // Ogham
			0x16A0, // Runic
			0x1700, // Tagalog
			0x1720, // Hanunoo
			0x1740, // Buhid
			0x1760, // Tagbanwa
			0x1780, // Khmer
			0x1800, // Mongolian
			0x18B0, // unassigned
			0x1900, // Limbu
			0x1950, // Tai Le
			0x1980, // unassigned
			0x19E0, // Khmer Symbols
			0x1A00, // unassigned
			0x1D00, // Phonetic Extensions
			0x1D80, // unassigned
			0x1E00, // Latin Extended Additional
			0x1F00, // Greek Extended
			0x2000, // General Punctuation
			0x2070, // Superscripts and Subscripts
			0x20A0, // Currency Symbols
			0x20D0, // Combining Diacritical Marks for Symbols
			0x2100, // Letterlike Symbols
			0x2150, // Number Forms
			0x2190, // Arrows
			0x2200, // Mathematical Operators
			0x2300, // Miscellaneous Technical
			0x2400, // Control Pictures
			0x2440, // Optical Character Recognition
			0x2460, // Enclosed Alphanumerics
			0x2500, // Box Drawing
			0x2580, // Block Elements
			0x25A0, // Geometric Shapes
			0x2600, // Miscellaneous Symbols
			0x2700, // Dingbats
			0x27C0, // Miscellaneous Mathematical Symbols-A
			0x27F0, // Supplemental Arrows-A
			0x2800, // Braille Patterns
			0x2900, // Supplemental Arrows-B
			0x2980, // Miscellaneous Mathematical Symbols-B
			0x2A00, // Supplemental Mathematical Operators
			0x2B00, // Miscellaneous Symbols and Arrows
			0x2C00, // unassigned
			0x2E80, // CJK Radicals Supplement
			0x2F00, // Kangxi Radicals
			0x2FE0, // unassigned
			0x2FF0, // Ideographic Description Characters
			0x3000, // CJK Symbols and Punctuation
			0x3040, // Hiragana
			0x30A0, // Katakana
			0x3100, // Bopomofo
			0x3130, // Hangul Compatibility Jamo
			0x3190, // Kanbun
			0x31A0, // Bopomofo Extended
			0x31C0, // unassigned
			0x31F0, // Katakana Phonetic Extensions
			0x3200, // Enclosed CJK Letters and Months
			0x3300, // CJK Compatibility
			0x3400, // CJK Unified Ideographs Extension A
			0x4DC0, // Yijing Hexagram Symbols
			0x4E00, // CJK Unified Ideographs
			0xA000, // Yi Syllables
			0xA490, // Yi Radicals
			0xA4D0, // unassigned
			0xAC00, // Hangul Syllables
			0xD7B0, // unassigned
			0xD800, // High Surrogates
			0xDB80, // High Private Use Surrogates
			0xDC00, // Low Surrogates
			0xE000, // Private Use
			0xF900, // CJK Compatibility Ideographs
			0xFB00, // Alphabetic Presentation Forms
			0xFB50, // Arabic Presentation Forms-A
			0xFE00, // Variation Selectors
			0xFE10, // unassigned
			0xFE20, // Combining Half Marks
			0xFE30, // CJK Compatibility Forms
			0xFE50, // Small Form Variants
			0xFE70, // Arabic Presentation Forms-B
			0xFF00, // Halfwidth and Fullwidth Forms
			0xFFF0, // Specials
			0x10000, // Linear B Syllabary
			0x10080, // Linear B Ideograms
			0x10100, // Aegean Numbers
			0x10140, // unassigned
			0x10300, // Old Italic
			0x10330, // Gothic
			0x10350, // unassigned
			0x10380, // Ugaritic
			0x103A0, // unassigned
			0x10400, // Deseret
			0x10450, // Shavian
			0x10480, // Osmanya
			0x104B0, // unassigned
			0x10800, // Cypriot Syllabary
			0x10840, // unassigned
			0x1D000, // Byzantine Musical Symbols
			0x1D100, // Musical Symbols
			0x1D200, // unassigned
			0x1D300, // Tai Xuan Jing Symbols
			0x1D360, // unassigned
			0x1D400, // Mathematical Alphanumeric Symbols
			0x1D800, // unassigned
			0x20000, // CJK Unified Ideographs Extension B
			0x2A6E0, // unassigned
			0x2F800, // CJK Compatibility Ideographs Supplement
			0x2FA20, // unassigned
			0xE0000, // Tags
			0xE0080, // unassigned
			0xE0100, // Variation Selectors Supplement
			0xE01F0, // unassigned
			0xF0000, // Supplementary Private Use Area-A
			0x100000, // Supplementary Private Use Area-B
	};

	private static final CCUnicodeBlock[] blocks = { 
		BASIC_LATIN, 
		LATIN_1_SUPPLEMENT, 
		LATIN_EXTENDED_A, 
		LATIN_EXTENDED_B, 
		IPA_EXTENSIONS, 
		SPACING_MODIFIER_LETTERS,
		COMBINING_DIACRITICAL_MARKS, 
		GREEK, 
		CYRILLIC, 
		CYRILLIC_SUPPLEMENTARY, 
		ARMENIAN, 
		HEBREW, 
		ARABIC, 
		SYRIAC, 
		null, 
		THAANA, 
		null, 
		DEVANAGARI, 
		BENGALI, 
		GURMUKHI, 
		GUJARATI,
		ORIYA, 
		TAMIL, 
		TELUGU, 
		KANNADA, 
		MALAYALAM, 
		SINHALA, 
		THAI, 
		LAO, 
		TIBETAN, 
		MYANMAR, 
		GEORGIAN, 
		HANGUL_JAMO, 
		ETHIOPIC, 
		null, 
		CHEROKEE, 
		UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
		OGHAM, 
		RUNIC, 
		TAGALOG, 
		HANUNOO, 
		BUHID, 
		TAGBANWA, 
		KHMER, 
		MONGOLIAN, 
		null, 
		LIMBU, 
		TAI_LE, 
		null, 
		KHMER_SYMBOLS, 
		null, 
		PHONETIC_EXTENSIONS, 
		null,
		LATIN_EXTENDED_ADDITIONAL, 
		GREEK_EXTENDED, 
		GENERAL_PUNCTUATION, 
		SUPERSCRIPTS_AND_SUBSCRIPTS, 
		CURRENCY_SYMBOLS, 
		COMBINING_MARKS_FOR_SYMBOLS, 
		LETTERLIKE_SYMBOLS,
		NUMBER_FORMS, 
		ARROWS, 
		MATHEMATICAL_OPERATORS, 
		MISCELLANEOUS_TECHNICAL, 
		CONTROL_PICTURES, 
		OPTICAL_CHARACTER_RECOGNITION, 
		ENCLOSED_ALPHANUMERICS, 
		BOX_DRAWING,
		BLOCK_ELEMENTS, 
		GEOMETRIC_SHAPES, 
		MISCELLANEOUS_SYMBOLS, 
		DINGBATS, 
		MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A, 
		SUPPLEMENTAL_ARROWS_A, 
		BRAILLE_PATTERNS,
		SUPPLEMENTAL_ARROWS_B, 
		MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B, 
		SUPPLEMENTAL_MATHEMATICAL_OPERATORS, 
		MISCELLANEOUS_SYMBOLS_AND_ARROWS, 
		null, 
		CJK_RADICALS_SUPPLEMENT,
		KANGXI_RADICALS, 
		null, 
		IDEOGRAPHIC_DESCRIPTION_CHARACTERS, 
		CJK_SYMBOLS_AND_PUNCTUATION, 
		HIRAGANA, 
		KATAKANA, 
		BOPOMOFO, 
		HANGUL_COMPATIBILITY_JAMO, 
		KANBUN,
		BOPOMOFO_EXTENDED, 
		null, 
		KATAKANA_PHONETIC_EXTENSIONS, 
		ENCLOSED_CJK_LETTERS_AND_MONTHS, 
		CJK_COMPATIBILITY, 
		CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, 
		YIJING_HEXAGRAM_SYMBOLS,
		CJK_UNIFIED_IDEOGRAPHS, 
		YI_SYLLABLES, YI_RADICALS, 
		null, 
		HANGUL_SYLLABLES, 
		null, 
		HIGH_SURROGATES, 
		HIGH_PRIVATE_USE_SURROGATES, 
		LOW_SURROGATES, 
		PRIVATE_USE_AREA,
		CJK_COMPATIBILITY_IDEOGRAPHS, 
		ALPHABETIC_PRESENTATION_FORMS, 
		ARABIC_PRESENTATION_FORMS_A, 
		VARIATION_SELECTORS, 
		null, 
		COMBINING_HALF_MARKS, 
		CJK_COMPATIBILITY_FORMS,
		SMALL_FORM_VARIANTS, 
		ARABIC_PRESENTATION_FORMS_B, 
		HALFWIDTH_AND_FULLWIDTH_FORMS, 
		SPECIALS
	};

	/**
	 * Returns the object representing the Unicode block containing the given character, or <code>null</code> if the
	 * character is not a member of a defined block.
	 * 
	 * <p>
	 * <b>Note:</b> This method cannot handle <a href="Character.html#supplementary"> supplementary characters</a>. To
	 * support all Unicode characters, including supplementary characters, use the {@link #of(int)} method.
	 * 
	 * @param c The character in question
	 * @return The <code>CCUnicodeBlock</code> instance representing the Unicode block of which this character is a
	 *         member, or <code>null</code> if the character is not a member of any Unicode block
	 */
	public static CCUnicodeBlock of(char c) {
		return of((int) c);
	}

	/**
	 * Returns the object representing the Unicode block containing the given character (Unicode code point), or
	 * <code>null</code> if the character is not a member of a defined block.
	 * 
	 * @param codePoint the character (Unicode code point) in question.
	 * @return The <code>CCUnicodeBlock</code> instance representing the Unicode block of which this character is a
	 *         member, or <code>null</code> if the character is not a member of any Unicode block
	 * @exception IllegalArgumentException if the specified <code>codePoint</code> is an invalid Unicode code point.
	 * @see Character#isValidCodePoint(int)
	 * @since 1.5
	 */
	public static CCUnicodeBlock of(int codePoint) {
		if (!Character.isValidCodePoint(codePoint)) {
			throw new IllegalArgumentException();
		}

		int top, bottom, current;
		bottom = 0;
		top = blockStarts.length;
		current = top / 2;

		// invariant: top > current >= bottom && codePoint >= unicodeBlockStarts[bottom]
		while (top - bottom > 1) {
			if (codePoint >= blockStarts[current]) {
				bottom = current;
			} else {
				top = current;
			}
			current = (top + bottom) / 2;
		}
		return blocks[current];
	}

	/**
	 * Returns the CCUnicodeBlock with the given name. Block names are determined by The Unicode Standard. The file
	 * Blocks-&lt;version&gt;.txt defines blocks for a particular version of the standard. The {@link Character} class
	 * specifies the version of the standard that it supports.
	 * <p>
	 * This method accepts block names in the following forms:
	 * <ol>
	 * <li>Canonical block names as defined by the Unicode Standard. For example, the standard defines a "Basic Latin"
	 * block. Therefore, this method accepts "Basic Latin" as a valid block name. The documentation of each
	 * CCUnicodeBlock provides the canonical name.
	 * <li>Canonical block names with all spaces removed. For example, "BasicLatin" is a valid block name for the
	 * "Basic Latin" block.
	 * <li>The text representation of each constant CCUnicodeBlock identifier. For example, this method will return the
	 * {@link #BASIC_LATIN} block if provided with the "BASIC_LATIN" name. This form replaces all spaces and hyphens in
	 * the canonical name with underscores.
	 * </ol>
	 * Finally, character case is ignored for all of the valid block name forms. For example, "BASIC_LATIN" and
	 * "basic_latin" are both valid block names. The en_US locale's case mapping rules are used to provide
	 * case-insensitive string comparisons for block name validation.
	 * <p>
	 * If the Unicode Standard changes block names, both the previous and current names will be accepted.
	 * 
	 * @param blockName A <code>CCUnicodeBlock</code> name.
	 * @return The <code>CCUnicodeBlock</code> instance identified by <code>blockName</code>
	 * @throws IllegalArgumentException if <code>blockName</code> is an invalid name
	 * @throws NullPointerException if <code>blockName</code> is null
	 * @since 1.5
	 */
	public static final CCUnicodeBlock forName(String blockName) {
		CCUnicodeBlock block = (CCUnicodeBlock) map.get(blockName.toUpperCase(Locale.US));
		if (block == null) {
			throw new IllegalArgumentException();
		}
		return block;
	}

}
