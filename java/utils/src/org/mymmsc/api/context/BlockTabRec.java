package org.mymmsc.api.context;

public class BlockTabRec { // block table record structure
	String blockName; // block name
	int nextWithSameName; // block no of next block with same name or -1
	// (blocks are backward linked related to their
	// position within the template)
	int tPosBegin; // template position of begin of block
	int tPosContentsBegin; // template pos of begin of block contents
	int tPosContentsEnd; // template pos of end of block contents
	int tPosEnd; // template position of end of block
	int nestingLevel; // block nesting level
	int parentBlockNo; // block no of parent block
	boolean definitionIsOpen; // true while BEGIN processed but no
	// END
	int blockVarCnt; // number of variables in block
	int[] blockVarNoToVarNoMap; // maps block variable numbers to variable
	// numbers
	int firstVarRefNo; // variable reference no of first variable of this
	// block or -1
	boolean dummy;
} // true if this is a dummy block that will never be included in the output

