package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class UiForClient {
	Client myClient;
	JFrame frame;
	JTextArea txtArea;

	public static void main(String[] args) {
		new UiForClient();
	}

	public UiForClient() {

		myClient = new Client(this);

		myClient.connect();
		myClient.start();

		frame = new JFrame("Oren Client");
		frame.add(runUi());
		frame.setSize(625, 700);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private JPanel runUi() {

		final JLabel lblAddCar = new JLabel("Add Car");
		lblAddCar.setFont(new Font("Serif", Font.BOLD, 30));
		lblAddCar.setForeground(Color.RED);
		JLabel lblLisencePlate = new JLabel("Lisence Plate:");
		lblLisencePlate.setFont(new Font("Serif", Font.BOLD, 20));
		final JTextField txtLisencePlate = new JTextField(10);

		JLabel lblFuelWanted = new JLabel("Fuel wanted:");
		lblFuelWanted.setFont(new Font("Serif", Font.BOLD, 20));
		final JTextField txtFuelWanted = new JTextField(10);

		JLabel lblCleaningWanted = new JLabel("Cleaning? :");
		lblCleaningWanted.setFont(new Font("Serif", Font.BOLD, 20));
		final JCheckBox ckbIsCleaningWanted = new JCheckBox();

		SpringLayout springLayout = new SpringLayout();

		JPanel panel = new JPanel();
		panel.setLayout(springLayout);

		panel.add(lblAddCar);
		panel.add(lblLisencePlate);
		panel.add(txtLisencePlate);
		panel.add(lblFuelWanted);
		panel.add(txtFuelWanted);
		panel.add(lblCleaningWanted);
		panel.add(ckbIsCleaningWanted);

		JButton btnSubmit = new JButton();
		btnSubmit.setText("Submit");
		btnSubmit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {

				try {
					String txtFormUILicensePlate = txtLisencePlate.getText();
					String txtFormUIfuelWanted = txtFuelWanted.getText();
					int licensePlate;
					float fuelWanted;
					if (txtFormUIfuelWanted.isEmpty())
						fuelWanted = 0;
					else
						fuelWanted = Float.parseFloat(txtFormUIfuelWanted);

					if (txtFormUILicensePlate.isEmpty())
						licensePlate = 0;
					else
						licensePlate = Integer.parseInt(txtFormUILicensePlate);

					addNewCar(licensePlate, fuelWanted,
							ckbIsCleaningWanted.isSelected());
				} catch (NumberFormatException nfex) {
					showErrorMessage(nfex.getMessage(), "Input Error");
				} catch (NullPointerException npex) {
					showErrorMessage(npex.getMessage(), "Input Error");
				} catch (Exception ex) {
					showErrorMessage(ex.getMessage(), "Error");
				}
			}
		});
		panel.add(btnSubmit);

		txtArea = new JTextArea(25, 55);
		txtArea.setSelectedTextColor(Color.BLUE);
		txtArea.setVisible(true);
		JScrollPane scrollPane = new JScrollPane(txtArea);
		txtArea.setEditable(false);
		panel.setPreferredSize(new Dimension(400, 400));
		panel.add(scrollPane);

		springLayout.putConstraint(SpringLayout.WEST, lblAddCar, 5,
				SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, lblAddCar, 30,
				SpringLayout.NORTH, panel);

		springLayout.putConstraint(SpringLayout.WEST, lblLisencePlate, 5,
				SpringLayout.WEST, lblAddCar);
		springLayout.putConstraint(SpringLayout.NORTH, lblLisencePlate, 10,
				SpringLayout.SOUTH, lblAddCar);

		springLayout.putConstraint(SpringLayout.WEST, txtLisencePlate, 25,
				SpringLayout.EAST, lblLisencePlate);
		springLayout.putConstraint(SpringLayout.NORTH, txtLisencePlate, 0,
				SpringLayout.NORTH, lblLisencePlate);

		springLayout.putConstraint(SpringLayout.WEST, lblFuelWanted, 0,
				SpringLayout.WEST, lblLisencePlate);
		springLayout.putConstraint(SpringLayout.NORTH, lblFuelWanted, 10,
				SpringLayout.SOUTH, lblLisencePlate);

		springLayout.putConstraint(SpringLayout.WEST, txtFuelWanted, 0,
				SpringLayout.WEST, txtLisencePlate);
		springLayout.putConstraint(SpringLayout.NORTH, txtFuelWanted, 0,
				SpringLayout.NORTH, lblFuelWanted);

		springLayout.putConstraint(SpringLayout.WEST, lblCleaningWanted, 0,
				SpringLayout.WEST, lblFuelWanted);
		springLayout.putConstraint(SpringLayout.NORTH, lblCleaningWanted, 10,
				SpringLayout.SOUTH, lblFuelWanted);

		springLayout.putConstraint(SpringLayout.WEST, ckbIsCleaningWanted, 0,
				SpringLayout.WEST, txtLisencePlate);
		springLayout.putConstraint(SpringLayout.NORTH, ckbIsCleaningWanted, 0,
				SpringLayout.NORTH, lblCleaningWanted);

		springLayout.putConstraint(SpringLayout.EAST, btnSubmit, 5,
				SpringLayout.EAST, txtFuelWanted);
		springLayout.putConstraint(SpringLayout.NORTH, btnSubmit, 10,
				SpringLayout.SOUTH, lblCleaningWanted);

		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 5,
				SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10,
				SpringLayout.SOUTH, btnSubmit);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				myClient.close();
			}
		});

		return panel;
	}

	public void addNewCar(int carLisence, float fuelWanted,
			Boolean cleaningWanted) {
		myClient.sendData(carLisence, fuelWanted, cleaningWanted);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void showErrorMessage(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	public void sendTextToTextArea(String message) {
		txtArea.append(message + "\n");
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}

}
