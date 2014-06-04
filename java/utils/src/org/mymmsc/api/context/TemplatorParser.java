package org.mymmsc.api.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//====================================================================================================================

//MiniTemplatorParser is an immutable object that contains the parsed template
//text.
class TemplatorParser {

	// --- constants ------------------------------------------------------
	private static final int maxNestingLevel = 20; // maximum number of block
	// nestings
	private static final int maxCondLevels = 20; // maximum number of nested
	// conditional commands
	// (if)
	private static final int maxInclTemplateSize = 1000000; // maximum length of
	// template string
	// when including
	// subtemplates

	// --- variables ------------------------------------------------------

	public String templateText; // contents of the template file
	private HashSet<String> conditionFlags; // set of the condition flags,
	// converted to uppercase

	public String[] varTab; // variables table, contains variable names, array
	// index is variable no
	public int varTabCnt; // no of entries used in VarTab
	private HashMap<String, Integer> varNameToNoMap; // maps variable names to
	// variable numbers
	public VarRefTabRec[] varRefTab; // variable references table
	// Contains an entry for each variable reference in the template. Ordered by
	// templatePos.
	public int varRefTabCnt; // no of entries used in VarRefTab

	public BlockTabRec[] blockTab; // Blocks table, array index is block no
	// Contains an entry for each block in the template. Ordered by tPosBegin.
	public int blockTabCnt; // no of entries used in BlockTab
	private HashMap<String, Integer> blockNameToNoMap; // maps block names to
	// block numbers

	// The following variables are only used temporarilly during parsing of the
	// template.
	private int currentNestingLevel; // current block nesting level during
	// parsing
	private int[] openBlocksTab; // indexed by the block nesting level
	// During parsing, this table contains the block numbers of the open parent
	// blocks (nested outer blocks).
	private int condLevel; // current nesting level of conditional commands
	// (if), -1 = main level
	private boolean[] condEnabled; // enabled/disables state for the conditions
	// of each level
	private boolean[] condPassed; // true if an enabled condition clause has
	// already been processed (separate for each
	// level)
	private Templator templator; // the Templator who created this parser
	// object
	// The reference to the MiniTemplator object is only used to call
	// MiniTemplator.loadSubtemplate().
	private boolean resumeCmdParsingFromStart; // true = resume command parsing

	// from the start position of
	// the last command

	// --- constructor ----------------------------------------------------

	// (The Templator object is only passed to the parser, because the
	// parser needs to call Templator.loadSubtemplate() to load
	// subtemplates.)
	public TemplatorParser(String templateText, Set<String> conditionFlags,
			Templator templator) throws TemplateSyntaxException {
		this.templateText = templateText;
		this.conditionFlags = createConditionFlagsSet(conditionFlags);
		this.templator = templator;
		parseTemplate();
		this.templator = null;
	}

	private HashSet<String> createConditionFlagsSet(Set<String> flags) {
		if (flags == null || flags.isEmpty()) {
			return null;
		}
		HashSet<String> flags2 = new HashSet<String>(flags.size());
		for (String flag : flags) {
			flags2.add(flag.toUpperCase());
		}
		return flags2;
	}

	// --- template parsing -----------------------------------------------

	private void parseTemplate() throws TemplateSyntaxException {
		initParsing();
		beginMainBlock();
		parseTemplateCommands();
		endMainBlock();
		checkBlockDefinitionsComplete();
		if (condLevel != -1) {
			throw new TemplateSyntaxException("if without matching endIf.");
		}
		parseTemplateVariables();
		associateVariablesWithBlocks();
		terminateParsing();
	}

	private void initParsing() {
		varTab = new String[64];
		varTabCnt = 0;
		varNameToNoMap = new HashMap<String, Integer>();
		varRefTab = new VarRefTabRec[64];
		varRefTabCnt = 0;
		blockTab = new BlockTabRec[16];
		blockTabCnt = 0;
		currentNestingLevel = 0;
		blockNameToNoMap = new HashMap<String, Integer>();
		openBlocksTab = new int[maxNestingLevel + 1];
		condLevel = -1;
		condEnabled = new boolean[maxCondLevels];
		condPassed = new boolean[maxCondLevels];
	}

