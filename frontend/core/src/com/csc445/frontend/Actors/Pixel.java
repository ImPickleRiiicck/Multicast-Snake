package com.csc445.frontend.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.graphics.Color;
import com.csc445.frontend.Utils.Helper;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.csc445.frontend.Utils.State;
import com.csc445.shared.game.Spot;
import com.csc445.shared.packets.PlayPacket;
import com.csc445.shared.utils.Constants;

public class Pixel extends Actor {

    private final Vector2 canvasPos;
    private final int size;
    private TextTooltip toolTip;
    private Skin toolTipSkin;
    private Texture t;

    private int x;
    private int y;

    private String userName;

    /**
     * @param gridPos   the pixel's actual grid position
     * @param canvasPos the pixel's position on the canvas
     * @param size      the size of the pixel
     */
    public Pixel(Vector2 gridPos, Vector2 canvasPos, int size, Skin toolTipSkin) {
        this.x = (int) gridPos.x;
        this.y = (int) gridPos.y;
        this.canvasPos = canvasPos;
        this.size = size;
        this.toolTipSkin = toolTipSkin;
        setColor(new Color(1, 1, 1, 1));
        setBounds(canvasPos.x, canvasPos.y, size, size);
        setListener();
    }

    /**
     * Sets the listener for this particular pixel
     */
    private void setListener() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float dontAccessX, float dontAccessY, int pointer, int button) {
                final Spot spot = new Spot(x, y);
                spot.setName(State.getUserName());
                spot.setColor(Helper.getSelectedColorByte());

                final PlayPacket playPacket = new PlayPacket(spot);
                Helper.sendPacket(playPacket.createPacket(), State.getServerName(), Constants.SERVER_PORT);

                setColor(Helper.getSelectedColor());
                setUserName(State.getUserName());
                return true;
            }

            public void enter(InputEvent event, float dontAccessX, float dontAccessXY, int pointer, Actor fromActor) {
                toolTip = new TextTooltip(x + ", " + y, toolTipSkin);
                event.getTarget().addListener(toolTip);
                toolTip.setInstant(true);

                toolTip.handle(event);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                toolTip.hide();
            }
        });
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

    /**
     * Generates a white pixel
     */
    @Override
    public void setColor(Color color) {
        final Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, size, size);
        t = new Texture(pixmap);
        pixmap.dispose();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
