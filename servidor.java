import java.io.*;
import java.net.*;
import java.util.*;

public class servidor {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clientes = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor do chat iniciado na porta " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter writer;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientes.add(writer);

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Mensagem recebida: " + message);
                    transmitirMensagem(message);
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + clientSocket.getInetAddress());
            } finally {
                clientes.remove(writer);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    // Ignora.
                }
            }
        }

        private void transmitirMensagem(String message) {
            synchronized (clientes) {
                for (PrintWriter writer : clientes) {
                    writer.println(message);
                }
            }
        }
    }
}
