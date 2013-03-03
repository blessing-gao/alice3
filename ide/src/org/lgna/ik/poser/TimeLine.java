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
package org.lgna.ik.poser;

import java.util.List;

import org.lgna.ik.poser.events.TimeLineListener;
import org.lgna.story.AnimationStyle;
import org.lgna.story.Duration;
import org.lgna.story.SetPose.Detail;

import edu.cmu.cs.dennisc.java.util.concurrent.Collections;

/**
 * @author Matt May
 */
public class TimeLine {
	private int startTime = 0;
	private int currTime = 5;
	private int endTime = 10;

	private final List<PoseEvent> posesInTimeline = Collections.newCopyOnWriteArrayList();

	private final List<TimeLineListener> listeners = Collections.newCopyOnWriteArrayList();

	public class PoseEvent {

		private int eventTime;
		private Pose pose;
		private AnimationStyle style = AnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY;

		PoseEvent( int time, Pose pose ) {
			this.eventTime = time;
			this.pose = pose;
		}

		public void setStyle( AnimationStyle style ) {
			this.style = style;
		}

		public int getEventTime() {
			return this.eventTime;
		}

	}

	public void addTimeLineListener( TimeLineListener listener ) {
		this.listeners.add( listener );
	}

	public void removeTimeLineListener( TimeLineListener listener ) {
		this.listeners.remove( listener );
	}

	private void fireChanged() {
		for( TimeLineListener listener : this.listeners ) {
			listener.changed();
		}
	}

	public void addEventAtCurrentTime( Pose pose ) {
		PoseEvent poseEvent = new PoseEvent( currTime, pose );
		insertPoseEvent( poseEvent );
	}

	private void insertPoseEvent( PoseEvent poseEvent ) {
		for( int i = 0; i != posesInTimeline.size(); ++i ) {
			if( posesInTimeline.get( i ).eventTime < poseEvent.eventTime ) {
				posesInTimeline.add( i, poseEvent );
				return;
			}
		}
		posesInTimeline.add( poseEvent );
		this.fireChanged();
	}

	public Detail[] getParametersForPose( Pose pose ) {
		Detail[] rv = new Detail[ 2 ];//duration, style
		for( int i = 0; i != posesInTimeline.size(); ++i ) {
			PoseEvent itr = posesInTimeline.get( i );
			if( itr.pose.equals( pose ) ) {
				rv[ 0 ] = itr.style;
				if( i == 0 ) {
					rv[ 1 ] = new Duration( 0 );
				} else {
					rv[ 1 ] = new Duration( itr.eventTime - itr.eventTime );
				}
			}
		}
		return rv;
	}

	public List<PoseEvent> getPosesInTimeline() {
		return this.posesInTimeline;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public int getCurrentTime() {
		return currTime;
	}

	public void setCurrentTime( int time ) {
		this.currTime = time;
		fireChanged();
	}
}
