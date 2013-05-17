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

package org.alice.ide.declarationseditor;

import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.AbstractMethod;

/**
 * @author Dennis Cosgrove
 */
public class DeclarationTabState extends org.lgna.croquet.MutableDataTabSelectionState<DeclarationComposite> {
	private final org.alice.ide.project.events.ProjectChangeOfInterestListener projectChangeOfInterestListener = new org.alice.ide.project.events.ProjectChangeOfInterestListener() {
		public void projectChanged() {
			handleAstChangeThatCouldBeOfInterest();
		}
	};

	public DeclarationTabState() {
		super( org.alice.ide.IDE.DOCUMENT_UI_GROUP, java.util.UUID.fromString( "7b3f95a0-c188-43bf-9089-21ec77c99a69" ), org.alice.ide.croquet.codecs.typeeditor.DeclarationCompositeCodec.SINGLETON );
		org.alice.ide.project.ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener( this.projectChangeOfInterestListener );
	}

	@Override
	protected void setCurrentTruthAndBeautyValue( DeclarationComposite declarationComposite ) {
		if( declarationComposite != null ) {
			org.lgna.croquet.data.ListData<DeclarationComposite> data = this.getData();
			if( data.contains( declarationComposite ) ) {
				//pass
			} else {
				class TypeListPair {
					private final org.lgna.project.ast.NamedUserType type;
					private final java.util.List<DeclarationComposite> list = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();

					public TypeListPair( org.lgna.project.ast.NamedUserType type ) {
						this.type = type;
					}

					public void addDeclarationComposite( DeclarationComposite declarationComposite ) {
						if( declarationComposite instanceof TypeComposite ) {
							this.list.add( 0, declarationComposite );
						} else {
							this.list.add( declarationComposite );
						}
					}

					public void update( java.util.List<DeclarationComposite> updatee, boolean isTypeRequired ) {
						if( isTypeRequired ) {
							TypeComposite typeComposite = TypeComposite.getInstance( this.type );
							if( this.list.contains( typeComposite ) ) {
								//pass
							} else {
								updatee.add( typeComposite );
							}
						}
						updatee.addAll( this.list );
					}
				}

				edu.cmu.cs.dennisc.java.util.InitializingIfAbsentMap<org.lgna.project.ast.NamedUserType, TypeListPair> map = edu.cmu.cs.dennisc.java.util.Collections.newInitializingIfAbsentHashMap();
				java.util.List<TypeListPair> typeListPairs = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();

				java.util.List<DeclarationComposite> prevItems = edu.cmu.cs.dennisc.java.util.Collections.newArrayList( data.toArray() );
				prevItems.add( declarationComposite );

				for( DeclarationComposite<?, ?> item : prevItems ) {
					if( item != null ) {
						org.lgna.project.ast.NamedUserType namedUserType = (org.lgna.project.ast.NamedUserType)item.getType();
						assert namedUserType != null : item;
						TypeListPair typeListPair = map.getInitializingIfAbsent( namedUserType, new edu.cmu.cs.dennisc.java.util.InitializingIfAbsentMap.Initializer<org.lgna.project.ast.NamedUserType, TypeListPair>() {
							public TypeListPair initialize( org.lgna.project.ast.NamedUserType key ) {
								return new TypeListPair( key );
							}
						} );
						typeListPair.addDeclarationComposite( item );
						if( typeListPairs.contains( typeListPair ) ) {
							//pass
						} else {
							typeListPairs.add( typeListPair );
						}
					}
				}
				java.util.List<DeclarationComposite> nextItems = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();
				boolean isTypeRequired = true; //typeListPairs.size() > 1;
				boolean isSeparatorDesired = false;
				for( TypeListPair typeListPair : typeListPairs ) {
					if( isSeparatorDesired ) {
						nextItems.add( null );
					}
					typeListPair.update( nextItems, isTypeRequired );
					isSeparatorDesired = true;
				}
				data.internalSetAllItems( nextItems );
			}
		}
		super.setCurrentTruthAndBeautyValue( declarationComposite );
	}

