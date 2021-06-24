import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Helper class containing auxiliary functions.
 */
public class Utility
{
    /**
     * Prints out the given message, while synchronizing between threads.
     * DO NOT CHANGE THIS METHOD.
     *
     * @param message The message to be printed.
     */
    public static void safePrintln(String message)
    {
        synchronized (System.out)
        {
            System.out.println(message);
        }
    }

    /**
     * Prints out the given debug message, while synchronizing between threads.
     * DO NOT CHANGE THIS METHOD.
     *
     * @param message The message to be printed.
     */
    public static void safeDebugPrintln(String message)
    {
        synchronized (System.err)
        {
            // Do not show debug output in lab mode, to avoid spamming the container log
            if (LabEnvironment.LAB_MODE)
                return;

            System.err.println(message);
        }
    }

    /**
     * Writes the given payload as a packet into the given output stream.
     *
     * @param outputStream The stream the packet shall be written to.
     * @param payload      The string payload to be sent.
     */
    public static void sendPacket(DataOutputStream outputStream, String payload) throws IOException
    {
        // Debug output
        safeDebugPrintln("Sending '" + payload + "'");

        // Encode payload
        byte[] payloadEncoded = payload.getBytes();

        // Write packet length
        outputStream.writeInt(payloadEncoded.length);

        // Write payload
        outputStream.write(payloadEncoded);
    }

    /**
     * Receives the next packet from the given input stream.
     *
     * @param inputStream The stream where the packet shall be retrieved.
     * @return The payload of the received packet.
     */
    public static String receivePacket(DataInputStream inputStream) throws IOException
    {
        // Prepare payload buffer
        byte[] payloadEncoded = new byte[inputStream.readInt()];
        inputStream.readFully(payloadEncoded);

        // Decode payload
        String payload = new String(payloadEncoded);
        safeDebugPrintln("Received '" + payload + "'");
        return payload;
    }

    /**
     * Returns a random alpha numeric string with the given length.
     *
     * @param length The length of the requested string.
     * @return A random alpha numeric string with the given length.
     */
    public static String getRandomString(int length)
    {
        // Generate random string efficiently
        int randomIndex = new Random().nextInt(100 - length);
        return "Sl4idafEVk9X1efZFSAUANyQefaua8JnnAVVQbhuEwrcA4c85yrMaaVjv1TiDbmPdQAD5pfyqcsj1obyEJxGulmaV8ezWYEXpyUs".substring(randomIndex, randomIndex + length);
    }
}
