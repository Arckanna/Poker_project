package com.ivray.poker.ui;

import com.ivray.poker.GameRunner;
import com.ivray.poker.business.Card;
import com.ivray.poker.util.GameDisplay;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Interface graphique Swing pour le poker 5 cartes.
 * Lancement : exécuter main() ou lancer avec l’argument "gui".
 */
public class PokerGUI extends JFrame implements GameView {

	private static final int CARD_WIDTH = 72;
	private static final int CARD_HEIGHT = 100;

	private final JLabel potLabel = new JLabel("Pot : 0");
	private final JLabel stackLabel = new JLabel("Votre stack : 0");
	private final JLabel combinationLabel = new JLabel("—");
	private final JLabel[] cardLabels = new JLabel[5];
	private final JTextArea logArea = new JTextArea(8, 35);
	private final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
	private final JButton btnCheck = new JButton("Check");
	private final JButton btnBet = new JButton("Miser");
	private final JButton btnCall = new JButton("Suivre");
	private final JButton btnFold = new JButton("Se coucher");
	private final JButton btnStart = new JButton("Démarrer la main");
	private final JButton btnConfirmBet = new JButton("Confirmer la mise");
	private final JSpinner betSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 50, 1));

	private final GUIInput guiInput = new GUIInput();

	public PokerGUI() {
		setTitle("Poker 5 cartes");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(520, 520);
		setResizable(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
		Font cardFont = new Font(Font.SERIF, Font.BOLD, 18);

		JPanel north = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel title = new JLabel("POKER 5 CARTES");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
		title.setForeground(new Color(0x1a5f7a));
		north.add(title);
		add(north, BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(10, 10));
		center.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		potLabel.setFont(titleFont);
		stackLabel.setFont(titleFont);
		potLabel.setForeground(new Color(0xb8860b));
		stackLabel.setForeground(new Color(0xb8860b));
		infoPanel.add(potLabel);
		infoPanel.add(new JLabel("   "));
		infoPanel.add(stackLabel);
		center.add(infoPanel, BorderLayout.NORTH);

		JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
		cardsPanel.setBorder(BorderFactory.createTitledBorder("Votre main"));
		for (int i = 0; i < 5; i++) {
			cardLabels[i] = new JLabel("—");
			cardLabels[i].setFont(cardFont);
			cardLabels[i].setOpaque(true);
			cardLabels[i].setBackground(Color.WHITE);
			cardLabels[i].setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(new Color(0x2e8b57), 2),
					BorderFactory.createEmptyBorder(8, 10, 8, 10)));
			cardLabels[i].setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
			cardLabels[i].setHorizontalAlignment(JLabel.CENTER);
			cardsPanel.add(cardLabels[i]);
		}
		center.add(cardsPanel, BorderLayout.CENTER);

		combinationLabel.setFont(titleFont);
		combinationLabel.setForeground(new Color(0x228b22));
		JPanel combPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		combPanel.add(new JLabel("Combinaison : "));
		combPanel.add(combinationLabel);
		center.add(combPanel, BorderLayout.SOUTH);

		add(center, BorderLayout.CENTER);

		actionPanel.setBorder(BorderFactory.createTitledBorder("Action"));
		actionPanel.add(btnStart);
		actionPanel.add(btnCheck);
		actionPanel.add(btnBet);
		actionPanel.add(betSpinner);
		actionPanel.add(btnConfirmBet);
		actionPanel.add(btnCall);
		actionPanel.add(btnFold);
		setActionModeStart();
		add(actionPanel, BorderLayout.SOUTH);

		logArea.setEditable(false);
		logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		logArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		add(new JScrollPane(logArea), BorderLayout.EAST);

		btnStart.addActionListener(e -> guiInput.notifyStart());
		btnCheck.addActionListener(e -> guiInput.notifyWantsToBet(false));
		btnBet.addActionListener(e -> guiInput.notifyWantsToBet(true));
		btnConfirmBet.addActionListener(e -> guiInput.notifyBetAmountConfirmed());
		btnCall.addActionListener(e -> guiInput.notifyCallOrFold(true));
		btnFold.addActionListener(e -> guiInput.notifyCallOrFold(false));
	}

	private void setActionModeStart() {
		btnStart.setVisible(true);
		btnCheck.setVisible(false);
		btnBet.setVisible(false);
		betSpinner.setVisible(false);
		btnConfirmBet.setVisible(false);
		btnCall.setVisible(false);
		btnFold.setVisible(false);
	}

	private void setActionModeCheckOrBet() {
		btnStart.setVisible(false);
		btnCheck.setVisible(true);
		btnBet.setVisible(true);
		betSpinner.setVisible(true);
		btnConfirmBet.setVisible(false);
		btnCall.setVisible(false);
		btnFold.setVisible(false);
	}

	private void setActionModeConfirmBet(int min, int max) {
		btnStart.setVisible(false);
		btnCheck.setVisible(false);
		btnBet.setVisible(false);
		betSpinner.setVisible(true);
		((SpinnerNumberModel) betSpinner.getModel()).setMinimum(min);
		((SpinnerNumberModel) betSpinner.getModel()).setMaximum(max);
		btnConfirmBet.setVisible(true);
		btnCall.setVisible(false);
		btnFold.setVisible(false);
	}

	private void setActionModeCallOrFold(float toCall) {
		btnStart.setVisible(false);
		btnCheck.setVisible(false);
		btnBet.setVisible(false);
		betSpinner.setVisible(false);
		btnConfirmBet.setVisible(false);
		btnCall.setVisible(true);
		btnFold.setVisible(true);
		btnCall.setText("Suivre (" + (int) toCall + ")");
	}

	private void setActionModeDisabled() {
		btnStart.setVisible(false);
		btnCheck.setVisible(false);
		btnBet.setVisible(false);
		betSpinner.setVisible(false);
		btnConfirmBet.setVisible(false);
		btnCall.setVisible(false);
		btnFold.setVisible(false);
	}

	public HumanInputProvider getInput() {
		return guiInput;
	}

	private void log(String message) {
		SwingUtilities.invokeLater(() -> {
			logArea.append(message + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	@Override
	public void showWelcome() {
		SwingUtilities.invokeLater(() -> {
			log("Bienvenue. Cliquez sur « Démarrer la main » pour jouer.");
		});
	}

	@Override
	public void showAnte(int ante, float potTotal) {
		SwingUtilities.invokeLater(() -> {
			potLabel.setText("Pot : " + (int) potTotal);
			log("Chaque joueur paie " + ante + ". Pot = " + (int) potTotal);
		});
	}

	@Override
	public void showYourHand(List<Card> hand, String combination, float potTotal, float stack) {
		SwingUtilities.invokeLater(() -> {
			potLabel.setText("Pot : " + (int) potTotal);
			stackLabel.setText("Votre stack : " + (int) stack);
			combinationLabel.setText(combination);
			for (int i = 0; i < 5 && i < hand.size(); i++) {
				cardLabels[i].setText(GameDisplay.formatCardShort(hand.get(i)));
			}
		});
	}

	@Override
	public void showBetRoundStart() {
		SwingUtilities.invokeLater(() -> log("--- Tour de mise ---"));
	}

	@Override
	public void showAction(String playerName, String actionText, float potAfter) {
		SwingUtilities.invokeLater(() -> {
			potLabel.setText("Pot : " + (int) potAfter);
			log(playerName + " " + actionText + "  (Pot : " + (int) potAfter + ")");
		});
	}

	@Override
	public void showShowdown() {
		SwingUtilities.invokeLater(() -> log("--- Showdown ---"));
	}

	@Override
	public void showShowdownHand(String playerName, List<Card> hand, String combination) {
		SwingUtilities.invokeLater(() -> log(playerName + " : " + hand + " → " + combination));
	}

	@Override
	public void showWinnerByFold(String playerName, float potWon) {
		SwingUtilities.invokeLater(() -> {
			stackLabel.setText("Votre stack : —");
			log("★ " + playerName + " remporte le pot (" + (int) potWon + ") : tout le monde s'est couché.");
		});
	}

	@Override
	public void showWinnerSingle(String playerName, String combination, float potWon) {
		SwingUtilities.invokeLater(() -> {
			log("★ " + playerName + " remporte la main avec " + combination + ". Pot : " + (int) potWon);
		});
	}

	@Override
	public void showWinnersTie(List<String> playerNames, float potEach) {
		SwingUtilities.invokeLater(() -> {
			log("Égalité entre : " + String.join(", ", playerNames) + ". Pot partagé : " + (int) potEach + " chacun.");
		});
	}

	private class GUIInput implements HumanInputProvider {
		private final Object lock = new Object();
		private volatile boolean started;
		private final AtomicReference<Boolean> wantsToBetResult = new AtomicReference<>();
		private final AtomicReference<Boolean> callOrFoldResult = new AtomicReference<>();
		private volatile int confirmedBetAmount;
		private volatile boolean betAmountConfirmed;

		void notifyStart() {
			synchronized (lock) {
				started = true;
				lock.notifyAll();
			}
		}

		void notifyWantsToBet(boolean bet) {
			synchronized (lock) {
				wantsToBetResult.set(bet);
				lock.notifyAll();
			}
		}

		void notifyBetAmountConfirmed() {
			synchronized (lock) {
				confirmedBetAmount = (Integer) betSpinner.getValue();
				betAmountConfirmed = true;
				lock.notifyAll();
			}
		}

		void notifyCallOrFold(boolean call) {
			synchronized (lock) {
				callOrFoldResult.set(call);
				lock.notifyAll();
			}
		}

		@Override
		public void waitForStart() {
			synchronized (lock) {
				while (!started) {
					try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				}
			}
		}

		@Override
		public boolean wantsToBet() {
			SwingUtilities.invokeLater(() -> setActionModeCheckOrBet());
			wantsToBetResult.set(null);
			synchronized (lock) {
				while (wantsToBetResult.get() == null) {
					try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				}
			}
			SwingUtilities.invokeLater(() -> setActionModeDisabled());
			return wantsToBetResult.get();
		}

		@Override
		public int getBetAmount(int min, int max) {
			SwingUtilities.invokeLater(() -> setActionModeConfirmBet(min, max));
			betAmountConfirmed = false;
			synchronized (lock) {
				while (!betAmountConfirmed) {
					try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				}
			}
			SwingUtilities.invokeLater(() -> setActionModeDisabled());
			return Math.max(min, Math.min(confirmedBetAmount, max));
		}

		@Override
		public boolean callOrFold(float toCall) {
			SwingUtilities.invokeLater(() -> setActionModeCallOrFold(toCall));
			callOrFoldResult.set(null);
			synchronized (lock) {
				while (callOrFoldResult.get() == null) {
					try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
				}
			}
			SwingUtilities.invokeLater(() -> setActionModeDisabled());
			return callOrFoldResult.get();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			PokerGUI gui = new PokerGUI();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
			Thread gameThread = new Thread(() -> {
				GameRunner runner = new GameRunner();
				runner.runHand(gui.getInput(), gui);
			});
			gameThread.setDaemon(false);
			gameThread.start();
		});
	}
}
