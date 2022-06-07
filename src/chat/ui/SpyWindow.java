package chat.ui;

import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import chat.tuples.Lookup;
import chat.tuples.SpyListener;
import net.jini.space.JavaSpace;

public class SpyWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//CONNECTION
	private JavaSpace space;
	
	//UI	
	private JTextArea chatArea;
	private JScrollPane chatScroll;
		
	public SpyWindow() {		
		Lookup finder = new Lookup(JavaSpace.class);
		this.space = (JavaSpace) finder.getService();
		
		initComponents();
		setUpGUI();
		
		startThread();			
	}
	
	private void initComponents() {		
		this.chatArea = new JTextArea();
		this.chatScroll = new JScrollPane();	
	}
	
	public void initSpace() {
		try {
			System.out.println("Procurando pelo servico JavaSpace...");

			if (space == null) {
				System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
				System.exit(-1);
			}

			System.out.println("O servico JavaSpace foi encontrado.");
			System.out.println(space);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(550, 530);
		this.setTitle("Sala do espião");
		this.setContentPane(new JLabel());	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		chatArea.setEditable(false);
		chatArea.setColumns(20);
		chatArea.setRows(5);		
		chatArea.setWrapStyleWord(true);
		chatArea.setLineWrap(true);
		chatArea.setFont(chatArea.getFont().deriveFont(12f));
		chatArea.setMargin(new Insets(10, 10, 10, 10));
		
		DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		chatScroll.setViewportView(chatArea);
		chatScroll.setBounds(15, 70, 500, 350);
		
		chatArea.append("\n----- Bem-vindo a sala do espião -----" 
				+ "\n----- Nenhum usuário sabe que você está aqui -----"
				+ "\n----- Para ver a lista de usuários conectados digite '/usuarios' -----\n");
			
		this.add(chatScroll);
		this.setVisible(true);
	}

	
	public void startThread() {
		SpyListener spyListener = new SpyListener(space, chatArea);
		spyListener.start();
	}
}
