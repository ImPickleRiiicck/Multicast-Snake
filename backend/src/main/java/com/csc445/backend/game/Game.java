package com.csc445.backend.game;

import com.csc445.shared.game.Player;
import com.csc445.shared.game.Spot;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 65;

    private final Spot[][] spots = new Spot[WIDTH][HEIGHT];
    private final List<Player> playersList = new ArrayList<>();

    public Game() {
        initializeSpaces();
    }

    /**
     * Function to initialize the spaces with new {@link com.csc445.shared.game.Spot} objects.
     */
    private void initializeSpaces() {
        for (int i = 0; i < spots.length; i++) {
            for (int j = 0; j < spots[0].length; j++) {
                spots[i][j] = new Spot(i, j);
            }
        }
    }

    /**
     * Function to check whether a particular player is in the game.  Checks the name, as well as the IP of the player
     * to make sure there are no duplicate names, or multiple games being played on the same client.
     *
     * @param newPlayer Player to check for their existence
     * @return true if they exist, false if they do not exist
     */
    private boolean playerIsInGame(Player newPlayer) {
        for (Player p : playersList) {
            if (p.getAddress().equals(newPlayer.getAddress())) {
                return true;
            } else if (p.getName().equals(newPlayer.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Function to add a player to the game.  Checks to see if the player is already in the game.  If they are not,
     * they are added to the player list, otherwise they are not added.
     *
     * @param newPlayer Player to add to the game
     * @return true if they are added, false if they are not added
     */
    public boolean addPlayerToGame(Player newPlayer) {
        if (!playerIsInGame(newPlayer)) {
            playersList.add(newPlayer);
            System.out.println(newPlayer.getName() + " has connected.");

            return true;
        }

        return false;
    }

    /**
     * Function to update the heartbeat for a particular player.  Finds the player and sets their heartbeat to true.
     *
     * @param address Address of the player that has sent a heartbeat
     * @return true if they exist, false if they do not exist
     */
    public boolean updatePlayerHeartbeat(InetAddress address) {
        for (Player p : playersList) {
            if (p.getAddress().equals(address)) {
                p.setHasHeartbeat(true);
                System.out.println(p.getName() + " heartbeat detected.");

                return true;
            }
        }

        return false;
    }

    /**
     * Function to update a spot in the server game state with the new data that has come in from a particular player.
     *
     * @param spotToUpdate Spot to update with new data
     */
    public void updateSpot(Spot spotToUpdate) {
        final Spot spot = spots[spotToUpdate.getX()][spotToUpdate.getY()];

        spot.setName(spotToUpdate.getName());
        spot.setColor(spotToUpdate.getColor());

        System.out.println("Spot (" + spot.getX() + "," + spot.getY() + ") updated - Name: " + spot.getName() + " | Color: " + spot.getColor());
    }
}
