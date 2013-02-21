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

import org.lgna.story.ImplementationAccessor;
import org.lgna.story.SBiped;
import org.lgna.story.SJoint;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.resources.JointId;

import edu.cmu.cs.dennisc.math.AffineMatrix4x4;

/**
 * @author Matt May
 */
public class Pose {
	private static JointId getIDFor( SJoint sJoint ) {
		return ( (JointImp)ImplementationAccessor.getImplementation( sJoint ) ).getJointId();
	}

	private static AffineMatrix4x4 getOrientation( SJoint sJoint ) {
		return ( (JointImp)ImplementationAccessor.getImplementation( sJoint ) ).getLocalTransformation();
	}

	public static Pose createPoseFromBiped( SBiped biped ) {
		JointQPair rightArmBase = new JointQPair( getIDFor( biped.getRightClavicle() ), getOrientation( biped.getRightClavicle() ),
				new JointQPair( getIDFor( biped.getRightShoulder() ), getOrientation( biped.getRightShoulder() ),
						new JointQPair( getIDFor( biped.getRightElbow() ), getOrientation( biped.getRightElbow() ),
								new JointQPair( getIDFor( biped.getRightWrist() ), getOrientation( biped.getRightWrist() ) ) ) ) );

		JointQPair leftArmBase = new JointQPair( getIDFor( biped.getLeftClavicle() ), getOrientation( biped.getLeftClavicle() ),
				new JointQPair( getIDFor( biped.getLeftShoulder() ), getOrientation( biped.getLeftShoulder() ),
						new JointQPair( getIDFor( biped.getLeftElbow() ), getOrientation( biped.getLeftElbow() ),
								new JointQPair( getIDFor( biped.getLeftWrist() ), getOrientation( biped.getLeftWrist() ) ) ) ) );

		JointQPair rightLegBase = new JointQPair( getIDFor( biped.getPelvis() ), getOrientation( biped.getPelvis() ),
				new JointQPair( getIDFor( biped.getRightHip() ), getOrientation( biped.getRightHip() ),
						new JointQPair( getIDFor( biped.getRightKnee() ), getOrientation( biped.getRightKnee() ),
								new JointQPair( getIDFor( biped.getRightAnkle() ), getOrientation( biped.getRightAnkle() ) ) ) ) );

		JointQPair leftLegBase = new JointQPair( getIDFor( biped.getPelvis() ), getOrientation( biped.getPelvis() ),
				new JointQPair( getIDFor( biped.getLeftHip() ), getOrientation( biped.getLeftHip() ),
						new JointQPair( getIDFor( biped.getLeftKnee() ), getOrientation( biped.getLeftKnee() ),
								new JointQPair( getIDFor( biped.getLeftAnkle() ), getOrientation( biped.getLeftAnkle() ) ) ) ) );

		return new Pose( rightArmBase, leftArmBase, rightLegBase, leftLegBase );
	}

	private static AffineMatrix4x4 TODO_DELETE_AND_USE_QUATERNION( edu.cmu.cs.dennisc.math.UnitQuaternion q ) {
		return new AffineMatrix4x4( q, new edu.cmu.cs.dennisc.math.Point3() );
	}

	private static edu.cmu.cs.dennisc.math.UnitQuaternion getQuaternion( org.lgna.story.Orientation orientation ) {
		//todo: org.lgna.story.Orientation should store the UnitQuaternion?
		return ImplementationAccessor.getOrthogonalMatrix3x3( orientation ).createUnitQuaternion();
	}

	public static class Builder {
		private JointQPair rightArmBase;
		private JointQPair leftArmBase;
		private JointQPair rightLegBase;
		private JointQPair leftLegBase;

