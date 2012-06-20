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
package org.alice.ide.croquet.models.project;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alice.ide.croquet.models.project.views.MethodFrequencyView;
import org.lgna.croquet.BooleanState;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.ListSelectionState;
import org.lgna.croquet.TabComposite;
import org.lgna.croquet.components.GridPanel;
import org.lgna.croquet.components.View;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserMethod;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.java.util.Collections;

/**
 * @author Matt May
 */
public class MethodFrequencyTabComposite extends TabComposite<View<?,?>> {

	private Map<UserMethod,InvocationCounts> mapMethodToInvocationCounts = Collections.newHashMap();

	ItemCodec<UserMethod> codec = new ItemCodec<UserMethod>() {

		public Class<UserMethod> getValueClass() {
			return UserMethod.class;
		}

		public UserMethod decodeValue( BinaryDecoder binaryDecoder ) {
			return null;
		}

		public void encodeValue( BinaryEncoder binaryEncoder, UserMethod value ) {
		}

		public StringBuilder appendRepresentation( StringBuilder rv, UserMethod value ) {
			return rv.append( value.getName() );
		}
	};
	private final ListSelectionState<UserMethod> listSelectionState = createListSelectionState( this.createKey( "userMethodList" ), UserMethod.class, codec, -1 );
	public static final UserMethod dummy = new UserMethod();
	private final BooleanState showFunctionsState = this.createBooleanState( this.createKey( "areFunctionsShowing" ), true );
	private final BooleanState showProceduresState = this.createBooleanState( this.createKey( "areProceduresShowing" ), true );
	private Integer maximum;

	public static class MethodCountPair {
		private final AbstractMethod method;
		private int count;

		public MethodCountPair( AbstractMethod method ) {
			this.method = method;
			this.count = 1;
		}
		public void bumpItUpANotch() {
			this.count++;
		}
		public AbstractMethod getMethod() {
			return this.method;
		}
		public int getCount() {
			return this.count;
		}
	}

	public static class InvocationCounts {
		private List<MethodCountPair> methodCountPairs = Collections.newLinkedList();

		public void addInvocation( MethodInvocation invocation ) {
			addMethod( invocation.method.getValue() );
		}
		public List<MethodCountPair> getMethodCountPairs() {
			return this.methodCountPairs;
		}
		public void addMethod( AbstractMethod method ) {
			if( get( method ) != null ) {
				get( method ).bumpItUpANotch();
			} else {
				methodCountPairs.add( new MethodCountPair( method ) );
				sort();
			}
		}
		private void sort() {
			java.util.Collections.sort( methodCountPairs, new Comparator<MethodCountPair>() {

				public int compare( MethodCountPair o1, MethodCountPair o2 ) {
					return o1.getMethod().getName().compareTo( o2.getMethod().getName() );
				}
			} );
		}
		public int size() {
			return methodCountPairs.size();
		}
		public AbstractMethod get( int i ) {
			return methodCountPairs.get( i ).getMethod();
		}
		public MethodCountPair get( AbstractMethod method ) {
			for( MethodCountPair methodCountPair : this.methodCountPairs ) {
				if( methodCountPair.getMethod().equals( method ) ) {
					return methodCountPair;
				}
			}
			return null;
		}
	}