	private static final java.awt.Dimension ICON_SIZE = new java.awt.Dimension( 16, 16 );
	private static final javax.swing.Icon TYPE_ICON = new org.alice.ide.icons.TabIcon( ICON_SIZE, org.alice.ide.DefaultTheme.DEFAULT_TYPE_COLOR );
	public static final javax.swing.Icon FIELD_ICON = new org.alice.ide.icons.TabIcon( ICON_SIZE, org.alice.ide.DefaultTheme.DEFAULT_TYPE_COLOR ) {
		@Override
		protected void paintIcon( java.awt.Component c, java.awt.Graphics2D g2, int width, int height, java.awt.Paint fillPaint, java.awt.Paint drawPaint ) {
			super.paintIcon( c, g2, width, height, fillPaint, drawPaint );
			g2.setPaint( org.alice.ide.DefaultTheme.DEFAULT_FIELD_COLOR );
			g2.fill( new java.awt.geom.Rectangle2D.Float( 0.3f * width, 0.7f * height, 0.6f * width, 0.1f * height ) );
		}
	};

	private static final javax.swing.Icon PROCEDURE_ICON = new org.alice.ide.icons.TabIcon( ICON_SIZE, org.alice.ide.DefaultTheme.DEFAULT_PROCEDURE_COLOR );
	private static final javax.swing.Icon FUNCTION_ICON = new org.alice.ide.icons.TabIcon( ICON_SIZE, org.alice.ide.DefaultTheme.DEFAULT_FUNCTION_COLOR );
	private static final javax.swing.Icon CONSTRUCTOR_ICON = new org.alice.ide.icons.TabIcon( ICON_SIZE, org.alice.ide.DefaultTheme.DEFAULT_CONSTRUCTOR_COLOR );

	public org.lgna.croquet.Operation getItemSelectionOperationForType( org.lgna.project.ast.NamedUserType type ) {
		org.lgna.croquet.Operation rv = this.getItemSelectionOperation( TypeComposite.getInstance( type ) );
		rv.setSmallIcon( TYPE_ICON );
		return rv;
	}

	public org.lgna.croquet.Operation getItemSelectionOperationForMethod( org.lgna.project.ast.AbstractMethod method ) {
		org.lgna.croquet.Operation rv = this.getItemSelectionOperation( CodeComposite.getInstance( method ) );
		if( method.isProcedure() ) {
			rv.setSmallIcon( PROCEDURE_ICON );
		} else {
			rv.setSmallIcon( FUNCTION_ICON );
		}
		return rv;
	}

	public org.lgna.croquet.Operation getItemSelectionOperationForConstructor( org.lgna.project.ast.AbstractConstructor constructor ) {
		org.lgna.croquet.Operation rv = this.getItemSelectionOperation( CodeComposite.getInstance( constructor ) );
		rv.setSmallIcon( CONSTRUCTOR_ICON );
		return rv;
	}

	public org.lgna.croquet.Operation getItemSelectionOperationForCode( org.lgna.project.ast.AbstractCode code ) {
		if( code instanceof AbstractMethod ) {
			return this.getItemSelectionOperationForMethod( (AbstractMethod)code );
		} else if( code instanceof AbstractConstructor ) {
			return this.getItemSelectionOperationForConstructor( (AbstractConstructor)code );
		} else {
			return null;
		}
	}

	private void handleAstChangeThatCouldBeOfInterest() {
		org.alice.ide.declarationseditor.DeclarationComposite declarationComposite = this.getValue();
		if( declarationComposite != null ) {
			org.lgna.croquet.components.View view = declarationComposite.getView();
			if( view instanceof org.alice.ide.declarationseditor.code.components.CodeDeclarationView ) {
				org.alice.ide.declarationseditor.code.components.CodeDeclarationView codeDeclarationView = (org.alice.ide.declarationseditor.code.components.CodeDeclarationView)view;
				org.alice.ide.codedrop.CodePanelWithDropReceptor codePanelWithDropReceptor = codeDeclarationView.getCodePanelWithDropReceptor();
				if( codePanelWithDropReceptor instanceof org.alice.ide.codeeditor.CodeEditor ) {
					org.alice.ide.codeeditor.CodeEditor codeEditor = (org.alice.ide.codeeditor.CodeEditor)codePanelWithDropReceptor;
					codeEditor.handleAstChangeThatCouldBeOfInterest();
				}
			}
		}
	}
}
