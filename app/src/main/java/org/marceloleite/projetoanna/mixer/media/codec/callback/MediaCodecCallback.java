package org.marceloleite.projetoanna.mixer.media.codec.callback;

import android.media.MediaCodec;

/**
 * Created by Marcelo Leite on 09/05/2017.
 */

public abstract class MediaCodecCallback extends MediaCodec.Callback {

    public abstract boolean finished();
}
