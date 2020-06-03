package fr.ensim.archi.archirest;

import fr.ensim.archi.archirest.model.Garantie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Base_de_donnees {

    final static Map<Integer, Garantie> database = new ConcurrentHashMap<Integer, Garantie>();

    public Map<Integer, Garantie> getDatabase() {
        return database;
    }
}
