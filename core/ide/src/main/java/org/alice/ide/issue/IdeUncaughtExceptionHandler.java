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
package org.alice.ide.issue;

import edu.cmu.cs.dennisc.javax.swing.JDialogBuilder;
import edu.cmu.cs.dennisc.javax.swing.WindowStack;
import org.alice.ide.issue.croquet.LgnaExceptionComposite;
import org.lgna.common.LgnaRuntimeException;
import org.lgna.croquet.Application;
import org.lgna.croquet.views.Frame;
import org.lgna.issue.AbstractUncaughtExceptionHandler;
import org.lgna.issue.ApplicationIssueConfiguration;
import org.lgna.issue.swing.JSubmitPane;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

/**
 * @author Dennis Cosgrove
 */
public abstract class IdeUncaughtExceptionHandler extends AbstractUncaughtExceptionHandler {
  private static final String SILENTLY_FAIL_TEXT = "Silently Fail";
  private static final String CONTINUE_TEXT = "Continue";

  public IdeUncaughtExceptionHandler(ApplicationIssueConfiguration config) {
    this.config = config;
  }

  @Override
  protected boolean handleUncaughtLgnaRuntimeException(Thread thread, Throwable originalThrowable, LgnaRuntimeException originalThrowableOrTarget) {
    Application application = Application.getActiveInstance();
    if (application != null) {
      //todo: check to see if program running
      new LgnaExceptionComposite(thread, originalThrowableOrTarget).getLaunchOperation().fire();
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected void handleUncaughtException(Thread thread, Throwable originalThrowable, Throwable originalThrowableOrTarget) {
    this.count++;
    if (this.isBugReportSubmissionPaneDesired) {
      final JSubmitPane jSubmitPane = new JSubmitPane(thread, originalThrowable, originalThrowableOrTarget, this.config);

      StringBuilder sbTitle = new StringBuilder();
      sbTitle.append("Please Submit Bug Report: ");
      sbTitle.append(config.getApplicationName());

      final JDialog dialog = new JDialogBuilder().title(sbTitle.toString()).isModal(true).build();

      dialog.getContentPane().add(jSubmitPane, BorderLayout.CENTER);
      dialog.pack();

      //todo
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          dialog.pack();
        }
      });

      dialog.setVisible(true);

      if (jSubmitPane.isSubmitAttempted()) {
        //pass
      } else {
        if (this.count > 1) {
          Object[] options = {CONTINUE_TEXT, SILENTLY_FAIL_TEXT};
          String message = "If you are caught in an unending stream of exceptions:\n    1) Press the \"" + SILENTLY_FAIL_TEXT + "\" button,\n    2) Attempt save your project to a different file (use Save As...), and\n    3) Restart " + config.getApplicationName() + ".\nElse\n    1) Press the \"" + CONTINUE_TEXT + "\" button.";
          String title = "Multiple Exceptions Detected";
          int resultIgnore = JOptionPane.showOptionDialog(WindowStack.peek(), message, title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, CONTINUE_TEXT);
          if (resultIgnore == JOptionPane.NO_OPTION) {
            this.isBugReportSubmissionPaneDesired = false;
          }
        }
      }
    }
    boolean isSystemExitDesired = true;
    Application application = Application.getActiveInstance();
    if (application != null) {
      Frame frame = application.getDocumentFrame().getFrame();
      if (frame != null) {
        if (frame.isVisible()) {
          isSystemExitDesired = false;
        }
      }
    }
    if (isSystemExitDesired) {
      JOptionPane.showMessageDialog(null, "Exception occurred before application was able to show window.  Exiting.");
      System.exit(-1);
    }
  }

  private final ApplicationIssueConfiguration config;
  private boolean isBugReportSubmissionPaneDesired = true;
  private int count = 0;
}
