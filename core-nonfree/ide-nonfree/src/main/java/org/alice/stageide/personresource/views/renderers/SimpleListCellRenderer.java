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
package org.alice.stageide.personresource.views.renderers;

import edu.cmu.cs.dennisc.javax.swing.components.JBorderPane;
import org.alice.stageide.personresource.views.IngredientsView;
import org.lgna.story.resources.sims2.LocalizedResource;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

/**
 * @author Dennis Cosgrove
 */
public enum SimpleListCellRenderer implements ListCellRenderer {
  SINGLETON;

  private JBorderPane pane = new JBorderPane();
  private JLabel label = new JLabel();

  private SimpleListCellRenderer() {
    label.setHorizontalAlignment(SwingUtilities.CENTER);
    label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    label.setOpaque(true);
    pane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    pane.setOpaque(false);
    pane.add(label, BorderLayout.CENTER);
  }

  public String getLocalizedText(Object value) {
    return value.toString();
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    String text;
    if (value instanceof LocalizedResource) {
      text = ((LocalizedResource) value).getLocalizedDisplayText();
    } else {
      text = value.toString();
    }
    this.label.setText(text);
    if (isSelected) {
      this.label.setBackground(IngredientsView.SELECTED_COLOR);
      this.label.setForeground(Color.BLACK);
      this.label.setOpaque(true);
    } else {
      this.label.setBackground(IngredientsView.UNSELECTED_COLOR);
      this.label.setForeground(list.isEnabled() ? Color.BLACK : Color.GRAY);
      this.label.setOpaque(list.isEnabled());
    }
    return this.pane;
  }
}
