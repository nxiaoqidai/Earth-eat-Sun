package com.example.chen.androidhelloworld;

import java.io.Serializable;

/**
 * Created by chenxixiang on 15/10/8.
 */
public class PlayerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String playerName;
    private Boolean isSingle;
    private Boolean isNightmare;
    private Boolean isGravity;
    private Boolean isServer;

    public PlayerInfo(String playerName, Boolean isSingle, Boolean isNightmare, Boolean isGravity,Boolean isServer) {
        this.playerName = playerName;
        this.isSingle = isSingle;
        this.isNightmare = isNightmare;
        this.isGravity = isGravity;
    }

    public String getPlayerName() {
        return playerName;
    }


    public Boolean getIsSingle() {
        return isSingle;
    }


    public Boolean getIsNightmare() {
        return isNightmare;
    }

    public Boolean getIsGravity() {
        return isGravity;
    }

    public Boolean getIsServer() {
        return isServer;
    }
}
