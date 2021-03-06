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
package org.lgna.croquet.edits;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import edu.cmu.cs.dennisc.java.lang.ClassUtilities;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.Group;
import org.lgna.croquet.history.UserActivity;

import javax.swing.undo.CannotRedoException;

/**
 * @author Dennis Cosgrove
 */
public abstract class AbstractEdit<M extends CompletionModel> implements Edit, BinaryEncodableAndDecodable {
  public static <E extends AbstractEdit<?>> E createCopy(E original, UserActivity activity) {
    assert activity != null : original;
    original.preCopy();
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(original);
    BinaryDecoder decoder = encoder.createDecoder();
    E rv = decoder.decodeBinaryEncodableAndDecodable(activity);
    original.postCopy(rv);
    return rv;
  }

  private transient UserActivity userActivity;

  public AbstractEdit(UserActivity userActivity) {
    this.userActivity = userActivity;
  }

  public AbstractEdit(BinaryDecoder binaryDecoder, Object step) {
    this.userActivity = (UserActivity) step;
  }

  @Override
  public void encode(BinaryEncoder binaryEncoder) {
  }

  protected void preCopy() {
  }

  protected void postCopy(AbstractEdit<?> result) {
  }

  public M getModel() {
    return userActivity != null ? (M) userActivity.getCompletionModel() : null;
  }

  @Override
  public Group getGroup() {
    M model = this.getModel();
    if (model != null) {
      return model.getGroup();
    } else {
      return null;
    }
  }

  @Override
  public boolean canUndo() {
    return true;
  }

  @Override
  public boolean canRedo() {
    return true;
  }

  protected abstract void doOrRedoInternal(boolean isDo);

  protected abstract void undoInternal();

  @Override
  public final void doOrRedo(boolean isDo) {
    if (!isDo && !canRedo()) {
      throw new CannotRedoException();
    }
    this.doOrRedoInternal(isDo);
  }

  @Override
  public final void undo() {
    if (!canUndo()) {
      throw new CannotRedoException();
    }
    this.undoInternal();
  }

  protected static enum DescriptionStyle {
    TERSE(false, false), DETAILED(true, false), LOG(true, true);
    private final boolean isDetailed;
    private final boolean isLog;

    private DescriptionStyle(boolean isDetailed, boolean isLog) {
      this.isDetailed = isDetailed;
      this.isLog = isLog;
    }

    public boolean isDetailed() {
      return this.isDetailed;
    }

    public boolean isLog() {
      return this.isLog;
    }
  }

  protected abstract void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle);

  @Override
  public String getRedoPresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Redo:");
    this.appendDescription(sb, DescriptionStyle.TERSE);
    return sb.toString();
  }

  @Override
  public String getUndoPresentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Undo:");
    this.appendDescription(sb, DescriptionStyle.TERSE);
    return sb.toString();
  }

  @Override
  public final String getTerseDescription() {
    StringBuilder sb = new StringBuilder();
    this.appendDescription(sb, DescriptionStyle.TERSE);
    if (sb.length() == 0) {
      sb.append(ClassUtilities.getTrimmedClassName(this.getClass()));
    }
    return sb.toString();
  }

  @Override
  public final String getDetailedDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": ");
    this.appendDescription(sb, DescriptionStyle.DETAILED);
    return sb.toString();
  }

  @Override
  public final String getLogDescription() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getName());
    sb.append(": ");
    this.appendDescription(sb, DescriptionStyle.LOG);
    return sb.toString();
  }

  @Override
  public String toString() {
    return this.getDetailedDescription();
  }
}
