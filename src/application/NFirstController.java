package application;

import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import toast.LoadAnchor;
import toast.MyDialoug;
import toast.Openscreen;
import toast.Toast;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;

import extrafont.Myfont;
import gnu.io.CommPortIdentifier;

public class NFirstController implements Initializable {

	MyDialoug mydia;

	@FXML
	Label lblpg1offset, lblpg2offset, lblconnection;

	@FXML
	
	StackPane maincontent;

	SimpleBooleanProperty ispopup = new SimpleBooleanProperty(false);

	public static StackPane maincontent1;
	static JFXDialog df;
	JFXDialogLayout dll;

	@FXML
	Button livetest, report, btncloud, btnsetting, txtuname, qtest, btnscada,
			btnrefresh, btnhelp, btnclose, btnport,btnzoom,btnrestart;

	@FXML
	static Button btnadminA;

	@FXML
	static
	Button btnadminlogoutA;

	@FXML
	Button btnadmin;

	@FXML
	
	Button btnadminlogout;

	
	@FXML
	Rectangle recmain;

	Database d = new Database();

	boolean bolConnected;

	void addShortCut() {

		KeyCombination manushortcut = new KeyCodeCombination(KeyCode.M,
				KeyCombination.CONTROL_ANY);
		KeyCombination quickshortcut = new KeyCodeCombination(KeyCode.Q,
				KeyCombination.CONTROL_ANY);
		KeyCombination startshortcut = new KeyCodeCombination(KeyCode.S,
				KeyCombination.CONTROL_ANY);
		KeyCombination confishortcut = new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_ANY);
		KeyCombination refreshshortcut = new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_ANY);
		KeyCombination exitshortcut = new KeyCodeCombination(KeyCode.E,
				KeyCombination.CONTROL_ANY);

		KeyCombination hardreset = new KeyCodeCombination(KeyCode.H,
				KeyCombination.CONTROL_ANY);

		maincontent.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {

				if (manushortcut.match(ke)) {

					Toast.makeText(Main.mainstage, "Opening Manualcontrol..",
							1000, 200, 200);

					Openscreen.open("/userinput/manualcontrol.fxml");

				}

				else if (refreshshortcut.match(ke)) {

					if (DataStore.connect_hardware.get()) {
						sendStop();
						Toast.makeText(Main.mainstage, "Refresing...", 1000,
								200, 200);

					} else {
						connectHardware(DataStore.getCom());

						if (DataStore.connect_hardware.get()) {
							sendStop();
							Toast.makeText(Main.mainstage, "Refresing...",
									1000, 200, 200);
						} else {
							MyDialoug.showError(102);

						}

					}

				}

				else if (hardreset.match(ke)) {

					if (DataStore.connect_hardware.get()) {
						DataStore.hardReset();
						Toast.makeText(Main.mainstage, "Hard reset...", 1000,
								200, 200);

					} else {
						connectHardware(DataStore.getCom());

						if (DataStore.connect_hardware.get()) {
							DataStore.hardReset();
							Toast.makeText(Main.mainstage, "Hard reset...",
									1000, 200, 200);
						} else {

							MyDialoug.showError(102);
						}
					}

				}

				else if (confishortcut.match(ke)) {

					Toast.makeText(Main.mainstage, "Opening Configuration..",
							1000, 200, 200);

					Openscreen.open("/ConfigurationPart/Nconfigurepage.fxml");
				} else if (quickshortcut.match(ke)) {
					quicktest();
				} else if (startshortcut.match(ke)) {

					Toast.makeText(Main.mainstage, "Opening Test..", 1000, 200,
							200);

					Openscreen.open("/userinput/Nselectproject1.fxml");

				} else if (exitshortcut.match(ke)) {

					exitpopup();

				}

			}
		});

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		btnadminA = btnadmin;
		btnadminlogoutA = btnadminlogout;
		System.out.println("Is Admin"+Myapp.isadmin);
		
		
		if (Myapp.isadmin == true)
		{
		
			btnadmin.setVisible(false);
			btnadminlogout.setVisible(true);
				
		}
		
		
		DataStore.isconfigure.set(true);
		DataStore.sc = new SerialCommunicator();

		addShortCut();
		if (DataStore.connect_hardware.get()) {

			lblconnection.setText("Connected(" + DataStore.getCom() + ")");
			btnport.setVisible(false);
		} else {
			lblconnection.setText("");
			//btnport.setVisible(true);
		}

		DataStore.connect_hardware.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				// TODO Auto-generated method stub
				System.out.println("Connection listener : " + arg2);

				if (arg2) {

					lblconnection.setText("Connected(" + DataStore.getCom()
							+ ")");
					btnport.setVisible(false);
				} else {
					lblconnection.setText("");
					btnport.setVisible(true);
				}
			}
		});

		connectHardware(DataStore.getCom());

		txtuname.setText("Welcome, " + Myapp.username);
		System.out.println("usename--:" + Myapp.username);

		Myfont f = new Myfont(15);
		txtuname.setFont(f.getM_M());

		setMainBtns();
		setAllThings();
		setBtnClicks();

		DataStore.isconfigure.set(true);
		setOffset();
		if (DataStore.connect_hardware.get()) {
			
			DataStore.hardReset();
			sendStop(1500);

		}

	}

	void setOffset() {

		lblpg1offset.setText(Myapp.pg1offset.get() + "");
		lblpg2offset.setText(Myapp.pg2offset.get() + "");

		Myapp.pg1offset.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						lblpg1offset.setText(Myapp.getRound((double) newValue,
								4));

					}
				});

			}

		});
		Myapp.pg2offset.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {

						lblpg2offset.setText(Myapp.getRound((double) newValue,
								4));
					}
				});

			}

		});
	}

	public void verifyloginc() {

		AnchorPane popanc = new AnchorPane();

		// popanc.setStyle("-fx-background-color: rgba(100, 300, 100, 0.5); -fx-background-radius: 10;");

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/application/verifycloudloginpopup.fxml"));
		try {

			Pane cmdPane = (Pane) fxmlLoader.load();

			popanc.getChildren().add(cmdPane);
			System.out.println("Load Anchorpane.....");
		} catch (Exception e) {
			e.printStackTrace();
		}

		dll.setBody(popanc);

		df.show();
	}

	void setBtnClicks() {
		
btnadminlogout.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Admin call");
			
				mydia = new MyDialoug(Main.mainstage,
						"/application/Adminpopuplogout.fxml");
				mydia.showDialoug();
				
			}
		});
		
		btnadmin.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("Admin call");
			
				mydia = new MyDialoug(Main.mainstage,
						"/application/Adminpopup.fxml");
				mydia.showDialoug();
				
			}
		});

		btnport.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

				mydia = new MyDialoug(Main.mainstage,
						"/application/Portlistpopup.fxml");
				mydia.showDialoug();

			}
		});

		btnrestart.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				mydia = new MyDialoug(Main.mainstage, "/application/restartapplication.fxml");
				mydia.showDialoug();
			}
		});
		
		btnrefresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				if (DataStore.connect_hardware.get()) {
					sendStop();
				} else {
					connectHardware(DataStore.getCom());

					if (DataStore.connect_hardware.get()) {
						sendStop();
					}

				}

			}
		});

		btncloud.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Open Cloud-------------->>>>");
				// Openscreen.open("/cloud/cloudfirst.fxml");
				try {
					if (Myapp.uid.equals("")) {
						verifyloginc();
					} else {
						Openscreen.open("/cloud/cloudfirst.fxml");
					}
				} catch (Exception e) {
					verifyloginc();
				}
			}
		});

	btnsetting.setOnAction(new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			if (Myapp.isadmin == true) {
				System.out.println("login");
				Openscreen.open("/ConfigurationPart/Nconfigurepage.fxml");
				
			}
				else {
					System.out.println("Not login");
					Toast.makeText(Main.mainstage, "You Don't have admin login.",
							2000, 300, 300);
					
				}

			
			System.out.println("Open Setting-------------->>>>");
			//MainancController.mainanc1.getChildren().setAll(LoadAnchor.configurationfxml);
			

		}
	});
	


		btnzoom.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub

				Main.mainstage.setMaximized(true);
				Main.mainstage.setFullScreen(true);

			}
		});

		txtuname.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				viewprofile();

			}
		});

		qtest.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				quicktest();

			}
		});

		livetest.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//MainancController.mainanc1.getChildren().setAll(LoadAnchor.createtestfxml);
						
						
						Openscreen.open("/userinput/Nselectproject1.fxml");

					}
				});

			}
		});

		report.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				Openscreen.open("/report/first.fxml");
				//MainancController.mainanc1.getChildren().setAll(LoadAnchor.reportfxml);
				
			}
		});

		btnscada.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (Myapp.isadmin == true) {
					System.out.println("login");
					Openscreen.open("/userinput/manualcontrol.fxml");
					
				}
					else {
						System.out.println("Not login");
						Toast.makeText(Main.mainstage, "You Don't have admin login.",
								2000, 300, 300);
						
					}
				// TODO Auto-generated method stub
			}
		});

	

		btnclose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				exitpopup();
			}
		});

		btnhelp.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (Myapp.isadmin == true) {
					System.out.println("login");
					mydia = new MyDialoug(Main.mainstage, "/application/info.fxml");
					mydia.showDialoug();
					
				}
					else {
						System.out.println("Not login");
						Toast.makeText(Main.mainstage, "You Don't have admin login.",
								2000, 300, 300);
						
					}
				

			}
		});

	}

	public void quicktest() {
		Database db = new Database();

		if (db.isExist("select * from lastprojects where lid='" + Myapp.email
				+ "' ")) {
			System.out.println("1 Last project.");
			// AnchorPane popanc=new AnchorPane();

			// popanc.setStyle("-fx-background-color: rgba(100, 300, 100, 0.5); -fx-background-radius: 10;");

			// mydia=new MyDialoug(Main.mainstage,
			// "/userinput/popupresult.fxml");

			mydia = new MyDialoug(Main.mainstage, "/application/popfxml.fxml");

			mydia.showDialoug();

			/*
			 * FXMLLoader fxmlLoader = new
			 * FXMLLoader(getClass().getResource("/application/popfxml.fxml"));
			 * try {
			 * 
			 * Pane cmdPane = (Pane) fxmlLoader.load();
			 * 
			 * popanc.getChildren().add(cmdPane);
			 * System.out.println("Load Anchorpane....."); } catch (Exception e)
			 * { e.printStackTrace(); }
			 */

			// dll.setBody(popanc);

			// df.show();
		} else {
			System.out.println("No Last project.");
			Toast.makeText(Main.mainstage, "You Don't have any previous test",
					2000, 300, 300);
		}
	}

	void setAllThings() {
		maincontent1 = maincontent;
		// h.visibleProperty().bind(Myapp.hb);

		recmain.visibleProperty().bind(ispopup);

		dll = new JFXDialogLayout();
		df = new JFXDialog(NFirstController.maincontent1, dll,
				DialogTransition.CENTER);

		df.visibleProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				ispopup.set(newValue);
				System.out.println(newValue + "");
			}
		});

	}

	public void exitpopup() {

		mydia = new MyDialoug(Main.mainstage, "/application/Nexitpopup.fxml");
		mydia.showDialoug();
	}

	public void viewprofile() {

		Openscreen.open("/application/editeprofile.fxml");
	}

	void sendStop() {
		writeFormat wrD = new writeFormat();
		wrD.stopTN();
		wrD.addLast();

		sendData(wrD);
	}
	void sendStop(int delay) {
		writeFormat wrD = new writeFormat();
		wrD.stopTN();
		wrD.addLast();

		sendData(wrD,delay);
	}

	void sendData(writeFormat w, int slp) {
		System.out.println("Sending Data......");
		w.showData();
		Thread t = new Thread(new SerialWriter(DataStore.out, w, slp));
		try {

			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendData(writeFormat w) {
		System.out.println("Sending Data......");
		w.showData();
		Thread t = new Thread(new SerialWriter(DataStore.out, w));
		t.start();
	}

	public boolean connectHardware(String st) {

		boolean bol = false;

		// sendDataToWeb();
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();

		int count = 0;

		while (pList.hasMoreElements()) {

			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
			System.out.print("Port " + cpi.getName() + " " + cpi.getPortType());
			if (cpi.getName().equals(st)) {
				DataStore.connect_hardware.set(true);
				try {

					DataStore.sc.connect(st);
					bol = true;
					Myapp.hb.set(false);

				} catch (Exception e) {

					e.printStackTrace();
				}

				break;
			}

			System.out.println("PORT :" + cpi.getName());
			count++;
		}

		if (bol == false) {
			// Toast.makeText(Main.mainstage,
			// "Hardware not connected please plugout and plugin", 200, 200,
			// 3000);
		} else {
			// Toast.makeText(Main.mainstage, "Successfully Connected", 200,
			// 200, 3000);

		}

		return bol;
	}

	void setMainBtns() {

		Image image = new Image(this.getClass().getResourceAsStream(
				"/application/quickimg.png"));
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(100);
		imageView.setFitHeight(110);
		qtest.setGraphic(imageView);

		Image image1 = new Image(this.getClass().getResourceAsStream(
				"/application/reportimg.png"));
		ImageView imageView1 = new ImageView(image1);
		imageView1.setFitWidth(100);
		imageView1.setFitHeight(110);
		report.setGraphic(imageView1);
		Image image2 = new Image(this.getClass().getResourceAsStream(
				"/application/startimg.png"));
		ImageView imageView2 = new ImageView(image2);
		imageView2.setFitWidth(100);
		imageView2.setFitHeight(110);
		livetest.setGraphic(imageView2);

		Image image4 = new Image(this.getClass().getResourceAsStream(
				"/application/scada.png"));
		ImageView imageView4 = new ImageView(image4);
		imageView4.setFitWidth(100);
		imageView4.setFitHeight(100);

		btnscada.setGraphic(imageView4);

		Image image5 = new Image(this.getClass().getResourceAsStream(
				"/application/setting.png"));
		ImageView imageView5 = new ImageView(image5);
		imageView5.setFitWidth(100);
		imageView5.setFitHeight(100);
		btnsetting.setGraphic(imageView5);

		Image image6 = new Image(this.getClass().getResourceAsStream(
				"/application/about.png"));
		ImageView imageView6 = new ImageView(image6);
		imageView6.setFitWidth(100);
		imageView6.setFitHeight(120);
		btnhelp.setGraphic(imageView6);

		btncloud.getStyleClass().add("transperant_comm");
	}

}
