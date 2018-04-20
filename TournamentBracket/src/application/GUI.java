package application;

import java.io.File;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

///////////////////////////////////////////////////////////////////////////////
//Assignment Name: TournamentBracket
//Author: Jiazhi Yang (jyang436@wisc.edu)
//        Kesong Cao (kcao22@wisc.edu)
//Due Date: May 3, 2018
//Other Source Credits: None
//Known Bugs: None, to the best of my knowledge
///////////////////////////////////////////////////////////////////////////////

public class GUI {

	public static BorderPane setupGUI(BorderPane root) {
		Main.gameCount = 0;
		
		Image image = new Image("march-madness-pic.png",350,350,true,true);
		ImageView imageView = new ImageView(image);
		BorderPane bp = new BorderPane();
		root.setBottom(bp);
		bp.setRight(new ImageView(image));
				
		Main.left = new VBox();
		Main.center = new HBox();

		preComputationLeft();
		root.setLeft(Main.left);

		Main.preComputationRanking();
		Main.setGames();

		preComputationCenter();
		root.setCenter(Main.center);

		return root;
	}

	private static void preComputationLeft() {
		Button loadFile = new Button();
		loadFile = setupLoadFile(loadFile);
		Main.left.getChildren().add(loadFile);

		VBox teamList = new VBox();
		teamList = setupTeamList(teamList);
		Main.left.getChildren().add(teamList);
	}

	private static VBox setupTeamList(VBox teamList) {
		Label labelHead = new Label();
		labelHead.setText("League Rank");
		labelHead.setFont(new Font("Algerian", 15));
		teamList.getChildren().add(labelHead);
		for (Team team : Main.teams) {
			Label label = new Label();
			label.setText(team.getRank() + "  " + team.getName());
			label.setFont(new Font("Algerian", 13));
			teamList.getChildren().add(label);
		}
		return teamList;
	}

	private static Button setupLoadFile(Button loadFile) {
		loadFile.setText("Load");
		loadFile.setFont(new Font("Algerian", 13));
		loadFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
						new ExtensionFilter("All Files", "*.*"));
				File selectedFile = fileChooser.showOpenDialog(Main.stage);
				if (selectedFile != null) {
					Main.teams.clear();
					Main.readFile(null, selectedFile);
					Main.root = GUI.setupGUI(Main.root);
				}
			}
		});
		return loadFile;
	}

	private static void preComputationCenter() {
		Main.currentPage = new HBox();
		if (Main.teams.size() > 1) {
			Main.currentPage = setupPageRegular();
		} else {

		}
		Main.center.getChildren().add(Main.currentPage);
	}

	private static HBox setupPageRegular() {
		ArrayList<VBox> vBoxList = new ArrayList<VBox>();
		int vBoxNum = (int)(Math.log10(Main.teams.size())/Math.log10(2)) * 2 - 1;
		int index = 0;

		for (int i = 0; i <vBoxNum; i++) {
			vBoxList.add(new VBox());
		}
		
		vBoxList.get(vBoxNum/2).getChildren().add(Main.games.get(Main.games.size() - 1).setUpGame());
		for (int i = 0; i < vBoxNum / 2; i++) {
			for (int j = 0; j < (int) Math.pow(2, vBoxNum / 2 - 1 - i); j++) {
				vBoxList.get(i).getChildren().add(Main.games.get(index).setUpGame());
				index++;
			}
			for (int j = 0; j < (int) Math.pow(2, vBoxNum / 2 - 1 - i); j++) {
				vBoxList.get(vBoxList.size() - i - 1).getChildren().add(Main.games.get(index).setUpGame());
				index++;
			}
		}

//		Button submit = new Button();
//		submit.setText("Submit Scores");
//		submit.setFont(new Font("Algerian", 11));
//		submit.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent event) {
//				progressRegular();
//			}
//		});
		
		HBox page = new HBox();
		for (int i = 0; i < vBoxList.size(); i++)
			page.getChildren().add(vBoxList.get(i));
		return page;
	}

//	private static HBox setupOneTeam(HBox oneTeam, Team team) {
//		Label teamProperty = new Label();
//		teamProperty.setFont(new Font("Algerian", 12));
//		teamProperty.setText(team.getRank() + "  " + team.getName());
//
//		TextField score = new TextField();
//		score.setMaxWidth(50);
//		score.setFont(new Font("Algerian", 12));
//		score.setPromptText("Score");
//
//		oneTeam.getChildren().add(teamProperty);
//		oneTeam.getChildren().add(score);
//		return oneTeam;
//	}

//	private static void progressRegular() {
//		if (saveCurrentScores() == -1)
//			return;
//		Main.computeWinners();
//		generateNewPage();
//	}

//	private static void generateNewPage() {
//		HBox root = (HBox) Main.center.getChildren().get(0);
//		for (int i = 0; i < Main.gameCount; i++)
//			root = (HBox) root.getChildren().get(1);
//		Main.gameCount++;
//		Main.currentPage = new HBox();
//		Main.currentPage = setupPageRegular(Main.currentPage);
//		root.getChildren().set(1, Main.currentPage);
//	}

	private static int saveCurrentScores() {
		VBox leftSide = (VBox) Main.currentPage.getChildren().get(0);
		for (int i = 0; i < leftSide.getChildren().size(); i++) {
			try {
				int score = Integer.parseInt(
						((TextField) (((HBox) (leftSide).getChildren().get(i)).getChildren().get(1))).getText());
				Main.teams.get(i).setGameScore(score);
				((TextField) (((HBox) (leftSide).getChildren().get(i)).getChildren().get(1))).setEditable(false);
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("The scores entered have illegal content!");
				alert.setContentText("Please enter scores as integers.\n");
				alert.showAndWait();
				return -1;
			}
		}

		VBox rightSide = (VBox) Main.currentPage.getChildren().get(2);
		for (int i = 0; i < rightSide.getChildren().size(); i++) {
			try {
				int score = Integer.parseInt(
						((TextField) (((HBox) (rightSide).getChildren().get(i)).getChildren().get(1))).getText());
				Main.teams.get(rightSide.getChildren().size() + i).setGameScore(score);
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("The scores entered have illegal content!");
				alert.setContentText("Please enter scores as integers.\n");
				alert.showAndWait();
				return -1;
			}
		}

		for (int i = 0; i < leftSide.getChildren().size(); i++)
			((TextField) (((HBox) (leftSide).getChildren().get(i)).getChildren().get(1))).setEditable(false);
		for (int i = 0; i < rightSide.getChildren().size(); i++)
			((TextField) (((HBox) (rightSide).getChildren().get(i)).getChildren().get(1))).setEditable(false);

		return 0;
	}

	private static void winningTeam() {

	}
}
