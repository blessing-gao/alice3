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

package org.alice.ide.croquet.resolvers;

/**
 * @author Dennis Cosgrove
 */
public class InternalCascadingMenuModelStaticGetInstanceKeyedResolver extends edu.cmu.cs.dennisc.croquet.StaticGetInstanceKeyedResolver< edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel > implements edu.cmu.cs.dennisc.croquet.RetargetableResolver< edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel > {
	private static final Class<?>[] PARAMETER_TYPES = new Class[] { java.util.UUID.class };
	public InternalCascadingMenuModelStaticGetInstanceKeyedResolver( edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel instance ) {
		super( instance );
		java.util.UUID id = this.getInstance().getFillIn().getId();
		System.err.println( "InternalCascadingMenuModelStaticGetInstanceKeyedResolver: " + id );
	}
	public InternalCascadingMenuModelStaticGetInstanceKeyedResolver( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
		super( binaryDecoder );
	}

	public void retarget( edu.cmu.cs.dennisc.croquet.Retargeter retargeter ) {
		Object[] arguments = this.getArguments();
		assert arguments != null;
		assert arguments.length == 1;
		
		Object original = arguments[ 0 ];
		Object replacement = retargeter.retarget( original );
		arguments[ 0 ] = replacement;
		edu.cmu.cs.dennisc.print.PrintUtilities.println( "retarget: ", original, "->", replacement );
	}

	@Override
	protected java.lang.Class< edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel > decodeInstanceClass( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
		return edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel.class;
	}
	@Override
	protected void encodeInstanceClass( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder, java.lang.Class< edu.cmu.cs.dennisc.cascade.InternalCascadingMenuModel > cls ) {
	}
	@Override
	protected Class< ? >[] decodeParameterTypes( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
		return PARAMETER_TYPES;
	}
	@Override
	protected void encodeParameterTypes( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder ) {
	}

	@Override
	protected Object[] decodeArguments( edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder ) {
		java.util.UUID id = binaryDecoder.decodeId();
		System.err.println( "decodeArguments: " + id );
		return new Object[] { id };
	}
	@Override
	protected void encodeArguments( edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder ) {
		java.util.UUID id = this.getInstance().getFillIn().getId();
		System.err.println( "encodeArguments: " + id );
		binaryEncoder.encode( id );
	}
}
