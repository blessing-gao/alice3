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

package org.lgna.croquet;

/**
 * @author Dennis Cosgrove
 */
public class CascadeMenuItemPrepModel<T> extends AbstractMenuModel {
	public static class CascadeMenuPrepModelResolver<T> implements org.lgna.croquet.resolvers.CodableResolver< CascadeMenuItemPrepModel<T> > {
		private final CascadeMenuItemPrepModel<T> model;
		public CascadeMenuPrepModelResolver( CascadeMenuItemPrepModel<T> model ) {
			this.model = model;
		}
		public CascadeMenuPrepModelResolver( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
			org.lgna.croquet.resolvers.CodableResolver<Cascade<T>> resolver = binaryDecoder.decodeBinaryEncodableAndDecodable();
			Cascade<T> model = resolver.getResolved();
			this.model = model.getMenuItemPrepModel();
		}
		public void encode( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder ) {
			org.lgna.croquet.resolvers.CodableResolver<Cascade<T>> resolver = this.model.cascade.getCodableResolver();
			binaryEncoder.encode( resolver );
		}
		public CascadeMenuItemPrepModel<T> getResolved() {
			return this.model;
		}
	}
	private final Cascade<T> cascade;
	/*package-private*/ CascadeMenuItemPrepModel( Cascade<T> cascade ) {
		super( java.util.UUID.fromString( "a6d47082-8859-4b7c-b654-37e928aa67ed" ), cascade.getPopupPrepModel().getClass() );
		assert cascade != null;
		this.cascade = cascade;
	}
	public Cascade<T> getCompletionModel() {
		return this.cascade;
	}
	@Override
	protected CascadeMenuPrepModelResolver<T> createCodableResolver() {
		return new CascadeMenuPrepModelResolver<T>( this );
	}
	public void handleMenuSelectionStateChanged( javax.swing.MenuElement menuElement ) {
		if( menuElement instanceof javax.swing.JMenu ) {
			javax.swing.JMenu jMenu = (javax.swing.JMenu)menuElement;
			org.lgna.croquet.components.MenuItemContainer menuItemContainer = (org.lgna.croquet.components.MenuItemContainer)org.lgna.croquet.components.Component.lookup( jMenu );
			final org.lgna.croquet.cascade.RtRoot< T > rtRoot = new org.lgna.croquet.cascade.RtRoot< T >( this.getCompletionModel().getRoot(), null );
			if( rtRoot.isGoodToGo() ) {
				throw new RuntimeException( "todo" );
			} else {
				final org.lgna.croquet.history.CascadePopupPrepStep< T > prepStep = org.lgna.croquet.history.TransactionManager.addCascadePopupPrepStep( cascade.getPopupPrepModel(), null );			
				jMenu.getPopupMenu().addComponentListener( new java.awt.event.ComponentListener() {
					public void componentShown( java.awt.event.ComponentEvent e ) {
					}
					public void componentMoved( java.awt.event.ComponentEvent e ) {
					}
					public void componentResized( java.awt.event.ComponentEvent e ) {
						org.lgna.croquet.history.TransactionManager.firePopupMenuResized( prepStep );
					}
					public void componentHidden( java.awt.event.ComponentEvent e ) {
					}
				} );
				jMenu.getPopupMenu().addPopupMenuListener( rtRoot.createPopupMenuListener( menuItemContainer ) );
				this.cascade.prologue();
			}
		}
	}
}
