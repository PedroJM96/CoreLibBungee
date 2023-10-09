package com.pedrojm96.core.bungee.libraryloader;

public interface CoreLoader {

    public void onLoad();

    public default void onEnable() {}

    public default void onDisable() {}

}
