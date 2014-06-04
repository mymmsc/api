/**
 * 
 */
package org.mymmsc.api.context;

/**
 * @author WangFeng
 * 
 */
class BlockInstTabRec { // block instance table record
	// structure
	int blockNo; // block number
	int instanceLevel; // instance level of this block
	// InstanceLevel is an instance counter per block.
	// (In contrast to blockInstNo, which is an instance counter over the
	// instances of all blocks)
	int parentInstLevel; // instance level of parent block
	int nextBlockInstNo; // pointer to next instance of this block or -1
	// Forward chain for instances of same block.
	String[] blockVarTab;
} // block instance variables

