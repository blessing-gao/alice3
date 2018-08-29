/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
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
 *******************************************************************************/

package org.alice.ide.ast.delete.edits;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.alice.ide.IDE;
import org.alice.ide.ast.delete.DeleteDeclarationLikeSubstanceOperation;
import org.alice.ide.croquet.codecs.NodeCodec;
import org.alice.ide.declarationseditor.DeclarationTabState;
import org.alice.ide.project.ProjectChangeOfInterestManager;
import org.lgna.croquet.Application;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.NodeListProperty;
import org.lgna.project.ast.NodeUtilities;
import org.lgna.project.ast.UserMember;
import org.lgna.project.ast.UserType;

/**
 * @author Dennis Cosgrove
 */
public abstract class DeleteMemberEdit<M extends UserMember> extends AbstractEdit<DeleteDeclarationLikeSubstanceOperation> {
	private final UserType<?> declaringType;
	private final int index;
	private final M member;

	public DeleteMemberEdit( UserActivity userActivity, M member ) {
		super( userActivity );
		this.declaringType = member.getDeclaringType();
		assert this.declaringType != null : member;
		this.index = this.getNodeListProperty( this.declaringType ).indexOf( member );
		assert this.index != -1 : member;
		this.member = member;
	}

	public DeleteMemberEdit( BinaryDecoder binaryDecoder, Object step ) {
		super( binaryDecoder, step );
		this.declaringType = NodeCodec.getInstance( UserType.class ).decodeValue( binaryDecoder );
		this.index = binaryDecoder.decodeInt();
		this.member = (M)NodeCodec.getInstance( UserMember.class ).decodeValue( binaryDecoder );
	}

	protected abstract NodeListProperty<M> getNodeListProperty( UserType<?> declaringType );

	@Override
	public void encode( BinaryEncoder binaryEncoder ) {
		super.encode( binaryEncoder );
		NodeCodec.getInstance( UserType.class ).encodeValue( binaryEncoder, this.declaringType );
		binaryEncoder.encode( this.index );
		NodeCodec.getInstance( UserMember.class ).encodeValue( binaryEncoder, this.member );
	}

	@Override
	protected final void doOrRedoInternal( boolean isDo ) {
		NodeListProperty<M> owner = this.getNodeListProperty( this.declaringType );
		assert this.index == owner.indexOf( this.member ) : this.member;
		owner.remove( this.index );
		//todo: remove
		ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
		DeclarationTabState declarationTabState = IDE.getActiveInstance().getDocumentFrame().getDeclarationsEditorComposite().getTabState();
		declarationTabState.removeAllOrphans();
	}

	@Override
	protected final void undoInternal() {
		NodeListProperty<M> owner = this.getNodeListProperty( this.declaringType );
		owner.add( this.index, member );
		//todo: remove
		ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();

	}

	@Override
	protected void appendDescription( StringBuilder rv, DescriptionStyle descriptionStyle ) {
		rv.append( "delete:" );
		NodeUtilities.safeAppendRepr( rv, member, Application.getLocale() );
	}
}
