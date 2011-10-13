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
package org.alice.ide.common;

import org.alice.ide.codeeditor.ExpressionPropertyDropDownPane;


/**
 * @author Dennis Cosgrove
 */
public abstract class Factory {
	@Deprecated
	public org.alice.ide.x.AstI18nFactory TODO_REMOVE_getBogusAstI18nFactory() {
		return null;
	}
	
	protected org.lgna.croquet.components.JComponent< ? > createGetsComponent( boolean isTowardLeading ) { 
		return new org.alice.ide.common.GetsPane( isTowardLeading );
	}
	protected org.lgna.croquet.components.JComponent< ? > createTextComponent( String text ) { 
		return new org.lgna.croquet.components.Label( text );
	}
	public org.lgna.croquet.components.JComponent< ? > createArgumentPane( org.lgna.project.ast.Argument argument, org.lgna.croquet.components.Component< ? > prefixPane ) {
		org.lgna.project.ast.ExpressionProperty expressionProperty = argument.expression;
		org.lgna.project.ast.Expression expression = expressionProperty.getValue();
		org.lgna.croquet.components.JComponent< ? > rv = new org.alice.ide.common.ExpressionPropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), expressionProperty );
		if( org.alice.ide.IDE.getActiveInstance().isDropDownDesiredFor( expression ) ) {
			org.alice.ide.croquet.models.ast.cascade.ArgumentCascade model = org.alice.ide.croquet.models.ast.cascade.ArgumentCascade.getInstance( argument );
			ExpressionPropertyDropDownPane expressionPropertyDropDownPane = new ExpressionPropertyDropDownPane( model.getRoot().getPopupPrepModel(), prefixPane, rv, expressionProperty );
			rv = expressionPropertyDropDownPane;
		}
		return rv;
	}
	public abstract org.lgna.croquet.components.JComponent< ? > createExpressionPropertyPane( org.lgna.project.ast.ExpressionProperty expressionProperty, org.lgna.croquet.components.Component< ? > prefixPane, org.lgna.project.ast.AbstractType<?,?,?> desiredValueType );
	public org.lgna.croquet.components.JComponent< ? > createExpressionPropertyPane( org.lgna.project.ast.ExpressionProperty expressionProperty, org.lgna.croquet.components.Component< ? > prefixPane ) {
		return createExpressionPropertyPane( expressionProperty, prefixPane, null );
	}
	protected abstract org.lgna.croquet.components.JComponent< ? > createArgumentListPropertyPane( org.lgna.project.ast.ArgumentListProperty argumentListProperty );
	
	protected org.lgna.croquet.components.JComponent< ? > createVariableDeclarationPane( org.lgna.project.ast.UserVariable variableDeclaredInAlice ) {
		return new VariableDeclarationPane( variableDeclaredInAlice, this.createVariablePane( variableDeclaredInAlice ) );
	}
	protected org.lgna.croquet.components.JComponent< ? > createConstantDeclarationPane( org.lgna.project.ast.UserConstant constantDeclaredInAlice ) {
		return new ConstantDeclarationPane( constantDeclaredInAlice, this.createConstantPane( constantDeclaredInAlice ) );
	}
	protected org.lgna.croquet.components.JComponent< ? > createVariablePane( org.lgna.project.ast.UserVariable variableDeclaredInAlice ) {
		return new VariablePane( variableDeclaredInAlice );
	}
	protected org.lgna.croquet.components.JComponent< ? > createConstantPane( org.lgna.project.ast.UserConstant constantDeclaredInAlice ) {
		return new ConstantPane( constantDeclaredInAlice );
	}
	
	protected org.lgna.croquet.components.JComponent< ? > createPropertyComponent( edu.cmu.cs.dennisc.property.InstanceProperty< ? > property, int underscoreCount ) {
		//todo:
		String propertyName = property.getName();
		//
		
		org.lgna.croquet.components.JComponent< ? > rv;
		if( underscoreCount == 2 ) {
			if( "variable".equals( propertyName ) ) {
				rv = this.createVariableDeclarationPane( (org.lgna.project.ast.UserVariable)property.getValue() );
			} else if( "constant".equals( propertyName ) ) {
				rv = this.createConstantDeclarationPane( (org.lgna.project.ast.UserConstant)property.getValue() );
			} else {
				rv = new org.lgna.croquet.components.Label( "TODO: handle underscore count 2: " + propertyName );
			}
		} else if( underscoreCount == 1 ) {
			if( "variable".equals( propertyName ) ) {
				rv = this.createVariablePane( (org.lgna.project.ast.UserVariable)property.getValue() );
			} else if( "constant".equals( propertyName ) ) {
				rv = this.createConstantPane( (org.lgna.project.ast.UserConstant)property.getValue() );
			} else {
				rv = new org.lgna.croquet.components.Label( "TODO: handle underscore count 1: " + propertyName );
			}
		} else {
			rv = null;
			//todo: remove
//			if( "operator".equals( propertyName ) ) {
//				String value = Factory.operatorMap.get( property.getValue() );
//				if( value != null ) {
//					zoot.ZLabel label = zoot.ZLabel.acquire( value, zoot.font.ZTextWeight.BOLD );
//					label.setFontToScaledFont( 1.5f );
//					rv = label;
//				}
//			}
			if( rv != null ) {
				//pass
			} else {
				if( property instanceof org.lgna.project.ast.NodeProperty< ? > ) {
					if( property instanceof org.lgna.project.ast.ExpressionProperty ) {
						rv = this.createExpressionPropertyPane( (org.lgna.project.ast.ExpressionProperty)property, null );
					} else {
						rv = new NodePropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.NodeProperty< ? >)property );
					}
				} else if( property instanceof org.lgna.project.ast.ResourceProperty ) {
					rv = new ResourcePropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.ResourceProperty)property );
				} else if( property instanceof edu.cmu.cs.dennisc.property.ListProperty< ? > ) {
					if( property instanceof org.lgna.project.ast.NodeListProperty< ? > ) {
						if( property instanceof org.lgna.project.ast.StatementListProperty ) {
							rv = new StatementListPropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.StatementListProperty)property );
						} else if( property instanceof org.lgna.project.ast.ArgumentListProperty ) {
							rv = this.createArgumentListPropertyPane( (org.lgna.project.ast.ArgumentListProperty)property );
						} else if( property instanceof org.lgna.project.ast.ExpressionListProperty ) {
							rv = new ExpressionListPropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.ExpressionListProperty)property );
						} else {
							rv = new DefaultNodeListPropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.NodeListProperty< ? >)property );
						}
					} else {
						rv = new DefaultListPropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (edu.cmu.cs.dennisc.property.ListProperty< ? >)property );
					}
				} else {
					rv = new InstancePropertyPane( this.TODO_REMOVE_getBogusAstI18nFactory(), property );
				}
				assert rv != null;
			}
		}
		return rv;
	}
	
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.GetsChunk getsChunk, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		return this.createGetsComponent( getsChunk.isTowardLeading() );
	}
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.TextChunk textChunk, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		return new org.lgna.croquet.components.Label( textChunk.getText() );
	}	
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.PropertyChunk propertyChunk, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		int underscoreCount = propertyChunk.getUnderscoreCount();
		String propertyName = propertyChunk.getPropertyName();
		edu.cmu.cs.dennisc.property.InstanceProperty< ? > property = owner.getInstancePropertyNamed( propertyName );
		return createPropertyComponent( property, underscoreCount );
	}
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.MethodInvocationChunk methodInvocationChunk, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		String methodName = methodInvocationChunk.getMethodName();
		org.lgna.croquet.components.JComponent< ? > rv;
		if( owner instanceof org.lgna.project.ast.AbstractDeclaration && methodName.equals( "getName" ) ) {
			org.lgna.project.ast.AbstractDeclaration declaration = (org.lgna.project.ast.AbstractDeclaration)owner;
			org.alice.ide.common.DeclarationNameLabel label = new org.alice.ide.common.DeclarationNameLabel( declaration );
			if( declaration instanceof org.lgna.project.ast.AbstractMethod ) {
				org.lgna.project.ast.AbstractMethod method = (org.lgna.project.ast.AbstractMethod)declaration;
				if( method.getReturnType() == org.lgna.project.ast.JavaType.VOID_TYPE ) {
					label.scaleFont( 1.1f );
					label.changeFont( edu.cmu.cs.dennisc.java.awt.font.TextWeight.BOLD );
				}
			}
			rv = label;
		} else if( owner instanceof org.lgna.project.ast.Argument && methodName.equals( "getParameterNameText" ) ) {
			org.lgna.project.ast.Argument argument = (org.lgna.project.ast.Argument)owner;
			rv = new org.alice.ide.common.DeclarationNameLabel( argument.parameter.getValue() );
		} else if( owner instanceof org.lgna.project.ast.AbstractConstructor && methodName.equals( "getDeclaringType" ) ) {
			org.lgna.project.ast.AbstractConstructor constructor = (org.lgna.project.ast.AbstractConstructor)owner;
			rv = TypeComponent.createInstance( constructor.getDeclaringType() );
//		} else if( owner instanceof org.lgna.project.ast.ResourceExpression && methodName.equals( "getResourceName" ) ) {
//			org.lgna.project.ast.ResourceExpression resourceExpression = (org.lgna.project.ast.ResourceExpression)owner;
//			org.alice.virtualmachine.Resource resource = resourceExpression.resource.getValue();
//			rv = edu.cmu.cs.dennisc.croquet.CroquetUtilities.createLabel( "resource " + resource.getName() );
		} else {
			java.lang.reflect.Method mthd = edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities.getMethod( owner.getClass(), methodName );
			Object o = edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities.invoke( owner, mthd );
			String s;
			if( o != null ) {
				if( o instanceof org.lgna.project.ast.AbstractType<?,?,?> ) {
					s = ((org.lgna.project.ast.AbstractType<?,?,?>)o).getName();
				} else {
					s = o.toString();
				}
			} else {
				s = null;
			}
			//s = "<html><h1>" + s + "</h1></html>";
			rv = new org.lgna.croquet.components.Label( s );
		}
		return rv;
	}

	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.Chunk chunk, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		if( chunk instanceof org.alice.ide.i18n.TextChunk ) {
			return createComponent( (org.alice.ide.i18n.TextChunk)chunk, owner );
		} else if( chunk instanceof org.alice.ide.i18n.PropertyChunk ) {
			return createComponent( (org.alice.ide.i18n.PropertyChunk)chunk, owner );
		} else if( chunk instanceof org.alice.ide.i18n.MethodInvocationChunk ) {
			return createComponent( (org.alice.ide.i18n.MethodInvocationChunk)chunk, owner );
		} else if( chunk instanceof org.alice.ide.i18n.GetsChunk ) {
			return createComponent( (org.alice.ide.i18n.GetsChunk)chunk, owner );
		} else {
			return new org.lgna.croquet.components.Label( "unhandled: " + chunk.toString() );
		}
	}
	protected int getPixelsPerIndent() {
		return 4;
	}
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.Line line, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		int indentCount = line.getIndentCount();
		org.alice.ide.i18n.Chunk[] chunks = line.getChunks();
		assert chunks.length > 0;
		if( indentCount > 0 || chunks.length > 1 ) {
			org.lgna.croquet.components.LineAxisPanel rv = new org.lgna.croquet.components.LineAxisPanel();
			if( indentCount > 0 ) {
				rv.addComponent( org.lgna.croquet.components.BoxUtilities.createHorizontalSliver( indentCount * this.getPixelsPerIndent() ) );
			}
			for( org.alice.ide.i18n.Chunk chunk : chunks ) {
				org.lgna.croquet.components.Component< ? > component = createComponent( chunk, owner );
				assert component != null : chunk.toString();
//				rv.setAlignmentY( 0.5f );
				rv.addComponent( component );
			}
			return rv;
		} else {
			//edu.cmu.cs.dennisc.print.PrintUtilities.println( "skipping line" );
			org.lgna.croquet.components.JComponent< ? > rv = createComponent( chunks[ 0 ], owner );
			assert rv != null : chunks[ 0 ].toString();
			return rv;
		}
	}
	protected org.lgna.croquet.components.JComponent< ? > createComponent( org.alice.ide.i18n.Page page, edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		org.alice.ide.i18n.Line[] lines = page.getLines();
		final int N = lines.length;
		assert N > 0;
		if( N > 1 ) {
			final boolean isLoop = lines[ N-1 ].isLoop();
			org.lgna.croquet.components.PageAxisPanel pagePane = new org.lgna.croquet.components.PageAxisPanel() {
				@Override
				protected javax.swing.JPanel createJPanel() {
					return new DefaultJPanel() {
						@Override
						protected void paintComponent(java.awt.Graphics g) {
							java.awt.Color prev = g.getColor();
							if( isLoop ) {
								int n = this.getComponentCount();
								java.awt.Component cFirst = this.getComponent( 0 );
								java.awt.Component cLast = this.getComponent( n-1 );
								g.setColor( edu.cmu.cs.dennisc.java.awt.ColorUtilities.createGray( 160 ) );
								int xB = Factory.this.getPixelsPerIndent();
								int xA = xB/2;
								int yTop = cFirst.getY() + cFirst.getHeight();
								int yBottom = cLast.getY() + cLast.getHeight()/2;
								g.drawLine( xA, yTop, xA, yBottom );
								g.drawLine( xA, yBottom, xB, yBottom );

								int xC = cLast.getX() + cLast.getWidth();
								int xD = xC + Factory.this.getPixelsPerIndent();;
								g.drawLine( xC, yBottom, xD, yBottom );
								g.drawLine( xD, yBottom, xD, cLast.getY() );
								
								final int HALF_TRIANGLE_WIDTH = 3;
								edu.cmu.cs.dennisc.java.awt.GraphicsUtilities.fillTriangle( g, edu.cmu.cs.dennisc.java.awt.GraphicsUtilities.Heading.NORTH, xA-HALF_TRIANGLE_WIDTH, yTop, HALF_TRIANGLE_WIDTH+1+HALF_TRIANGLE_WIDTH, 10 );
							}
							g.setColor( prev );
							super.paintComponent(g);
						}
					};
				}
			};
			for( org.alice.ide.i18n.Line line : lines ) {
				pagePane.addComponent( createComponent( line, owner ) );
			}
			pagePane.revalidateAndRepaint();
			return pagePane;
		} else {
			//edu.cmu.cs.dennisc.print.PrintUtilities.println( "skipping page" );
			return createComponent( lines[ 0 ], owner );
		}
	}
	public org.lgna.croquet.components.JComponent< ? > createComponent( edu.cmu.cs.dennisc.property.InstancePropertyOwner owner ) {
		org.lgna.croquet.components.JComponent< ? > rv;
		if( owner != null ) {
//			if( owner instanceof org.alice.ide.ast.EmptyExpression ) {
//				rv = new EmptyExpressionPane( (org.alice.ide.ast.EmptyExpression)owner );
//			} else if( owner instanceof org.alice.ide.ast.SelectedFieldExpression ) {
//				rv = new SelectedFieldExpressionPane( (org.alice.ide.ast.SelectedFieldExpression)owner );
//			} else if( owner instanceof org.lgna.project.ast.FieldAccess ) {
//				rv = new FieldAccessPane( this, (org.lgna.project.ast.FieldAccess)owner );
//			} else if( owner instanceof org.lgna.project.ast.TypeExpression ) {
//				rv = new TypeComponent( ((org.lgna.project.ast.TypeExpression)owner).value.getValue() );
//			} else {
				Class< ? > cls = owner.getClass();
				String value = edu.cmu.cs.dennisc.java.util.ResourceBundleUtilities.getStringFromSimpleNames( cls, "org.alice.ide.formatter.Templates", org.alice.ide.croquet.models.ui.formatter.FormatterSelectionState.getInstance().getSelectedItem().getLocale() );
				org.alice.ide.i18n.Page page = new org.alice.ide.i18n.Page( value );
				rv = createComponent( page, owner );
//			}
		} else {
			//rv = edu.cmu.cs.dennisc.croquet.CroquetUtilities.createLabel( "todo: handle null" );
			rv = new org.lgna.croquet.components.Label( org.alice.ide.croquet.models.ui.formatter.FormatterSelectionState.getInstance().getSelectedItem().getTextForNull() );
		}
		return rv;
	}
	

	private java.util.Map< org.lgna.project.ast.Statement, AbstractStatementPane > map = new java.util.HashMap< org.lgna.project.ast.Statement, AbstractStatementPane >();
	public java.util.Map< org.lgna.project.ast.Statement, AbstractStatementPane > getStatementMap() {
		return this.map;
	}
	public AbstractStatementPane lookup( org.lgna.project.ast.Statement statement ) {
		return this.map.get( statement );
	}
	public org.alice.ide.common.AbstractStatementPane createStatementPane( org.lgna.project.ast.Statement statement, org.lgna.project.ast.StatementListProperty statementListProperty ) {
		org.alice.ide.common.AbstractStatementPane rv;
		if( statement instanceof org.lgna.project.ast.ExpressionStatement ) {
			rv = new org.alice.ide.common.ExpressionStatementPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.ExpressionStatement)statement, statementListProperty );
		} else if( statement instanceof org.lgna.project.ast.Comment ) {
			rv = new org.alice.ide.codeeditor.CommentPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.Comment)statement, statementListProperty );
		} else {
			rv = new org.alice.ide.common.DefaultStatementPane( this.TODO_REMOVE_getBogusAstI18nFactory(), statement, statementListProperty );
		}
		return rv;
	}
	public final org.alice.ide.common.AbstractStatementPane createStatementPane( org.lgna.project.ast.Statement statement ) {
		return this.createStatementPane( statement, null );
	}
