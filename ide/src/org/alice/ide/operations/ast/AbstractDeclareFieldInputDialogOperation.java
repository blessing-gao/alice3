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
package org.alice.ide.operations.ast;

/**
 * @author Dennis Cosgrove
 */
public abstract class AbstractDeclareFieldInputDialogOperation extends org.alice.ide.operations.InputDialogWithPreviewOperation {
	protected abstract edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInAlice getOwnerType();
	protected abstract edu.cmu.cs.dennisc.pattern.Tuple2<edu.cmu.cs.dennisc.alice.ast.FieldDeclaredInAlice, Object> createFieldAndInstance( edu.cmu.cs.dennisc.croquet.ModelContext context );
	protected abstract boolean isInstanceValid();
	
	public AbstractDeclareFieldInputDialogOperation( java.util.UUID individualId ) {
		super( edu.cmu.cs.dennisc.alice.Project.GROUP, individualId );
	}
	
	protected org.alice.ide.IDE getIDE() {
		return org.alice.ide.IDE.getSingleton();
	}
	@Override
	protected final void epilogue(edu.cmu.cs.dennisc.croquet.ModelContext context, boolean isOk) {
		if( isOk ) {
			edu.cmu.cs.dennisc.pattern.Tuple2<edu.cmu.cs.dennisc.alice.ast.FieldDeclaredInAlice, Object> tuple = this.createFieldAndInstance( context );
			if( tuple != null ) {
				edu.cmu.cs.dennisc.alice.ast.FieldDeclaredInAlice field = tuple.getA();
				if( field != null ) {
					edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInAlice ownerType = this.getOwnerType();
					int index = ownerType.fields.size();
					context.commitAndInvokeDo( new DeclareFieldEdit( ownerType, field, index, tuple.getB(), this.isInstanceValid() ) );
				} else {
					context.cancel();
				}
			} else {
				context.cancel();
			}
		} else {
			context.cancel();
		}
	}
//	@Override
//	protected final void perform( edu.cmu.cs.dennisc.croquet.Context context, java.awt.event.ActionEvent e, edu.cmu.cs.dennisc.croquet.AbstractButton< ? > button ) {
//		edu.cmu.cs.dennisc.alice.ast.TypeDeclaredInAlice ownerType = this.getOwnerType();
//		final edu.cmu.cs.dennisc.pattern.Tuple2<edu.cmu.cs.dennisc.alice.ast.FieldDeclaredInAlice, Object> tuple = this.createFieldAndInstance( context, e, button, ownerType );
//		if( tuple != null ) {
//			edu.cmu.cs.dennisc.alice.ast.FieldDeclaredInAlice field = tuple.getA();
//			if( field != null ) {
//				int index = ownerType.fields.size();
//				context.commitAndInvokeDo( new DeclareFieldEdit( ownerType, field, index, tuple.getB(), this.isInstanceValid() ) );
//			} else {
//				context.cancel();
//			}
//		} else {
//			context.cancel();
//		}
//	}
}
