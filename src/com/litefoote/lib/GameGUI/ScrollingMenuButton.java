package com.litefoote.lib.GameGUI;

import org.andengine.opengl.texture.region.ITextureRegion;


public class ScrollingMenuButton {
	
	public String srcImage;
	public String key;
	ITextureRegion mMenuTextureRegion;
	IOnButtonTouchListener mOnButtonListener;
	
	public ScrollingMenuButton (String source) {
		srcImage= source;
	}
	
	public ScrollingMenuButton (String key, String source, IOnButtonTouchListener listener) {
		srcImage= source;
		mOnButtonListener = listener;
		this.key = key;
	}
	
}
