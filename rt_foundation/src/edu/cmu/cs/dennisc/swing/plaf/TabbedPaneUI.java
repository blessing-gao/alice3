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
package edu.cmu.cs.dennisc.swing.plaf;

/**
 * @author Dennis Cosgrove
 */
class CloseIcon implements javax.swing.Icon {
	private static final java.awt.Color BASE_COLOR = new java.awt.Color( 127, 63, 63 );
	private static final java.awt.Color HIGHLIGHT_COLOR = edu.cmu.cs.dennisc.awt.ColorUtilities.shiftHSB( BASE_COLOR, 0, 0, +0.25f );
	private static final java.awt.Color PRESS_COLOR = edu.cmu.cs.dennisc.awt.ColorUtilities.shiftHSB( BASE_COLOR, 0, 0, -0.125f );

	private boolean isFilled = false;
	private boolean isHighlighted = false;
	private boolean isPressed = false;

	public boolean isFilled() {
		return this.isFilled;
	}
	public void setFilled( boolean isFilled ) {
		this.isFilled = isFilled;
	}

	public boolean isHighlighted() {
		return this.isHighlighted;
	}
	public void setHighlighted( boolean isHighlighted ) {
		if( this.isHighlighted != isHighlighted ) {
			this.isHighlighted = isHighlighted;
		}
	}
	public boolean isPressed() {
		return this.isPressed;
	}
	public void setPressed( boolean isPressed ) {
		if( this.isPressed != isPressed ) {
			this.isPressed = isPressed;
		}
	}

	public int getIconWidth() {
		return 14;
	}
	public int getIconHeight() {
		return getIconWidth();
	}

	public void paintIcon( java.awt.Component c, java.awt.Graphics g, int x0, int y0 ) {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;

		float size = Math.min( getIconWidth(), getIconWidth() ) * 0.9f;

		float w = size;
		float h = size * 0.25f;
		float xC = -w * 0.5f;
		float yC = -h * 0.5f;
		java.awt.geom.RoundRectangle2D.Float rr = new java.awt.geom.RoundRectangle2D.Float( xC, yC, w, h, h, h );

		java.awt.geom.Area area0 = new java.awt.geom.Area( rr );
		java.awt.geom.Area area1 = new java.awt.geom.Area( rr );

		java.awt.geom.AffineTransform m0 = new java.awt.geom.AffineTransform();
		m0.rotate( Math.PI * 0.25 );
		area0.transform( m0 );

		java.awt.geom.AffineTransform m1 = new java.awt.geom.AffineTransform();
		m1.rotate( Math.PI * 0.75 );
		area1.transform( m1 );

		area0.add( area1 );

		java.awt.geom.AffineTransform m = new java.awt.geom.AffineTransform();
		m.translate( x0 + getIconWidth() / 2, y0 + getIconWidth() / 2 );
		area0.transform( m );

		java.awt.Paint prevPaint = g2.getPaint();
		g2.setRenderingHint( java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
		if( isHighlighted() ) {
			if( isPressed() ) {
				g2.setPaint( PRESS_COLOR );
			} else {
				g2.setPaint( HIGHLIGHT_COLOR );
			}
		} else {
			g2.setPaint( java.awt.Color.WHITE );
		}
		if( this.isFilled ) {
			g2.fill( area0 );
			g2.setPaint( java.awt.Color.BLACK );
		} else {
			g2.setPaint( java.awt.Color.DARK_GRAY );
		}
		g2.draw( area0 );
		g2.setPaint( prevPaint );
	}
}

public class TabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
	private static final int NORTH_AREA_PAD = 4;
	private static final int EAST_TAB_PAD = 48;
	private java.awt.Stroke selectedStroke = new java.awt.BasicStroke( 3.0f );
	private java.awt.Stroke normalStroke = new java.awt.BasicStroke( 1.0f );

	private CloseIcon closeIcon = new CloseIcon();

	@Override
	protected void installDefaults() {
		super.installDefaults();
		java.awt.Font normalFont = this.tabPane.getFont();
		this.tabPane.setFont( normalFont.deriveFont( java.awt.Font.ITALIC ) );

		this.tabAreaInsets.set( 2, 0, 0, 0 );
		this.tabInsets.set( 2, 2, 2, 2 );
		this.selectedTabPadInsets.set( 0, 0, 0, 0 );
		this.contentBorderInsets.set( 3, 3, 0, 0 );
		//this.lightHighlight = java.awt.Color.YELLOW;
	}

