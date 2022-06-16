package chat.ui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import chat.tuples.Lookup;
import chat.tuples.Message;
import chat.tuples.Spy;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class SpyWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private List<String> wordsToSpy;
	
	//CONNECTION
	private JavaSpace space;
	
	//UI	
	private JTextArea chatArea;
	private JScrollPane chatScroll;
	
	private JTextField addWordsTextField;	
	private JButton addWordsButton;
		
	public SpyWindow() {		
		Lookup finder = new Lookup(JavaSpace.class);
		this.space = (JavaSpace) finder.getService();
		
		this.wordsToSpy = new ArrayList<>();
		
		initComponents();
		setUpGUI();
		setUpAddWords();
		
		startThread();			
	}

	private void initComponents() {		
		this.chatArea = new JTextArea();
		this.chatScroll = new JScrollPane();
		this.addWordsTextField = new JTextField();
		this.addWordsButton = new JButton();
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
		this.setSize(580, 530);
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
		chatScroll.setBounds(15, 70, 538, 350);
		
		addWordsTextField.setBounds(15, 430, 430, 40);

		addWordsButton.setText("Adicionar");
		addWordsButton.setBounds(453, 430, 100, 39);
		
		chatArea.append("\n----- Bem-vindo a sala do espião -----" 
				+ "\n----- Nenhum usuário sabe que você está aqui -----"
				+ "\n----- Para ver a lista de palavras suspeitas digite '/palavras' -----\n");
//				+ "\n----- Para ver a lista de usuários conectados digite '/usuarios' -----\n");
			
		this.add(chatScroll);
		this.add(addWordsTextField);
		this.add(addWordsButton);
		this.setVisible(true);
	}

	private void setUpAddWords() {
		ActionListener actionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) {
				addWordToList();
			}
		};
		
		KeyListener keyListener = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addWordToList();
				}
			}
		};
		
		addWordsButton.addActionListener(actionListener);
		addWordsTextField.addKeyListener(keyListener);
	}
	
	private void addWordToList() {
		String word = addWordsTextField.getText();
		
		addWordsTextField.setText("");
		
		if (word != null && !word.isEmpty()) {
			
			if (word.startsWith("/palavras")) {
				StringBuilder sb = new StringBuilder();

				sb.append("\n\n----- Palavras Monitoradas -----\n");

				for (String wordToSpy : wordsToSpy) {
					sb.append("\n" + wordToSpy);
				}

				sb.append("\n\n-----------------------------------------\n");

				chatArea.append(sb.toString());
			}
			else {
				this.wordsToSpy.add(word);
				chatArea.append("\n\n----- A palavra " + word + " foi adicionada a lista de palavras monitoradas -----\n");	
			}
		
			try {
				chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startThread() {
		SpyListener spyListener = new SpyListener(space, chatArea);
		spyListener.start();
	}
	
	private class SpyListener extends Thread {
		JavaSpace space;

		JTextArea chatArea;

		public SpyListener(JavaSpace space, JTextArea chatArea) {
			this.space = space;
			this.chatArea = chatArea;
		}

		@Override
		public void run() {
			while (true) {
				Spy template = new Spy();
				Spy spy;

				try {
					spy = (Spy) space.read(template, null, Lease.FOREVER);

					if (spy != null) {

						if (spy.type.contentEquals("connected") || spy.type.contentEquals("disconnected")) {
							chatArea.append(spy.content);
							chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
						} else {
							if (!spy.isPrivate) {
								chatArea.append("\n" + spy.username + ": " + spy.content);
								chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
							}

							if (spy.isPrivate) {

								chatArea.append("\n** MP de " + spy.pmSender + " para " + spy.pmReceiver + ": " + spy.content + " **");
								chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));

							}

							if (wordsToSpy.contains(spy.content)) {
								chatArea.append("\n\n----- PALAVRA SUSPEITA DETECTADA ENVIANDO PARA O SERVIDOR -----\n");
								chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
							}
							
							Message msg = new Message();
							
							msg.username = spy.username;
							msg.content = spy.content;
							
							msg.pmSender = spy.pmSender;
							msg.pmReceiver = spy.pmReceiver;
							msg.isPrivate = spy.isPrivate;
							
							msg.type = spy.type;
							
							space.write(msg, null, Lease.FOREVER);
							
							Message msgTemplate = new Message();
							space.take(msgTemplate, null, Lease.FOREVER);

						}

						Thread.sleep(10);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
