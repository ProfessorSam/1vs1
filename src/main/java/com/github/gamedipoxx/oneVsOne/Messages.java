package com.github.gamedipoxx.oneVsOne;

public enum Messages {
    PREFIX("§7[§f§lGamedipoxx§r§7] §r"), 
    TELEPORTTOLOBBY("§7Du wurdest zur Lobby teleportiert"), 
    NOARENAFOUND("§cEs konnte keine Arena gefunden werden"), 
    COMMANDS("§cCommands: /onevsone [create, join, delete]"),
    NOARENAAVAIBLE("§cKeine Arena verfügbar!"),
    CONFIGRELOADED("§2Die Konfigurationen wurden neu geladen!");
 
    private String string;
 
    Messages(String string) {
        this.string = string;
    }
 
    public String getString() {
        return string;
    }
}


