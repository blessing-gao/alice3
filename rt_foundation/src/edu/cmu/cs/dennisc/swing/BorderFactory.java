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
package edu.cmu.cs.dennisc.swing;

/**
 * @author Dennis Cosgrove
 */
public class BorderFactory {
	private static java.util.Map< java.awt.Insets, javax.swing.border.Border > outlinedBorderMap = new java.util.HashMap< java.awt.Insets, javax.swing.border.Border >();
	public static javax.swing.border.Border createOutlinedBorder( int top, int left, int bottom, int right, java.awt.Color color ) {
		java.awt.Insets insets = new java.awt.Insets( top, left, bottom, right );
		javax.swing.border.Border rv = BorderFactory.outlinedBorderMap.get( insets );
		if( rv != null ) {
			//pass
		} else {
			rv = new edu.cmu.cs.dennisc.swing.border.OutlinedBorder( insets, color );
			BorderFactory.outlinedBorderMap.put( insets, rv );
		}
		return rv; 
	}
}