	private void terminateParsing() {
		openBlocksTab = null;
	}

	// Registers the main block.
	// The main block is an implicitly defined block that covers the whole
	// template.
	private void beginMainBlock() {
		int blockNo = registerBlock(null); // =0
		BlockTabRec btr = blockTab[blockNo];
		btr.tPosBegin = 0;
		btr.tPosContentsBegin = 0;
		openBlocksTab[currentNestingLevel] = blockNo;
		currentNestingLevel++;
	}

	// Completes the main block registration.
	private void endMainBlock() {
		BlockTabRec btr = blockTab[0];
		btr.tPosContentsEnd = templateText.length();
		btr.tPosEnd = templateText.length();
		btr.definitionIsOpen = false;
		currentNestingLevel--;
	}

	// Parses commands within the template in the format
	// "<!-- $command parameters -->".
	private void parseTemplateCommands() throws TemplateSyntaxException {
		int p = 0;
		while (true) {
			int p0 = templateText.indexOf("<!--", p);
			if (p0 == -1) {
				break;
			}
			conditionalExclude(p, p0);
			p = templateText.indexOf("-->", p0);
			if (p == -1) {
				throw new TemplateSyntaxException(
						"Invalid HTML comment in template at offset " + p0
								+ ".");
			}
			p += 3;
			String cmdLine = templateText.substring(p0 + 4, p - 3);
			resumeCmdParsingFromStart = false;
			processTemplateCommand(cmdLine, p0, p);
			if (resumeCmdParsingFromStart) {
				p = p0;
			}
		}
	}

