import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class ClientMain
{
    /**
     * Client application entry point.
     */
    public static void main(String[] args)
    {
        // Check parameters
        if (args.length < 4)
        {
            // Crash
            System.out.println("Please provide the client configuration file, the server's host name or IP address, its port and a directory for storing device codes.");
            return;
        }

        // Create scanner for terminal input
        Scanner terminalScanner = new Scanner(System.in);

        // Load client configuration
        ClientConfiguration clientConfiguration = new ClientConfiguration(args[0]);

        // Print implementation version
        // This value is hardcoded in the server, and automatically added to the compiled client
        System.err.println("[ " + clientConfiguration.getVersion() + " ]");

        SSLSocketFactory factory = createTrustAllSocketFactory();

        // Connect to server
        System.out.println("Connecting to server '" + args[1] + "' on port " + args[2]);
        try (SSLSocket socket = (SSLSocket) factory.createSocket(args[1], Integer.parseInt(args[2])))
        {
            socket.startHandshake();
            // Get I/O streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // Run login task
            LoginTask loginTask = new LoginTask(inputStream, outputStream, terminalScanner);
            loginTask.run();
            if (!loginTask.getSuccessful())
            {
                System.out.println("Login not successful, exiting...");
                return;
            }
            String userName = loginTask.getName();

            // Run until exit
            boolean deviceAuthenticated = false;
            while (true)
            {
                // Show action string
                System.out.println("What do you want to do?   View balance [b]   Do transaction [t]   Exit [e]");
                String action = terminalScanner.next();
                if (action.length() < 1)
                    continue;
                switch (action.charAt(0))
                {
                    case 'b' -> {
                        // Run balance retrieval task
                        System.err.println("Starting balance task...");
                        new BalanceTask(inputStream, outputStream).run();
                    }
                    case 't' -> {
                        // Check for device authentication
                        if (!deviceAuthenticated)
                        {
                            // Run registration
                            System.err.println("Starting registration task...");
                            RegistrationTask registrationTask = new RegistrationTask(inputStream, outputStream, terminalScanner, userName, args[3]);
                            registrationTask.run();
                            if (!registrationTask.getSuccessful())
                                break;
                            deviceAuthenticated = true;
                        }

                        // Run transaction task
                        System.err.println("Starting transaction task...");
                        TransactionTask transactionTask = new TransactionTask(inputStream, outputStream, terminalScanner);
                        transactionTask.run();

                        if (transactionTask.getSuccessful())
                            System.out.println("The transaction has been successful.");
                        else
                            System.out.println("The transaction has failed.");
                    }
                    case 'e' -> {
                        System.out.println("Terminating the connection...");
                        return;
                    }
                    default -> System.out.println("Unknown command.");
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static SSLSocketFactory createTrustAllSocketFactory() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager()
            {
                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType)
                {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType)
                {
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
