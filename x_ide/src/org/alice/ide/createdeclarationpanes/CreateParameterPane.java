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
package org.alice.ide.createdeclarationpanes;

class MethodInvocationsNotificationPane extends swing.PageAxisPane {
	private javax.swing.JCheckBox checkBox;
	public MethodInvocationsNotificationPane( int invocationReferenceCount, String codeText ) {
		//this.setBorder( javax.swing.BorderFactory.createEtchedBorder() );

		this.add( new zoot.ZLabel( "There are invocations to this " + codeText + " in your program." ) );
		this.add( new zoot.ZLabel( "You will need to fill in argument values for this new parameter." ) );
		this.add( new swing.LineAxisPane( new zoot.ZLabel( "Tip: look for " ), org.alice.ide.IDE.getSingleton().getPreviewFactory().createExpressionPane( new edu.cmu.cs.dennisc.alice.ast.NullLiteral() ) ) );
		this.checkBox = new javax.swing.JCheckBox( "I understand that I need to update the invocations to this " + codeText + "." );
		this.checkBox.setOpaque( false );
//		this.checkbox.addItemListener( ecc.dennisc.swing.event.ItemAdapter( self._handleCheckBoxChange ) )

		this.add( this.checkBox );
	}
//	def _handleCheckBoxChange( self, e ):
//		self.updateOKButton()
//
//	def isValid( self ):
//		return self._checkbox.isSelected()
	}

/**
 * @author Dennis Cosgrove
 */
public class CreateParameterPane extends CreateDeclarationPane<edu.cmu.cs.dennisc.alice.ast.ParameterDeclaredInAlice> {
	private edu.cmu.cs.dennisc.alice.ast.CodeDeclaredInAlice code;
	public CreateParameterPane( edu.cmu.cs.dennisc.alice.ast.CodeDeclaredInAlice code ) {
		super( new org.alice.ide.namevalidators.ParameterNameValidator( code ) );
		this.code = code;
		this.setBackground( getIDE().getParameterColor() );
		
	}
	@Override
	protected java.awt.Component[] createWarningRow() {
		String codeText;
		if( code instanceof edu.cmu.cs.dennisc.alice.ast.AbstractMethod ) {
			edu.cmu.cs.dennisc.alice.ast.AbstractMethod method = (edu.cmu.cs.dennisc.alice.ast.AbstractMethod)code;
			if( method.isProcedure() ) {
				codeText = "procedure";
			} else {
				codeText = "function";
			}
		} else {
			codeText = "constructor";
		}
		return new java.awt.Component[] { new zoot.ZLabel( "WARNING:" ), new MethodInvocationsNotificationPane( 2, codeText ) };
	}
	@Override
	protected boolean isEditableInitializerComponentDesired() {
		return false;
	}
	@Override
	protected boolean isIsReassignableComponentDesired() {
		return false;
	}
	@Override
	protected boolean isEditableValueTypeComponentDesired() {
		return true;
	}
	@Override
	protected java.lang.String getValueTypeText() {
		return "value type:";
	}
//	@Override
//	protected java.awt.Component[] createDeclarationRow() {
//		return null;
//	}
	@Override
	protected java.awt.Component createInitializerComponent() {
		return null;
	}
	@Override
	protected java.awt.Component createPreviewSubComponent() {
		return new org.alice.ide.codeeditor.TypedParameterPane( null, this.getActualInputValue() );
	}
	@Override
	protected String getTitleDefault() {
		return "Declare Parameter";
	}
	@Override
	protected edu.cmu.cs.dennisc.alice.ast.ParameterDeclaredInAlice getActualInputValue() {
		return new edu.cmu.cs.dennisc.alice.ast.ParameterDeclaredInAlice( this.getDeclarationName(), this.getValueType() );
	}
}