	private void processTemplateCommand(String cmdLine, int cmdTPosBegin,
			int cmdTPosEnd) throws TemplateSyntaxException {
		int p0 = skipBlanks(cmdLine, 0);
		if (p0 >= cmdLine.length()) {
			return;
		}
		int p = skipNonBlanks(cmdLine, p0);
		String cmd = cmdLine.substring(p0, p);
		String parms = cmdLine.substring(p);

		/* select */
		if (cmd.equalsIgnoreCase("BEGIN")) {
			// 块开始
			processBeginBlockCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("END")) {
			// 块结束
			processEndBlockCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("include")) {
			// 包含
			processIncludeCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("if")) {
			// 条件判断开始
			processIfCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("elseIf")) {
			// 条件判断
			processElseIfCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("else")) {
			// 否则
			processElseCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else if (cmd.equalsIgnoreCase("endIf")) {
			// 条件判断结束
			processEndIfCmd(parms, cmdTPosBegin, cmdTPosEnd);
		} else {
			if (cmd.startsWith("$") && !cmd.startsWith("${")) {
				throw new TemplateSyntaxException("Unknown command \"" + cmd
						+ "\" in template at offset " + cmdTPosBegin + ".");
			}
		}
	}

	// Processes the BEGIN command.
	private void processBeginBlockCmd(String parms, int cmdTPosBegin,
			int cmdTPosEnd) throws TemplateSyntaxException {
		if (conditionalExclude(cmdTPosBegin, cmdTPosEnd)) {
			return;
		}
		int p0 = skipBlanks(parms, 0);
		if (p0 >= parms.length()) {
			throw new TemplateSyntaxException(
					"Missing block name in BEGIN command in template at offset "
							+ cmdTPosBegin + ".");
		}
		int p = skipNonBlanks(parms, p0);
		String blockName = parms.substring(p0, p);
		if (!isRestOfStringBlank(parms, p)) {
			throw new TemplateSyntaxException(
					"Extra parameter in BEGIN command in template at offset "
							+ cmdTPosBegin + ".");
		}
		int blockNo = registerBlock(blockName);
		BlockTabRec btr = blockTab[blockNo];
		btr.tPosBegin = cmdTPosBegin;
		btr.tPosContentsBegin = cmdTPosEnd;
		openBlocksTab[currentNestingLevel] = blockNo;
		currentNestingLevel++;
		if (currentNestingLevel > maxNestingLevel) {
			throw new TemplateSyntaxException(
					"Block nesting overflow for block \"" + blockName
							+ "\" in template at offset " + cmdTPosBegin + ".");
		}
	}

	// Processes the END command.
	private void processEndBlockCmd(String parms, int cmdTPosBegin,
			int cmdTPosEnd) throws TemplateSyntaxException {
		if (conditionalExclude(cmdTPosBegin, cmdTPosEnd)) {
			return;
		}
		int p0 = skipBlanks(parms, 0);
		if (p0 >= parms.length()) {
			throw new TemplateSyntaxException(
					"Missing block name in END command in template at offset "
							+ cmdTPosBegin + ".");
		}
		int p = skipNonBlanks(parms, p0);
		String blockName = parms.substring(p0, p);
		if (!isRestOfStringBlank(parms, p)) {
			throw new TemplateSyntaxException(
					"Extra parameter in END command in template at offset "
							+ cmdTPosBegin + ".");
		}
		int blockNo = lookupBlockName(blockName);
		if (blockNo == -1) {
			throw new TemplateSyntaxException("Undefined block name \""
					+ blockName + "\" in END command in template at offset "
					+ cmdTPosBegin + ".");
		}
		currentNestingLevel--;
		BlockTabRec btr = blockTab[blockNo];
		if (!btr.definitionIsOpen) {
			throw new TemplateSyntaxException(
					"Multiple END command for block \"" + blockName
							+ "\" in template at offset " + cmdTPosBegin + ".");
		}
		if (btr.nestingLevel != currentNestingLevel) {
			throw new TemplateSyntaxException(
					"Block nesting level mismatch at END command for block \""
							+ blockName + "\" in template at offset "
							+ cmdTPosBegin + ".");
		}
		btr.tPosContentsEnd = cmdTPosBegin;
		btr.tPosEnd = cmdTPosEnd;
		btr.definitionIsOpen = false;
	}

	// Returns the block number of the newly registered block.
	private int registerBlock(String blockName) {
		int blockNo = blockTabCnt++;
		if (blockTabCnt > blockTab.length) {
			blockTab = (BlockTabRec[]) resizeArray(blockTab, 2 * blockTabCnt);
		}
		BlockTabRec btr = new BlockTabRec();
		blockTab[blockNo] = btr;
		btr.blockName = blockName;
		if (blockName != null) {
			btr.nextWithSameName = lookupBlockName(blockName);
		} else {
			btr.nextWithSameName = -1;
		}
		btr.nestingLevel = currentNestingLevel;
		if (currentNestingLevel > 0) {
			btr.parentBlockNo = openBlocksTab[currentNestingLevel - 1];
		} else {
			btr.parentBlockNo = -1;
		}
		btr.definitionIsOpen = true;
		btr.blockVarCnt = 0;
		btr.firstVarRefNo = -1;
		btr.blockVarNoToVarNoMap = new int[32];
		btr.dummy = false;
		if (blockName != null) {
			blockNameToNoMap.put(blockName.toUpperCase(), new Integer(blockNo));
		}
		return blockNo;
	}

	// Registers a dummy block to exclude a range within the template text.
	private void excludeTemplateRange(int tPosBegin, int tPosEnd) {
		if (blockTabCnt > 0) {
			// Check whether we can extend the previous block.
			BlockTabRec btr = blockTab[blockTabCnt - 1];
			if (btr.dummy && btr.tPosEnd == tPosBegin) {
				btr.tPosContentsEnd = tPosEnd;
				btr.tPosEnd = tPosEnd;
				return;
			}
		}
		int blockNo = registerBlock(null);
		BlockTabRec btr = blockTab[blockNo];
		btr.tPosBegin = tPosBegin;
		btr.tPosContentsBegin = tPosBegin;
		btr.tPosContentsEnd = tPosEnd;
		btr.tPosEnd = tPosEnd;
		btr.definitionIsOpen = false;
		btr.dummy = true;
	}

	// Checks that all block definitions are closed.
	private void checkBlockDefinitionsComplete() throws TemplateSyntaxException {
		for (int blockNo = 0; blockNo < blockTabCnt; blockNo++) {
			BlockTabRec btr = blockTab[blockNo];
			if (btr.definitionIsOpen) {
				throw new TemplateSyntaxException(
						"Missing END command in template for block \""
								+ btr.blockName + "\".");
			}
		}
		if (currentNestingLevel != 0) {
			throw new TemplateSyntaxException(
					"Block nesting level error at end of template.");
		}
	}

	// Processes the include command.
	private void processIncludeCmd(String parms, int cmdTPosBegin,
			int cmdTPosEnd) throws TemplateSyntaxException {
		if (conditionalExclude(cmdTPosBegin, cmdTPosEnd)) {
			return;
		}
		int p0 = skipBlanks(parms, 0);
		if (p0 >= parms.length()) {
			throw new TemplateSyntaxException(
					"Missing subtemplate name in include command in template at offset "
							+ cmdTPosBegin + ".");
		}
		int p;
		if (parms.charAt(p0) == '"') { // subtemplate name is quoted
			p0++;
			p = parms.indexOf('"', p0);
			if (p == -1) {
				throw new TemplateSyntaxException(
						"Missing closing quote for subtemplate name in include command in template at offset "
								+ cmdTPosBegin + ".");
			}
		} else {
			p = skipNonBlanks(parms, p0);
		}
		String subtemplateName = parms.substring(p0, p);
		p++;
		if (!isRestOfStringBlank(parms, p)) {
			throw new TemplateSyntaxException(
					"Extra parameter in include command in template at offset "
							+ cmdTPosBegin + ".");
		}
		insertSubtemplate(subtemplateName, cmdTPosBegin, cmdTPosEnd);
	}

	private void insertSubtemplate(String subtemplateName, int tPos1, int tPos2) {
		if (templateText.length() > maxInclTemplateSize)
			throw new RuntimeException(
					"Subtemplate include aborted because the internal template string is longer than "
							+ maxInclTemplateSize + " characters.");
		String subtemplate;
		try {
			subtemplate = templator.loadSubtemplate(subtemplateName);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading subtemplate \""
					+ subtemplateName + "\"", e);
		}
		// (Copying the template to insert a subtemplate is a bit slow. In a
		// future implementation of MiniTemplator,
		// a table could be used that contains references to the string
		// fragments.)
		StringBuilder s = new StringBuilder(templateText.length()
				+ subtemplate.length());
		s.append(templateText, 0, tPos1);
		s.append(subtemplate);
		s.append(templateText, tPos2, templateText.length());
		templateText = s.toString();
		resumeCmdParsingFromStart = true;
	}

