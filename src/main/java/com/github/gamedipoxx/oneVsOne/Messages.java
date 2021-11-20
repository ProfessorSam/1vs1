package com.github.gamedipoxx.oneVsOne;

public enum Messages {
    PREFIX("§7[§f§lGamedipoxx§r§7] §r"), 
    TELEPORTTOLOBBY("§7Du wurdest zur Lobby teleportiert"), 
    NOARENAFOUND("§cEs konnte keine Arena gefunden werden"), 
    DEV("https://dev.domain.com:21323/");
 
    private String string;
 
    Messages(String string) {
        this.string = string;
    }
 
    public String getString() {
        return string;
    }
}


