package mom;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

public class MomListener extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextArea logArea;
	private JScrollPane logScroll;
	
	public MomListener() {
		initComponents();
		setUpGUI();
	}
	
	private void initComponents() {
		this.logArea = new JTextArea();
		this.logScroll = new JScrollPane();
	}
	
	private void setUpGUI() {
		this.setResizable(false);
		this.setSize(500, 520);
		this.setTitle("Log do Chat Spy");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(new JLabel());	
		
		JLabel logLabel = new JLabel("Log do Chat Spy");
		logLabel.setFont(new Font("Arial", Font.BOLD, 14));
		logLabel.setBounds(180, 0, 200, 60);
		
		logArea.setEditable(false);
		logArea.setColumns(20);
		logArea.setRows(5);		
		logArea.setWrapStyleWord(true);
		logArea.setLineWrap(true);
		logArea.setFont(logArea.getFont().deriveFont(12f));
		logArea.setMargin(new Insets(10, 10, 10, 10));
		
		DefaultCaret caret = (DefaultCaret) logArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		logScroll.setViewportView(logArea);
		logScroll.setBounds(20, 60, 450, 400);
		
		this.add(logLabel);
		this.add(logScroll);
		this.setVisible(true);
	}
	
	public void receiveMessage(String message) {
		logArea.append(message + "\n");
		
		try {
			logArea.setCaretPosition(logArea.getLineStartOffset(logArea.getLineCount() - 1));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}
