package src.br.com.msn.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServidorMain {

    private static final int PORTA = 12345;
    
    // Conjunto thread-safe para armazenar todos os clientes ativos
    private static Set<GerenciadorCliente> clientesConectados = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("🚀 Servidor MSN iniciado na porta " + PORTA);
        System.out.println("==========================================");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {

            while (true) {
                // Aguarda um cliente se conectar (bloqueante)
                Socket socket = serverSocket.accept();
                System.out.println("⚡ Nova conexão recebida de: " + socket.getInetAddress().getHostAddress());

                // Cria o gerenciador e adiciona na lista de clientes
                GerenciadorCliente cliente = new GerenciadorCliente(socket);
                clientesConectados.add(cliente);

                // Cria e inicia uma nova Thread dedicada a esse cliente
                Thread threadCliente = new Thread(cliente);
                threadCliente.start();
            }

        } catch (IOException e) {
            System.err.println("❌ Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Transmite uma mensagem para todos os clientes conectados.
     * @param mensagem Texto a ser enviado.
     * @param remetente Cliente que enviou (pode ser null se for para enviar para todos, inclusive quem enviou).
     */
    public static void transmitirMensagem(String mensagem, GerenciadorCliente remetente) {
        // Fazemos uma cópia rápida da lista para iterar com segurança fora do bloco sincronizado
        List<GerenciadorCliente> listaParaEnvio;
        synchronized (clientesConectados) {
            listaParaEnvio = new ArrayList<>(clientesConectados);
        }

        for (GerenciadorCliente cliente : listaParaEnvio) {
            if (remetente != null && cliente == remetente) {
                continue;
            }
            cliente.enviarMensagem(mensagem);
        }
    }

    /**
     * Remove o cliente da lista quando ele se desconecta.
     */
    public static void removerCliente(GerenciadorCliente cliente) {
        clientesConectados.remove(cliente);
    }
}