		public Builder rightArm( org.lgna.story.Orientation rightClavicleOrientation, org.lgna.story.Orientation rightShoulderOrientation, org.lgna.story.Orientation rightElbowOrientation, org.lgna.story.Orientation rightWristOrientation ) {
			assert this.rightArmBase == null : this;
			assert rightClavicleOrientation == null : this;
			assert rightShoulderOrientation == null : this;
			assert rightElbowOrientation == null : this;
			assert rightWristOrientation == null : this;
			this.rightArmBase =
					new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_CLAVICLE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightClavicleOrientation ) ),
							new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_SHOULDER, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightShoulderOrientation ) ),
									new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_ELBOW, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightElbowOrientation ) ),
											new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_WRIST, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightWristOrientation ) ) ) ) ) );
			return this;
		}

		public Builder leftArm( org.lgna.story.Orientation leftClavicleOrientation, org.lgna.story.Orientation leftShoulderOrientation, org.lgna.story.Orientation leftElbowOrientation, org.lgna.story.Orientation leftWristOrientation ) {
			assert this.leftArmBase == null : this;
			assert leftClavicleOrientation == null : this;
			assert leftShoulderOrientation == null : this;
			assert leftElbowOrientation == null : this;
			assert leftWristOrientation == null : this;
			this.leftArmBase =
					new JointQPair( org.lgna.story.resources.BipedResource.LEFT_CLAVICLE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftClavicleOrientation ) ),
							new JointQPair( org.lgna.story.resources.BipedResource.LEFT_SHOULDER, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftShoulderOrientation ) ),
									new JointQPair( org.lgna.story.resources.BipedResource.LEFT_ELBOW, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftElbowOrientation ) ),
											new JointQPair( org.lgna.story.resources.BipedResource.LEFT_WRIST, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftWristOrientation ) ) ) ) ) );
			return this;
		}

		public Builder rightLeg( org.lgna.story.Orientation pelvisOrientation, org.lgna.story.Orientation rightHipOrientation, org.lgna.story.Orientation rightKneeOrientation, org.lgna.story.Orientation rightAnkleOrientation ) {
			assert this.rightLegBase == null : this;
			assert pelvisOrientation == null : this;
			assert rightHipOrientation == null : this;
			assert rightKneeOrientation == null : this;
			assert rightAnkleOrientation == null : this;
			this.rightLegBase =
					new JointQPair( org.lgna.story.resources.BipedResource.PELVIS_LOWER_BODY, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( pelvisOrientation ) ),
							new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_HIP, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightHipOrientation ) ),
									new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_KNEE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightKneeOrientation ) ),
											new JointQPair( org.lgna.story.resources.BipedResource.RIGHT_ANKLE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( rightAnkleOrientation ) ) ) ) ) );
			return this;
		}

		public Builder leftLeg( org.lgna.story.Orientation pelvisOrientation, org.lgna.story.Orientation leftHipOrientation, org.lgna.story.Orientation leftKneeOrientation, org.lgna.story.Orientation leftAnkleOrientation ) {
			assert this.leftLegBase == null : this;
			assert pelvisOrientation == null : this;
			assert leftHipOrientation == null : this;
			assert leftKneeOrientation == null : this;
			assert leftAnkleOrientation == null : this;
			this.leftLegBase =
					new JointQPair( org.lgna.story.resources.BipedResource.PELVIS_LOWER_BODY, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( pelvisOrientation ) ),
							new JointQPair( org.lgna.story.resources.BipedResource.LEFT_HIP, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftHipOrientation ) ),
									new JointQPair( org.lgna.story.resources.BipedResource.LEFT_KNEE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftKneeOrientation ) ),
											new JointQPair( org.lgna.story.resources.BipedResource.LEFT_ANKLE, TODO_DELETE_AND_USE_QUATERNION( getQuaternion( leftAnkleOrientation ) ) ) ) ) );
			return this;
		}

		public Pose build() {
			return new Pose( this.rightArmBase, this.rightLegBase, this.leftArmBase, this.leftLegBase );
		}
	}

	private final JointQPair rightArmBase;
	private final JointQPair leftArmBase;
	private final JointQPair rightLegBase;
	private final JointQPair leftLegBase;

	public Pose( JointQPair raBase, JointQPair rlBase, JointQPair laBase, JointQPair llBase ) {
		this.rightArmBase = raBase;
		this.rightLegBase = rlBase;
		this.leftArmBase = laBase;
		this.leftLegBase = llBase;
	}

	private static org.lgna.story.Orientation createOrientation( edu.cmu.cs.dennisc.math.UnitQuaternion q ) {
		return new org.lgna.story.Orientation( q.x, q.y, q.z, q.w );
	}

	public org.lgna.story.Orientation getRightClavicleOrientation() {
		return createOrientation( this.rightArmBase.getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightShoulderOrientation() {
		return createOrientation( this.rightArmBase.getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightElbowOrientation() {
		return createOrientation( this.rightArmBase.getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightWristOrientation() {
		return createOrientation( this.rightArmBase.getChild().getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftClavicleOrientation() {
		return createOrientation( this.leftArmBase.getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftShoulderOrientation() {
		return createOrientation( this.leftArmBase.getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftElbowOrientation() {
		return createOrientation( this.leftArmBase.getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftWristOrientation() {
		return createOrientation( this.leftArmBase.getChild().getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getPelvisOrientation() {
		return createOrientation( this.rightLegBase.getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightHipOrientation() {
		return createOrientation( this.rightLegBase.getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightKneeOrientation() {
		return createOrientation( this.rightLegBase.getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getRightAnkleOrientation() {
		return createOrientation( this.rightLegBase.getChild().getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftHipOrientation() {
		return createOrientation( this.leftLegBase.getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftKneeOrientation() {
		return createOrientation( this.leftLegBase.getChild().getChild().getUnitQuaternion() );
	}

	public org.lgna.story.Orientation getLeftAnkleOrientation() {
		return createOrientation( this.leftLegBase.getChild().getChild().getChild().getUnitQuaternion() );
	}

	public JointQPair getRightArmBase() {
		return this.rightArmBase;
	}

	public JointQPair getLeftArmBase() {
		return this.leftArmBase;
	}

	public JointQPair getRightLegBase() {
		return this.rightLegBase;
	}

	public JointQPair getLeftLegBase() {
		return this.leftLegBase;
	}

	@Override
	public String toString() {
		String rv = "";
		rv += "\n";
		rv += "Right Arm: ";
		rv += stringForChain( rightArmBase );
		rv += "\n";
		rv += "Left Arm: ";
		rv += stringForChain( leftArmBase );
		rv += "\n";
		rv += "Right Leg: ";
		rv += stringForChain( rightLegBase );
		rv += "\n";
		rv += "Left Leg: ";
		rv += stringForChain( leftLegBase );
		return rv;
	}

	private String stringForChain( JointQPair base ) {
		JointQPair jqpPointer = base;
		String rv = "";
		boolean comma = false;
		while( jqpPointer != null ) {
			if( comma ) {
				rv += ", ";
			}
			comma = true;
			rv += jqpPointer.toString();
			jqpPointer = jqpPointer.getChild();
		}
		return rv;
	}

	public boolean equals( Pose other ) {
		return ( rightArmBase.equals( other.rightArmBase ) && leftArmBase.equals( other.leftArmBase ) && rightLegBase.equals( other.rightLegBase ) && leftLegBase.equals( other.leftLegBase ) );
	}

	//	public MethodInvocation createAliceMethod( AnimateToPose.Detail[] details ) {
	//		Expression[] exArr = new Expression[ details.length + 1 ];
	//		try {
	//			exArr[ 0 ] = blah.createExpression( this );
	//		} catch( CannotCreateExpressionException e1 ) {
	//			e1.printStackTrace();
	//		}
	//		int i = 1;
	//		for( AnimateToPose.Detail detail : details ) {
	//			try {
	//				exArr[ i ] = blah.createExpression( detail );
	//			} catch( CannotCreateExpressionException e ) {
	//				e.printStackTrace();
	//			}
	//			++i;
	//		}
	//		//		Constructor constructor = this.getClass().getConstructor( JointQPair.class, JointQPair.class, JointQPair.class, JointQPair.class );
	//		JavaConstructor blah = JavaConstructor.getInstance( this.getClass(), JointQPair.class, JointQPair.class, JointQPair.class, JointQPair.class );
	//		Expression[] constArgs = this.createExpressionArrForBases();
	//		AstUtilities.createInstanceCreation( blah, constArgs );//getRightArmBase(), getRightLegBase(), getLeftArmBase(), getLeftLegBase() );
	//		//newThisExpresson
	//		//		AddProcedureComposite
	//		Builder builder = new Builder();
	//		Pose pose = builder.build();
	//		//		AstUtilities.createMethodInvocation( new ThisExpression(), ADD_POSE_ANIMATION, pose,  );
	//		//		MethodInvocation rv = new MethodInvocation( null, ADD_POSE_ANIMATION, this, details, details );
	//		return null;
	//	}
	//
	//	private Expression[] createExpressionArrForBases() {
	//		Expression[] rv = new Expression[ 4 ];
	//		Expression bleh = JointQPair.createInstance( getRightArmBase() );
	//		//		rv[ 0 ] = ;
	//		return null;
	//	}
}
