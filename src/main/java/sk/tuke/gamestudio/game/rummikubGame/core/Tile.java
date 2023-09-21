package sk.tuke.gamestudio.game.rummikubGame.core;

import java.util.ArrayList;
import java.util.Arrays;

public class Tile {
    private String[] sourceGameTiles = new String[] {
            "[01R]","[02R]","[03R]","[04R]","[05R]","[06R]","[07R]","[08R]","[09R]","[10R]","[11R]","[12R]","[13R]",
            "[01B]","[02B]","[03B]","[04B]","[05B]","[06B]","[07B]","[08B]","[09B]","[10B]","[11B]","[12B]","[13B]",
            "[01Y]","[02Y]","[03Y]","[04Y]","[05Y]","[06Y]","[07Y]","[08Y]","[09Y]","[10Y]","[11Y]","[12Y]","[13Y]",
            "[01G]","[02G]","[03G]","[04G]","[05G]","[06G]","[07G]","[08G]","[09G]","[10G]","[11G]","[12G]","[13G]",
            "[01R]","[02R]","[03R]","[04R]","[05R]","[06R]","[07R]","[08R]","[09R]","[10R]","[11R]","[12R]","[13R]",
            "[01B]","[02B]","[03B]","[04B]","[05B]","[06B]","[07B]","[08B]","[09B]","[10B]","[11B]","[12B]","[13B]",
            "[01Y]","[02Y]","[03Y]","[04Y]","[05Y]","[06Y]","[07Y]","[08Y]","[09Y]","[10Y]","[11Y]","[12Y]","[13Y]",
            "[01G]","[02G]","[03G]","[04G]","[05G]","[06G]","[07G]","[08G]","[09G]","[10G]","[11G]","[12G]","[13G]"
    };

    public void setGameTiles(ArrayList<String> gameTiles){
        gameTiles.addAll(Arrays.asList(sourceGameTiles));
    }
}
