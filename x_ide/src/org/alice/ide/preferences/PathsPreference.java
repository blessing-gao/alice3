/*
 * Copyright (c) 2006-2009, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */
package org.alice.ide.preferences;

/**
 * @author Dennis Cosgrove
 */
public class PathsPreference extends edu.cmu.cs.dennisc.preference.ListPreference< String > {
	public PathsPreference() {
		super( new java.util.LinkedList<String>() );
	}
	
	public void clear() {
		javax.swing.JOptionPane.showMessageDialog( null, "todo" );
	}

	@Override
	protected int getItemVersion() {
		return 1;
	}
	@Override
	protected String decode( int itemVersion, edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder) {
		if( itemVersion == 1 ) {
			return binaryDecoder.decodeString();
		} else {
			return null;
		}
	}
	@Override
	protected void encode( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder, String item ) {
		binaryEncoder.encode(item);
	}
}