	// --- Conditional commands
	// -----------------------------------------------------

	// Returns the enabled/disabled state of the condition at level condLevel2.
	private boolean isCondEnabled(int condLevel2) {
		if (condLevel2 < 0) {
			return true;
		}
		return condEnabled[condLevel2];
	}

	// Excludes a text range and returns true, if the current condition is
	// disabled.
	private boolean conditionalExclude(int tPosBegin, int tPosEnd) {
		if (isCondEnabled(condLevel)) {
			return false;
		}
		excludeTemplateRange(tPosBegin, tPosEnd);
		return true;
	}

	// Evaluates a condition expression of a conditional command, by comparing
	// the
	// flags in the expression with the flags in
	// TemplateSpecification.conditionFlags.
	// Returns true the condition is met.
	private boolean evaluateConditionFlags(String flags) {
		if (conditionFlags == null) {
			return false;
		}
		int p = 0;
		while (true) {
			p = skipBlanks(flags, p);
			if (p >= flags.length()) {
				break;
			}
			boolean complement = false;
			if (flags.charAt(p) == '!') {
				complement = true;
				p++;
			}
			p = skipBlanks(flags, p);
			if (p >= flags.length()) {
				break;
			}
			int p0 = p;
			p = skipNonBlanks(flags, p0 + 1);
			String flag = flags.substring(p0, p).toUpperCase();
			if (conditionFlags.contains(flag) ^ complement) {
				return true;
			}
		}
		return false;
	}

