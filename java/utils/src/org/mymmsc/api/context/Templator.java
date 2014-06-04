/**
 * @(#)Templator.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * A compact template engine for Text files.
 *
 * <p>
 * Template syntax:<br>
 * <pre>
 *    Variables:
 *       ${VariableName}
 *
 *    Blocks:
 *       &lt;!-- BEGIN blockName --&gt;
 *         ... block contents ...
 *       &lt;!-- END blockName --&gt;
 *
 *    Conditional blocks:
 *       &lt;!-- if flag1 flag2 --&gt;
 *         ... included if flag1 or flag2 is set ...
 *       &lt;!-- elseIf !flag3 flag4 --&gt;
 *         ... included if flag3 is not set or flag4 is set ...
 *       &lt;!-- else --&gt;
 *         ... included if none of the above conditions is met ...
 *       &lt;!-- endIf --&gt;
 *
 *    Include a subtemplate:
 *       &lt;!-- include relativeFileName --&gt;</pre>
 *
 * <p>
 * General remarks:</p>
 * <ul>
 *  <li>Variable names, block names, condition flags and commands (e.g. "BEGIN") are case-insensitive.</li>
 *  <li>The same variable may be used multiple times within a template.</li>
 *  <li>Multiple blocks with the same name may occur within a template.</li>
 *  <li>Blocks can be nested.</li>
 *  <li>Conditional blocks (if) and includes (include) are resolved when the template is parsed.
 *  <li>Normal blocks (BEGIN) must be added (and can be repeated) by the application program.
 *  <li>The {@link TemplatorCache} class may be used to cache MiniTemplator objects with parsed templates.</li>
 *  </ul>
 *
 * <p>
 * Home page: <a href="http://www.source-code.biz/MiniTemplator">www.source-code.biz/MiniTemplator</a><br>
 * Author: Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br>
 * Multi-licensed: EPL/LGPL.
 *
 * <p>
 * Version history:</p>
 * <table cellpadding=3 cellspacing=0 border=1><tbody style="vertical-align:top">
 * <tr><td>2001-10-24<td>chdh<br>(Christian d'Heureuse)<td> VBasic version created.
 * <tr><td>2003-03-25<td>chdh<td> Converted from VB to Java.
 * <tr><td>2003-07-08<td>chdh<td> Method variableExists added.
 * <tr><td>2003-07-16<td>chdh<td> Method setVariable changed to throw an exception when the variable does not exist (instead of returning false).
 * <tr><td>2004-04-07<td>chdh<td> Parameter isOptional added to method setVariable.
 *   Licensing changed from GPL to LGPL.
 * <tr><td>2004-04-19<td>chdh<td> Methods blockExists, setVariableEsc and escapeHtml added.
 * <tr><td>2004-10-28<td>chdh<td>
 *   Multiple blocks with the same name may now occur within a template.<br>
 *   No syntax error exception ("unknown command") is thrown any more, if a HTML comment starts with "${".<br>
 *   serialVersionUID added to exception classes (for Java 5 compatibility).
 * <tr><td>2004-11-06<td>chdh<td>
 *   Changes for Java 5. (Unfortunately this version of MiniTemplator is no longer compatible with Java 1.4).<br>
 *   "include" command implemented. Method loadSubtemplate and a new constructor variant added.<br>
 *   Method cloneReset and class MiniTemplatorCache added.<br>
 * <tr><td>2004-11-20<td>chdh<td> "include" command changed so that the command text is not copied to the output file.
 * <tr><td>2006-07-07<td>chdh<td> Extended constructor with <code>charset</code> argument added.
 * <tr><td>2006-10-18<td>chdh<td> New variant of <code>addBlock()</code> added with an <code>isOptional</code> parameter.<br>
 * <tr><td>2007-05-19<td>chdh<td><ul style="margin-top:0; margin-bottom:0">
 *  <li>Conditional blocks (if statement) implemented.
 *  <li>New nested class {@link Templator.TemplateSpecification}.
 *  <li>Old constructors replaced by the new general constructor {@link #MiniTemplator(TemplateSpecification)}.
 *  <li>Parameter type of {@link #generateOutput(String)} changed form <code>File</code> to <code>String</code>.
 *  </ul>
 * <tr><td>2009-01-22<td>chdh<td> Method {@link #getVariables()} added.
 * <tr><td>2009-01-25<td>chdh<td><ul style="margin-top:0; margin-bottom:0">
 *   <li>New constructor {@link #MiniTemplator(String)} added.
 *       Note that this constructor is not compatible with the old (prior to 2007-05-19) constructor with the same signature,
 *       because the old constructor with that signature expected the template string in the string argument instead of the file name.
 *   <li>The nested exceptions ({@link TemplateSyntaxException}, {@link VariableNotDefinedException} and
 *       {@link BlockNotDefinedException}) are now derived from <code>RuntimeException</code> instead of
 *       <code>Exception</code>, to make them unchecked exceptions that do not have to be catched or declared.
 *   <li>Convenience methods added: {@link #setVariableOpt(String, String)}, {@link #setVariableOptEsc(String, String)} and {@link #addBlockOpt(String)}.
 *   <li>EPL license added.
 *  </ul>
 * <tr><td>2009-04-15<td>chdh<td> The complement ("not") operator "!" may now be used in the flag expressions of the if and elseIf commands.
 * </tbody></table>
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class Templator {
	// --- private variables ----------------------------------------------
	private TemplatorParser mtp; // contains the parsed template
	private Charset charset; // charset used for reading and writing files
	private String subtemplateBasePath; // base path for relative file names of
	// subtemplates, may be null

	private String[] varValuesTab; // variable values table, entries may be null

	private BlockDynTabRec[] blockDynTab; // dynamic block-specific values
	private BlockInstTabRec[] blockInstTab; // block instances table This table
	// contains an entry for each block
	// instance that has been added.
	// Indexed by BlockInstNo.
	private int blockInstTabCnt; // no of entries used in BlockInstTab

	// --- constructors ---------------------------------------------------

	/**
	 * Constructs a MiniTemplator object.
	 * <p>
	 * During construction, the template and subtemplate files are read and
	 * parsed.
	 * <p>
	 * Note: The {@link TemplatorCache} class may be used to cache MiniTemplator
	 * objects.
	 * 
	 * @param templateSpec
	 *            the template specification.
	 * @throws TemplateSyntaxException
	 *             when a syntax error is detected within the template.
	 * @throws IOException
	 *             when an i/o error occurs while reading the template.
	 */
	public Templator(TemplateSpecification templateSpec) throws IOException,
			TemplateSyntaxException {
		init(templateSpec);
	}

	/**
	 * Constructs a MiniTemplator object by specifying only the file name.
	 * <p>
	 * This is a convenience constructor that may be used when only the file
	 * name has to be specified.
	 * 
	 * @param templateFileName
	 *            the file name of the template file.
	 * @throws TemplateSyntaxException
	 *             when a syntax error is detected within the template.
	 * @throws IOException
	 *             when an i/o error occurs while reading the template.
	 * @see #MiniTemplator(TemplateSpecification)
	 */
	public Templator(String templateFileName) throws IOException,
			TemplateSyntaxException {
		TemplateSpecification templateSpec = new TemplateSpecification();
		templateSpec.templateFileName = templateFileName;
		init(templateSpec);
	}

	public Templator(InputStream is) throws IOException,
			TemplateSyntaxException {
		TemplateSpecification templateSpec = new TemplateSpecification();
		templateSpec.is = is;
		init(templateSpec);
	}

	/**
	 * Constructs a MiniTemplator object by specifying only the file name.
	 * <p>
	 * This is a convenience constructor that may be used when only the file
	 * name has to be specified.
	 * 
	 * @param templateFileName
	 *            the file name of the template file.
	 * @throws TemplateSyntaxException
	 *             when a syntax error is detected within the template.
	 * @throws IOException
	 *             when an i/o error occurs while reading the template.
	 * @see #MiniTemplator(TemplateSpecification)
	 */
	public Templator(String templateFileName, String charset)
			throws IOException, TemplateSyntaxException {
		TemplateSpecification templateSpec = new TemplateSpecification();
		templateSpec.templateFileName = templateFileName;
		templateSpec.charset = Charset.forName(charset);
		init(templateSpec);
	}

	private void init(TemplateSpecification templateSpec) throws IOException,
			TemplateSyntaxException {
		charset = templateSpec.charset;
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		subtemplateBasePath = templateSpec.subtemplateBasePath;
		if (subtemplateBasePath == null
				&& templateSpec.templateFileName != null) {
			subtemplateBasePath = new File(templateSpec.templateFileName)
					.getParent();
		}
		String templateText = templateSpec.templateText;
		if (templateText == null && templateSpec.templateFileName != null) {
			templateText = readFileIntoString(templateSpec.templateFileName);
		} else if (templateSpec.is != null) {
			Reader reader = new BufferedReader(new InputStreamReader(
					templateSpec.is));
			templateText = readStreamIntoString(reader);
		}
		if (templateText == null) {
			throw new IllegalArgumentException(
					"No templateFileName or templateText specified.");
		}
		mtp = new TemplatorParser(templateText, templateSpec.conditionFlags,
				this);
		reset();
	}

	// Private dummy constructor, used for cloneReset().
	private Templator() {
		//
	}

	// --- loadSubtemplate ------------------------------------------------

	/**
	 * Loads the template string of a subtemplate (used for the include
	 * command). This method can be overridden in a subclass, to load
	 * subtemplates from somewhere else, e.g. from a database.
	 * <p>
	 * This implementation of the method interprets <code>subtemplateName</code>
	 * as a relative file path name and reads the template string from that
	 * file. {@link Templator.TemplateSpecification#subtemplateBasePath} is used
	 * to convert the relative path of the subtemplate into an absolute path.
	 * 
	 * @param subtemplateName
	 *            the name of the subtemplate. Normally a relative file path.
	 *            This is the argument string that was specified with the
	 *            "include" command. If the string has quotes, the quotes are
	 *            removed before this method is called.
	 * @return the template text string of the subtemplate.
	 **/
	protected String loadSubtemplate(String subtemplateName) throws IOException {
		String fileName = new File(subtemplateBasePath, subtemplateName)
				.getPath();
		return readFileIntoString(fileName);
	}

	// --- build up (template variables and blocks) ------------------------

	/**
	 * Resets the MiniTemplator object to the initial state. All variable values
	 * are cleared and all added block instances are deleted. This method can be
	 * used to produce another HTML page with the same template. It is faster
	 * than creating another MiniTemplator object, because the template does not
	 * have to be read and parsed again.
	 */
	public void reset() {
		if (varValuesTab == null) {
			varValuesTab = new String[mtp.varTabCnt];
		} else {
			for (int varNo = 0; varNo < mtp.varTabCnt; varNo++) {
				varValuesTab[varNo] = null;
			}
		}
		if (blockDynTab == null) {
			blockDynTab = new BlockDynTabRec[mtp.blockTabCnt];
		}
		for (int blockNo = 0; blockNo < mtp.blockTabCnt; blockNo++) {
			BlockDynTabRec bdtr = blockDynTab[blockNo];
			if (bdtr == null) {
				bdtr = new BlockDynTabRec();
				blockDynTab[blockNo] = bdtr;
			}
			bdtr.instances = 0;
			bdtr.firstBlockInstNo = -1;
			bdtr.lastBlockInstNo = -1;
		}
		blockInstTabCnt = 0;
	}

	/**
	 * Clones this MiniTemplator object and resets the clone. This method is
	 * used to copy a MiniTemplator object. It is fast, because the template
	 * does not have to be parsed again, and the internal data structures that
	 * contain the parsed template information are shared among the clones.
	 * <p>
	 * This method is used by the {@link TemplatorCache} class to clone the
	 * cached MiniTemplator objects.
	 */
	public Templator cloneReset() {
		Templator m = new Templator();
		m.mtp = mtp; // the MiniTemplatorParser object is shared among the
		// clones
		m.charset = charset;
		// (subtemplateBasePath does not have to be copied, because the
		// subtemplates have already been read)
		m.reset();
		return m;
	}
	
	/**
	 * 设置模版KV对, 忽略变量未定义
	 * @param key
	 * @param value
	 */
	public void setVariableQuietly(String key, String value) {
		try {
			setVariable(key, value);
		} catch (VariableNotDefinedException e) {
			// 
		}
	}
	
	/**
	 * Sets a template variable.
	 * <p>
	 * For variables that are used in blocks, the variable value must be set
	 * before <code>addBlock()</code> is called.
	 * 
	 * @param variableName
	 *            the name of the variable to be set. Case-insensitive.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 * @param isOptional
	 *            specifies whether an exception should be thrown when the
	 *            variable does not exist in the template. If
	 *            <code>isOptional</code> is <code>false</code> and the variable
	 *            does not exist, an exception is thrown.
	 * @throws VariableNotDefinedException
	 *             when no variable with the specified name exists in the
	 *             template and <code>isOptional</code> is <code>false</code>.
	 */
	public void setVariable(String variableName, String variableValue,
			boolean isOptional) throws VariableNotDefinedException {
		int varNo = mtp.lookupVariableName(variableName);
		if (varNo == -1) {
			if (isOptional) {
				return;
			}
			throw new VariableNotDefinedException(variableName);
		}
		varValuesTab[varNo] = variableValue;
	}

	/**
	 * Sets a template variable.
	 * <p>
	 * Convenience method for:
	 * <code>setVariable (variableName, variableValue, false)</code>
	 * 
	 * @param variableName
	 *            the name of the variable to be set. Case-insensitive.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 * @throws VariableNotDefinedException
	 *             when no variable with the specified name exists in the
	 *             template.
	 * @see #setVariable(String, String, boolean)
	 */
	public void setVariable(String variableName, String variableValue)
			throws VariableNotDefinedException {
		setVariable(variableName, variableValue, false);
	}

	/**
	 * Sets an optional template variable.
	 * <p>
	 * Convenience method for:
	 * <code>setVariable (variableName, variableValue, true)</code>
	 * 
	 * @param variableName
	 *            the name of the variable to be set. Case-insensitive.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 * @see #setVariable(String, String, boolean)
	 */
	public void setVariableOpt(String variableName, String variableValue) {
		setVariable(variableName, variableValue, true);
	}

	/**
	 * Sets a template variable to an escaped value.
	 * <p>
	 * Convenience method for:
	 * <code>setVariable (variableName, MiniTemplator.escapeHtml(variableValue), isOptional)</code>
	 * 
	 * @param variableName
	 *            the name of the variable to be set.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 *            Special HTML/XML characters are escaped.
	 * @param isOptional
	 *            specifies whether an exception should be thrown when the
	 *            variable does not exist in the template. If
	 *            <code>isOptional</code> is <code>false</code> and the variable
	 *            does not exist, an exception is thrown.
	 * @throws VariableNotDefinedException
	 *             when no variable with the specified name exists in the
	 *             template and <code>isOptional</code> is <code>false</code>.
	 * @see #setVariable(String, String, boolean)
	 * @see #escapeHtml(String)
	 */
	public void setVariableEsc(String variableName, String variableValue,
			boolean isOptional) throws VariableNotDefinedException {
		setVariable(variableName, escapeHtml(variableValue), isOptional);
	}

	/**
	 * Sets a template variable to an escaped value.
	 * <p>
	 * Convenience method for:
	 * <code>setVariable (variableName, MiniTemplator.escapeHtml(variableValue), false)</code>
	 * 
	 * @param variableName
	 *            the name of the variable to be set. Case-insensitive.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 *            Special HTML/XML characters are escaped.
	 * @throws VariableNotDefinedException
	 *             when no variable with the specified name exists in the
	 *             template.
	 * @see #setVariable(String, String, boolean)
	 * @see #escapeHtml(String)
	 */
	public void setVariableEsc(String variableName, String variableValue)
			throws VariableNotDefinedException {
		setVariable(variableName, escapeHtml(variableValue), false);
	}

	/**
	 * Sets an optional template variable to an escaped value.
	 * <p>
	 * Convenience method for:
	 * <code>setVariable (variableName, MiniTemplator.escapeHtml(variableValue), true)</code>
	 * 
	 * @param variableName
	 *            the name of the variable to be set. Case-insensitive.
	 * @param variableValue
	 *            the new value of the variable. May be <code>null</code>.
	 *            Special HTML/XML characters are escaped.
	 * @see #setVariable(String, String, boolean)
	 * @see #escapeHtml(String)
	 */
	public void setVariableOptEsc(String variableName, String variableValue) {
		setVariable(variableName, escapeHtml(variableValue), true);
	}

	/**
	 * Checks whether a variable with the specified name exists within the
	 * template.
	 * 
	 * @param variableName
	 *            the name of the variable. Case-insensitive.
	 * @return <code>true</code> if the variable exists.<br>
	 *         <code>false</code> if no variable with the specified name exists
	 *         in the template.
	 */
	public boolean variableExists(String variableName) {
		return mtp.lookupVariableName(variableName) != -1;
	}

	/**
	 * Returns a map with the names and current values of the template
	 * variables.
	 */
	public Map<String, String> getVariables() {
		HashMap<String, String> map = new HashMap<String, String>(mtp.varTabCnt);
		for (int varNo = 0; varNo < mtp.varTabCnt; varNo++) {
			map.put(mtp.varTab[varNo], varValuesTab[varNo]);
		}
		return map;
	}

	/**
	 * Adds an instance of a template block.
	 * <p>
	 * If the block contains variables, these variables must be set before the
	 * block is added. If the block contains subblocks (nested blocks), the
	 * subblocks must be added before this block is added. If multiple blocks
	 * exist with the specified name, an instance is added for each block
	 * occurrence.
	 * 
	 * @param blockName
	 *            the name of the block to be added. Case-insensitive.
	 * @param isOptional
	 *            specifies whether an exception should be thrown when the block
	 *            does not exist in the template. If <code>isOptional</code> is
	 *            <code>false</code> and the block does not exist, an exception
	 *            is thrown.
	 * @throws BlockNotDefinedException
	 *             when no block with the specified name exists in the template
	 *             and <code>isOptional</code> is <code>false</code>.
	 */
	public void addBlock(String blockName, boolean isOptional)
			throws BlockNotDefinedException {
		int blockNo = mtp.lookupBlockName(blockName);
		if (blockNo == -1) {
			if (isOptional) {
				return;
			}
			throw new BlockNotDefinedException(blockName);
		}
		while (blockNo != -1) {
			addBlockByNo(blockNo);
			blockNo = mtp.blockTab[blockNo].nextWithSameName;
		}
	}

	/**
	 * Adds an instance of a template block.
	 * <p>
	 * Convenience method for: <code>addBlock (blockName, false)</code>
	 * 
	 * @param blockName
	 *            the name of the block to be added. Case-insensitive.
	 * @throws BlockNotDefinedException
	 *             when no block with the specified name exists in the template.
	 * @see #addBlock(String, boolean)
	 */
	public void addBlock(String blockName) throws BlockNotDefinedException {
		addBlock(blockName, false);
	}

	/**
	 * Adds an instance of an optional template block.
	 * <p>
	 * Convenience method for: <code>addBlock (blockName, true)</code>
	 * 
	 * @param blockName
	 *            the name of the block to be added. Case-insensitive.
	 * @see #addBlock(String, boolean)
	 */
	public void addBlockOpt(String blockName) {
		addBlock(blockName, true);
	}

	private void addBlockByNo(int blockNo) {
		BlockTabRec btr = mtp.blockTab[blockNo];
		BlockDynTabRec bdtr = blockDynTab[blockNo];
		int blockInstNo = registerBlockInstance();
		BlockInstTabRec bitr = blockInstTab[blockInstNo];
		if (bdtr.firstBlockInstNo == -1) {
			bdtr.firstBlockInstNo = blockInstNo;
		}
		if (bdtr.lastBlockInstNo != -1) {
			blockInstTab[bdtr.lastBlockInstNo].nextBlockInstNo = blockInstNo; // set
		}
		// forward
		// pointer
		// of
		// chain
		bdtr.lastBlockInstNo = blockInstNo;
		bitr.blockNo = blockNo;
		bitr.instanceLevel = bdtr.instances++;
		if (btr.parentBlockNo == -1) {
			bitr.parentInstLevel = -1;
		} else {
			bitr.parentInstLevel = blockDynTab[btr.parentBlockNo].instances;
		}
		bitr.nextBlockInstNo = -1;
		if (btr.blockVarCnt > 0) {
			bitr.blockVarTab = new String[btr.blockVarCnt];
		}
		for (int blockVarNo = 0; blockVarNo < btr.blockVarCnt; blockVarNo++) { // copy
			// instance
			// variables
			// for
			// this
			// block
			int varNo = btr.blockVarNoToVarNoMap[blockVarNo];
			bitr.blockVarTab[blockVarNo] = varValuesTab[varNo];
		}
	}

	// Returns the block instance number.
	private int registerBlockInstance() {
		int blockInstNo = blockInstTabCnt++;
		if (blockInstTab == null) {
			blockInstTab = new BlockInstTabRec[64];
		}
		if (blockInstTabCnt > blockInstTab.length) {
			blockInstTab = (BlockInstTabRec[]) TemplatorParser.resizeArray(
					blockInstTab, 2 * blockInstTabCnt);
		}
		blockInstTab[blockInstNo] = new BlockInstTabRec();
		return blockInstNo;
	}

	/**
	 * Checks whether a block with the specified name exists within the
	 * template.
	 * 
	 * @param blockName
	 *            the name of the block.
	 * @return <code>true</code> if the block exists.<br>
	 *         <code>false</code> if no block with the specified name exists in
	 *         the template.
	 */
	public boolean blockExists(String blockName) {
		return mtp.lookupBlockName(blockName) != -1;
	}

	// --- output generation ----------------------------------------------

	/**
	 * Generates the HTML page and writes it into a file.
	 * 
	 * @param outputFileName
	 *            name of the file to which the generated HTML page will be
	 *            written.
	 * @throws IOException
	 *             when an i/o error occurs while writing to the file.
	 */
	public void generateOutput(String outputFileName) throws IOException {
		FileOutputStream stream = null;
		OutputStreamWriter writer = null;
		try {
			stream = new FileOutputStream(outputFileName);
			writer = new OutputStreamWriter(stream, charset);
			generateOutput(writer);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * Generates the HTML page and writes it to a character stream.
	 * 
	 * @param outputWriter
	 *            a character stream (<code>writer</code>) to which the HTML
	 *            page will be written.
	 * @throws IOException
	 *             when an i/o error occurs while writing to the stream.
	 */
	public void generateOutput(Writer outputWriter) throws IOException {
		String s = generateOutput();
		outputWriter.write(s);
	}

	/**
	 * Generates the HTML page and returns it as a string.
	 * 
	 * @return A string that contains the generated HTML page.
	 */
	public String generateOutput() {
		if (blockDynTab[0].instances == 0) {
			addBlockByNo(0); // add main block
		}
		for (int blockNo = 0; blockNo < mtp.blockTabCnt; blockNo++) {
			BlockDynTabRec bdtr = blockDynTab[blockNo];
			bdtr.currBlockInstNo = bdtr.firstBlockInstNo;
		}
		StringBuilder out = new StringBuilder();
		writeBlockInstances(out, 0, -1);
		return out.toString();
	}

	// Writes all instances of a block that are contained within a specific
	// parent block instance.
	// Called recursively.
	private void writeBlockInstances(StringBuilder out, int blockNo,
			int parentInstLevel) {
		BlockDynTabRec bdtr = blockDynTab[blockNo];
		while (true) {
			int blockInstNo = bdtr.currBlockInstNo;
			if (blockInstNo == -1) {
				break;
			}
			BlockInstTabRec bitr = blockInstTab[blockInstNo];
			if (bitr.parentInstLevel < parentInstLevel) {
				throw new Error();
			}
			if (bitr.parentInstLevel > parentInstLevel) {
				break;
			}
			writeBlockInstance(out, blockInstNo);
			bdtr.currBlockInstNo = bitr.nextBlockInstNo;
		}
	}

	private void writeBlockInstance(StringBuilder out, int blockInstNo) {
		BlockInstTabRec bitr = blockInstTab[blockInstNo];
		int blockNo = bitr.blockNo;
		BlockTabRec btr = mtp.blockTab[blockNo];
		int tPos = btr.tPosContentsBegin;
		int subBlockNo = blockNo + 1;
		int varRefNo = btr.firstVarRefNo;
		while (true) {
			int tPos2 = btr.tPosContentsEnd;
			int kind = 0; // assume end-of-block
			if (varRefNo != -1 && varRefNo < mtp.varRefTabCnt) { // check for
				// variable
				// reference
				VarRefTabRec vrtr = mtp.varRefTab[varRefNo];
				if (vrtr.tPosBegin < tPos) {
					varRefNo++;
					continue;
				}
				if (vrtr.tPosBegin < tPos2) {
					tPos2 = vrtr.tPosBegin;
					kind = 1;
				}
			}
			if (subBlockNo < mtp.blockTabCnt) { // check for subblock
				BlockTabRec subBtr = mtp.blockTab[subBlockNo];
				if (subBtr.tPosBegin < tPos) {
					subBlockNo++;
					continue;
				}
				if (subBtr.tPosBegin < tPos2) {
					tPos2 = subBtr.tPosBegin;
					kind = 2;
				}
			}
			if (tPos2 > tPos) {
				out.append(mtp.templateText.substring(tPos, tPos2));
			}
			switch (kind) {
			case 0: // end of block
				return;
			case 1: { // variable
				VarRefTabRec vrtr = mtp.varRefTab[varRefNo];
				if (vrtr.blockNo != blockNo)
					throw new Error();
				String variableValue = bitr.blockVarTab[vrtr.blockVarNo];
				if (variableValue != null)
					out.append(variableValue);
				tPos = vrtr.tPosEnd;
				varRefNo++;
				break;
			}
			case 2: { // sub block
				BlockTabRec subBtr = mtp.blockTab[subBlockNo];
				if (subBtr.parentBlockNo != blockNo)
					throw new Error();
				writeBlockInstances(out, subBlockNo, bitr.instanceLevel); // recursive
				// call
				tPos = subBtr.tPosEnd;
				subBlockNo++;
				break;
			}
			}
		}
	}

	// --- general utility routines ---------------------------------------

	// Reads the contents of a file into a string variable.
	private String readFileIntoString(String fileName) throws IOException {
		FileInputStream stream = null;
		InputStreamReader reader = null;
		try {
			stream = new FileInputStream(fileName);
			reader = new InputStreamReader(stream, charset);
			return readStreamIntoString(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (stream != null) {
				stream.close();
			}
		}
	}

	// Reads the contents of a stream into a string variable.
	private static String readStreamIntoString(Reader reader)
			throws IOException {
		StringBuilder s = new StringBuilder();
		char a[] = new char[0x10000];
		while (true) {
			int l = reader.read(a);
			if (l == -1) {
				break;
			}
			if (l <= 0) {
				throw new IOException();
			}
			s.append(a, 0, l);
		}
		return s.toString();
	}

	/**
	 * Escapes special HTML characters. Replaces the characters &lt;, &gt;,
	 * &amp;, ' and " by their corresponding HTML/XML character entity codes.
	 * 
	 * @param s
	 *            the input string.
	 * @return the escaped output string.
	 */
	public static String escapeHtml(String s) {
		// (The code of this method is a bit redundant in order to optimize
		// speed)
		if (s == null) {
			return null;
		}
		int sLength = s.length();
		boolean found = false;
		int p;
		loop1: for (p = 0; p < sLength; p++) {
			switch (s.charAt(p)) {
			case '<':
			case '>':
			case '&':
			case '\'':
			case '"':
				found = true;
				break loop1;
			}
		}
		if (!found) {
			return s;
		}
		StringBuilder sb = new StringBuilder(sLength + 16);
		sb.append(s.substring(0, p));
		for (; p < sLength; p++) {
			char c = s.charAt(p);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '\'':
				sb.append("&#39;");
				break;
			case '"':
				sb.append("&#34;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
