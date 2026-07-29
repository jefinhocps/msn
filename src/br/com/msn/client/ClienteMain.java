package src.br.com.msn.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteMain {

    private static final String SERVIDOR_IP = "127.0.0.1"; // localhost
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("💬 Conectando ao Servidor MSN...");
        System.out.println("==========================================");

        try {
            Socket socket = new Socket(SERVIDOR_IP, PORTA);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Thread dedicada exclusivamente a ESCUTAR as mensagens do servidor
            Thread threadEscuta = new Thread(() -> {
                try {
                    String mensagemServidor;
                    while ((mensagemServidor = in.readLine()) != null) {
                        System.out.println(mensagemServidor);
                    }
                } catch (IOException e) {
                    System.out.println("\n⚠️ Conexão com o servidor encerrada.");
                }
            });
            threadEscuta.start();

            // Thread principal fica responsável por ENVIAR as mensagens
            while (true) {
                String mensagemLocal = scanner.nextLine();
                out.println(mensagemLocal);

                if (mensagemLocal.equalsIgnoreCase("/sair")) {
                    System.out.println("👋 Saindo do chat...");
                    break;
                }
            }

            socket.close();
            scanner.close();

        } catch (IOException e) {
            System.err.println("❌ Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}