	// Processes the if command.
	private void processIfCmd(String parms, int cmdTPosBegin, int cmdTPosEnd)
			throws TemplateSyntaxException {
		excludeTemplateRange(cmdTPosBegin, cmdTPosEnd);
		if (condLevel >= maxCondLevels - 1) {
			throw new TemplateSyntaxException("Too many nested if commands.");
		}
		condLevel++;
		boolean enabled = isCondEnabled(condLevel - 1)
				&& evaluateConditionFlags(parms);
		condEnabled[condLevel] = enabled;
		condPassed[condLevel] = enabled;
	}

	// Processes the elseIf command.
	private void processElseIfCmd(String parms, int cmdTPosBegin, int cmdTPosEnd)
			throws TemplateSyntaxException {
		excludeTemplateRange(cmdTPosBegin, cmdTPosEnd);
		if (condLevel < 0) {
			throw new TemplateSyntaxException("elseIf without matching if.");
		}
		boolean enabled = isCondEnabled(condLevel - 1)
				&& !condPassed[condLevel] && evaluateConditionFlags(parms);
		condEnabled[condLevel] = enabled;
		if (enabled) {
			condPassed[condLevel] = true;
		}
	}

	// Processes the else command.
	private void processElseCmd(String parms, int cmdTPosBegin, int cmdTPosEnd)
			throws TemplateSyntaxException {
		excludeTemplateRange(cmdTPosBegin, cmdTPosEnd);
		if (parms.trim().length() != 0)
			throw new TemplateSyntaxException(
					"Invalid parameters for else command.");
		if (condLevel < 0) {
			throw new TemplateSyntaxException("else without matching if.");
		}
		boolean enabled = isCondEnabled(condLevel - 1)
				&& !condPassed[condLevel];
		condEnabled[condLevel] = enabled;
		if (enabled) {
			condPassed[condLevel] = true;
		}
	}

	// Processes the endIf command.
	private void processEndIfCmd(String parms, int cmdTPosBegin, int cmdTPosEnd)
			throws TemplateSyntaxException {
		excludeTemplateRange(cmdTPosBegin, cmdTPosEnd);
		if (parms.trim().length() != 0) {
			throw new TemplateSyntaxException(
					"Invalid parameters for endIf command.");
		}
		if (condLevel < 0) {
			throw new TemplateSyntaxException("endIf without matching if.");
		}
		condLevel--;
	}

	// ------------------------------------------------------------------------------

	// Associates variable references with blocks.
	private void associateVariablesWithBlocks() {
		int varRefNo = 0;
		int activeBlockNo = 0;
		int nextBlockNo = 1;
		while (varRefNo < varRefTabCnt) {
			VarRefTabRec vrtr = varRefTab[varRefNo];
			int varRefTPos = vrtr.tPosBegin;
			int varNo = vrtr.varNo;
			if (varRefTPos >= blockTab[activeBlockNo].tPosEnd) {
				activeBlockNo = blockTab[activeBlockNo].parentBlockNo;
				continue;
			}
			if (nextBlockNo < blockTabCnt
					&& varRefTPos >= blockTab[nextBlockNo].tPosBegin) {
				activeBlockNo = nextBlockNo;
				nextBlockNo++;
				continue;
			}
			BlockTabRec btr = blockTab[activeBlockNo];
			if (varRefTPos < btr.tPosBegin) {
				throw new Error();
			}
			int blockVarNo = btr.blockVarCnt++;
			if (btr.blockVarCnt > btr.blockVarNoToVarNoMap.length) {
				btr.blockVarNoToVarNoMap = (int[]) resizeArray(
						btr.blockVarNoToVarNoMap, 2 * btr.blockVarCnt);
			}
			btr.blockVarNoToVarNoMap[blockVarNo] = varNo;
			if (btr.firstVarRefNo == -1) {
				btr.firstVarRefNo = varRefNo;
			}
			vrtr.blockNo = activeBlockNo;
			vrtr.blockVarNo = blockVarNo;
			varRefNo++;
		}
	}

