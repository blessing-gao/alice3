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

package org.alice.ide.instancefactory;

/**
 * @author Dennis Cosgrove
 */
public class InstanceFactoryState extends org.lgna.croquet.CustomItemStateWithInternalBlank< InstanceFactory > {
	private static class SingletonHolder {
		private static InstanceFactoryState instance = new InstanceFactoryState();
	}
	public static InstanceFactoryState getInstance() {
		return SingletonHolder.instance;
	}
	private InstanceFactory value;
	private InstanceFactoryState() {
		super( org.lgna.croquet.Application.UI_STATE_GROUP, java.util.UUID.fromString( "f4e26c9c-0c3d-4221-95b3-c25df0744a97" ), InstanceFactoryCodec.SINGLETON );
	}
	@Override
	protected java.util.List< org.lgna.croquet.CascadeBlankChild > updateBlankChildren( java.util.List< org.lgna.croquet.CascadeBlankChild > rv, org.lgna.croquet.cascade.BlankNode< InstanceFactory > blankNode ) {
		org.alice.ide.ApiConfigurationManager apiConfigurationManager = org.alice.ide.IDE.getActiveInstance().getApiConfigurationManager();
		rv.add( ThisInstanceFactoryFillIn.getInstance() );
		//rv.add( ThisMethodInvocationFactoryFillIn.getInstance( org.lookingglassandalice.storytelling.Entity.class, "getName" ) );
		org.lgna.project.ast.NamedUserType type = org.alice.ide.IDE.getActiveInstance().getSceneType();
		for( org.lgna.project.ast.UserField field : type.getDeclaredFields() ) {
			if( apiConfigurationManager.isInstanceFactoryDesiredForType( field.getValueType() ) ) {
				InstanceFactoryFillInWithoutBlanks fillIn = ThisFieldAccessFactoryFillIn.getInstance( field );
				org.lgna.croquet.CascadeMenuModel< InstanceFactory > subMenu = apiConfigurationManager.getInstanceFactorySubMenuForThisFieldAccess( field );
				if( subMenu != null ) {
					rv.add( new org.lgna.croquet.CascadeFillInMenuCombo< InstanceFactory >( fillIn, subMenu ) );
				} else {
					rv.add( fillIn );
				}
			}
			//rv.add( ThisFieldAccessMethodInvocationFactoryFillIn.getInstance( field, org.lookingglassandalice.storytelling.Entity.class, "getName" ) );
		}
		return rv;
	}
	@Override
	public org.alice.ide.instancefactory.InstanceFactory getValue() {
		return this.value;
	}
	@Override
	protected void handleValueChange( org.alice.ide.instancefactory.InstanceFactory value ) {
		this.value = value;
	}
	
	public void setValue( org.alice.ide.instancefactory.InstanceFactory value ) {
		this.fireChanging( this.value, value, false );
		this.handleValueChange( value );
		this.fireChanged( this.value, value, false );
	}
}
