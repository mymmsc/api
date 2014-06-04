package org.mymmsc.api.context;

public class VarRefTabRec { // variable reference table record
	// structure
	int varNo; // variable no
	int tPosBegin; // template position of begin of variable reference
	int tPosEnd; // template position of end of variable reference
	int blockNo; // block no of the (innermost) block that contains this
	// variable reference
	int blockVarNo;
} // block variable no. Index into BlockInstTab.BlockVarTab

