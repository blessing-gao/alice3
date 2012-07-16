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
package org.alice.ide.ast.declaration;

/**
 * @author Dennis Cosgrove
 */
public abstract class ManagedFieldDeclarationComposite extends FieldDeclarationComposite {
	public ManagedFieldDeclarationComposite( java.util.UUID migrationId, Details details ) {
		super( migrationId, details );
	}
	@Override
	protected boolean isFieldFinal() {
		return false;
	}
	@Override
	protected org.lgna.project.ast.ManagementLevel getManagementLevel() {
		return org.lgna.project.ast.ManagementLevel.MANAGED;
	}
	@Override
	public org.lgna.project.ast.UserType<?> getDeclaringType() {
		return org.alice.ide.IDE.getActiveInstance().getSceneType();
	}
	protected static class EditCustomization {
		private final java.util.List< org.lgna.project.ast.Statement > doStatements = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();
		private final java.util.List< org.lgna.project.ast.Statement > undoStatements = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();
		private final java.util.List< org.lgna.common.Resource > resources = edu.cmu.cs.dennisc.java.util.Collections.newLinkedList();
		
		public void addDoStatement( org.lgna.project.ast.Statement statement ) {
			this.doStatements.add( statement );
		}
		public org.lgna.project.ast.Statement[] getDoStatements() {
			return edu.cmu.cs.dennisc.java.lang.ArrayUtilities.createArray( this.doStatements, org.lgna.project.ast.Statement.class );
		}
		public void addUndoStatement( org.lgna.project.ast.Statement statement ) {
			this.undoStatements.add( statement );
		}
		public org.lgna.project.ast.Statement[] getUndoStatements() {
			return edu.cmu.cs.dennisc.java.lang.ArrayUtilities.createArray( this.undoStatements, org.lgna.project.ast.Statement.class );
		}
		public void addResource( org.lgna.common.Resource resource ) {
			this.resources.add( resource );
		}
		public org.lgna.common.Resource[] getResources() {
			return edu.cmu.cs.dennisc.java.lang.ArrayUtilities.createArray( this.resources, org.lgna.common.Resource.class );
		}
	}

	
	protected EditCustomization customize( org.lgna.croquet.history.CompletionStep<?> step, org.lgna.project.ast.UserType< ? > declaringType, org.lgna.project.ast.UserField field, EditCustomization rv ) {
		rv.addDoStatement(org.alice.stageide.sceneeditor.SetUpMethodGenerator.createSetVehicleStatement( field, null, true));
		rv.addUndoStatement(org.alice.stageide.sceneeditor.SetUpMethodGenerator.createSetVehicleStatement( field, null, false));
		return rv;
	}
	@Override
	protected org.alice.ide.croquet.edits.ast.DeclareFieldEdit createEdit( org.lgna.croquet.history.CompletionStep<?> completionStep, org.lgna.project.ast.UserType<?> declaringType, org.lgna.project.ast.UserField field ) {
		EditCustomization customization = new EditCustomization();
		this.customize( completionStep, declaringType, field, customization );
		return new org.alice.ide.croquet.edits.ast.DeclareGalleryFieldEdit( completionStep, this.getDeclaringType(), field, customization.getDoStatements(), customization.getUndoStatements() );
	}
}
