package com.example.andengineinvestigation;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;
import android.util.Log;

public class AndEngineInvestigation extends SimpleBaseGameActivity {

    private static final String TAG = "AndEngineInvestigation";

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;

    private ITexture mTexture;
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private BitmapTextureAtlas mBitmapTextureAtlasGrass;
    private BitmapTextureAtlas mBitmapTextureAtlasClouds;
    private ITextureRegion mFaceTextureRegion;
    private ITextureRegion mFaceTextureRegion2;
    private ITextureRegion mGroundGrass;
    private ITextureRegion mClouds;

    Music mMusic;
    Sound mSound;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Log.v(TAG, "onCreateEngineOptions 1");
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        Log.v(TAG, "onCreateEngineOptions 2");
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        // we plan to use Sound and Music objects in our game
        engineOptions.getAudioOptions().setNeedsMusic(true);
        engineOptions.getAudioOptions().setNeedsSound(true);
        return engineOptions; 
    }

    @Override
    protected void onCreateResources() {
        Log.v(TAG, "onCreateResources");
        /* Set the base path for our SoundFactory and MusicFactory to
         * define where they will look for audio files.
         */
         SoundFactory.setAssetBasePath("sfx/");
         MusicFactory.setAssetBasePath("sfx/");

         // Load our "sound.mp3" file into a Sound object
         try {
             mSound = SoundFactory.createSoundFromAsset(getSoundManager(), this, "sound.mp3");
         } catch (IOException e) {
             e.printStackTrace();
         }
         // Load our "music.mp3" file into a music object
         try {
             mMusic = MusicFactory.createMusicFromAsset(getMusicManager(), this, "music.mp3");
             mMusic.setVolume(0.5f);
         } catch (IOException e) {
             e.printStackTrace();
         }
//        try {
//            this.mTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
//                @Override
//                public InputStream open() throws IOException {
//                    return getAssets().open("gfx/face_box.png");
//                }
//            });
//
//            this.mTexture.load();
//            this.mFaceTextureRegion = TextureRegionFactory.extractFromTexture(this.mTexture);
            
            BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
            this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
            this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0);
            this.mFaceTextureRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0);
            this.mBitmapTextureAtlas.load();

            this.mBitmapTextureAtlasGrass = new BitmapTextureAtlas(this.getTextureManager(), 800, 109);
            this.mBitmapTextureAtlasClouds = new BitmapTextureAtlas(this.getTextureManager(), 800, 109);
            this.mGroundGrass = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlasGrass, this, "grass.png", 0, 0);
            this.mClouds = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlasClouds, this, "clouds.png", 0, 0);
            this.mBitmapTextureAtlasGrass.load();
            this.mBitmapTextureAtlasClouds.load();

//        } catch (IOException e) {
//            Debug.e(e);
//        }
    }
    
    private void setPos(Sprite droppedSprite, int x, int y)
    {
        Log.v(TAG, "setPos");
        droppedSprite.setPosition(x, y);
    }
    
    private void dropSimulation(Sprite droppedSprite, final TouchEvent pSceneTouchEvent)
    {
        Log.v(TAG, "dropSimulation");
        int inc = (int) ((pSceneTouchEvent.getY() - droppedSprite.getHeight() / 2));
        for(int i = (int) (pSceneTouchEvent.getY() - droppedSprite.getHeight() / 2); i < CAMERA_HEIGHT - (mFaceTextureRegion.getHeight()+mGroundGrass.getHeight()); i++)
        {
            inc++;
            int x = (int) (pSceneTouchEvent.getX() - droppedSprite.getWidth() / 2);
            setPos(droppedSprite, x, inc);
            Log.v(TAG, "index=" + inc);
        }
    }

    @Override
    protected Scene onCreateScene() {
        Log.v(TAG, "onCreateScene");
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

        /* Calculate the coordinates for the face, so its centered on the camera. */
        final float centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;
        
        final float centerX2 = (CAMERA_WIDTH) / 2;
        final float centerY2 = (CAMERA_HEIGHT) / 2;

        /* Create the face and add it to the scene. */
        final Sprite face = new Sprite(centerX, centerY, this.mFaceTextureRegion, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch(pSceneTouchEvent.getAction())
                {
                case TouchEvent.ACTION_DOWN:
                    mSound.play();
                    this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
                    break;
                case TouchEvent.ACTION_MOVE:
                    this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
                    break;
                case TouchEvent.ACTION_UP:
                    mSound.play();
                    dropSimulation(this, pSceneTouchEvent);
                    break;
                }
                return true;
            }
        };
        final Sprite face2 = new Sprite(centerX2, centerY2, this.mFaceTextureRegion2, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
                switch(pSceneTouchEvent.getAction())
                {
                case TouchEvent.ACTION_DOWN:
                case TouchEvent.ACTION_UP:
                    mSound.play();
                    break;
                }
                return true;
            }
        };
        final Sprite grassSprite = new Sprite(0, CAMERA_HEIGHT-109, this.mGroundGrass, this.getVertexBufferObjectManager());
        final Sprite cloudsSprite = new Sprite(0, 0, this.mClouds, this.getVertexBufferObjectManager());
        face.setScale(4);
        scene.attachChild(grassSprite);
        scene.attachChild(cloudsSprite);
        scene.attachChild(face);
        scene.registerTouchArea(face);
        scene.attachChild(face2);
        scene.registerTouchArea(face2);
        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;
    }
    
    /* Music objects which loop continuously should be played in
    * onResumeGame() of the activity life cycle
    */
    @Override
    public synchronized void onResumeGame() {
        if(mMusic != null && !mMusic.isPlaying()){
            mMusic.play();
        }
        super.onResumeGame();
    }

    /* Music objects which loop continuously should be paused in
    * onPauseGame() of the activity life cycle
    */
    @Override
    public synchronized void onPauseGame() {
        if(mMusic != null && mMusic.isPlaying()){
            mMusic.pause();
        }
        super.onPauseGame();
    }

}
