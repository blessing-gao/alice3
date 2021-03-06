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

package org.alice.ide.declarationseditor.components;

import org.alice.ide.croquet.models.IdeDragModel;
import org.alice.ide.croquet.models.ui.preferences.IsJavaCodeOnTheSideState;
import org.alice.ide.declarationseditor.DeclarationComposite;
import org.alice.ide.javacode.croquet.views.JavaCodeView;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.event.ValueEvent;
import org.lgna.croquet.event.ValueListener;
import org.lgna.croquet.views.AwtComponentView;
import org.lgna.croquet.views.BorderPanel;
import org.lgna.croquet.views.SideBySideScrollPane;

import javax.swing.BorderFactory;
import java.util.List;

/**
 * @author Dennis Cosgrove
 */
public abstract class DeclarationView extends BorderPanel {
  public DeclarationView(DeclarationComposite composite) {
    super(composite);
    this.javaCodeView = new JavaCodeView(composite.getDeclaration());
    this.sideBySideScrollPane.setBackgroundColor(this.getBackgroundColor());
    this.sideBySideScrollPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
  }

  public abstract void addPotentialDropReceptors(List<DropReceptor> out, IdeDragModel dragModel);

  protected void setJavaCodeOnTheSide(boolean value, boolean isFirstTime) {
    AwtComponentView<?> mainComponent = this.getMainComponent();
    if (value) {
      if (isFirstTime) {
        //pass
      } else {
        this.removeComponent(mainComponent);
      }
      this.sideBySideScrollPane.setLeadingView(mainComponent);
      this.sideBySideScrollPane.setTrailingView(this.javaCodeView);
      this.addCenterComponent(sideBySideScrollPane);
    } else {
      if (isFirstTime) {
        //pass
      } else {
        this.removeComponent(this.sideBySideScrollPane);
      }
      this.sideBySideScrollPane.setLeadingView(null);
      this.sideBySideScrollPane.setTrailingView(null);
      this.addCenterComponent(mainComponent);
    }
  }

  private final ValueListener<Boolean> isJavaCodeOnTheSideListener = new ValueListener<Boolean>() {
    @Override
    public void valueChanged(ValueEvent<Boolean> e) {
      synchronized (getTreeLock()) {
        boolean isFirstTime = getCenterComponent() == null;
        setJavaCodeOnTheSide(e.getNextValue(), isFirstTime);
        revalidateAndRepaint();
      }
    }
  };

  @Override
  protected void handleDisplayable() {
    IsJavaCodeOnTheSideState.getInstance().addAndInvokeNewSchoolValueListener(this.isJavaCodeOnTheSideListener);
    super.handleDisplayable();
  }

  @Override
  protected void handleUndisplayable() {
    super.handleUndisplayable();
    IsJavaCodeOnTheSideState.getInstance().removeNewSchoolValueListener(this.isJavaCodeOnTheSideListener);
  }

  public SideBySideScrollPane getSideBySideScrollPane() {
    return this.sideBySideScrollPane;
  }

  protected abstract AwtComponentView<?> getMainComponent();

  private final SideBySideScrollPane sideBySideScrollPane = new SideBySideScrollPane();
  private final JavaCodeView javaCodeView;
}