	// Parses variable references within the template in the format "${VarName}"
	// .
	private void parseTemplateVariables() throws TemplateSyntaxException {
		int p = 0;
		while (true) {
			p = templateText.indexOf("${", p);
			if (p == -1) {
				break;
			}
			int p0 = p;
			p = templateText.indexOf("}", p);
			if (p == -1) {
				throw new TemplateSyntaxException(
						"Invalid variable reference in template at offset "
								+ p0 + ".");
			}
			p++;
			String varName = templateText.substring(p0 + 2, p - 1).trim();
			if (varName.length() == 0) {
				throw new TemplateSyntaxException(
						"Empty variable name in template at offset " + p0 + ".");
			}
			registerVariableReference(varName, p0, p);
		}
	}

	private void registerVariableReference(String varName, int tPosBegin,
			int tPosEnd) {
		int varNo;
		varNo = lookupVariableName(varName);
		if (varNo == -1) {
			varNo = registerVariable(varName);
		}
		int varRefNo = varRefTabCnt++;
		if (varRefTabCnt > varRefTab.length) {
			varRefTab = (VarRefTabRec[]) resizeArray(varRefTab,
					2 * varRefTabCnt);
		}
		VarRefTabRec vrtr = new VarRefTabRec();
		varRefTab[varRefNo] = vrtr;
		vrtr.tPosBegin = tPosBegin;
		vrtr.tPosEnd = tPosEnd;
		vrtr.varNo = varNo;
	}

	// Returns the variable number of the newly registered variable.
	private int registerVariable(String varName) {
		int varNo = varTabCnt++;
		if (varTabCnt > varTab.length) {
			varTab = (String[]) resizeArray(varTab, 2 * varTabCnt);
		}
		varTab[varNo] = varName;
		varNameToNoMap.put(varName.toUpperCase(), new Integer(varNo));
		return varNo;
	}

	// --- name lookup routines -------------------------------------------

	// Maps variable name to variable number.
	// Returns -1 if the variable name is not found.
	public int lookupVariableName(String varName) {
		Integer varNoWrapper = varNameToNoMap.get(varName.toUpperCase());
		if (varNoWrapper == null) {
			return -1;
		}
		int varNo = varNoWrapper.intValue();
		return varNo;
	}

	// Maps block name to block number.
	// If there are multiple blocks with the same name, the block number of the
	// last
	// registered block with that name is returned.
	// Returns -1 if the block name is not found.
	public int lookupBlockName(String blockName) {
		Integer blockNoWrapper = blockNameToNoMap.get(blockName.toUpperCase());
		if (blockNoWrapper == null) {
			return -1;
		}
		int blockNo = blockNoWrapper.intValue();
		return blockNo;
	}

	// --- general utility routines ---------------------------------------

	// Reallocates an array with a new size and copies the contents
	// of the old array to the new array.
	public static Object resizeArray(Object oldArray, int newSize) {
		int oldSize = java.lang.reflect.Array.getLength(oldArray);
		Class<?> elementType = oldArray.getClass().getComponentType();
		Object newArray = java.lang.reflect.Array.newInstance(elementType,
				newSize);
		int preserveLength = Math.min(oldSize, newSize);
		if (preserveLength > 0) {
			System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
		}
		return newArray;
	}

	// Skips blanks (white space) in string s starting at position p.
	private static int skipBlanks(String s, int p) {
		while (p < s.length() && Character.isWhitespace(s.charAt(p))) {
			p++;
		}
		return p;
	}

	// Skips non-blanks (no-white space) in string s starting at position p.
	private static int skipNonBlanks(String s, int p) {
		while (p < s.length() && !Character.isWhitespace(s.charAt(p))) {
			p++;
		}
		return p;
	}

	// Returns true if string s is blank (white space) from position p to the
	// end.
	public static boolean isRestOfStringBlank(String s, int p) {
		return skipBlanks(s, p) >= s.length();
	}

} // End class MiniTemplatorParser
