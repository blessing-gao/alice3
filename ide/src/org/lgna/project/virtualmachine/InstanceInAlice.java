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

package org.lgna.project.virtualmachine;

import org.lgna.project.ast.*;

/**
 * @author Dennis Cosgrove
 */
public class InstanceInAlice {
	public static Object getInstanceInJavaIfNecessary( Object instance ) {
		if( instance instanceof InstanceInAlice ) {
			return ((InstanceInAlice)instance).getInstanceInJava();
		} else {
			return instance;
		}
	}
	public static Object[] updateArrayWithInstancesInJavaIfNecessary( Object[] rv ) {
		for( int i=0; i<rv.length; i++ ) {
			rv[ i ] = getInstanceInJavaIfNecessary( rv[ i ] );
		}
		return rv;
	}
	public static InstanceInAlice createInstance( VirtualMachine vm, NamedUserConstructor constructor, Object[] arguments ) {
		return new InstanceInAlice( vm, constructor, arguments, new java.util.HashMap< UserField, Object >(), null );
	}
	public static InstanceInAlice createInstanceWithInverseMap( VirtualMachine vm, NamedUserConstructor constructor, Object[] arguments ) {
		return new InstanceInAlice( vm, constructor, arguments, new java.util.HashMap< UserField, Object >(), new java.util.HashMap< Object, UserField >() );
	}
	
	private final Object nextInstance;
	private final UserType<?> type;
	private final java.util.Map< UserField, Object > fieldMap;
	private final java.util.Map< Object, UserField > inverseFieldMap;
	private InstanceInAlice( VirtualMachine vm, NamedUserConstructor constructor, Object[] arguments, java.util.Map< UserField, Object > fieldMap, java.util.Map< Object, UserField > inverseFieldMap ) {
		this.type = constructor.getDeclaringType();
		this.fieldMap = fieldMap;
		this.inverseFieldMap = inverseFieldMap;
		
		ConstructorBlockStatement constructorBlockStatement = constructor.body.getValue();
		ConstructorInvocationStatement constructorInvocationStatement = constructorBlockStatement.constructorInvocationStatement.getValue();
		AbstractConstructor nextConstructor = constructorInvocationStatement.contructor.getValue();
		java.util.Map<org.lgna.project.ast.AbstractParameter,Object> stackMap = new java.util.HashMap< org.lgna.project.ast.AbstractParameter, Object >();
		for( int i=0; i<arguments.length; i++ ) {
			stackMap.put( constructor.parameters.get( i ), arguments[ i ] );
		}
		org.lgna.project.ast.NamedUserType type = (org.lgna.project.ast.NamedUserType)constructor.getDeclaringType();
		vm.pushConstructorFrame( type, stackMap );
		try {
			Object[] nextArguments = vm.evaluateArguments( nextConstructor.getParameters(), constructorInvocationStatement.arguments );
			if( nextConstructor.isDeclaredInAlice() ) {
				this.nextInstance = new InstanceInAlice( vm, (NamedUserConstructor)nextConstructor, nextArguments, fieldMap, inverseFieldMap );
			} else {
				JavaConstructor nextConstructorDeclaredInJava = (JavaConstructor)nextConstructor;
				ConstructorReflectionProxy constructorReflectionProxy =  nextConstructorDeclaredInJava.getConstructorReflectionProxy();
				java.lang.reflect.Constructor<?> cnstrctr = constructorReflectionProxy.getReification();
				assert cnstrctr != null : constructorReflectionProxy.getDeclaringClassReflectionProxy().getName();
				this.nextInstance = vm.createInstance( this.type, this, cnstrctr, nextArguments );
			}
			vm.setConstructorFrameInstanceInAlice( this );
			for( AbstractField field : this.type.getDeclaredFields() ) {
				assert field instanceof UserField;
				UserField fieldDeclaredInAlice = (UserField)field;
				this.createAndSetFieldInstance( vm, fieldDeclaredInAlice );
			}
			try {
				vm.executeBlockStatement( constructorBlockStatement );
			} catch( ReturnException re ) {
				throw new RuntimeException( re );
			}
		} finally {
			vm.popFrame();
		}
	}

	public Object createAndSetFieldInstance( VirtualMachine vm, UserField field ) {
		Object rv = vm.evaluate( field.initializer.getValue() );
		this.setFieldValue( field, rv );
		//System.err.println( field + " " + rv );
		return rv;
	}
	public UserType<?> getType() {
		return this.type;
	}
	public Object getInstanceInJava() {
		return getInstanceInJavaIfNecessary( this.nextInstance );
	}
	public <E> E getInstanceInJava( Class<E> cls ) {
		return edu.cmu.cs.dennisc.java.lang.ClassUtilities.getInstance( this.getInstanceInJava(), cls );
	}
	
	public Object getFieldValue( UserField field ) {
		return this.fieldMap.get( field );
	}
	public Object getFieldValueInstanceInJava( UserField field ) {
		return getInstanceInJavaIfNecessary( this.getFieldValue( field ) );
	}
	public <E> E getFieldValueInstanceInJava( UserField field, Class<E> cls ) {
		return edu.cmu.cs.dennisc.java.lang.ClassUtilities.getInstance( this.getFieldValueInstanceInJava( field ), cls );
	}
	public void setFieldValue( UserField field, Object value ) {
		this.fieldMap.put( field, value );
		if( this.inverseFieldMap != null ) {
			this.inverseFieldMap.put( getInstanceInJavaIfNecessary( value ), field );
		}
	}
	
	public UserField ACCEPTABLE_HACK_FOR_SCENE_EDITOR_getFieldForInstanceInJava( Object key ) {
		assert this.inverseFieldMap != null;
		return this.inverseFieldMap.get( key );
	}

	@Override
	public String toString() {
		if( this.nextInstance != null ) {
			return this.nextInstance.toString();
		} else {
			return null;
		}
	}
}
