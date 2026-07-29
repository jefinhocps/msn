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
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Digite seu nome de usuário:");
            this.nomeUsuario = in.readLine();

            if(nomeUsuario == null || nomeUsuario.isBlank()) {
                out.println("Nome de usuário inválido. Conexão encerrada.");
                socket.close();
                return;
            }

            System.out.println("Usuário conectado: " + nomeUsuario + " Entrou no chat.");
            ServidorMain.transmitirMensagem(nomeUsuario + " entrou no chat.", this);

            String mensagem;
            while ((mensagem = in.readLine()) != null) {
                if (mensagem.equalsIgnoreCase("/sair")) {
                    break;
                }
                System.out.println(nomeUsuario + ": " + mensagem);
                ServidorMain.transmitirMensagem(nomeUsuario + ": " + mensagem, this);
            }

        } catch (IOException e) {
            System.out.println("Conexão perdida: " + nomeUsuario);
            //e.printStackTrace();
        } finally {
            desconectar();
        }
    }

    public void enviarMensagem(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    private void desconectar() {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
    
}
