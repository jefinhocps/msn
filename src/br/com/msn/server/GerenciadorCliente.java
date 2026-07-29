package src.br.com.msn.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GerenciadorCliente implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nomeUsuario;

    public GerenciadorCliente(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            this.nomeUsuario = in.readLine();

            if (nomeUsuario == null || nomeUsuario.isBlank()) {
                out.println("Nome de usuário inválido. Conexão encerrada.");
                socket.close();
                return;
            }

            System.out.println("Usuário conectado: " + nomeUsuario + " Entrou no chat.");
            ServidorMain.transmitirMensagem(nomeUsuario + " entrou no chat.", null);

        } catch (IOException e) {
            System.out.println("Falha ao inicializar cliente: " + e.getMessage());
            desconectar();
        }
    }

    public void enviarMensagem(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    private void desconectar() {
        ServidorMain.removerCliente(this);
        if (nomeUsuario != null) {
            System.out.println("Usuário desconectado: " + nomeUsuario);
            ServidorMain.transmitirMensagem(nomeUsuario + " saiu do chat.", null);
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (nomeUsuario == null) {
            return;
        }

        try {
            String mensagem;
            while ((mensagem = in.readLine()) != null) {
                if (mensagem.equalsIgnoreCase("/sair")) {
                    out.println("👋 Saindo do chat...");
                    break;
                }
                System.out.println(nomeUsuario + ": " + mensagem);
                ServidorMain.transmitirMensagem(nomeUsuario + ": " + mensagem, this);
            }
        } catch (IOException e) {
            System.out.println("Conexão perdida: " + nomeUsuario);
        } finally {
            desconectar();
        }
    }
    
}
