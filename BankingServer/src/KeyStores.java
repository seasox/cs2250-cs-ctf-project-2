import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class KeyStores
{
    /**
     * The base64-encoded keystore. This will be converted to `InputStream` during runtime
     */
    private static final String KEYSTORE_B64 = "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";

    /**
     * Create an InputStream from base64 encoded keystore
     * @return
     */
    public static InputStream toInputStream() {
        Base64.Decoder d = Base64.getDecoder();
        byte[] ba = d.decode(KEYSTORE_B64);
        return new ByteArrayInputStream(ba);
    }
}