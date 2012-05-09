package com.litefoote.lib.GameGUI;

import java.util.LinkedList;
import java.util.Queue;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.litefoote.lib.GameGUI.ScrollingMenuButton;


public abstract class BaseScrollingMenuActivity 
        extends SimpleBaseGameActivity
		implements IScrollDetectorListener, IOnSceneTouchListener, OnClickListener
{

    
	public int PADDING = 50;
    public int CAMERA_WIDTH = 720;
    public int CAMERA_HEIGHT = 480;
    
    public int START_X = CAMERA_WIDTH/2;
    public int START_Y = 0 + PADDING * 2;
    
    public boolean LockY = false;
    public boolean LockX = true;
        
    public ScreenOrientation screenOrientation =  ScreenOrientation.LANDSCAPE_SENSOR;
    public Background mBackground = new Background(0,0,0);
    
    private SurfaceScrollDetector mScrollDetector;
    
    private Camera mCamera;
    
    private float mCurrentX= 0;
    private float mCurrentY = 0;
    
    private float mMaxX = 0;
    private float mMaxY = 0;
    
    
    private Scene mScene;
    
    private Queue<ScrollingMenuButton> buttons = new LinkedList<ScrollingMenuButton>();
    
    abstract public void onSetupEngineOptions();
    abstract public void onConfigureButtons();
    abstract public void onSceneCreated();
    
	public void addButton(ScrollingMenuButton btn) {
		buttons.add(btn);
	}
	
	public EngineOptions onCreateEngineOptions() {
		this.onSetupEngineOptions();
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, screenOrientation, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	protected void onCreateResources() {
		onConfigureButtons();
		
		for (ScrollingMenuButton button: buttons) {
	    	BitmapTextureAtlas mMenuBitmapTextureAtlas = new BitmapTextureAtlas(this.mEngine.getTextureManager(), 154,34, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			ITextureRegion mMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBitmapTextureAtlas, this, button.srcImage , 0, 0);    	
			mMenuBitmapTextureAtlas.load();
	    	
	    	button.mMenuTextureRegion =  mMenuTextureRegion;
		}
		
	}

	@Override
	protected Scene onCreateScene() {
		this.mScene = new Scene();
		mScene.setBackground(mBackground);

		int buttonX = START_X;
		int buttonY = START_Y;
		

		for (ScrollingMenuButton button: buttons) {
			ButtonSprite bSprite = new ButtonSprite(buttonX - (154/2), buttonY - (34/2), button.mMenuTextureRegion,this.getVertexBufferObjectManager(), this);
			bSprite.setUserData(button);
			mScene.registerTouchArea(bSprite );
			mScene.attachChild(bSprite );
			
			buttonY += PADDING + (34/2);
			mMaxY = buttonY + 34 + PADDING;
		}

		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mScene.setOnSceneTouchListener(this);    
		this.mScene.setTouchAreaBindingOnActionDownEnabled(true);
		
		onSceneCreated();
		return mScene;
	}
	
	
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
            this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
            return true;
    }	
	
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {

           	if (!LockY){
                //Return if ends are reached
                if ( ((mCurrentY - pDistanceY) < 0)  ){                	
                    return;
                }else if((mCurrentY - pDistanceY) > mMaxY - CAMERA_HEIGHT){
                	return;
                }
                
                //Center camera to the current point
                this.mCamera.offsetCenter(0,-pDistanceY);
                mCurrentY -= pDistanceY;       	
           	}
           	
           	
    }
    
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}

	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}    
	
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if (pButtonSprite.getUserData() != null ) {
			ScrollingMenuButton button = (ScrollingMenuButton) pButtonSprite.getUserData();
			button.mOnButtonListener.onButtonTouched(button);
		};
		
	}

	
}
