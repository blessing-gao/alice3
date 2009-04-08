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
package org.alice.ide.initializer;

class ListPropertyComboBoxModel<E> extends javax.swing.AbstractListModel implements javax.swing.ComboBoxModel {
	private edu.cmu.cs.dennisc.property.ListProperty< E > listProperty;
	private Object selectedItem;
	public ListPropertyComboBoxModel( edu.cmu.cs.dennisc.property.ListProperty< E > listProperty ) {
		this.listProperty = listProperty;
		this.listProperty.addListPropertyListener( new edu.cmu.cs.dennisc.property.event.ListPropertyListener< E >() {

			private int n;
			
			public void adding( edu.cmu.cs.dennisc.property.event.AddListPropertyEvent< E > e ) {
			}
			public void added( edu.cmu.cs.dennisc.property.event.AddListPropertyEvent< E > e ) {
				int i = e.getStartIndex();
				ListPropertyComboBoxModel.this.fireIntervalAdded( e.getSource(), i, i+e.getElements().size()-1 );
			}


			public void clearing( edu.cmu.cs.dennisc.property.event.ClearListPropertyEvent< E > e ) {
				n = e.getTypedSource().size();
			}
			public void cleared( edu.cmu.cs.dennisc.property.event.ClearListPropertyEvent< E > e ) {
				ListPropertyComboBoxModel.this.fireContentsChanged( e.getSource(), 0, n );
			}


			public void removing( edu.cmu.cs.dennisc.property.event.RemoveListPropertyEvent< E > e ) {
			}
			public void removed( edu.cmu.cs.dennisc.property.event.RemoveListPropertyEvent< E > e ) {
				int i = e.getStartIndex();
				ListPropertyComboBoxModel.this.fireIntervalRemoved( e.getSource(), i, i+e.getElements().size()-1 );
			}


			public void setting( edu.cmu.cs.dennisc.property.event.SetListPropertyEvent< E > e ) {
				n = e.getTypedSource().size();
			}
			public void set( edu.cmu.cs.dennisc.property.event.SetListPropertyEvent< E > e ) {
				int i = e.getStartIndex();
				ListPropertyComboBoxModel.this.fireContentsChanged( e.getSource(), i, i+e.getElements().size()-1 );
			}
			
		} );
	}
	public Object getElementAt( int index ) {
		return this.listProperty.get( index );
	}
	public int getSize() {
		return this.listProperty.size();
	}
	public Object getSelectedItem() {
		return this.selectedItem;
	}
	public void setSelectedItem( Object selectedItem ) {
		this.selectedItem = selectedItem;
	}
}

/**
 * @author Dennis Cosgrove
 */
public class ArrayInitializerPane extends AbstractInitializerPane {
	class AddItemOperation extends org.alice.ide.operations.AbstractActionOperation {
		public AddItemOperation() {
			this.putValue( javax.swing.Action.NAME, "add" );
		}
		public void perform( zoot.ActionContext actionContext ) {
			ArrayInitializerPane.this.arrayInstanceCreation.expressions.add( ArrayInitializerPane.this.createDefaultInitializer() );
		}
	}

	class RemoveItemOperation extends org.alice.ide.operations.AbstractActionOperation {
		public RemoveItemOperation() {
			this.putValue( javax.swing.Action.NAME, "remove" );
		}
		public void perform( zoot.ActionContext actionContext ) {
			int index = ArrayInitializerPane.this.list.getSelectedIndex();
			if( index >= 0 ) {
				ArrayInitializerPane.this.arrayInstanceCreation.expressions.remove( index );
			}
		}
	}

	abstract class AbstractMoveItemOperation extends org.alice.ide.operations.AbstractActionOperation {
		protected void swapWithNext( int index ) {
			ArrayInitializerPane.this.swapWithNext( index );
		}
	}
	
	class MoveItemUpOperation extends AbstractMoveItemOperation {
		public MoveItemUpOperation() {
			this.putValue( javax.swing.Action.NAME, "move up" );
		}
		public void perform( zoot.ActionContext actionContext ) {
			this.swapWithNext( ArrayInitializerPane.this.list.getSelectedIndex()-1 );
		}
	}

