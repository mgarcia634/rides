package gui;

/**
 * @author Software Engineering teachers
 */


import javax.swing.*;

import gui.RateUserGUI;


import gui.ManageVehicleGUI;

import domain.Driver;
import domain.User;
import businessLogic.BLFacade;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class MainGUI extends JFrame {
	private static User loggedInUser = null;

	public static void setLoggedInUser(User user) {
	    loggedInUser = user;
	}

	public static User getLoggedInUser() {
	    return loggedInUser;
	}

	
    private Driver driver;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;
	private JButton viewReservationsButton;
	



    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	 
	public static void setBussinessLogic (BLFacade afi){
		appFacadeInterface=afi;
	}
	protected JLabel jLabelSelectOption;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JPanel panel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	/**
	 * This is the default constructor
	 */
	public MainGUI(Driver d) {
		super();

		driver=d;
		

		// this.setSize(271, 295);
		this.setSize(495, 290);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();				}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				System.out.println("Locale: "+Locale.getDefault());
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);
	
		panel = new JPanel();
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);
		
		
		





		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateRide"));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateRideGUI(driver);
				a.setVisible(true);
			}
		});
		
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QueryRides"));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new FindRidesGUI();

				a.setVisible(true);
			}
		});
		
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridLayout(4, 1, 0, 0));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(panel);
		
		
		// Botón para abrir la ventana de Login
				JButton loginButton = new JButton("Login");
				loginButton.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
				        LoginGUI loginFrame = new LoginGUI();
				        loginFrame.setVisible(true);
				    }
				});

				// Botón para abrir la ventana de Registro
				JButton registerButton = new JButton("Register");
				registerButton.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
				        RegisterGUI registerFrame = new RegisterGUI();
				        registerFrame.setVisible(true);
				    }
				});

				// Agregar los botones al panel principal
				jContentPane.add(loginButton);
				jContentPane.add(registerButton);
				
				// Botón para solicitar una reserva de viaje
				JButton requestRideButton = new JButton("Solicitar Reserva");
				requestRideButton.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
				        RequestRideGUI requestRideFrame = new RequestRideGUI();
				        requestRideFrame.setVisible(true);
				    }
				});

				// Agregar el botón al panel principal
				jContentPane.add(requestRideButton);

		
		setContentPane(jContentPane);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + " - driver :"+driver.getName());
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		// Botón para visualizar solicitudes de reservas
		// Botón para visualizar solicitudes de reservas (solo para conductores)
		viewReservationsButton = new JButton("Visualizar Solicitudes Reserva");
		viewReservationsButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        User loggedInUser = MainGUI.getLoggedInUser();
		        
		        if (loggedInUser == null) {
		            JOptionPane.showMessageDialog(null, "Debes iniciar sesión para ver las reservas.");
		            return;
		        }

		        /*if (!(loggedInUser instanceof Driver)) {
		            JOptionPane.showMessageDialog(null, "Solo los conductores pueden ver las solicitudes de reserva.");
		            return;
		        }
*/
		        // Si es un Driver, abre la ventana
		        JFrame viewReservationsFrame = new ViewReservationsGUI(loggedInUser.getEmail());
		        viewReservationsFrame.setVisible(true);
		    }
		});
		
		// Botón para gestionar solicitudes de reserva (solo para conductores)
		JButton manageRequestsButton = new JButton("Gestionar Solicitudes");
		manageRequestsButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        User loggedInUser = MainGUI.getLoggedInUser();

		        if (loggedInUser == null) {
		            JOptionPane.showMessageDialog(null, "Debes iniciar sesión para gestionar las solicitudes.");
		            return;
		        }

		        JFrame manageRequestsFrame = new ManageReservationsGUI(loggedInUser.getEmail());
		        manageRequestsFrame.setVisible(true);
		    }
		});
		
		// Botón para calificar a otro usuario
		JButton rateUserButton = new JButton("Calificar Usuario");
		rateUserButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        User user = MainGUI.getLoggedInUser();
		        if (user != null) {
		            new RateUserGUI(user).setVisible(true);
		        } else {
		            JOptionPane.showMessageDialog(null, "Debes iniciar sesión para calificar.");
		        }
		    }
		});
		
		JButton rechargeButton = new JButton("Recargar Monedero");
		rechargeButton.addActionListener(e -> {
		    User user = MainGUI.getLoggedInUser();
		    if (user != null) {
		        new RechargeWalletGUI(user).setVisible(true);
		    } else {
		        JOptionPane.showMessageDialog(null, "Debes iniciar sesión para recargar el monedero.");
		    }
		});
		jContentPane.add(rechargeButton);

		// Botón para consultar el saldo actual del usuario
		JButton checkBalanceButton = new JButton("Consultar Saldo");
		checkBalanceButton.addActionListener(e -> {
		    User user = MainGUI.getLoggedInUser();
		    if (user != null) {
		        float balance = user.getWalletBalance();
		        JOptionPane.showMessageDialog(null, "Tu saldo actual es: " + balance + " €");
		    } else {
		        JOptionPane.showMessageDialog(null, "Debes iniciar sesión para consultar tu saldo.");
		    }
		});
		jContentPane.add(checkBalanceButton);
		
		JButton payRideButton = new JButton("Pagar Viaje");
		payRideButton.addActionListener(e -> {
		    User user = MainGUI.getLoggedInUser();
		    if (user != null) {
		        new PayRideGUI(user).setVisible(true);
		    } else {
		        JOptionPane.showMessageDialog(null, "Debes iniciar sesión para pagar un viaje.");
		    }
		});
		jContentPane.add(payRideButton);

		
		// Botón para abrir el buzón
		JButton inboxButton = new JButton("Buzón");
		inboxButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        User user = MainGUI.getLoggedInUser();
		        if (user != null) {
		            new InboxGUI(user.getEmail()).setVisible(true);
		        } else {
		            JOptionPane.showMessageDialog(null, "Debes iniciar sesión para ver el buzón.");
		        }
		    }
		});
		jContentPane.add(inboxButton);

		
		
		jContentPane.add(rateUserButton);

		
		// Botón para gestionar el vehículo del conductor
		JButton manageVehicleButton = new JButton("Gestionar Vehículo");
		manageVehicleButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        new ManageVehicleGUI(driver).setVisible(true);
		    }
		});
		jContentPane.add(manageVehicleButton);


		// Agregar el botón al panel
		jContentPane.add(manageRequestsButton);


		// Agregar el botón al panel
		jContentPane.add(viewReservationsButton);
	}
	
	
	public static void refreshLoggedInUser() {
	    if (loggedInUser != null) {
	        loggedInUser = appFacadeInterface.getUserByEmail(loggedInUser.getEmail());
	    }
	}


	
	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QueryRides"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateRide"));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle")+ " - driver :"+driver.getName());
	}
	
} // @jve:decl-index=0:visual-constraint="0,0"