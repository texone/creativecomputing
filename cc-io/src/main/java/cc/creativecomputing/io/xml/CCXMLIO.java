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
package cc.creativecomputing.io.xml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;



/**
 * <p>
 * Use CCXMLIO for simple loading and saving of XML files. There are different ways
 * to load XML files. The simplest way is to use createXMLElement. This is a static
 * function that parses the XML into a DOM structure. For more advanced loading you
 * have to create an instance of CCXMLIO and use its loadElement method.
 * </p>
 * 
 * @see CCXMLElement
 */
public class CCXMLIO{

	/**
	 * Loader for loading XML in background while running the sketch.
	 * @author tex
	 *
	 */
	public static class CCXMLParser<CCXMLTool extends CCAbstractXMLTool> implements Runnable{

		/**
		 * String to keep the String of the document to parse
		 */
		private final Reader _myDocument;

		/**
		 * String to keep the String of the document to parse
		 */
		private final Reader _myDocumentCopy;
		
		/**
		 * the tool used to analyse the parsed xml structure
		 */
		private CCAbstractXMLTool _myTool;
		
		/**
		 * 
		 */
		private boolean _myIgnoreLineFeed;
		
		/**
		 * Object handling the incoming XML
		 */
		private final CCIXMLFeedbackHandler<CCXMLTool> _myFeedbackHandler;

		CCXMLParser(
			final Reader theDocument, 
			final CCAbstractXMLTool theTool,
			final CCIXMLFeedbackHandler<CCXMLTool> theFeedBackHandler,
			final boolean theIgnoreLineFeed
		){
			_myDocument = theDocument;
			_myDocumentCopy = theDocument;
			_myTool = theTool;
			_myTool.setParser(this);
			_myTool.initBeforeParsing();
			_myFeedbackHandler = theFeedBackHandler;
			_myIgnoreLineFeed = theIgnoreLineFeed;
		}

		/**
		 * Returns the source of the desired document
		 * @return
		 */
		String getSource(){
			int iChar;
			StringBuffer result = new StringBuffer();
			try{
				while ((iChar = _myDocumentCopy.read()) != -1){
					result.append((char) iChar);
				}
			}catch (Exception e){
				return ("fails");
			}
			return result.toString();
		}
		
		public void setTool(CCAbstractXMLTool theTool){
			_myTool = theTool;
			_myTool.setParser(this);
		}

		protected int line = 0;

