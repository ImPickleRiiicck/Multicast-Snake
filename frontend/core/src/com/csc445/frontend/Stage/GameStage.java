package com.csc445.frontend.Stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.csc445.frontend.Actors.ColorPalletColor;
import com.csc445.frontend.Actors.Pixel;
import com.csc445.frontend.Utils.Helper;
import com.csc445.frontend.Utils.State;
import com.csc445.shared.packets.JoinPacket;
import com.csc445.shared.utils.Constants;

public class GameStage extends Stage {

    // Array of pixels to be displayed on the screen
    private Pixel[][] pixels = new Pixel[50][65];

    // Array of Pallet Colors
    private ColorPalletColor[] colorPalletColors = new ColorPalletColor[Helper.getColors().length];

    private final Skin whiteSkin = new Skin(Gdx.files.internal("skins/whitefont/uiskin.json"));
    private final Skin altSkin = new Skin(Gdx.files.internal("skins/altfont/uiskin.json"));

    private final Button addJoinButton = new TextButton("JOIN", whiteSkin);

    private final TextField passwordTextField = new TextField("Password", whiteSkin);
    private final TextField nameTextField = new TextField("Name", whiteSkin);
    private final TextField serverTextField = new TextField("Server Address", whiteSkin);

    private final TextArea textArea = new TextArea("Welcome to PixelArt!\n"
            // Adding long text for soft line breaks
            + "This game was inspired by r/Place. Credits to Landon Patmore, Ye Bhone Myat, Robert Kilmer, and Benjamin Caro ", altSkin) {
        public float getPrefHeight() {
            float prefHeight = getLines() * getStyle().font.getLineHeight();
            TextFieldStyle style = getStyle();
            if (style.background != null) {
                prefHeight = Math.max(prefHeight + style.background.getBottomHeight() + style.background.getTopHeight(), style.background.getMinHeight());
            }
            return prefHeight;
        }
    };

    /**
     * @param viewport ScreenViewport
     */
    public GameStage(Viewport viewport) {
        super(viewport);

        Gdx.gl.glClearColor(192 / 255f, 192 / 255f, 192 / 255f, 1);

        generatePixels();
        addPalletColors();
        addText();
        addJoin();

        Helper.receivePackets(this);
    }

    private void addText() {
        int xPos = 513;
        int yPos = 200;
        Gdx.input.setInputProcessor(this);
        Table container = new Table();
        this.addActor(container);
        container.setColor(Color.BLUE);
        int textHeight = 500;
        int textWidth = 225;
        container.setSize(textWidth, textHeight);
        container.setPosition(xPos, yPos);
        container.row().width(textWidth);
        container.row().height(textHeight);
        container.pad(10).defaults().expandX().fillX().space(4);

        final OpenScrollPane scrollPane = new OpenScrollPane(null, altSkin);
        scrollPane.setSize(textWidth, textHeight);
        scrollPane.setPosition(xPos, yPos);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        scrollPane.setScrollingDisabled(true, false);

        textArea.setSize(textWidth, textHeight);
        textArea.setDisabled(true);

        scrollPane.setWidget(textArea);

        container.add(scrollPane);
        container.debugAll();
    }

    private void addJoin() {
        int buttonPassWidth = 200;
        int buttonPassHeight = 30;
        int positionX = 525;
        int positionY = 100;

        nameTextField.setPosition(positionX, positionY + buttonPassHeight + 5);
        nameTextField.setSize(buttonPassWidth, buttonPassHeight);
        passwordTextField.setPosition(positionX, positionY);
        passwordTextField.setSize(buttonPassWidth, buttonPassHeight);
        addJoinButton.setSize(buttonPassWidth, buttonPassHeight);
        addJoinButton.setPosition(positionX, positionY - (buttonPassHeight + 5));
        serverTextField.setPosition(positionX, positionY+buttonPassHeight+buttonPassHeight+10);
        serverTextField.setSize(buttonPassWidth, buttonPassHeight);

        addTextFieldListener(nameTextField);
        addTextFieldListener(passwordTextField);
        addTextFieldListener(serverTextField);

        addJoinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                State.setUserName(nameTextField.getText());
                State.setSecretKey(passwordTextField.getText());
                State.setServerName(serverTextField.getText());

                nameTextField.invalidate();
                passwordTextField.invalidate();
                serverTextField.invalidate();
                addJoinButton.invalidate();

                sendJoinPacket();
            }
        });
        this.addActor(this.serverTextField);
        this.addActor(this.nameTextField);
        this.addActor(this.passwordTextField);
        this.addActor(this.addJoinButton);
    }

    private void addTextFieldListener(TextField textField) {
        textField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                textField.setText("");
            }
        });
    }

    private void sendJoinPacket() {
        final JoinPacket j = new JoinPacket(State.getUserName());
        Helper.sendPacket(j.createPacket(), State.getServerName(), Constants.SERVER_PORT);
    }

    /**
     * Generates the pixels that will be rendered on the screen
     */
    private void generatePixels() {
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                // Pixel size, could be a local variable but like it out here @Landon
                int p_SIZE = 10;
                pixels[i][j] = new Pixel(new Vector2(i, j), new Vector2(i * p_SIZE, 100 + j * p_SIZE), p_SIZE, whiteSkin);
                addActor(pixels[i][j]);
            }
        }
    }

    private void addPalletColors() {
        for (int i = 0; i < colorPalletColors.length; i++) {
            // Pallet Color size
            int CP_SIZE = 50;
            colorPalletColors[i] = new ColorPalletColor(Helper.getColors()[i], new Vector2(i * CP_SIZE, 25), CP_SIZE);
            addActor(colorPalletColors[i]);
        }
    }

    public void updatePixel(int x, int y, byte color, String username) {
        final Pixel p = pixels[x][y];
        p.setColor(Helper.convertByteToColor(color));
        p.setUserName(username);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public class OpenScrollPane extends ScrollPane {

        private boolean scrollToBottom;

        public OpenScrollPane(Actor widget, Skin skin) {
            super(widget, skin);
        }

        public void scheduleScrollToBottom() {
            scrollToBottom = true;
        }

        @Override
        public void layout() {
            super.layout();
            if (scrollToBottom) {
                setScrollY(getMaxY());
            }
        }
    }
}