	class MoveItemDownOperation extends AbstractMoveItemOperation {
		public MoveItemDownOperation() {
			this.putValue( javax.swing.Action.NAME, "move down" );
		}
		public void perform( zoot.ActionContext actionContext ) {
			this.swapWithNext( ArrayInitializerPane.this.list.getSelectedIndex() );
		}
	}

	class ItemSelectionOperation extends org.alice.ide.operations.AbstractItemSelectionOperation< edu.cmu.cs.dennisc.alice.ast.Expression > {
		public ItemSelectionOperation( javax.swing.ComboBoxModel comboBoxModel ) {
			super( comboBoxModel );
		}
		public void performSelectionChange( zoot.ItemSelectionContext< edu.cmu.cs.dennisc.alice.ast.Expression > context ) {
			ArrayInitializerPane.this.handleSelectionChange( context );
		}
	}

	class ExpressionList extends zoot.ZList< edu.cmu.cs.dennisc.alice.ast.Expression > {
		public ExpressionList( zoot.ItemSelectionOperation< edu.cmu.cs.dennisc.alice.ast.Expression > itemSelectionOperation ) {
			super( itemSelectionOperation );
			this.setOpaque( true );
			this.setBackground( java.awt.Color.WHITE );
		}
		@Override
		public void updateUI() {
			setUI( new edu.cmu.cs.dennisc.swing.plaf.ListUI< edu.cmu.cs.dennisc.alice.ast.Expression >() {
				@Override
				protected javax.swing.AbstractButton createComponentFor( int index, edu.cmu.cs.dennisc.alice.ast.Expression e ) {
					FauxItem rv = new FauxItem( index, e );
					edu.cmu.cs.dennisc.alice.ast.AbstractType type = ArrayInitializerPane.this.arrayInstanceCreation.arrayType.getValue();
					if( type != null && type.isArray() ) {
						rv.handleTypeChange( type.getComponentType() );
					}
					return rv;
				}
				@Override
				protected void updateIndex( javax.swing.AbstractButton button, int index ) {
					FauxItem fauxItem = (FauxItem)button;
					fauxItem.setIndex( index );
				}
			} );
		}
	}

	private ExpressionList list;
	private edu.cmu.cs.dennisc.alice.ast.ArrayInstanceCreation arrayInstanceCreation;

	private zoot.ZButton addButton = new zoot.ZButton( new AddItemOperation() );
	private zoot.ZButton removeButton = new zoot.ZButton( new RemoveItemOperation() );
	private zoot.ZButton moveUpButton = new zoot.ZButton( new MoveItemUpOperation() );
	private zoot.ZButton moveDownButton = new zoot.ZButton( new MoveItemDownOperation() );

	public ArrayInitializerPane( edu.cmu.cs.dennisc.alice.ast.ArrayInstanceCreation arrayInstanceCreation ) {
		this.arrayInstanceCreation = arrayInstanceCreation;
		this.arrayInstanceCreation.arrayType.addPropertyListener( new edu.cmu.cs.dennisc.property.event.PropertyListener() {
			public void propertyChanging( edu.cmu.cs.dennisc.property.event.PropertyEvent e ) {
			}
			public void propertyChanged( edu.cmu.cs.dennisc.property.event.PropertyEvent e ) {
				ArrayInitializerPane.this.handleTypeChange( e );
			}
		} );
		ListPropertyComboBoxModel< edu.cmu.cs.dennisc.alice.ast.Expression > comboBoxModel = new ListPropertyComboBoxModel< edu.cmu.cs.dennisc.alice.ast.Expression >( arrayInstanceCreation.expressions );
		this.list = new ExpressionList( new ItemSelectionOperation( comboBoxModel ) );
		this.setLayout( new java.awt.BorderLayout( 8, 0 ) );
		this.updateButtons();
		
		swing.GridBagPane buttonPane = new swing.GridBagPane();
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		buttonPane.add( this.addButton, gbc );
		buttonPane.add( this.removeButton, gbc );
		gbc.insets.top = 8;
		buttonPane.add( this.moveUpButton, gbc );
		gbc.insets.top = 0;
		buttonPane.add( this.moveDownButton, gbc );
		gbc.weighty = 1.0;
		buttonPane.add( javax.swing.Box.createGlue(), gbc );

		this.add( buttonPane, java.awt.BorderLayout.EAST );

		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane( this.list ) {
			@Override
			public java.awt.Dimension getPreferredSize() {
				return edu.cmu.cs.dennisc.awt.DimensionUtilties.constrainToMinimumSize( super.getPreferredSize(), 240, 180 );
			}
		};
		scrollPane.setBorder( null );
		this.add( scrollPane, java.awt.BorderLayout.CENTER );
	}

