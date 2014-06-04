// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   Translator.java

package org.mymmsc.api.security;

public interface Translator {

	public abstract int getEncodedBlockSize();

	public abstract int encode(byte abyte0[], int i, int j, byte abyte1[], int k);

	public abstract int getDecodedBlockSize();

	public abstract int decode(byte abyte0[], int i, int j, byte abyte1[], int k);
}
