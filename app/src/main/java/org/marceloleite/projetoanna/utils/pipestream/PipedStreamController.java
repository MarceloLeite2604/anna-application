package org.marceloleite.projetoanna.utils.pipestream;

import java.util.HashMap;

/**
 * Created by Marcelo Leite on 14/05/2017.
 */

public abstract class PipedStreamController {

    private volatile static HashMap<Integer, PipedStream> pipedStreamHashMap = new HashMap<>();

    public static HashMap<Integer, PipedStream> getPipedStreamHashMap() {
        return pipedStreamHashMap;
    }
}