	public MethodFrequencyTabComposite() {
		super( java.util.UUID.fromString( "93b531e2-69a3-4721-b2c8-d2793181a41c" ) );

		org.alice.ide.IDE ide = org.alice.ide.IDE.getActiveInstance();
		org.lgna.project.ast.NamedUserType programType = ide.getStrippedProgramType();

		MethodInvocationCrawler crawler = new MethodInvocationCrawler();
		programType.crawl( crawler, true );

		for( AbstractMethod method : crawler.getMethods() ) {
			List<MethodInvocation> invocations = crawler.getInvocationsFor( method );
			for( MethodInvocation invocation : invocations ) {
				UserMethod invocationOwner = invocation.getFirstAncestorAssignableTo( UserMethod.class );
				InvocationCounts invocationCounts = this.getMapMethodToInvocationCounts().get( invocationOwner );
				if( !invocationOwner.getManagementLevel().isGenerated() && !invocationOwner.getDeclaringType().isAssignableTo( org.lgna.story.Program.class ) ) {
					if( invocationCounts != null ) {
						//pass
					} else {
						invocationCounts = new InvocationCounts();
						this.getMapMethodToInvocationCounts().put( invocationOwner, invocationCounts );
					}
					Statement statement = invocation.getFirstAncestorAssignableTo( Statement.class );
					if( statement.isEnabled.getValue() ) {
						invocationCounts.addInvocation( invocation );
					}
				}
			}
		}

		setMaximum();
		dummy.setName( "Project" );
		List<UserMethod> a = new LinkedList<UserMethod>();
		for( UserMethod method : getMapMethodToInvocationCounts().keySet() ) {
			a.add( method );
		}
		sort( a );
		for( UserMethod method : a ) {
			getUserMethodList().addItem( method );
		}
	}

	public void setMaximum() {
		InvocationCounts invocationCounts = new InvocationCounts();
		getMapMethodToInvocationCounts().put( dummy, invocationCounts );
		maximum = getCount( dummy, null );
	}

	public Integer getMaximum() {
		return this.maximum;
	}

	public int getCount( AbstractMethod method, AbstractMethod methodTwo ) {
		int count = 0;
		if( methodTwo != null ) {
			count = getMapMethodToInvocationCounts().get( method ).get( methodTwo ).getCount();
		} else {
			if( getMapMethodToInvocationCounts().get( method ) != null ) {
				for( MethodCountPair pair : getMapMethodToInvocationCounts().get( method ).methodCountPairs ) {
					if( pair.getMethod() != dummy ) {
						count += pair.getCount();
					}
				}
			}
		}
		return count;
	}

	private void sort( List<? extends AbstractMethod> a ) {
		java.util.Collections.sort( a, new Comparator<AbstractMethod>() {

			public int compare( AbstractMethod o1, AbstractMethod o2 ) {
				return o1.getName().compareTo( o2.getName() );
			}
		} );
	}

	@Override
	public boolean isCloseable() {
		return false;
	}

	@Override
	protected View<?,?> createView() {
		return new MethodFrequencyView( this );
	}

	public ListSelectionState<UserMethod> getUserMethodList() {
		return this.listSelectionState;
	}

	public BooleanState getShowFunctionsState() {
		return this.showFunctionsState;
	}

	public BooleanState getShowProceduresState() {
		return this.showProceduresState;
	}

	public Map<UserMethod,InvocationCounts> getMapMethodToInvocationCounts() {
		return this.mapMethodToInvocationCounts;
	}

	public void setMapMethodToInvocationCounts( Map<UserMethod,InvocationCounts> mapMethodToInvocationCounts ) {
		this.mapMethodToInvocationCounts = mapMethodToInvocationCounts;
	}

	private static class MethodInvocationCrawler implements edu.cmu.cs.dennisc.pattern.Crawler {
		private final Map<AbstractMethod,List<MethodInvocation>> mapMethodToInvocations = Collections.newHashMap();

		public void visit( edu.cmu.cs.dennisc.pattern.Crawlable crawlable ) {
			if( crawlable instanceof MethodInvocation ) {
				MethodInvocation methodInvocation = (MethodInvocation)crawlable;
				AbstractMethod method = methodInvocation.method.getValue();
				List<MethodInvocation> list = this.mapMethodToInvocations.get( method );
				if( list != null ) {
					list.add( methodInvocation );
				} else {
					list = Collections.newLinkedList( methodInvocation );
					this.mapMethodToInvocations.put( method, list );
				}
			}
		}

		public java.util.Set<AbstractMethod> getMethods() {
			return this.mapMethodToInvocations.keySet();
		}
		public List<MethodInvocation> getInvocationsFor( AbstractMethod method ) {
			return this.mapMethodToInvocations.get( method );
		}

	}
}