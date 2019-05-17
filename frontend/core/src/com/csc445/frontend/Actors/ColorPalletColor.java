package com.csc445.frontend.Actors;

import com.csc445.frontend.Utils.Helper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class ColorPalletColor extends Actor {

    private final Vector2 canvasPos;
    private final int size;
    private Texture t;


    public ColorPalletColor(Color color, Vector2 canvasPos, int size) {
        this.canvasPos = canvasPos;
        this.size = size;

        setColor(color);
        createPixmap(color);
        setBounds(canvasPos.x, canvasPos.y, size, size);
        setListener();
    }

    /**
     * Sets the listener for this particular pixel
     */
    private void setListener() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Helper.setSelectedColor(getColor());
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(getColor());
            }
        });
    }

    /**
     * Creates a pixmap to be piped into a texture to create a filled rectangle
     */
    private void createPixmap(Color color) {
        final Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, size, size);
        t = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * @param batch       batch
     * @param parentAlpha parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(t, canvasPos.x, canvasPos.y);
    }
}
