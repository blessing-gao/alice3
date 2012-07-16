/*
 * Copyright (c) 2006-2010, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may 
 *    "Alice" appear in their name, without prior written permission of 
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software 
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is 
 *    contributed by Electronic Arts Inc. and may be used for personal, 
 *    non-commercial, and academic use only. Redistributions of any program 
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in 
 *    The Alice 3.0 Art Gallery License.
 * 
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.  
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A 
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO 
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package test;

abstract class AbstractSmallIcon implements javax.swing.Icon {
	private final static int SIZE = org.alice.stageide.gallerybrowser.ResourceManager.NULL_SMALL_ICON.getIconWidth();
	private final static int PAD = 2;
	
	public int getIconWidth() {
		return SIZE;
	}
	public int getIconHeight() {
		return SIZE;
	}
	protected abstract void paintIcon( java.awt.Graphics2D g2, int width, int height, java.awt.Paint fillPaint, java.awt.Paint drawPaint );
	public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
		int xOffset = x+PAD;
		int yOffset = y+PAD;
		int width = this.getIconWidth()-PAD-PAD;
		int height = this.getIconHeight()-PAD-PAD;
		
		java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
		java.awt.Paint prevPaint = g2.getPaint();
		Object prevAntialiasing = g2.getRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING );
		g2.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
		g2.translate( xOffset, yOffset );
		this.paintIcon( g2, width, height, java.awt.Color.LIGHT_GRAY, java.awt.Color.BLACK );
		g2.translate( -xOffset, -yOffset );
		g2.setPaint( prevPaint );
		g2.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, prevAntialiasing );
	}
}

class TextIcon extends AbstractSmallIcon {
	private static final java.awt.Font font = new java.awt.Font( null, java.awt.Font.ITALIC, 20 );
	@Override
	protected void paintIcon( java.awt.Graphics2D g2, int width, int height, java.awt.Paint fillPaint, java.awt.Paint drawPaint ) {
		g2.setFont( font );
		g2.setPaint( fillPaint );
		edu.cmu.cs.dennisc.java.awt.GraphicsUtilities.drawCenteredText( g2, "a", 0, 0, width, height );
	}
}

/**
 * @author Dennis Cosgrove
 */
public class TestIcon extends org.lgna.croquet.simple.SimpleApplication {
	public static void main( String[] args ) {
		TestCroquet testCroquet = new TestCroquet();
		testCroquet.initialize( args );

		javax.swing.Icon icon = new TextIcon();
		org.lgna.croquet.components.Label label = new org.lgna.croquet.components.Label( icon );
		testCroquet.getFrame().getContentPanel().addCenterComponent( label );
		testCroquet.getFrame().setDefaultCloseOperation( org.lgna.croquet.components.Frame.DefaultCloseOperation.EXIT );
		testCroquet.getFrame().pack();
		testCroquet.getFrame().setVisible( true );
	}
}