		/**
		 * Parses a given String and gives back box with the parsed Element and the
		 * String still have to be parsed.
		 * @param toParse String
		 * @return BoxToParseElement
		 */
		private void parseDocument(Reader document){

			int iChar; //keeps the int value of the current char
			char cChar; //keeps the char value of the current char

			StringBuffer sbText = new StringBuffer(); //StringBuffer to parse words in
			StringBuffer mySpaceBuffer = new StringBuffer();
			int countSpace = 0;
			boolean bText = false; //has a word been parsed
			boolean inText = false;
			int tagInText = 0;
			try{
				while ((iChar = document.read()) != -1){ //as long there is something to read
					cChar = (char) iChar; //get the current char value
					switch (cChar){ //check the char value
						case '\b':
							break;
						case '\n':
							if(!_myIgnoreLineFeed && bText)sbText.append(cChar);
							line++;
							break;
						case '\f':
							break;
						case '\r':
							break;
						case '\t':
							break;
						case '<': //this opens a tag so...
							if (bText){
								bText = false;
								if(sbText.charAt(0) == '\n')sbText.deleteCharAt(0);
								_myTool.handleText(sbText.toString());
								sbText = new StringBuffer();
							}
							if ((iChar = document.read()) != -1){ //check the next sign...
								cChar = (char) iChar; //get its char value..

								if (cChar == '/'){ //in this case we have an end tag
									document = handleEndTag(document); // and handle it
									inText = tagInText > 0;
									if(inText)tagInText--;
									break;
								}else if (cChar == '!'){ //this could be a comment, but we need a further test
									if ((iChar = document.read()) != -1){ //you should know this now
										cChar = (char) iChar; //also this one
										if (cChar == '-'){ //okay its a comment
											handleComment(document); //handle it
											break;
										}else if (cChar == '['){//seems to be CDATA Section
											handleCDATASection(document);
											break;
										}else if (cChar == 'D'){//seems to be Doctype Section
											handleDoctypeSection(document);
											break;
										}
									}
								}
							}

							handleStartTag(document, new StringBuffer().append(cChar));
							if(inText)tagInText++;

							break;
						default:
							if (cChar == ' ' && !inText){
								break;
							}
							countSpace++;
							bText = true;
							inText = true;
							if (cChar == '&'){
								handleEntity(document, sbText);
							}else{
								sbText.append(cChar);
							}
							
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		/**
		 * Parses a TemplateTag and extracts its Name and Attributes.
		 * @param page Reader
		 * @param alreadyParsed StringBuffer
		 * @return Reader
		 * @throws Exception
		 */
		private Reader handleStartTag(Reader page, StringBuffer alreadyParsed) throws Exception{
			int iChar;
			char cChar;

			boolean bTagName = true;
			boolean bSpaceBefore = false;
			boolean bLeftAttribute = false;

			StringBuffer sbTagName = alreadyParsed;
			StringBuffer _myAttributeNameBuffer = new StringBuffer();
			StringBuffer _myAttributeValueBuffer = new StringBuffer();
			StringBuffer sbActual = sbTagName;

			final Map<String,String> myAttributes = new LinkedHashMap<String,String>();
			boolean inValue = false;
			char oChar = ' ';

			while ((iChar = page.read()) != -1){
				cChar = (char) iChar;
				switch (cChar){
					case '\b':
						break;
					case '\f':
						break;
					case '\r':
						break;
					case '\n':
						line++;
					case '\t':
					case ' ':
						if (!bSpaceBefore){
							if (!inValue){
								if (bTagName){
									bTagName = false;
								}else{
									final String _myAttributeName = _myAttributeNameBuffer.toString();
									final String _myAttributeValue = _myAttributeValueBuffer.toString();
									_myTool.handleAttribute(_myAttributeName, _myAttributeValue);
									myAttributes.put(_myAttributeName, _myAttributeValue);

									_myAttributeNameBuffer = new StringBuffer();
									_myAttributeValueBuffer = new StringBuffer();
									bLeftAttribute = false;
								}
								sbActual = _myAttributeNameBuffer;
							}else{
								sbActual.append(cChar);
							}
						}
						bSpaceBefore = true;
						break;
					case '=':
						if (!inValue){
							sbActual = _myAttributeValueBuffer;
							bLeftAttribute = true;
						}else{
							sbActual.append(cChar);
						}
						break;
					case '"':
						inValue = !inValue;
						try{
							if (!inValue && sbActual.charAt(sbActual.length() - 1) == ' '){
								sbActual.deleteCharAt(sbActual.length() - 1);
							}
						}catch (java.lang.StringIndexOutOfBoundsException e){
						}
						bSpaceBefore = false;
						break;
					case '\'':
						break;
					case '/':
						if (inValue)
							sbActual.append(cChar);
						break;
					case '>':
						if (bLeftAttribute){
							final String _myAttributeName = _myAttributeNameBuffer.toString();
							final String _myAttributeValue = _myAttributeValueBuffer.toString();
							_myTool.handleAttribute(_myAttributeName, _myAttributeValue);
							myAttributes.put(_myAttributeName, _myAttributeValue);
						}
						String sTagName = sbTagName.toString();
						
						_myTool.handleStartTag(sTagName, myAttributes, oChar == '/');
						
						return page;

					default:
						bSpaceBefore = false;
						sbActual.append(cChar);
				}
				oChar = cChar;
			}

			throw new RuntimeException("Error in line:"+line);
		}

		/**
		 * Parses the end tags of a XML document
		 * 
		 * @param toParse Reader
		 * @return Reader
		 * @throws Exception
		 */
		private Reader handleEndTag(Reader toParse) throws Exception{
			int iChar;
			char cChar;
			StringBuilder buffer = new StringBuilder();
			while ((iChar = toParse.read()) != -1){

				cChar = (char) iChar;
				switch (cChar){
					case '\b':
						break;
					case '\n':
						line++;
						break;
					case '\f':
						break;
					case '\r':
						break;
					case '\t':
						break;
					case '>':
						_myTool.doAfterEndTag(buffer.toString());
						return toParse;
					default:
						buffer.append(cChar);
				}
			}
			throw new RuntimeException("Error in line:"+line);
		}

		/**
		 * Parses the comments of a XML document
		 * 
		 * @param toParse Reader
		 * @return Reader
		 * @throws Exception
		 */
		private Reader handleComment(Reader toParse) throws Exception{
			int iChar;
			char cChar;
			char prevChar = ' ';

			while ((iChar = toParse.read()) != -1){
				cChar = (char) iChar;
				if (prevChar == '-' && cChar == '>'){
					return toParse;
				}
				prevChar = cChar;
			}
			throw new RuntimeException("Comment is not correctly closed in Line:"+line);
		}
		
		/**
		 * Parses the Doctype section of a XML document
		 * 
		 * @param toParse Reader
		 * @return Reader
		 * @throws Exception
		 */
		private Reader handleDoctypeSection(Reader toParse) throws Exception{
			int iChar;
			char cChar;
			char prevChar = ' ';
			
			boolean entities = false;

			while ((iChar = toParse.read()) != -1){
				cChar = (char) iChar;
				if(cChar == '[')entities = true;
				if (cChar == '>'){
					if(prevChar == ']' && entities || !entities)
					return toParse;
				}
				prevChar = cChar;
			}
			throw new RuntimeException("Comment is not correctly closed in Line:"+line);
		}

		/**
		 * Parses Entities of a document
		 * 
		 * @param toParse
		 * @param stringBuffer
		 * @return
		 * @throws Exception
		 */
		private Reader handleEntity(Reader toParse, final StringBuffer stringBuffer) throws Exception{
			int iChar;
			char cChar;
			final StringBuffer result = new StringBuffer();
			int counter = 0;

			while ((iChar = toParse.read()) != -1){
				cChar = (char) iChar;
				result.append(cChar);
				if (cChar == ';'){
					final String entity = result.toString().toLowerCase();
					if (entity.equals("lt;"))
						stringBuffer.append("<");
					else if (entity.equals("gt;"))
						stringBuffer.append(">");
					else if (entity.equals("amp;"))
						stringBuffer.append("&");
					else if (entity.equals("quot;"))
						stringBuffer.append("\"");
					else if (entity.equals("apos;"))
						stringBuffer.append("'");
					break;
				}
				counter++;
				if (counter > 4)
					return toParse;
//					throw new RuntimeException("Illegal use of &. Use &amp; entity instead. Line:"+line);
			}

			return toParse;
		}

		/**
		 * Parses a CData Section of a document
		 * @param toParse
		 * @return
		 * @throws Exception
		 */
		private Reader handleCDATASection(Reader toParse) throws Exception{
			int iChar;
			char cChar;
			StringBuffer result = new StringBuffer();
			int counter = 0;
			boolean checkedCDATA = false;

			while ((iChar = toParse.read()) != -1){
				cChar = (char) iChar;
				if (cChar == ']'){
					_myTool.handleCDATASection(result.toString());
					break;
				}
				result.append(cChar);
				counter++;
				if (counter > 5 && !checkedCDATA){
					checkedCDATA = true;
					if (!result.toString().toUpperCase().equals("CDATA["))
						throw new RuntimeException(
							"Illegal use of <![. " + 
							"These operators are used to start a CDATA section. <![CDATA[]]>" +
							" Line:" + line
						);
					result = new StringBuffer();
				}
			}

			if ((char) toParse.read() != ']')
				throw new RuntimeException("Wrong Syntax at the end of a CDATA section <![CDATA[]]> Line:"+line);
			if ((char) toParse.read() != '>')
				throw new RuntimeException("Wrong Syntax at the end of a CDATA section <![CDATA[]]> Line:"+line);

			//XMLElement keep = new XMLElement(sTagName,attributes);
			//actualElement.addChild(keep);
			//if(oChar != '/')actualElement = keep;
			return toParse;
		}
		CCXMLElement xmlElement;
		
		@SuppressWarnings("unchecked")
		public void run(){
			parseDocument(_myDocument);
			if(_myFeedbackHandler != null)_myFeedbackHandler.onXMLFeedback((CCXMLTool)_myTool);
		}
	}
	
	private static CCXMLIO _myInstance;

	/**
	 * Initializes a new CCXMLIO Object for loading and saving XML files.
	 */
	public CCXMLIO(){
	}

	/**
	 * <p>
	 * Use this method to parse an xml file. If the given String is xml, it is
	 * directly parsed and converted by the given XMLTool. Be aware that it has to
	 * start with &quot;&lt;?xml&quot to be detected as xml.
	 * </p>
	 * <p>
	 * If you call the function with an url the according file is loaded. You 
	 * can load xml files from your harddisk or the internet. Both works in
	 * an application if you export it as an applet it is not possible to 
	 * directly load xml from external sources, because of java security resctictions.
	 * If you want to load external sources you have to use an application on
	 * the serverside that passes the file to your applet. You will find
	 * examples using php in the processing forum.
	 * </p>
	 * <p>
	 * To define how the parsed XML is handled you have to pass {@link CCAbstractXMLTool}.
	 * You can force the parser to ignore line feed by setting theIgnoreLineFeed true.
	 * </p>
	 * <p>
	 * To handle the loaded XML you also have to pass an implementation of the {@link CCIXMLFeedbackHandler}
	 * </p>
	 * 
	 * @param <XMLTool>
	 * @param thePath url from where the Element has to be loaded
	 * @param theXMLTool the tool to handle the parsed xml {@link CCAbstractXMLTool}
	 * @param theFeedbackHandler the handler for the parsed result
	 * @param theIgnoreLineFeed tell the parser to ignore new line feeds
	 * @see #loadElementNow(String, CCAbstractXMLTool)
	 * @see #saveXMLElement(CCXMLElement, String)
	 */
	public <XMLTool extends CCAbstractXMLTool> void parseXML(
		final String thePath, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler,
		final boolean theIgnoreLineFeed
	){
		readXML(new StringReader(thePath), theXMLTool, theFeedbackHandler,theIgnoreLineFeed);
		
	}
	
	/**
	 * Same like {@link #parseXML(String, CCAbstractXMLTool, CCIXMLFeedbackHandler, boolean)} but automatically
	 * ignores new linefeeds.
	 * @param <XMLTool>
	 * @param theDocument url from where the Element has to be loaded
	 * @param theXMLTool the tool to handle the parsed xml {@link CCAbstractXMLTool}
	 * @param theFeedbackHandler the handler for the parsed result
	 */
	public <XMLTool extends CCAbstractXMLTool> void loadElement(
		final String theDocument, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler
	){
		parseXML(theDocument, theXMLTool,theFeedbackHandler,true);
	}
	
	/**
	 * Same like {@link #loadElement(String, CCAbstractXMLTool, CCIXMLFeedbackHandler)} but reads the 
	 * document from the given input stream.
	 * @param <XMLTool>
	 * @param theInput
	 * @param theXMLTool the tool to handle the parsed xml {@link CCAbstractXMLTool}
	 * @param theFeedbackHandler the handler for the parsed result
	 * @param theIgnoreLineFeed tell the parser to ignore new line feeds
	 */
	public <XMLTool extends CCAbstractXMLTool> void loadElement(
		final InputStream theInput, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler,
		final boolean theIgnoreLineFeed
	){
		try{
			readXML(new BufferedReader(new InputStreamReader(theInput,"UTF-8")), theXMLTool, theFeedbackHandler,theIgnoreLineFeed);
		}catch (Exception e){
			throw new RuntimeException("CCXML was not able to load the given xml-document.",e);
		}
	}
	
	/**
	 * 
	 * @param <XMLTool>
	 * @param theInput
	 * @param theXMLTool
	 * @param theFeedbackHandler
	 * @param theIgnoreLineFeed
	 */
	public <XMLTool extends CCAbstractXMLTool> void loadElement(
		final InputStream theInput, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler
	){
		loadElement(theInput, theXMLTool, theFeedbackHandler,true);
	}
	
	/**
	 * Same like {@link #loadElement(String, CCAbstractXMLTool, CCIXMLFeedbackHandler)} but reads the 
	 * document from the given reader.
	 * @param <XMLTool>
	 * @param theInput
	 * @param theXMLTool the tool to handle the parsed xml {@link CCAbstractXMLTool}
	 * @param theFeedbackHandler the handler for the parsed result
	 * @param theIgnoreLineFeed tell the parser to ignore new line feeds
	 */
	public <XMLTool extends CCAbstractXMLTool> void readXML(
		final Reader theInput, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler,
		final boolean theIgnoreLineFeed
	){
		Thread loader;
					
		try{
			loader = new Thread(new CCXMLParser<XMLTool>(theInput,theXMLTool,theFeedbackHandler,theIgnoreLineFeed));
		}catch (Exception e){
			throw new RuntimeException("CCXML was not able to load the given xml-document.",e);
		}
		try{
			loader.start();
		}catch (Exception e){
			throw new RuntimeException("CCXML was not able to read the given xml-document. Please make sure that you load a document that contains valid xml.");
		}
	}
	
	/**
	 * Same like {@link #loadElement(String, CCAbstractXMLTool, CCIXMLFeedbackHandler)} but reads the 
	 * document from the given reader. 
	 * @param <XMLTool>
	 * @param theInput
	 * @param theXMLTool the tool to handle the parsed xml {@link CCAbstractXMLTool}
	 * @param theFeedbackHandler the handler for the parsed result
	 */
	public <XMLTool extends CCAbstractXMLTool> void loadElement(
		final Reader theInput, 
		final XMLTool theXMLTool, 
		final CCIXMLFeedbackHandler<XMLTool> theFeedbackHandler
	){
		readXML(theInput, theXMLTool, theFeedbackHandler,true);
	}
	
	/**
	 * Same like {@link #parseXML(String, CCAbstractXMLTool, CCIXMLFeedbackHandler, boolean)}, but forces to load
	 * the xml directly and not in a background thread.
	 * @param <XMLTool>
	 * @param thePath
	 * @param theXMLTool
	 * @param theIgnoreLineFeed
	 * @return the given xml tool after applying to the xml
	 */
	public <XMLTool extends CCAbstractXMLTool> XMLTool loadElement(
		final Path thePath, 
		final XMLTool theXMLTool,
		final boolean theIgnoreLineFeed
	){
		return loadElementNow(thePath, theXMLTool,theIgnoreLineFeed);
	}
	
	/**
	 * Loads an xml file by applying the given xml tool
	 * @param <XMLTool>
	 * @param thePath
	 * @param theXMLTool
	 * @return the given xml tool after applying to the xml
	 */
	public <XMLTool extends CCAbstractXMLTool> XMLTool loadElement(
		final Path thePath, 
		final XMLTool theXMLTool
	){
		return loadElementNow(thePath, theXMLTool,true);
	}
	
	public <XMLTool extends CCAbstractXMLTool> XMLTool loadElementNow(
		final Path thePath, 
		final XMLTool theXMLTool,
		final boolean theIgnoreLineFeed
	){
		try{
			InputStream test = Files.newInputStream(thePath);
			return parse(new BufferedReader(new InputStreamReader(test,"UTF-8")),theXMLTool,theIgnoreLineFeed);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public <XMLTool extends CCAbstractXMLTool> XMLTool parse(
		Reader theXML,
		final XMLTool theXMLTool,
		final boolean theIgnoreLineFeed
	){
		CCXMLParser<XMLTool> myParser = new CCXMLParser<XMLTool>(theXML,theXMLTool,null,theIgnoreLineFeed);
		try{
			myParser.run();
			return theXMLTool;
		}catch (Exception e){
			throw new RuntimeException("XML was not able to read the given xml Please make sure that you load a file that contains valid xml.",e);
		}
	}
	
	public <XMLTool extends CCAbstractXMLTool> XMLTool loadElementNow(
		final Path thePath, 
		final XMLTool theXMLTool
	){
		return loadElementNow(thePath, theXMLTool, true);
	}
	
	/**
	 * Returns a XMLElement containing the XML document as tree structure.
	 * @param thePath
	 * @return xml element for the given url
	 */
	public static CCXMLElement createXMLElement(final Path thePath, final boolean theIgnoreLineFeed){
		if(_myInstance == null){
			_myInstance = new CCXMLIO();
		}
		CCXMLTree myTree = _myInstance.loadElement(thePath, new CCXMLTree(),theIgnoreLineFeed);
		if(myTree != null) {
			return myTree.rootElement();
		}
		return null;
	}
	
	public static CCXMLElement createXMLElement(final Path thePath){
		return createXMLElement(thePath, true);
	}
	
	public static CCXMLElement parse(String theXML, boolean theIgnoreLineFeed){
		if(_myInstance == null){
			_myInstance = new CCXMLIO();
		}
		StringReader myReader = new StringReader(theXML);
		CCXMLTree myTree = _myInstance.parse(myReader, new CCXMLTree(),theIgnoreLineFeed);
		
		if(myTree != null) {
			return myTree.rootElement();
		}
		return null;
	}
	
	public static CCXMLElement parse(String theXML){
		return parse(theXML, true);
	}
	
	private static String docstart(String theVersion, String theEncoding){

		return "<?xml version=\""+theVersion+"\" encoding=\""+theEncoding+"\"?>";
	}

	/**
	 * Saves the XMLElement to a given filename.
	 * 
	 * @param theXMLElement the element to be saved
	 * @param thePath path to save the XMLElement as XML File 
	 * @param theEncoding used for the XML File 
	 */
	public static void saveXMLElement(final CCXMLElement theXMLElement, Path thePath, String theEncoding){
		try{
			CCNIOUtil.createDirectories(thePath);
			PrintStream myWriter = new PrintStream(Files.newOutputStream(thePath), true, theEncoding);
			myWriter.println(docstart("1.0", theEncoding));
			theXMLElement.print(myWriter);
			myWriter.flush();
			myWriter.close();
		}catch (Exception e){
			e.printStackTrace();
			CCLog.error("You cannot write to this destination. Make sure destionation is a valid path");
		}
	}
	
	/**
	 * Saves the XMLElement to a given filename using standard encoding "ISO-8859-1".
	 * 
	 * @param theXMLElement the element to be saved
	 * @param thePath path to save the XMLElement as XML File 
	 */
	public static void saveXMLElement(final CCXMLElement theXMLElement, Path thePath){
		saveXMLElement(theXMLElement, thePath, "ISO-8859-1");
	}

}
