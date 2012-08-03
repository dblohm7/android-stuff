/*
 * Copyright (C) 2011 Aaron Klotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ohmsoft.lib.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.ohmsoft.lib.android.BuildConfig;
import com.ohmsoft.lib.android.R;

public class ToggleImageButton extends ImageButton implements Checkable {

    public static interface OnCheckedChangeListener {
	void onCheckedChanged( ToggleImageButton buttonView, boolean isChecked );
    }

    public ToggleImageButton( Context context ) {
	super( context );
	init( context, null, 0 );
    }

    public ToggleImageButton( Context context, AttributeSet attrs ) {
	super( context, attrs );
	init( context, attrs, 0 );
    }

    public ToggleImageButton( Context context, AttributeSet attrs, int defStyle ) {
	super( context, attrs, defStyle );
	init( context, attrs, defStyle );
    }

    private void init( Context context, AttributeSet attrs, int defStyle ) {
	TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ToggleImageButton, defStyle, 0 );
	boolean checked = a.getBoolean( R.styleable.ToggleImageButton_android_checked, false );
	setChecked( checked );
	mSrcDrawable = a.getDrawable( R.styleable.ToggleImageButton_android_src );
	mTintColor = a.getColor( R.styleable.ToggleImageButton_android_tint, 0 );
	mCheckedDrawable = a.getDrawable( R.styleable.ToggleImageButton_checkedSrc );
	mCheckedTintColor = a.getColor( R.styleable.ToggleImageButton_checkedTint, 0 );
	mBlurRadius = a.getFloat( R.styleable.ToggleImageButton_checkedBlurRadius, 0 );
	a.recycle();

	super.setOnClickListener( new View.OnClickListener() {
	    public void onClick( View view ) {
		setChecked( !isChecked(), true );
	    }
	} );

	if( mBlurRadius != 0.0f ) {
	    Drawable drawable = resolveCheckedDrawable();
	    if( BuildConfig.DEBUG ) Log.d( LOG_TAG, "Checked drawable is a " + drawable.toString() );
	    if( drawable instanceof BitmapDrawable ) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Paint paint = new Paint();
		paint.setMaskFilter( new BlurMaskFilter( mBlurRadius, BlurMaskFilter.Blur.SOLID ) );
		Bitmap bitmap = bd.getBitmap().extractAlpha( paint, null );
		mCheckedDrawable = new BitmapDrawable( getContext().getResources(), bitmap );
	    }
	}
    }

    private Drawable resolveCheckedDrawable() {
	return ( mCheckedDrawable == null ? mSrcDrawable : mCheckedDrawable );
    }

    public boolean isChecked() {
	return mChecked;
    }

    public void setChecked( boolean checked ) {
	setChecked( checked, false );
    }

    private void setChecked( boolean checked, boolean isUser ) {
	if( checked != mChecked ) {
	    mChecked = checked;
	    updateDrawables();
	    refreshDrawableState();

	    if( mBroadcasting ) {
		return;
	    }

	    if( isUser ) {
		mBroadcasting = true;
		if( mListener != null ) {
		    mListener.onCheckedChanged( this, mChecked );
		}
		mBroadcasting = false;
	    }
	}
    }

    public void setCheckedDrawable( Drawable drawable ) {
	mCheckedDrawable = drawable;
    }

    public void setOnCheckedChangeListener( OnCheckedChangeListener listener ) {
	mListener = listener;
    }

    public void toggle() {
	setChecked( !isChecked() );
    }

    private void updateDrawables() {
	if( mChecked ) {
	    if( mCheckedDrawable != null ) {
		setImageDrawable( mCheckedDrawable );
	    }
	    if( mCheckedTintColor != 0 ) {
		setColorFilter( mCheckedTintColor, PorterDuff.Mode.SRC_ATOP );
	    }
	} else {
	    if( mCheckedDrawable != null ) {
		setImageDrawable( mSrcDrawable );
	    }
	    if( mCheckedTintColor != 0 ) {
		if( mTintColor == 0 ) {
		    setColorFilter( null );
		} else {
		    setColorFilter( mTintColor, PorterDuff.Mode.SRC_ATOP );
		}
	    }
	}
    }

    private float mBlurRadius;
    private boolean mBroadcasting = false;
    private boolean mChecked = false;
    private int mTintColor;
    private int mCheckedTintColor;
    private Drawable mSrcDrawable;
    private Drawable mCheckedDrawable;
    private OnCheckedChangeListener mListener;
    private static final String LOG_TAG = "ToggleImageButton";
}

