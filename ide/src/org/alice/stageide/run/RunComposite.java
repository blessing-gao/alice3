/**
 * Copyright (c) 2006-2012, Carnegie Mellon University. All rights reserved.
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
package org.alice.stageide.run;

/**
 * @author Dennis Cosgrove
 */
public class RunComposite extends org.lgna.croquet.PlainDialogOperationComposite<org.alice.stageide.run.views.RunView> {
	private static class SingletonHolder {
		private static RunComposite instance = new RunComposite();
	}

	public static RunComposite getInstance() {
		return SingletonHolder.instance;
	}

	private RunComposite() {
		super( java.util.UUID.fromString( "985b3795-e1c7-4114-9819-fae4dcfe5676" ), org.alice.ide.IDE.RUN_GROUP );
		//todo: move to localize
		this.getOperation().setSmallIcon( new org.alice.stageide.run.views.icons.RunIcon() );
	}

	private transient org.alice.stageide.program.RunProgramContext programContext;
	public static final double WIDTH_TO_HEIGHT_RATIO = 16.0 / 9.0;
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = (int)( DEFAULT_WIDTH / WIDTH_TO_HEIGHT_RATIO );
	private java.awt.Point location = new java.awt.Point( 100, 100 );
	private java.awt.Dimension size = null;

	@Override
	protected GoldenRatioPolicy getGoldenRatioPolicy() {
		return null;
	}

	@Override
	protected java.awt.Point getDesiredWindowLocation() {
		return this.location;
	}

	@Override
	protected java.awt.Dimension calculateWindowSize( org.lgna.croquet.components.AbstractWindow<?> window ) {
		if( this.size != null ) {
			return this.size;
		} else {
			return super.calculateWindowSize( window );
		}
	}

	private class ProgramRunnable implements Runnable {
		public ProgramRunnable( org.lgna.story.implementation.ProgramImp.AwtContainerInitializer awtContainerInitializer ) {
			RunComposite.this.programContext = new org.alice.stageide.program.RunProgramContext();
			RunComposite.this.programContext.getProgramImp().setRestartAction( RunComposite.this.restartAction );
			RunComposite.this.programContext.initializeInContainer( awtContainerInitializer );
		}

		public void run() {
			RunComposite.this.programContext.setActiveScene();
		}
	}

	private final class RunAwtContainerInitializer implements org.lgna.story.implementation.ProgramImp.AwtContainerInitializer {
		public void addComponents( edu.cmu.cs.dennisc.lookingglass.OnscreenLookingGlass onscreenLookingGlass, javax.swing.JPanel controlPanel ) {
			org.alice.stageide.run.views.RunView runView = RunComposite.this.getView();
			runView.forgetAndRemoveAllComponents();

			org.lgna.croquet.components.Component<?> lookingGlassContainer = new org.lgna.croquet.components.AwtAdapter( onscreenLookingGlass.getAWTComponent() );
			org.lgna.croquet.components.FixedAspectRatioPanel fixedAspectRatioPanel = new org.lgna.croquet.components.FixedAspectRatioPanel( lookingGlassContainer, WIDTH_TO_HEIGHT_RATIO );
			fixedAspectRatioPanel.setBackgroundColor( java.awt.Color.BLACK );
			runView.getAwtComponent().add( controlPanel, java.awt.BorderLayout.PAGE_START );
			runView.addCenterComponent( fixedAspectRatioPanel );
			runView.revalidateAndRepaint();
		}
	}

	private final RunAwtContainerInitializer runAwtContainerInitializer = new RunAwtContainerInitializer();

	private void startProgram() {
		new org.lgna.common.ComponentThread( new ProgramRunnable( runAwtContainerInitializer ), RunComposite.this.getName() ).start();
	}

	private void stopProgram() {
		if( this.programContext != null ) {
			this.programContext.cleanUpProgram();
			this.programContext = null;
		} else {
			edu.cmu.cs.dennisc.java.util.logging.Logger.warning( this );
		}
	}

	private class RestartAction extends javax.swing.AbstractAction {
		public void actionPerformed( java.awt.event.ActionEvent e ) {
			RunComposite.this.stopProgram();
			RunComposite.this.startProgram();
		}
	};

	private final RestartAction restartAction = new RestartAction();

	@Override
	protected void localize() {
		super.localize();
		this.restartAction.putValue( javax.swing.Action.NAME, "restart" );
	}

	@Override
	protected void handlePreShowDialog( org.lgna.croquet.history.CompletionStep<?> step ) {
		super.handlePreShowDialog( step );
		this.startProgram();
		if( this.size != null ) {
			//pas
		} else {
			this.programContext.getOnscreenLookingGlass().getAWTComponent().setPreferredSize( new java.awt.Dimension( DEFAULT_WIDTH, DEFAULT_HEIGHT ) );
			org.lgna.croquet.components.Dialog dialog = step.getEphemeralDataFor( org.lgna.croquet.dialog.DialogUtilities.DIALOG_KEY );
			dialog.pack();
		}
	}

	@Override
	protected void handlePostHideDialog( org.lgna.croquet.history.CompletionStep<?> step ) {
		org.lgna.croquet.components.Dialog dialog = step.getEphemeralDataFor( org.lgna.croquet.dialog.DialogUtilities.DIALOG_KEY );
		this.location = dialog.getLocation();
		this.size = dialog.getSize();
		super.handlePostHideDialog( step );
	}

	@Override
	protected void handleFinally( org.lgna.croquet.history.CompletionStep<?> step, org.lgna.croquet.components.Dialog dialog ) {
		this.stopProgram();
		super.handleFinally( step, dialog );
	}

	@Override
	protected org.alice.stageide.run.views.RunView createView() {
		return new org.alice.stageide.run.views.RunView( this );
	}
}
