package org.mymmsc.api.context;

class BlockDynTabRec { // block dynamic data table record
	// structure
	int instances; // number of instances of this block
	int firstBlockInstNo; // block instance no of first instance of this
	// block or -1
	int lastBlockInstNo; // block instance no of last instance of this block
	// or -1
	int currBlockInstNo;
} // current block instance no, used during generation of output file

