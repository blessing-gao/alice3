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

package org.alice.ide.croquet.models.cascade;

/**
 * @author Dennis Cosgrove
 */
public abstract class ExpressionFillInWithBlanks< F extends edu.cmu.cs.dennisc.alice.ast.Expression, B > extends ExpressionFillIn< F, B > {
	private final Class<B> cls;
	public ExpressionFillInWithBlanks( java.util.UUID id, Class<B> cls ) {
		super( id );
		this.cls = cls;
	}
	private enum BlankOperation {
		CREATE_VALUES() {
			@Override
			public <F,M extends edu.cmu.cs.dennisc.croquet.CascadeItem< F,C >,C extends org.lgna.croquet.steps.CascadeItemStep<F,M,C>> F operate( org.lgna.croquet.steps.CascadeItemStep< F,M,C > selectedFillInContext ) {
				return selectedFillInContext.createValue();
			}
		},
		GET_TRANSIENT_VALUES() {
			@Override
			public <F,M extends edu.cmu.cs.dennisc.croquet.CascadeItem< F,C >,C extends org.lgna.croquet.steps.CascadeItemStep<F,M,C>> F operate( org.lgna.croquet.steps.CascadeItemStep< F,M,C > selectedFillInContext ) {
				if( selectedFillInContext != null ) {
					return selectedFillInContext.getTransientValue();
				} else {
					return null;
				}
			}
		};
		public abstract <F,M extends edu.cmu.cs.dennisc.croquet.CascadeItem< F,C >, C extends org.lgna.croquet.steps.CascadeItemStep<F,M,C> > F operate( org.lgna.croquet.steps.CascadeItemStep< F,M,C > selectedFillInContext );
	}
	private B[] runBlanks( org.lgna.croquet.steps.CascadeFillInPrepStep<F,B> step, BlankOperation blankOperation ) { 
		edu.cmu.cs.dennisc.croquet.CascadeBlank< B >[] blanks = this.getBlanks();
		B[] rv = edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities.newTypedArrayInstance( this.cls, blanks.length );
		for( int i=0; i<rv.length; i++ ) {
			org.lgna.croquet.steps.CascadeBlankStep< B > blankStep = step.getBlankStepAt( i );
			org.lgna.croquet.steps.CascadeItemStep< B,?,? > selectedFillInContext = blankStep.getSelectedFillInContext();
			rv[ i ] = (B)blankOperation.operate( selectedFillInContext );
//			if( rv[ i ] == null ) {
//				if( this.cls == edu.cmu.cs.dennisc.alice.ast.Expression.class ) {
//					edu.cmu.cs.dennisc.croquet.CascadeBlank< B > blank = blankContext.getModel();
//					if( blank instanceof ExpressionBlank ) {
//						ExpressionBlank expressionBlank = (ExpressionBlank)blank;
//						//todo:
//						//this cast is very, very wrong
//						rv[ i ] = (B)new org.alice.ide.ast.EmptyExpression( expressionBlank.getValueType() );
//					}
//				}
//			}
		}
		return rv;
	}
	protected abstract F createValue( B[] expressions );
	@Override
	public final F createValue( org.lgna.croquet.steps.CascadeFillInPrepStep<F,B> step ) {
		return this.createValue( runBlanks( step, BlankOperation.CREATE_VALUES ) );
	}
//	protected abstract F getTransientValue( B[] expressions );
//	@Override
//	public final F getTransientValue( edu.cmu.cs.dennisc.croquet.CascadeFillInContext< F, B > step ) {
//		return this.getTransientValue( runBlanks( step, BlankOperation.GET_TRANSIENT_VALUES ) );
//	}
}