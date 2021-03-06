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

package org.alice.ide.ast.draganddrop;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.java.util.Objects;
import org.alice.ide.ProjectStack;
import org.alice.ide.declarationseditor.CodeComposite;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.DropSite;
import org.lgna.project.ProgramTypeUtilities;
import org.lgna.project.Project;
import org.lgna.project.ast.AbstractCode;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.Statement;

import java.util.UUID;

/**
 * @author Dennis Cosgrove
 */
public final class BlockStatementIndexPair implements DropSite {
  private final BlockStatement blockStatement;
  private final int index;

  public static BlockStatementIndexPair createInstanceFromChildStatement(Statement statement) {
    assert statement != null;
    Node parent = statement.getParent();
    assert parent instanceof BlockStatement : parent;
    BlockStatement blockStatement = (BlockStatement) parent;
    int index = blockStatement.statements.indexOf(statement);
    return new BlockStatementIndexPair(blockStatement, index);
  }

  public BlockStatementIndexPair(BlockStatement blockStatement, int index) {
    assert blockStatement != null;
    assert index >= 0 : index + " " + blockStatement;
    this.blockStatement = blockStatement;
    this.index = index;
  }

  public BlockStatementIndexPair(BinaryDecoder binaryDecoder) {
    Project project = ProjectStack.peekProject();
    UUID id = binaryDecoder.decodeId();
    this.blockStatement = ProgramTypeUtilities.lookupNode(project, id);
    assert this.blockStatement != null;
    this.index = binaryDecoder.decodeInt();
  }

  @Override
  public void encode(BinaryEncoder binaryEncoder) {
    binaryEncoder.encode(this.blockStatement.getId());
    binaryEncoder.encode(this.index);
  }

  public BlockStatement getBlockStatement() {
    return this.blockStatement;
  }

  public int getIndex() {
    return this.index;
  }

  @Override
  public DropReceptor getOwningDropReceptor() {
    AbstractCode code = this.blockStatement.getFirstAncestorAssignableTo(AbstractCode.class);
    return CodeComposite.getInstance(code).getView().getCodePanelWithDropReceptor().getDropReceptor();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BlockStatementIndexPair) {
      BlockStatementIndexPair bsip = (BlockStatementIndexPair) o;
      return Objects.equals(this.blockStatement, bsip.blockStatement) && (this.index == bsip.index);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int rv = 17;
    if (this.blockStatement != null) {
      rv = (37 * rv) + this.blockStatement.hashCode();
    }
    rv = (37 * rv) + this.index;
    return rv;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append("[blockStatement=");
    sb.append(this.blockStatement);
    sb.append(";index=");
    sb.append(this.index);
    sb.append(";parent=");
    sb.append(this.blockStatement != null ? this.blockStatement.getParent() : null);
    sb.append("]");
    return sb.toString();
  }
}