//	public final java.awt.Component createExpressionPane( org.lgna.project.ast.Expression expression ) {
//		return new ExpressionPane( this, expression );
//	}
	
	
	protected org.lgna.croquet.components.JComponent< ? > createFieldAccessPane( org.lgna.project.ast.FieldAccess fieldAccess ) {
		org.lgna.croquet.components.JComponent< ? > rv;
		FieldAccessPane fieldAccessPane = new FieldAccessPane( this.TODO_REMOVE_getBogusAstI18nFactory(), fieldAccess );
		org.lgna.croquet.components.Component< ? > prefixPane = org.alice.ide.IDE.getActiveInstance().getPrefixPaneForFieldAccessIfAppropriate( fieldAccess );
		if( prefixPane != null ) {
			rv = new org.lgna.croquet.components.LineAxisPanel( prefixPane, fieldAccessPane );
		} else {
			rv = fieldAccessPane;
		}
		return rv;
	}
	protected org.lgna.croquet.components.JComponent< ? > createInstanceCreationPane( org.lgna.project.ast.InstanceCreation instanceCreation ) {
		org.lgna.project.ast.AbstractConstructor constructor = instanceCreation.constructor.getValue();
		if( constructor instanceof org.lgna.project.ast.AnonymousUserConstructor ) {
			return new AnonymousConstructorPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.AnonymousUserConstructor)constructor );
		} else {
			return new ExpressionPane( instanceCreation, this.createComponent( instanceCreation ) );
		}
	}
	
	protected org.lgna.croquet.components.JComponent< ? > EPIC_HACK_createWrapperIfNecessaryForExpressionPanelessComponent( org.lgna.croquet.components.JComponent< ? > component ) {
		return component;
	}

	public org.lgna.croquet.components.JComponent< ? > createExpressionPane( org.lgna.project.ast.Expression expression ) {
//		java.awt.Component rv;
//		if( expression instanceof org.lgna.project.ast.TypeExpression ) {
//			rv = new TypeComponent( ((org.lgna.project.ast.TypeExpression)expression).value.getValue() );
////		} else if( expression instanceof org.lgna.project.ast.FieldAccess ) {
////			rv = new FieldAccessPane( this, (org.lgna.project.ast.FieldAccess)expression );
//		} else {
//			rv = new ExpressionPane( this, expression );
//		}
		org.lgna.croquet.components.JComponent< ? > rv = org.alice.ide.IDE.getActiveInstance().getOverrideComponent( this.TODO_REMOVE_getBogusAstI18nFactory(), expression );
		if( rv != null ) {
			//pass
		} else {
			if( expression instanceof org.lgna.project.ast.InfixExpression ) {
				org.lgna.project.ast.InfixExpression infixExpression = (org.lgna.project.ast.InfixExpression)expression;
				String clsName = infixExpression.getClass().getName();
				java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle( clsName, javax.swing.JComponent.getDefaultLocale() );
				
				
				
				//todo: investigate the need for this cast
				Enum e = (Enum)infixExpression.operator.getValue();
				
				
				
				String value = resourceBundle.getString( e.name() );
				org.alice.ide.i18n.Page page = new org.alice.ide.i18n.Page( value );
				org.lgna.croquet.components.JComponent< ? > component = this.createComponent( page, infixExpression );
				for( java.awt.Component child : component.getAwtComponent().getComponents() ) {
					if( child instanceof javax.swing.JLabel ) {
						javax.swing.JLabel label = (javax.swing.JLabel)child;
						String text = label.getText();
						//todo: remove this terrible hack
						boolean isScaleDesired = false;
						if( text.length() == 3 ) {
							char c0 = text.charAt( 0 );
							char c1 = text.charAt( 1 );
							char c2 = text.charAt( 2 );
							if( c0==' ' && c2 == ' ' ) {
								if( Character.isLetterOrDigit( c1 ) ) {
									//pass
								} else {
									isScaleDesired = true;
								}
							}
						} else if( text.length() == 4 ) {
							isScaleDesired = " >= ".equals( text ) || " <= ".equals( text ) || " == ".equals( text );
						}
						edu.cmu.cs.dennisc.java.awt.font.FontUtilities.setFontToDerivedFont( label, edu.cmu.cs.dennisc.java.awt.font.TextWeight.BOLD );
						if( isScaleDesired ) {
							edu.cmu.cs.dennisc.java.awt.font.FontUtilities.setFontToScaledFont( label, 1.5f );
						}
						//label.setVerticalAlignment( javax.swing.SwingConstants.CENTER );
					}
				}
				rv = new ExpressionPane( infixExpression, component );
				
			} else if( expression instanceof org.alice.ide.ast.EmptyExpression ) {
				rv = new EmptyExpressionPane( (org.alice.ide.ast.EmptyExpression)expression );
			} else if( expression instanceof org.alice.ide.ast.PreviousValueExpression ) {
				rv = new PreviousValueExpressionPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.alice.ide.ast.PreviousValueExpression)expression );
			} else if( expression instanceof org.alice.ide.ast.SelectedFieldExpression ) {
				rv = new SelectedFieldExpressionPane( (org.alice.ide.ast.SelectedFieldExpression)expression );
			} else if( expression instanceof org.lgna.project.ast.AssignmentExpression ) {
				rv = new AssignmentExpressionPane( this.TODO_REMOVE_getBogusAstI18nFactory(), (org.lgna.project.ast.AssignmentExpression)expression );
			} else if( expression instanceof org.lgna.project.ast.FieldAccess ) {
				rv = this.createFieldAccessPane( (org.lgna.project.ast.FieldAccess)expression );
			} else if( expression instanceof org.lgna.project.ast.TypeExpression ) {
				if( org.alice.ide.croquet.models.ui.formatter.FormatterSelectionState.getInstance().getSelectedItem().isTypeExpressionDesired() ) {
					
					rv = new org.lgna.croquet.components.LineAxisPanel(
							new DeclarationNameLabel( ((org.lgna.project.ast.TypeExpression)expression).value.getValue() ),
							new org.lgna.croquet.components.Label( "." )
					);
					//rv = TypeComponent.createInstance( ((org.lgna.project.ast.TypeExpression)expression).value.getValue() );
				} else {
					rv = new org.lgna.croquet.components.Label();
				}
			} else if( expression instanceof org.lgna.project.ast.InstanceCreation ) {
				rv = this.createInstanceCreationPane( (org.lgna.project.ast.InstanceCreation)expression );
//			} else if( expression instanceof org.lgna.project.ast.AbstractLiteral ) {
//				rv = this.createComponent( expression );
			} else {
				org.lgna.croquet.components.JComponent< ? > component = this.createComponent( expression );
				if( org.alice.ide.croquet.models.ui.preferences.IsIncludingTypeFeedbackForExpressionsState.getInstance().getValue() ) {
					rv = new ExpressionPane( expression, component );
				} else {
					rv = this.EPIC_HACK_createWrapperIfNecessaryForExpressionPanelessComponent( component );
				}
			}
		}
		return rv;
	}
}
