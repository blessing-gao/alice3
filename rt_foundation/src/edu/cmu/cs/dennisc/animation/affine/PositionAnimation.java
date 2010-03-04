/*
 * Copyright (c) 2006-2009, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */
package edu.cmu.cs.dennisc.animation.affine;

/**
 * @author Dennis Cosgrove
 */
public class PositionAnimation extends AffineAnimation {
	public static final edu.cmu.cs.dennisc.math.Point3 USE_EXISTING_VALUE_AT_RUN_TIME = null;
	
	private edu.cmu.cs.dennisc.math.Point3 m_posBegin = new edu.cmu.cs.dennisc.math.Point3();
	private edu.cmu.cs.dennisc.math.Point3 m_posEnd = new edu.cmu.cs.dennisc.math.Point3();
	
	private edu.cmu.cs.dennisc.math.Point3 m_posBeginUsedAtRuntime = new edu.cmu.cs.dennisc.math.Point3();

	public PositionAnimation() {
		m_posBeginUsedAtRuntime.setNaN();
		m_posBegin.setNaN();
		m_posEnd.setNaN();
	}
	public PositionAnimation( edu.cmu.cs.dennisc.scenegraph.AbstractTransformable sgSubject, edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgAsSeenBy, edu.cmu.cs.dennisc.math.Point3 posBegin, edu.cmu.cs.dennisc.math.Point3 posEnd ) {
		super( sgSubject, sgAsSeenBy );
		m_posBeginUsedAtRuntime.setNaN();
		setPositionBegin( posBegin );
		setPositionEnd( posEnd );
	}

	public edu.cmu.cs.dennisc.math.Point3 accessPositionBeginUsedAtRuntime() {
		return m_posBeginUsedAtRuntime;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionBeginUsedAtRuntime( edu.cmu.cs.dennisc.math.Point3 rv ) {
		rv.set( m_posBeginUsedAtRuntime );
		return rv;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionBeginUsedAtRuntime() {
		return getPositionBeginUsedAtRuntime( new edu.cmu.cs.dennisc.math.Point3() );
	}

	public edu.cmu.cs.dennisc.math.Point3 accessPositionBegin() {
		return m_posBegin;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionBegin( edu.cmu.cs.dennisc.math.Point3 rv ) {
		rv.set( m_posBegin );
		return rv;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionBegin() {
		return getPositionBegin( new edu.cmu.cs.dennisc.math.Point3() );
	}
	public void setPositionBegin( edu.cmu.cs.dennisc.math.Point3 posBegin ) {
		if( posBegin != USE_EXISTING_VALUE_AT_RUN_TIME ) {
			m_posBegin.set( posBegin );
		} else {
			m_posBegin.setNaN();
		}
	}

	public edu.cmu.cs.dennisc.math.Point3 accessPositionEnd() {
		return m_posEnd;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionEnd( edu.cmu.cs.dennisc.math.Point3 rv ) {
		rv.set( m_posEnd );
		return rv;
	}
	public edu.cmu.cs.dennisc.math.Point3 getPositionEnd() {
		return getPositionEnd( new edu.cmu.cs.dennisc.math.Point3() );
	}
	public void setPositionEnd( edu.cmu.cs.dennisc.math.Point3 posEnd ) {
		m_posEnd.set( posEnd );
	}
	
	@Override
	public void prologue() {
		if( m_posBegin.isNaN() ) {
			m_posBeginUsedAtRuntime.set( getSubject().getTranslation( getAsSeenBy() ) );
		} else {
			m_posBeginUsedAtRuntime.set( m_posBegin );
		}
	}
	@Override
	public void setPortion( double portion ) {
		getSubject().setTranslationOnly( edu.cmu.cs.dennisc.math.InterpolationUtilities.interpolate( m_posBeginUsedAtRuntime, m_posEnd, portion ), getAsSeenBy() );
	}
	@Override
	public void epilogue() {
		getSubject().setTranslationOnly( m_posEnd, getAsSeenBy() );
		m_posBeginUsedAtRuntime.setNaN();
	}
}
