package com.litefoote.lib.GameGUI;

import java.util.LinkedList;
import java.util.Queue;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;


public abstract class BaseSplashScreenActivity 
	extends SimpleBaseGameActivity implements IOnSceneTouchListener,IEntityModifierListener {
	    // ===========================================================
	    // Constants
	    // ===========================================================

	    public int CAMERA_WIDTH = 720;
	    public int CAMERA_HEIGHT = 480;
	    public ScreenOrientation screenOrientation =  ScreenOrientation.LANDSCAPE_SENSOR;
	    public Background mBackground = new Background(0,0,0);
	    public boolean allowSkip = false;

	    BitmapTextureAtlas mBitmapTextureAtlas;
		ITextureRegion mSplashTextureRegion;
		
		private Sprite mSplash;
		private Scene mScene;
	
		private Queue<String> splashScreens = new LinkedList<String>();
				
		abstract public void onSetupEngineOptions();
		abstract public void onFinished();
		
		public void onCreateResources() {}
		
		
		public void addSplash(String imageSrc) {
				splashScreens.add(imageSrc);
		}
		
		public EngineOptions onCreateEngineOptions() {
			this.onSetupEngineOptions();
			final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
			return new EngineOptions(true, screenOrientation, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		}
		
		public Sprite createSplash(String imgSource,int width, int height) {
			if (mSplash != null) {
				 this.mScene.detachChild(mSplash);		
			}

			mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), width, height, TextureOptions.BILINEAR);
			mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, imgSource, 0, 0);
			mBitmapTextureAtlas.load();

			mSplash = new Sprite(0, 0, mSplashTextureRegion, this.getVertexBufferObjectManager());
			mSplash.setPosition((CAMERA_WIDTH - mSplashTextureRegion.getWidth())/2, (CAMERA_HEIGHT- mSplashTextureRegion.getHeight())/2);
			mSplash.setAlpha(0);
			SequenceEntityModifier splash = new SequenceEntityModifier(this, new AlphaModifier(1.0f, 0.0f, 1.0f), 
																			 new DelayModifier(1.0f),
																			 new AlphaModifier(1.0f, 1.0f, 0.0f)
																			); 

			mSplash.registerEntityModifier(splash);
			mScene.attachChild(mSplash);
			return mSplash;
		}
		
		abstract public void onLoadSplashScreens();
		
		public Scene onCreateScene() {
			this.mEngine.registerUpdateHandler(new FPSLogger());

			this.mScene = new Scene();
		
			mScene.setBackground(mBackground);
			
			onLoadSplashScreens();
			

			if (!splashScreens.isEmpty()) {
				String splashSrc = splashScreens.remove();
				this.createSplash(splashSrc,CAMERA_WIDTH,CAMERA_HEIGHT);	
			} else {
				finish();
			}

			mScene.setOnSceneTouchListener(this);
			return mScene;
		} 

		public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
			if (allowSkip) {
				onFinished();
			}
			return false;
		}
		
		public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
	
		}

		public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
			BaseSplashScreenActivity.this.runOnUiThread(new Runnable() {
		        public void run() {
					//Toast.makeText(SplashScreenDemoActivity.this, "This is a Test", 30).show();
					if (!splashScreens.isEmpty()) {
						String splashSrc = splashScreens.remove();
						BaseSplashScreenActivity.this.createSplash(splashSrc,CAMERA_WIDTH,CAMERA_HEIGHT);	
					} else {
						onFinished();
					}
			
		        }});		
		}
	}
