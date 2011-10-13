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

package org.lgna.story;

import org.lgna.project.annotations.*;
/**
 * @author Dennis Cosgrove
 */
public abstract class Turnable extends Entity {
	@Override
	/*package-private*/abstract org.lgna.story.implementation.AbstractTransformableImp getImplementation();

//	@MethodTemplate(isFollowedByLongerMethod = true)
//	public void turn( TurnDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount ) {
//		this.turn( direction, amount, new Turn.Detail[] {} );
//	}
	@MethodTemplate()
	public void turn( TurnDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount, Turn.Detail... details ) {
		this.getImplementation().animateApplyRotationInRevolutions( 
				direction.getAxis(), 
				amount.doubleValue(), 
				AsSeenBy.getValue( details, this ).getImplementation(), 
				Duration.getValue( details ), 
				AnimationStyle.getValue( details ) 
		);
		this.turn( TurnDirection.LEFT, 1.0, Turn.duration( 1.0 ), Turn.animationStyle( true, false ), Turn.asSeenBy( this.getVehicle() ) );
	}
//	@MethodTemplate(isFollowedByLongerMethod = true)
//	public void roll( RollDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount ) {
//		this.roll( direction, amount, new Roll.Detail[] {} );
//	}
	@MethodTemplate()
	public void roll( RollDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount, Roll.Detail... details ) {
		this.getImplementation().animateApplyRotationInRevolutions( 
				direction.getAxis(), 
				amount.doubleValue(), 
				AsSeenBy.getValue( details, this ).getImplementation(), 
				Duration.getValue( details ), 
				AnimationStyle.getValue( details ) 
		);
	}
//	@MethodTemplate(isFollowedByLongerMethod = true)
//	public void turn( TurnDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount ) {
//		this.turn( direction, amount, new VantagePointWithAsSeenByDetails.Value() );
//	}
//	@MethodTemplate()
//	public void turn( TurnDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount, VantagePointWithAsSeenByDetails.Value details ) {
//		this.getImplementation().animateApplyRotationInRevolutions( direction.getAxis(), amount.doubleValue(), details.getAsSeenBy( this ).getImplementation(), details.getDuration(), details.getStyle() );
//	}
//	@MethodTemplate(isFollowedByLongerMethod = true)
//	public void roll( RollDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount ) {
//		this.roll( direction, amount, new VantagePointWithAsSeenByDetails.Value() );
//	}
//	@MethodTemplate()
//	public void roll( RollDirection direction, @ValueTemplate(detailsEnumCls = org.lgna.story.annotation.AngleDetails.class) Number amount, VantagePointWithAsSeenByDetails.Value details ) {
//		this.getImplementation().animateApplyRotationInRevolutions( direction.getAxis(), amount.doubleValue(), details.getAsSeenBy( this ).getImplementation(), details.getDuration(), details.getStyle() );
//	}

	@MethodTemplate(visibility = Visibility.TUCKED_AWAY)
	public Orientation getOrientationRelativeToVehicle() {
		return Orientation.createInstance( this.getImplementation().getLocalOrientation() );
	}
	@MethodTemplate(visibility = Visibility.TUCKED_AWAY)
	public void setOrientationRelativeToVehicle( Orientation position ) {
		this.getImplementation().setLocalOrientation( position.getInternal() );
	}
}
