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
package org.alice.ide.croquet.models.project.TreeNodesAndManagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.CrawlPolicy;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.NodeListProperty;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.UserType;

import edu.cmu.cs.dennisc.java.util.Collections;
import edu.cmu.cs.dennisc.pattern.Crawlable;
import edu.cmu.cs.dennisc.pattern.Crawler;

/**
 * @author Matt May
 */
public class FindContentManager {

	private List<SearchObject<?>> objectList = Collections.newArrayList();
	private List<Object> superTypeList = Collections.newArrayList();

	Crawler crawler = new Crawler() {
		public void visit( Crawlable crawlable ) {
			if( crawlable instanceof MethodInvocation ) {
				MethodInvocation methodInv = (MethodInvocation)crawlable;
				SearchObject<?> checkFind = checkFind( methodInv.method.getValue() );
				if( checkFind != null ) {
					checkFind.addReference( methodInv );
				}
			} else if( crawlable instanceof FieldAccess ) {
				FieldAccess fieldAccess = (FieldAccess)crawlable;
				SearchObject<?> checkFind = checkFind( fieldAccess.field.getValue() );
				if( checkFind != null ) {
					checkFind.addReference( fieldAccess );
				}
			} else if( crawlable instanceof LocalAccess ) {
				LocalAccess localAccess = (LocalAccess)crawlable;
				SearchObject<?> checkFind = checkFind( localAccess.local.getValue() );
				if( checkFind != null ) {
					checkFind.addReference( localAccess );
				}
			}
		}

	};

	public void initialize( UserType scene ) {
		objectList = Collections.newArrayList();
		superTypeList = Collections.newArrayList();
		System.out.println( "init" );
		tunnelField( scene );
		for( SearchObject<?> object : objectList ) {
			if( object.getSearchObject() instanceof UserMethod ) {
				UserMethod method = (UserMethod)object.getSearchObject();
				method.crawl( crawler, CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY );
			}
		}
	}

	private void tunnelField( UserType type ) {
		NodeListProperty<Node> methods = type.methods;
		NodeListProperty<Node> fields = type.fields;
		tunnelSuper( (AbstractType)type.superType.getValue() );
		for( Node o : fields ) {
			assert o instanceof AbstractField;
			AbstractField field = (AbstractField)o;
			if( !checkContains( field ) ) {
				objectList.add( new SearchObject<AbstractField>( AbstractField.class, field ) );
				if( field.getValueType() instanceof UserType ) {
					tunnelField( (UserType)field.getValueType() );
				}
			}
		}
		for( Node o : methods ) {
			assert o instanceof AbstractMethod;
			AbstractMethod method = (AbstractMethod)o;
			if( !checkContains( method ) ) {
				objectList.add( new SearchObject<AbstractMethod>( AbstractMethod.class, method ) );
				if( method instanceof UserMethod ) {
					tunnelMethod( (UserMethod)method );
				}
			}
		}
	}

	private void tunnelSuper( AbstractType parent ) {
		if( ( parent != null ) && !superTypeList.contains( parent ) ) {
			//pass
		} else {
			return;
		}
		superTypeList.add( parent );
		ArrayList<AbstractField> fields = parent.getDeclaredFields();
		ArrayList<AbstractMethod> methods = parent.getDeclaredMethods();
		for( AbstractField field : fields ) {
			if( !checkContains( field ) ) {
				objectList.add( new SearchObject<AbstractField>( AbstractField.class, field ) );
			}
		}
		for( AbstractMethod method : methods ) {
			if( !checkContains( method ) ) {
				objectList.add( new SearchObject<AbstractMethod>( AbstractMethod.class, method ) );
			}
		}
		if( parent != null ) {
			tunnelSuper( parent.getSuperType() );
		}
	}

	private void tunnelMethod( UserMethod method ) {
		NodeListProperty<UserParameter> nodeListProperty = method.requiredParameters;
		BlockStatement blockStatement = method.body.getValue();
		for( UserParameter parameter : nodeListProperty ) {
			assert !checkContains( parameter );
			objectList.add( new SearchObject<UserParameter>( UserParameter.class, parameter ) );
		}
		for( Statement statement : blockStatement.statements ) {
			if( statement instanceof LocalDeclarationStatement ) {
				UserLocal local = ( (LocalDeclarationStatement)statement ).local.getValue();
				assert !checkContains( local );
				objectList.add( new SearchObject<UserLocal>( UserLocal.class, local ) );
			}
		}
	}

	private boolean checkContains( Object searchObject ) {
		return checkFind( searchObject ) != null;
	}

	private SearchObject<?> checkFind( Object searchObject ) {
		for( SearchObject<?> object : objectList ) {
			if( object.getSearchObject().equals( searchObject ) ) {
				return object;
			}
		}
		return null;
	}

	public List<SearchObject<?>> getResultsForString( String nextValue ) {
		List<SearchObject<?>> rv = Collections.newArrayList();
		if( nextValue.length() == 0 ) {
			return rv;
		}
		String check = nextValue;
		check = check.replaceAll( "\\*", ".*" );
		try {
			Pattern pattern = Pattern.compile( check.toLowerCase() );
			for( SearchObject<?> o : objectList ) {
				String name = o.getName();
				Matcher matcher = pattern.matcher( name.toLowerCase() );
				if( matcher.find() ) {
					rv.add( o );
				}
			}
		} catch( PatternSyntaxException pse ) {
			throw new RuntimeException( "Regex PatternSyntaxException" );
		}
		rv = sortByRelevance( nextValue, rv );
		return rv;
	}

	private List<SearchObject<?>> sortByRelevance( String string, List<SearchObject<?>> searchResults ) {
		List<SearchObject<?>> unsortedList = Collections.newArrayList( searchResults );
		List<SearchObject<?>> rv = Collections.newArrayList();
		Map<SearchObject<?>, Double> scoreMap = Collections.newHashMap();
		for( SearchObject<?> obj : unsortedList ) {
			scoreMap.put( obj, score( obj, string ) );
		}
		//n^2 sort O:-)
		while( !unsortedList.isEmpty() ) {
			double max = Double.NEGATIVE_INFINITY;
			SearchObject<?> maxObj = null;
			for( SearchObject<?> o : unsortedList ) {
				if( scoreMap.get( o ) > max ) {
					max = scoreMap.get( o );
					maxObj = o;
				}
			}
			assert maxObj != null : unsortedList.size();
			rv.add( unsortedList.remove( unsortedList.indexOf( maxObj ) ) );
		}
		return rv;
	}

	private Double score( SearchObject<?> obj, String string ) {
		double rv = 0;
		if( obj.getName().equals( string ) ) {
			rv += 2;
		}
		if( obj.getName().contains( string ) ) {
			rv += 1;
		}
		if( obj.getName().toLowerCase().startsWith( string.toLowerCase() ) ) {
			rv += 1;
		}
		rv += obj.getReferenceCount() / 10.0;
		return rv;
	}
}
