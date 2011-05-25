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
package org.alice.ide.croquet.edits.ast;

/**
 * @author Dennis Cosgrove
 */
public class DeclareMethodEdit extends org.lgna.croquet.edits.OperationEdit<org.alice.ide.croquet.models.ast.DeclareMethodOperation> {
	private edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice<?> declaringType;
	private edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice method;
	
	private transient edu.cmu.cs.dennisc.alice.ast.AbstractCode prevFocusedCode;

	public DeclareMethodEdit( edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice<?> declaringType, edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice method ) {
		this.declaringType = declaringType;
		this.method = method;
	}
	public DeclareMethodEdit( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
		super( binaryDecoder );
		this.declaringType = org.alice.ide.croquet.codecs.NodeCodec.getInstance( edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice.class ).decodeValue( binaryDecoder );
		this.method = org.alice.ide.croquet.codecs.NodeCodec.getInstance( edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice.class ).decodeValue( binaryDecoder );
	}
	@Override
	public void encode( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder ) {
		super.encode( binaryEncoder );
		org.alice.ide.croquet.codecs.NodeCodec.getInstance( edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice.class ).encodeValue( binaryEncoder, this.declaringType );
		org.alice.ide.croquet.codecs.NodeCodec.getInstance( edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice.class ).encodeValue( binaryEncoder, this.method );
	}
	
	public edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice< ? > getDeclaringType() {
		return this.declaringType;
	}
	public edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice getMethod() {
		return this.method;
	}

	@Override
	protected final void doOrRedoInternal( boolean isDo ) {
		this.declaringType.methods.add( this.method );
		org.alice.ide.IDE ide = org.alice.ide.IDE.getSingleton();
		this.prevFocusedCode = ide.getFocusedCode();
		ide.setFocusedCode( method );
	}
	@Override
	protected final void undoInternal() {
		int index = this.declaringType.methods.indexOf( method );
		if( index != -1 ) {
			this.declaringType.methods.remove( index );
			org.alice.ide.IDE ide = org.alice.ide.IDE.getSingleton();
			ide.setFocusedCode( this.prevFocusedCode );
		} else {
			throw new javax.swing.undo.CannotUndoException();
		}
	}
	
	@Override
	protected StringBuilder updatePresentation(StringBuilder rv, java.util.Locale locale) {
		rv.append( "declare: " );
		edu.cmu.cs.dennisc.alice.ast.NodeUtilities.safeAppendRepr(rv, this.method, locale);
		return rv;
	}
	
	@Override
	protected StringBuilder updateTutorialTransactionTitle( StringBuilder rv, edu.cmu.cs.dennisc.croquet.UserInformation userInformation ) {
		rv.append( "declare " );
		edu.cmu.cs.dennisc.alice.ast.NodeUtilities.safeAppendRepr(rv, this.method, userInformation.getLocale() );
		return rv;
	}

	@Override
	public edu.cmu.cs.dennisc.croquet.ReplacementAcceptability getReplacementAcceptability( edu.cmu.cs.dennisc.croquet.Edit< ? > replacementCandidate, edu.cmu.cs.dennisc.croquet.UserInformation userInformation ) {
		if( replacementCandidate instanceof DeclareMethodEdit ) {
			DeclareMethodEdit declareMethodEdit = (DeclareMethodEdit)replacementCandidate;
			if( this.method != null ) {
				edu.cmu.cs.dennisc.alice.ast.AbstractType< ?,?,? > originalReturnType = this.method.getReturnType();
				edu.cmu.cs.dennisc.alice.ast.AbstractType< ?,?,? > replacementReturnType = declareMethodEdit.method.getReturnType();
				if( originalReturnType == replacementReturnType ) {
					String originalName = this.method.getName();
					String replacementName = declareMethodEdit.method.getName();
					if( edu.cmu.cs.dennisc.equivalence.EquivalenceUtilities.areEquivalent( originalName, replacementName ) ) {
						return edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.PERFECT_MATCH;
					} else {
						StringBuilder sb = new StringBuilder();
						sb.append( "original name: " );
						sb.append( originalName );
						sb.append( "; changed to: " );
						sb.append( replacementName );
						//sb.append( "." );
						return edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.createDeviation( edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.DeviationSeverity.SHOULD_BE_FINE, sb.toString() ); 
					}
				} else {
					org.alice.ide.formatter.Formatter formatter = org.alice.ide.croquet.models.ui.formatter.FormatterSelectionState.getInstance().getSelectedItem();
					return edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.createRejection( "<html>return type <strong>MUST</strong> be <strong>" + formatter.getTextForType( method.getReturnType() ) + "</strong></html>" ); 
				}
			} else {
				return edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.createRejection( "replacement method is null" ); 
			}
		} else {
			return edu.cmu.cs.dennisc.croquet.ReplacementAcceptability.createRejection( "replacement is not an instance of DeclareMethodEdit" ); 
		}
	}
	
	public DeclareMethodEdit createTutorialCompletionEdit( edu.cmu.cs.dennisc.croquet.Retargeter retargeter ) {
		edu.cmu.cs.dennisc.alice.ast.AbstractTypeDeclaredInAlice<?> replacementDeclaringType = retargeter.retarget( this.declaringType );
		edu.cmu.cs.dennisc.alice.ast.MethodDeclaredInAlice replacementMethod = org.alice.ide.ast.NodeUtilities.createMethod( this.method.getName(), this.method.getReturnType() );
		retargeter.addKeyValuePair( this.method, replacementMethod );
		retargeter.addKeyValuePair( this.method.body.getValue(), replacementMethod.body.getValue() );
		return new DeclareMethodEdit( replacementDeclaringType, replacementMethod );
	}

	@Override
	public void retarget( edu.cmu.cs.dennisc.croquet.Retargeter retargeter ) {
		super.retarget( retargeter );
		this.declaringType = retargeter.retarget( this.declaringType );
		this.method = retargeter.retarget( this.method );
	}
	@Override
	public void addKeyValuePairs( edu.cmu.cs.dennisc.croquet.Retargeter retargeter, edu.cmu.cs.dennisc.croquet.Edit< ? > edit ) {
		super.addKeyValuePairs( retargeter, edit );
		assert edit instanceof DeclareMethodEdit;
		DeclareMethodEdit replacementEdit = (DeclareMethodEdit)edit;
		retargeter.addKeyValuePair( this.declaringType, replacementEdit.declaringType );
		retargeter.addKeyValuePair( this.method, replacementEdit.method );
		retargeter.addKeyValuePair( this.method.body.getValue(), replacementEdit.method.body.getValue() );
	}
}
