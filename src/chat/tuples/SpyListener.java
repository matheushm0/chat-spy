package chat.tuples;

import javax.swing.JTextArea;

import chat.enums.SpyWords;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

public class SpyListener extends Thread {

	JavaSpace space;

	JTextArea chatArea;

	public SpyListener(JavaSpace space, JTextArea chatArea) {
		this.space = space;
		this.chatArea = chatArea;
	}

	@Override
	public void run() {
		while (true) {
			Message template = new Message();
			Message msg;

			try {
				msg = (Message) space.read(template, null, Lease.FOREVER);

				if (msg != null) {

					if (msg.type.contentEquals("connected") || msg.type.contentEquals("disconnected")) {
						chatArea.append(msg.content);
						chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
					} else {
						if (!msg.isPrivate) {
							chatArea.append("\n" + msg.username + ": " + msg.content);
							chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));
						}

						if (msg.isPrivate) {

							chatArea.append("\n** MP de " + msg.pmSender + " para " + msg.pmReceiver + ": " + msg.content + " **");
							chatArea.setCaretPosition(chatArea.getLineStartOffset(chatArea.getLineCount() - 1));

						}

						if (SpyWords.contains(msg.content)) {
							chatArea.append("\n----- PALAVRA SUSPEITA DETECTADA ENVIANDO PARA O SERVIDOR -----\n");
						}

					}

					Thread.sleep(10);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
