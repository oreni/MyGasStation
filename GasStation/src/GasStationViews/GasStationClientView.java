package GasStationViews;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import javafx.scene.control.TextArea;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import GasStationBL.GasStation;
import GasStationController.GasStationController;
import GasStationDAL.ObjectLoader;
import GasStationDAL.dbConnect;
import GasStationEvents.GasStationEventsListener;
import GasStationExeptions.InvalidPumpException;

public class GasStationClientView extends Application implements
		AbstractGasStationView {

	private LinkedList<GasStationEventsListener> allListeners = new LinkedList<GasStationEventsListener>();;
	TextArea tAshowMessageFromModel;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void registerListener(GasStationEventsListener uiEventListener) {
		allListeners.add(uiEventListener);
	}

	@Override
	public void getException(Exception e) {
		showMessageFromModel("**********");
		showMessageFromModel("Error-->" + e.getMessage());
		showMessageFromModel("**********");
	}

	@Override
	public void start(final Stage primaryStage) {
		try {
			GasStation gasStation = ObjectLoader.loadObject(
					new File("test.xml")).get(0);
			GasStationController gasControl = new GasStationController(this,
					gasStation);
			gasControl.start();

			primaryStage.setTitle("Gas Station View");
			Pane pane = new StackPane();
			final Label lblGasStation = new Label("Oren's Station");
			final TextField txtFillMainFuelPool = new TextField();

			final Button btnOpenAddCarForm = new Button();
			btnOpenAddCarForm.setText("Open Add Car Form");
			btnOpenAddCarForm.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					Pane addCarPane = new StackPane();
					Label lblAddCar = new Label("Add Car");
					Label lblLisencePlate = new Label("Lisence Plate:");
					final TextField txtLisencePlate = new TextField();
					txtLisencePlate.promptTextProperty().set(
							"Enter License Plate");
					Label lblFuelWanted = new Label("Fuel wanted:");
					final TextField txtFuelWanted = new TextField();
					txtFuelWanted.promptTextProperty().set(
							"Enter Amount Of Fuel");
					Label lblWashingWanted = new Label("Washing? :");
					final CheckBox ckbIsWashingWanted = new CheckBox();

					GridPane grid = new GridPane();
					grid.setAlignment(Pos.TOP_LEFT);
					grid.setHgap(10);
					grid.setVgap(10);
					grid.setPadding(new Insets(25, 25, 25, 25));
					grid.add(lblAddCar, 0, 0, 2, 1);
					grid.add(lblLisencePlate, 0, 1);
					grid.add(txtLisencePlate, 1, 1);
					grid.add(lblFuelWanted, 0, 2);
					grid.add(txtFuelWanted, 1, 2);
					grid.add(lblWashingWanted, 0, 3);
					grid.add(ckbIsWashingWanted, 1, 3);

					Button btnSubmit = new Button();
					btnSubmit.setText("Submit");
					btnSubmit.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							try {
								String txtFormUILicensePlate = txtLisencePlate
										.getText();
								String txtFormUIfuelWanted = txtFuelWanted
										.getText();
								float fuelWanted;
								int licensePlate;
								if (txtFormUIfuelWanted.isEmpty())
									fuelWanted = 0;
								else
									fuelWanted = Float
											.parseFloat(txtFormUIfuelWanted);

								if (txtFormUILicensePlate.isEmpty())
									licensePlate = 0;
								else
									licensePlate = Integer
											.parseInt(txtFormUILicensePlate);

								addNewCar(licensePlate, fuelWanted,
										ckbIsWashingWanted.isSelected());

							} catch (NumberFormatException nfex) {
								getException(nfex);
							} catch (NullPointerException npex) {
								getException(npex);
							} catch (Exception ex) {
								getException(ex);
							}
						}
					});

					HBox hbBtnSubmit = new HBox(10);
					hbBtnSubmit.setAlignment(Pos.BOTTOM_RIGHT);
					hbBtnSubmit.getChildren().add(btnSubmit);
					grid.add(hbBtnSubmit, 1, 4);
					addCarPane.getChildren().add(grid);
					Scene scene = new Scene(addCarPane);

					Stage addCarStage = new Stage();
					addCarStage.setScene(scene);
					addCarStage.setTitle("Add Car");
					addCarStage.setAlwaysOnTop(true);
					// Set position of second window, related to primary window.
					addCarStage.setX(primaryStage.getX() + 250);
					addCarStage.setY(primaryStage.getY() + 100);
					addCarStage.setHeight(300);
					addCarStage.setWidth(400);
					addCarStage.setResizable(false);
					addCarStage.show();
				}
			});

			GridPane grid = new GridPane();
			grid.setAlignment(Pos.TOP_LEFT);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25, 25, 25, 25));
			grid.add(lblGasStation, 0, 0, 2, 1);
			grid.add(txtFillMainFuelPool, 1, 2);

			HBox hbBtnOpenNewCarForm = new HBox(10);
			hbBtnOpenNewCarForm.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnOpenNewCarForm.getChildren().add(btnOpenAddCarForm);
			grid.add(hbBtnOpenNewCarForm, 0, 1);

			final Button btnFillMainFuelPool = new Button();
			btnFillMainFuelPool.setText("Fill Main Fuel Pool");
			btnFillMainFuelPool.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						String tmp = txtFillMainFuelPool.getText();
						fillMainFuelPool(Float.parseFloat(tmp));

					} catch (NumberFormatException nfex) {
						getException(nfex);
					} catch (NullPointerException npex) {
						getException(npex);
					} catch (Exception ex) {
						getException(ex);
					}
				}
			});

			HBox hbBtnFillMainFuelPool = new HBox(10);
			hbBtnFillMainFuelPool.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnFillMainFuelPool.getChildren().add(btnFillMainFuelPool);
			grid.add(hbBtnFillMainFuelPool, 0, 2);

			final Button btnProfitForm = new Button();
			btnProfitForm.setText("Open Profits Form");

			btnProfitForm.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {

						Pane profitPane = new StackPane();

						final TextField txtProfit = new TextField();

						final RadioButton rdbProfitFromWash = new RadioButton();
						rdbProfitFromWash.setText("Current Profit Wash");
						rdbProfitFromWash.setUserData("ProfitFromWash");

						final RadioButton rdbProfitFromFuel = new RadioButton();
						rdbProfitFromFuel.setText("Current Profit Fuel");
						rdbProfitFromFuel.setUserData("ProfitFromFuel");

						final RadioButton rdbLisencePlate = new RadioButton();
						rdbLisencePlate.setText("Profit by Car:");
						rdbLisencePlate.setUserData("LisencePlate");
						final TextField txtLisencePlate = new TextField();
						txtLisencePlate.promptTextProperty().set(
								"License Plate Number");
						txtLisencePlate.setEditable(false);

						final RadioButton rdbByPumpID = new RadioButton();
						rdbByPumpID.setText("Profit by pump:");
						rdbByPumpID.setUserData("ByPumpID");
						final TextField txtByPumpID = new TextField();
						txtByPumpID.promptTextProperty().set("Pump Number");
						txtByPumpID.setEditable(false);

						final ToggleGroup rdbGroup = new ToggleGroup();

						rdbProfitFromWash.setToggleGroup(rdbGroup);
						rdbProfitFromFuel.setToggleGroup(rdbGroup);
						rdbLisencePlate.setToggleGroup(rdbGroup);
						rdbByPumpID.setToggleGroup(rdbGroup);

						GridPane profitGrid = new GridPane();
						profitGrid.setAlignment(Pos.TOP_LEFT);
						profitGrid.setHgap(10);
						profitGrid.setVgap(10);
						profitGrid.setPadding(new Insets(25, 25, 25, 25));

						profitGrid.add(rdbProfitFromWash, 0, 0);
						profitGrid.add(rdbProfitFromFuel, 0, 1);
						profitGrid.add(rdbLisencePlate, 0, 2);
						profitGrid.add(txtLisencePlate, 1, 2);
						profitGrid.add(rdbByPumpID, 0, 3);
						profitGrid.add(txtByPumpID, 1, 3);
						profitGrid.add(txtProfit, 0, 5, 2, 1);

						rdbByPumpID.selectedProperty().addListener(
								new ChangeListener<Boolean>() {
									@Override
									public void changed(
											ObservableValue<? extends Boolean> obs,
											Boolean wasPreviouslySelected,
											Boolean isNowSelected) {
										if (isNowSelected) {
											txtByPumpID.setEditable(true);
										} else {
											txtByPumpID.setEditable(false);
										}
									}
								});

						rdbLisencePlate.selectedProperty().addListener(
								new ChangeListener<Boolean>() {
									@Override
									public void changed(
											ObservableValue<? extends Boolean> obs,
											Boolean wasPreviouslySelected,
											Boolean isNowSelected) {
										if (isNowSelected) {
											txtLisencePlate.setEditable(true);
										} else {
											txtLisencePlate.setEditable(false);
										}
									}
								});

						Button btnSubmitProfit = new Button();
						btnSubmitProfit.setText("Submit");
						btnSubmitProfit
								.setOnAction(new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent event) {
										try {
											System.out.println("pressed");
											String selection = (String) rdbGroup
													.getSelectedToggle()
													.getUserData();
											dbConnect.connectToDb();
											int profit = 0;
											String profitMessage = "";
											switch (selection) {

											case "ProfitFromWash":
												System.out.println("selected");
												profitMessage = "Profit from all car wash is: ";
												profit = dbConnect
														.getProfitFromWashing();
												break;

											case "ProfitFromFuel":
												profitMessage = "Profit from all car fueling is: ";
												profit = dbConnect
														.getProfitFromFueling();
												break;

											case "LisencePlate":
												String licensePlate = txtLisencePlate
														.getText();
												profitMessage = "Profit from all car "
														+ licensePlate
														+ " is: ";
												profit = dbConnect
														.getFuelingProfitFromCar(Integer
																.parseInt(licensePlate));
												profit += dbConnect
														.getWashingProfitFromCar(Integer
																.parseInt(licensePlate));
												break;

											case "ByPumpID":
												String pumpId = txtByPumpID
														.getText();
												profitMessage = "Profit from all car fueling on pump "
														+ pumpId + " is: ";
												profit = dbConnect
														.getProfitFromPump(Integer
																.parseInt(pumpId));
												break;

											default:

											}
											System.out.println(profitMessage
													+ profit);
											txtProfit.setText(profitMessage
													+ profit);
										} catch (SQLException
												| NumberFormatException
												| InstantiationException
												| IllegalAccessException
												| ClassNotFoundException e) {
											getException(e);
										}
									}
								});

						HBox hbBtnSubmitProfit = new HBox(10);
						hbBtnSubmitProfit.setAlignment(Pos.BOTTOM_RIGHT);
						hbBtnSubmitProfit.getChildren().add(btnSubmitProfit);
						profitGrid.add(hbBtnSubmitProfit, 1, 4);

						profitPane.getChildren().add(profitGrid);
						Scene profitScene = new Scene(profitPane);
						profitScene.getStylesheets().add(
								GasStationClientView.class.getResource(
										"gasStationCSS.css").toExternalForm());
						Stage profitStage = new Stage();
						profitStage.setScene(profitScene);
						profitStage.setTitle("Profit Form");
						profitStage.setAlwaysOnTop(true);
						profitStage.setX(primaryStage.getX() + 250);
						profitStage.setY(primaryStage.getY() + 100);
						profitStage.setHeight(350);
						profitStage.setWidth(450);
						profitStage.setResizable(false);
						profitStage.show();

					} catch (NumberFormatException nfex) {
						getException(nfex);
					} catch (NullPointerException npex) {
						getException(npex);
					} catch (Exception ex) {
						getException(ex);
					}
				}
			});

			HBox hbBtnProfitForm = new HBox(10);
			hbBtnProfitForm.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnProfitForm.getChildren().add(btnProfitForm);
			grid.add(hbBtnProfitForm, 0, 4);

			final Button btnShowStatistics = new Button();
			btnShowStatistics.setText("Show Statistics");
			btnShowStatistics.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Pane showStatisticsPane = new StackPane();
					GridPane showStatisticsGrid = new GridPane();
					showStatisticsGrid.setAlignment(Pos.TOP_LEFT);
					showStatisticsGrid.setHgap(10);
					showStatisticsGrid.setVgap(10);
					showStatisticsGrid.setPadding(new Insets(25, 25, 25, 25));

					TextArea tAshowStatistics = new TextArea();
					tAshowStatistics.setPrefRowCount(25);
					tAshowStatistics.setPrefColumnCount(40);
					tAshowStatistics.setEditable(false);
					tAshowStatistics.setVisible(true);

					showStatisticsGrid.add(tAshowStatistics, 0, 0);

					showStatisticsPane.getChildren().add(showStatisticsGrid);
					Scene showStatisticsScene = new Scene(showStatisticsPane);
					showStatisticsScene.getStylesheets().add(
							GasStationClientView.class.getResource(
									"gasStationCSS.css").toExternalForm());
					Stage showStatisticStage = new Stage();
					showStatisticStage.setScene(showStatisticsScene);
					showStatisticStage.setTitle("Profit Form");
					showStatisticStage.setAlwaysOnTop(true);
					showStatisticStage.setX(primaryStage.getX() + 250);
					showStatisticStage.setY(primaryStage.getY() + 100);
					showStatisticStage.setHeight(450);
					showStatisticStage.setWidth(450);
					showStatisticStage.setResizable(false);
					showStatisticStage.show();
					tAshowStatistics.setEditable(false);
					tAshowStatistics.appendText(showStatistics());
				}
			});

			HBox hbBtnShowStatistics = new HBox(10);
			hbBtnShowStatistics.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnShowStatistics.getChildren().add(btnShowStatistics);

			grid.add(hbBtnShowStatistics, 0, 5);

			final Button btnCloseStation = new Button();
			btnCloseStation.setText("Close Station");
			btnCloseStation.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					CloseTheStation();
					btnCloseStation.setDisable(true);
					btnFillMainFuelPool.setDisable(true);
					btnOpenAddCarForm.setDisable(true);
					btnProfitForm.setDisable(true);
				}
			});

			HBox hbBtnCloseStation = new HBox(10);
			hbBtnCloseStation.setAlignment(Pos.BOTTOM_LEFT);
			hbBtnCloseStation.getChildren().add(btnCloseStation);
			grid.add(hbBtnCloseStation, 0, 3);

			tAshowMessageFromModel = new TextArea();
			tAshowMessageFromModel.setPrefRowCount(25);
			tAshowMessageFromModel.setPrefColumnCount(40);
			tAshowMessageFromModel.setEditable(false);
			tAshowMessageFromModel.setVisible(true);

			grid.add(tAshowMessageFromModel, 0, 6, 4, 1);

			pane.getChildren().add(grid);
			Scene scene = new Scene(pane);
			// -- Add the CSS
			scene.getStylesheets().add(
					GasStationClientView.class.getResource("gasStationCSS.css")
							.toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (InvalidPumpException e1) {
			getException(e1);
		}
	}

	@Override
	public void stop() {
		CloseTheStation();
		System.exit(0);
	}

	public void addNewCar(int licensePlate, float fuelWanted,
			Boolean washingWanted) {
		try {
			Boolean wantFuel = false;
			if (fuelWanted > 0)
				wantFuel = true;
			for (GasStationEventsListener lis : allListeners)
				lis.addcarModel(licensePlate, washingWanted, wantFuel,
						fuelWanted);
		} catch (Exception e) {
			getException(e);
		}
	}

	public String showStatistics() {
		String stats = "";
		for (GasStationEventsListener lis : allListeners)
			stats = lis.getStatistics();
		return stats;
	}

	public void CloseTheStation() {
		for (GasStationEventsListener lis : allListeners)
			lis.closeTheDayModel();

	}

	public void fillMainFuelPool(float amountToFill) {
		try {
			for (GasStationEventsListener lis : allListeners)
				lis.fillTheMainPumpModel(amountToFill);
		} catch (Exception e) {
			getException(e);
		}
	}

	@Override
	public void showMessageFromModel(final String message) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				tAshowMessageFromModel.appendText(message + "\n");
			}
		});

	}
}