	//	@Override
	//	protected java.awt.LayoutManager createLayoutManager() {
	//		class MyLayoutManager extends TabbedPaneLayout {
	//			
	//		}
	//		return super.createLayoutManager();
	//	}
	private java.awt.geom.GeneralPath createPath( int width, int height ) {
		float x0 = width - EAST_TAB_PAD / 2;
		float x1 = width + EAST_TAB_PAD;
		//x0 += EAST_TAB_PAD;
		float cx0 = x0 + EAST_TAB_PAD * 0.75f;
		float cx1 = x0;

		float y0 = NORTH_AREA_PAD;
		float y1 = height + this.contentBorderInsets.top;
		float cy0 = y0;
		float cy1 = y1;

		float xA = height * 0.4f;
		float yA = xA;
		java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

		path.moveTo( x1, y1 );
		path.curveTo( cx1, cy1, cx0, cy0, x0, y0 );
		path.lineTo( xA, y0 );
		path.quadTo( 0, y0, 0, yA );
		path.lineTo( 0, y1 );
		return path;
	}
	@Override
	protected void paintTabBorder( java.awt.Graphics g, int tabPlacement, int tabIndex, int x, int y, int width, int height, boolean isSelected ) {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
		java.awt.Color prev = g.getColor();
		try {
			if( isSelected ) {
				g.setColor( this.lightHighlight );
				g2.setStroke( this.selectedStroke );
				g2.setClip( x, y, width + EAST_TAB_PAD, height + this.contentBorderInsets.top );
			} else {
				g.setColor( this.shadow );
				g2.setStroke( this.normalStroke );
			}
			java.awt.geom.GeneralPath path = createPath( width, height );
			g2.translate( x, y );
			g2.draw( path );
			g2.translate( -x, -y );
		} finally {
			g.setColor( prev );
		}
	}
	@Override
	protected void paintTabBackground( java.awt.Graphics g, int tabPlacement, int tabIndex, int x, int y, int width, int height, boolean isSelected ) {
		java.awt.Color prev = g.getColor();
		try {
			int tabRolloverIndex = this.getRolloverTab();
			java.awt.Component component = this.tabPane.getComponentAt( tabIndex );
			java.awt.Color color = component.getBackground();
			if( isSelected ) {
				//pass
			} else {
				color = color.darker();
				if( tabIndex == tabRolloverIndex ) {
					//pass
				} else {
					color = color.darker();
				}
			}
			g.setColor( color );
			java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
			//g2.setClip( x-100, y, width+200, height + this.contentBorderInsets.top );
			java.awt.geom.GeneralPath path = createPath( width, height );
			g2.translate( x, y );
			g2.fill( path );
			g2.translate( -x, -y );
			if( isCloseButtonDesiredAt( tabIndex ) ) {
				if( this.getRolloverTab() == tabIndex ) {
					this.closeIcon.setFilled( true );
				} else {
					this.closeIcon.setFilled( false );
				}
				this.closeIcon.paintIcon( this.tabPane, g2, x + width - EAST_TAB_PAD / 2, y + height / 2 - 4 );
			}
		} finally {
			g.setColor( prev );
		}
	}
	@Override
	protected void paintTabArea( java.awt.Graphics g, int tabPlacement, int selectedIndex ) {
		java.awt.Color prev = g.getColor();
		try {
			//g.setColor( this.darkShadow );
			java.awt.Color color = javax.swing.UIManager.getColor( "TabbedPane.contentAreaColor" );
			g.setColor( color );
			java.awt.Rectangle bounds = g.getClipBounds();
			g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
		} finally {
			g.setColor( prev );
		}
		super.paintTabArea( g, tabPlacement, selectedIndex );
	}
	@Override
	protected void paintFocusIndicator( java.awt.Graphics g, int tabPlacement, java.awt.Rectangle[] rects, int tabIndex, java.awt.Rectangle iconRect, java.awt.Rectangle textRect, boolean isSelected ) {
	}

	@Override
	protected void setRolloverTab( int nextRollOverTab ) {
		int prevRollOverTab = this.getRolloverTab();
		super.setRolloverTab( nextRollOverTab );
		if( prevRollOverTab != nextRollOverTab ) {
			this.tabPane.repaint();
		}
	}

	class CloseIconMouseAdapter implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
		private java.awt.Rectangle buffer = new java.awt.Rectangle();

		private int getTabIndex( java.awt.event.MouseEvent e ) {
			return TabbedPaneUI.this.tabForCoordinate( TabbedPaneUI.this.tabPane, e.getX(), e.getY() );
		}
		private boolean isWithinCloseIcon( java.awt.event.MouseEvent e ) {
			if( TabbedPaneUI.this.tabPane.isEnabled() ) {
				int index = getTabIndex( e );
				TabbedPaneUI.this.setRolloverTab( index );
				if( index >= 0 ) {
					if( TabbedPaneUI.this.isCloseButtonDesiredAt( index ) ) {
						TabbedPaneUI.this.getTabBounds( index, buffer );
						int xMin = buffer.x + buffer.width - EAST_TAB_PAD / 2;
						int xMax = xMin + closeIcon.getIconWidth();
						int yMin = buffer.y + buffer.height / 2 - 4;
						int yMax = yMin + closeIcon.getIconHeight();
						return (xMin < e.getX() && e.getX() < xMax) && (yMin < e.getY() && e.getY() < yMax);
					}
				}
			}
			return false;
		}

