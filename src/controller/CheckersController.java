package controller;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Board;
import view.CheckersPane;

public class CheckersController extends Application {

    private class EnterHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent ke) {
            KeyCode keyCode = ke.getCode();
            if (keyCode == KeyCode.ENTER) {
                String userText = CheckersController.this.botTextArea.getText();
                String[] moves;
                ArrayList<String> theMoves = new ArrayList<>();
                if (userText.contains("-") && userText.contains("x")) {
                    System.out.println("invalid user input, cannot have x and -");
                } else if (userText.contains("-")) {
                    moves = userText.split("-");

                } else if (userText.contains("x")) {
                    moves = userText.split("x");
                }

                CheckersController.this.gameText.append("\n" + userText);
                CheckersController.this.gameTextBox.setText(CheckersController.this.gameText
                        .toString());
                CheckersController.this.botTextArea.setText("");
                CheckersController.this.scrollPane.setVvalue(1.0);

            }

        }

    }

    /**
     * @author Will This handler continually drags the piece that the user has already clicked on.
     *         The peice.s center will be updated to be wherever the cursor's xy coords are.
     */
    private class MouseDraggedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent drag) {
            if (CheckersController.this.root.imageHasBeenSelected()) {
                double x = drag.getX();
                double y = drag.getY();
                CheckersController.this.root.moveSelectedImageToCurrentXY(x, y);
            }
        }
    }

    /**
     * @author Will This Event Handler moves the peice's center to the xy coords of the user's
     *         cursor when the user clicks on a piece.
     */
    private class MousePressedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent press) {
            double x = press.getX();
            double y = press.getY();
            // ascertain if an image was pressed
            CheckersController.this.root.selectImage(x, y);
            if (CheckersController.this.root.imageHasBeenSelected()) {
                CheckersController.this.root.moveSelectedImageToCurrentXY(x, y);
            }
        }
    }

    /**
     * @author Will This event handler places the selected piece on the square that the user
     *         released the mouse button over.
     */
    private class MouseReleasedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent release) {
            if (CheckersController.this.root.imageHasBeenSelected()) {
                double x = release.getX();
                double y = release.getY();
                /*
                 * now I have to find out if the user has selected an image. If that is true, then
                 * we query the model if the user can move the piece to the position that they
                 * released the mouse on.
                 */
                if (CheckersController.this.root.imageHasBeenSelected()) {// if the image is
                    // selected, we might move
                    int row = CheckersController.this.root.getRowForY(y);
                    int col = CheckersController.this.root.getColForX(x);
                    /*
                     * here I will ask the model if it is a valid move, if so the piece will move to
                     * the position.
                     */
                    CheckersController.this.root.placeInSquareOfXY(x, y);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);// causes start() to be called
    }

    // all drawing on the canvas and repositioning of the pieces occurs through
    // this reference.
    CheckersPane root;
    StringBuffer gameText = new StringBuffer("");
    Text gameTextBox;
    TextArea botTextArea;

    ScrollPane scrollPane;

    public void computerFinishedMove() {
        System.out.println("computer finished");
        // TODO Auto-generated method stub

    }

    /**
     * When this is called, that means that the human player has finished a move and it is the
     * computer's turn to move
     */
    public void humanFinishedMove(ArrayList<String> moves) {
        System.out.println("human finished");

        // let computer calculate move
        // execute the move method of the view to reflect this
        // have a handler in the view for the computer finished moving.
        // when the computer's move has finished
    }

    /*
     * This is where are app starts; place any model or other logic below the initialization of the
     * view.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
         * Will's Note: This is where I initialize the view and add the event handlers
         */
        Group rootGroup = new Group();
        primaryStage.setTitle("checkers");
        // root = new CheckersPane();
        Board board = new Board();
        this.root = new CheckersPane(board);

        this.gameTextBox = new Text();
        this.gameTextBox.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        this.gameText.append("welcome to checkers\n");
        this.gameTextBox.setText(this.gameText.toString());
        this.gameTextBox.prefWidth(400);
        this.gameTextBox.prefHeight(580);

        this.scrollPane = new ScrollPane();
        this.scrollPane.setContent(this.gameTextBox);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setPrefWidth(400);
        this.scrollPane.setPrefHeight(580);

        VBox vBox = new VBox();
        vBox.getChildren().add(this.scrollPane);

        this.botTextArea = new TextArea();
        this.botTextArea.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        this.botTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                KeyCode keyCode = ke.getCode();
                if (keyCode == KeyCode.ENTER) {
                    CheckersController.this.gameText.append("\n"
                            + CheckersController.this.botTextArea.getText());
                    CheckersController.this.gameTextBox.setText(CheckersController.this.gameText
                            .toString());
                    CheckersController.this.botTextArea.setText("");
                    CheckersController.this.scrollPane.setVvalue(1.0);

                }
            }
        });
        this.botTextArea.setPrefWidth(400);
        this.botTextArea.setPrefHeight(120);
        vBox.getChildren().addAll(this.botTextArea);

        HBox hBox = new HBox();
        hBox.getChildren().add(this.root);
        hBox.getChildren().add(vBox);

        rootGroup.getChildren().add(hBox);
        primaryStage.setScene(new Scene(rootGroup, 1100, 700));
        primaryStage.show();
        /*
         * checkersScene.addEventHandler(MouseEvent.MOUSE_PRESSED, new MousePressedHandler());
         * checkersScene.addEventHandler(MouseEvent.MOUSE_DRAGGED, new MouseDraggedHandler());
         * checkersScene.addEventHandler(MouseEvent.MOUSE_RELEASED, new MouseReleasedHandler());
         */
        // set the controller to listen for human has finished moving events
        this.root.addHumanFinishedMoveListener(this);
        this.root.addComputerFinishedMoveListener(this);

        /*
         * End of view initialization and setting of event handlers
         */
    }
}