	private edu.cmu.cs.dennisc.alice.ast.Expression createDefaultInitializer() {
		edu.cmu.cs.dennisc.alice.ast.AbstractType type = this.arrayInstanceCreation.arrayType.getValue();
		if( type != null && type.isArray() ) {
			edu.cmu.cs.dennisc.alice.ast.AbstractType componentType = type.getComponentType();
			if( componentType == edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInJava.BOOLEAN_OBJECT_TYPE ) {
				return new edu.cmu.cs.dennisc.alice.ast.BooleanLiteral( false );
			} else if( componentType == edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInJava.DOUBLE_OBJECT_TYPE ) {
				return new edu.cmu.cs.dennisc.alice.ast.DoubleLiteral( 0.0 );
			} else if( componentType == edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInJava.INTEGER_OBJECT_TYPE ) {
				return new edu.cmu.cs.dennisc.alice.ast.IntegerLiteral( 0 );
			} else {
				return new edu.cmu.cs.dennisc.alice.ast.NullLiteral();
			}
		} else {
			return null;
		}
	}

	private void updateButtons() {
		edu.cmu.cs.dennisc.alice.ast.AbstractType type = this.arrayInstanceCreation.arrayType.getValue();
		boolean isTypeValid = type != null && type.isArray();
		this.addButton.setEnabled( isTypeValid );
		int index = this.list.getSelectedIndex();
		final int N = this.list.getModel().getSize();
		this.removeButton.setEnabled( isTypeValid && index != -1 );
		this.moveUpButton.setEnabled( isTypeValid && index > 0 );
		this.moveDownButton.setEnabled( isTypeValid && index >= 0 && index < N-1 );
	}
	private void handleSelectionChange( zoot.ItemSelectionContext< edu.cmu.cs.dennisc.alice.ast.Expression > context ) {
		this.updateButtons();
	}
	private void swapWithNext( int index ) {
		if( index >= 0 ) {
			edu.cmu.cs.dennisc.alice.ast.Expression expression0 = this.arrayInstanceCreation.expressions.get( index );
			edu.cmu.cs.dennisc.alice.ast.Expression expression1 = this.arrayInstanceCreation.expressions.get( index+1 );
			this.arrayInstanceCreation.expressions.set( index, expression1, expression0 );
		}
	}
	public void handleTypeChange( edu.cmu.cs.dennisc.property.event.PropertyEvent e ) {
		edu.cmu.cs.dennisc.alice.ast.AbstractType type = this.arrayInstanceCreation.arrayType.getValue();
		boolean isTypeValid = type != null && type.isArray();
		if( isTypeValid ) {
			edu.cmu.cs.dennisc.alice.ast.AbstractType componentType = type.getComponentType();
			for( java.awt.Component component : this.list.getComponents() ) {
				if( component instanceof FauxItem ) {
					FauxItem fauxItem = (FauxItem)component;
					fauxItem.handleTypeChange( componentType );
				}
			}
			this.updateButtons();
		}
	}
	@Override
	public edu.cmu.cs.dennisc.alice.ast.Expression getInitializer() {
		return this.arrayInstanceCreation;
	}
//	@Override
//	public edu.cmu.cs.dennisc.alice.ast.Expression getInitializer() {
//		javax.swing.ListModel model = this.list.getModel();
//		final int N = model.getSize();
//		edu.cmu.cs.dennisc.alice.ast.Expression[] expressions = new edu.cmu.cs.dennisc.alice.ast.Expression[ N ];
//		for( int i=0; i<N; i++ ) {
//			expressions[ i ] = (edu.cmu.cs.dennisc.alice.ast.Expression)model.getElementAt( i );
//		}
//		return new edu.cmu.cs.dennisc.alice.ast.ArrayInstanceCreation( this.arrayType, new Integer[] { expressions.length }, expressions );
//	}
}