		private void updateRollover( java.awt.event.MouseEvent e ) {
			closeIcon.setHighlighted( isWithinCloseIcon( e ) );
//			tabPane.repaint();
		}

		public void mouseEntered( java.awt.event.MouseEvent e ) {
			updateRollover( e );
		}
		public void mouseExited( java.awt.event.MouseEvent e ) {
		}
		public void mousePressed( java.awt.event.MouseEvent e ) {
			closeIcon.setPressed( isWithinCloseIcon( e ) );
			tabPane.repaint();
		}
		public void mouseReleased( java.awt.event.MouseEvent e ) {
			closeIcon.setPressed( false );
			if( isWithinCloseIcon( e ) ) {
				closeTab( getTabIndex( e ), e );
			} else {
				tabPane.repaint();
			}
		}
		public void mouseClicked( java.awt.event.MouseEvent e ) {
		}
		public void mouseMoved( java.awt.event.MouseEvent e ) {
			updateRollover( e );
		}
		public void mouseDragged( java.awt.event.MouseEvent e ) {
		}
	}

	protected boolean isCloseButtonDesiredAt( int index ) {
		return true;
	}
	protected void closeTab( int index, java.awt.event.MouseEvent e ) {
		tabPane.remove( index );
	}

	private CloseIconMouseAdapter closeIconMouseAdapter = new CloseIconMouseAdapter();

	//	@Override
	//	protected java.awt.event.MouseListener createMouseListener() {
	//		return mouseAdapter;
	//	}

	@Override
	protected void installListeners() {
		super.installListeners();
		this.tabPane.addMouseListener( closeIconMouseAdapter );
		this.tabPane.addMouseMotionListener( closeIconMouseAdapter );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		this.tabPane.removeMouseListener( closeIconMouseAdapter );
		this.tabPane.removeMouseMotionListener( closeIconMouseAdapter );
	}

	@Override
	protected void paintContentBorderTopEdge( java.awt.Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
		java.awt.Color prev = g.getColor();
		try {
			if( selectedIndex >= 0 ) {
				java.awt.Rectangle boundsTab = getTabBounds( this.tabPane, selectedIndex );
				int xA = boundsTab.x;
				int xB = boundsTab.x + boundsTab.width + EAST_TAB_PAD / 2;

				java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
				g2.setStroke( this.selectedStroke );
				g.setColor( this.lightHighlight );
				g.fillRect( x, y, xA - x, h );
				g.fillRect( xB, y, w - xB, h );

				java.awt.Component component = this.tabPane.getComponentAt( selectedIndex );
				java.awt.Color color = component.getBackground();
				g.setColor( color );
				g.fillRect( xA, y, xB - xA, h );
			}
		} finally {
			g.setColor( prev );
		}
	}
	@Override
	protected void paintContentBorderBottomEdge( java.awt.Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}
	@Override
	protected void paintContentBorderLeftEdge( java.awt.Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
		if( this.tabPane.getTabCount() > 0 ) {
			super.paintContentBorderLeftEdge( g, tabPlacement, selectedIndex, x, y, w, h );
		}
	}
	@Override
	protected void paintContentBorderRightEdge( java.awt.Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h ) {
	}

	@Override
	protected int calculateTabHeight( int tabPlacement, int tabIndex, int fontHeight ) {
		int rv = super.calculateTabHeight( tabPlacement, tabIndex, fontHeight );
		return rv + NORTH_AREA_PAD;
	}
	@Override
	protected int calculateTabWidth( int tabPlacement, int tabIndex, java.awt.FontMetrics metrics ) {
		int rv = super.calculateTabWidth( tabPlacement, tabIndex, metrics );
		//rv += EAST_TAB_PAD;
		rv += EAST_TAB_PAD / 2;
		return rv;
	}
	@Override
	protected void paintText( java.awt.Graphics g, int tabPlacement, java.awt.Font font, java.awt.FontMetrics metrics, int tabIndex, String title, java.awt.Rectangle textRect, boolean isSelected ) {
		java.awt.Color prev = g.getColor();
		try {
			g.setColor( java.awt.Color.BLACK );
			int x = textRect.x;
			x -= EAST_TAB_PAD / 4;
			g.drawString( title, x, textRect.y + textRect.height );
			//super.paintText( g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected );
		} finally {
			g.setColor( prev );
		}
	}

	@Override
	protected void paintIcon( java.awt.Graphics g, int tabPlacement, int tabIndex, javax.swing.Icon icon, java.awt.Rectangle iconRect, boolean isSelected ) {
		if( icon != null ) {
			int x = iconRect.x;
			x -= EAST_TAB_PAD / 4;
			int y = iconRect.y;
			y += NORTH_AREA_PAD;
			java.awt.Color prev = g.getColor();
			try {
				icon.paintIcon( this.tabPane, g, x, y );
			} finally {
				g.setColor( prev );
			}
		} else {
			super.paintIcon( g, tabPlacement, tabIndex, icon, iconRect, isSelected );
		}
	}
}
