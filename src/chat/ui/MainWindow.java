package chat.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import chat.tuples.Lookup;
import chat.tuples.User;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class MainWindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	//CONNECTION
	private JavaSpace space;
	
	//UI	
	private JButton confirmButton;
	private JTextField usernameField;
	
	public MainWindow() {		
		initComponents();
		
		Lookup finder = new Lookup(JavaSpace.class);
		this.space = (JavaSpace) finder.getService();
		
		initSpace();
		
		setUpGUI();
	}
	
	private void initComponents() {
		this.usernameField = new JTextField();
		this.confirmButton = new JButton();
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
		this.setSize(300, 180);
		this.setTitle("Chat");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(new JLabel());	
		
		JLabel usernameLabel = new JLabel("Nome de usuário");
		usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		usernameLabel.setBounds(40, 0, 200, 60);
		
		usernameField.setBounds(40, 50, 200, 25);
		
		confirmButton.setText("Confirmar");
		confirmButton.setBounds(80, 90, 100, 30);
		confirmButton.addActionListener(this);
		
		this.add(usernameLabel);
		this.add(usernameField);
		this.add(confirmButton);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (!validateFields()) {
			return;
		}

		String username = usernameField.getText();
		
		if (!verifyIfUserExists(username)) {
			this.removeAll();
			this.setVisible(false);
			new ChatWindow(username, space);	
			
			User user = new User();
			user.name = username;
			
			try {
				space.write(user, null, Lease.FOREVER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "Já existe um usuário com o nome: " + username,
					"Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean validateFields() {
		boolean isValid = true;
		
		if (usernameField.getText().isEmpty()) {
			usernameField.setBorder(new LineBorder(Color.RED, 1));

			isValid = false;
		} else {
			usernameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
		}
		 
		return isValid;
	}	

	private boolean verifyIfUserExists(String username) {
		User template = new User();
		template.name = username;
		
		User user;
		
		try {
			user = (User) space.read(template, null, 1000);
			
			if (user != null) {
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
