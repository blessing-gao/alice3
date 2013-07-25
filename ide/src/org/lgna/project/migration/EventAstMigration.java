/**
 * Copyright (c) 2006-2012, Carnegie Mellon University. All rights reserved.
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
package org.lgna.project.migration;

import java.util.ArrayList;

import org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException;
import org.alice.stageide.ast.ExpressionCreator;
import org.lgna.project.Version;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.event.MouseClickOnScreenEvent;

/**
 * @author Matt May
 */
public class EventAstMigration extends AstMigration {

	ExpressionCreator creator = new ExpressionCreator();

	public EventAstMigration() {
		super( new Version( "3.1.68.0.0" ), new Version( "3.1.70.0.0" ) );
	}

	@Override
	public final void migrate( org.lgna.project.ast.Node node ) {
		node.crawl( new edu.cmu.cs.dennisc.pattern.Crawler() {
			public void visit( edu.cmu.cs.dennisc.pattern.Crawlable crawlable ) {
				if( crawlable instanceof org.lgna.project.ast.MethodInvocation ) {
					org.lgna.project.ast.MethodInvocation methodInvocation = (org.lgna.project.ast.MethodInvocation)crawlable;
					EventAstMigration.this.migrateMethodInv( methodInvocation );
				} else if( crawlable instanceof JavaMethod ) {
					JavaMethod javaMethod = (JavaMethod)crawlable;
					EventAstMigration.this.migrateJavaMethod( javaMethod );
				}
			}
		}, org.lgna.project.ast.CrawlPolicy.COMPLETE, null );
	}

	protected void migrateMethodInv( MethodInvocation methodInvocation ) {

		org.lgna.project.ast.AbstractMethod method = methodInvocation.method.getValue();
		if( method instanceof org.lgna.project.ast.JavaMethod ) {
			org.lgna.project.ast.JavaMethod javaMethod = (org.lgna.project.ast.JavaMethod)method;

			if( javaMethod.getDeclaringType() == org.lgna.project.ast.JavaType.getInstance( org.lgna.story.SScene.class ) ) {
				String methodName = method.getName();
				if( methodName.equals( "addTimeListener" ) ) {
					handleAddTimeListener( methodInvocation, javaMethod );
				} else if( methodName.equals( "addProximityEnterListener" ) || methodName.equals( "addProximityExitListener" ) ) {
					changeDoubleToNumber( methodInvocation, javaMethod, 3 );
				} else if( method.getName().equals( "addMouseClickOnScreenListener" ) ) {
					addMouseClickOnScreenEventParameter( methodInvocation );
				}
			}
		}
	}

	private void addMouseClickOnScreenEventParameter( MethodInvocation methodInvocation ) {
		SimpleArgument simpleArgument = methodInvocation.requiredArguments.get( 0 );
		LambdaExpression lambda = (LambdaExpression)simpleArgument.expression.getValue();
		UserLambda value = (UserLambda)lambda.value.getValue();
		value.requiredParameters.add( new UserParameter( "event", MouseClickOnScreenEvent.class ) );
	}

	private void changeDoubleToNumber( MethodInvocation methodInvocation, org.lgna.project.ast.JavaMethod javaMethod, int index ) {
		Expression exp = methodInvocation.requiredArguments.get( index ).expression.getValue();
		try {
			assert exp instanceof DoubleLiteral : exp.getClass();
			Number distance = ( (DoubleLiteral)exp ).value.getValue();
			methodInvocation.requiredArguments.set( index, new SimpleArgument( javaMethod.getRequiredParameters().get( 0 ), creator.createExpression( distance ) ) );
		} catch( CannotCreateExpressionException e ) {
			e.printStackTrace();
		}
	}

	protected void migrateJavaMethod( JavaMethod javaMethod ) {
		//		System.out.println( "javaMethod: " + javaMethod );
		//		if( javaMethod.getName().equals( "addSceneActivationListener" ) ) {
		//
		//		}
	}

	private void handleAddTimeListener( MethodInvocation methodInvocation, org.lgna.project.ast.JavaMethod javaMethod ) {
		ArrayList<JavaKeyedArgument> keyedParameter = methodInvocation.keyedArguments.getValue();
		Number duration = null;
		JavaKeyedArgument argToRemove = null;
		for( JavaKeyedArgument arg : keyedParameter ) {
			Expression value = ( (MethodInvocation)arg.expression.getValue() ).requiredArguments.get( 0 ).expression.getValue();
			if( value instanceof DoubleLiteral ) {
				duration = ( (DoubleLiteral)value ).value.getValue();
				argToRemove = arg;
			} else {
				//do nothing
			}
		}
		if( duration != null ) {
			keyedParameter.remove( argToRemove );
		} else {
			duration = 0;
		}
		try {
			methodInvocation.requiredArguments.add( new SimpleArgument( javaMethod.getRequiredParameters().get( 0 ), creator.createExpression( duration ) ) );
		} catch( CannotCreateExpressionException e ) {
			e.printStackTrace();
		}
	}

	public static TextMigration getTextMigration() {
		return null;
	}
}
