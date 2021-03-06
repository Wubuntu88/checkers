package view;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Board;
import model.PieceInterface;
import model.Square;
import controller.CheckersController;

public class CheckersPane extends Pane {

    Canvas canvas = new Canvas(700, 700);
    /*
     * the position of the image is the key, the image is the value
     */
    Hashtable<String, ImageView> positionsOfImages = new Hashtable<String, ImageView>();
    Hashtable<String, String> typeOfpieceAtPosition = new Hashtable<String, String>();
    ImageView selectedImageView = null;
    String selectedKey = null;

    int numberOfSquaresHorizontally = 8;
    double squareWidth = this.canvas.getWidth() / this.numberOfSquaresHorizontally;
    double squareHeight = this.canvas.getHeight() / this.numberOfSquaresHorizontally;

    String typeOfPieceHumanPlaysWith;

    String zerglingString = "zergling";
    String stalkerString = "stalker";

    /**
     * deals with the event listener (the controller) regarding requesting a move
     */
    List<CheckersController> listenersForHumanMove = new ArrayList<CheckersController>();

    List<CheckersController> listenersForComputerMove = new ArrayList<CheckersController>();

    /**
     * Instantiates the CheckersPane and does the following: -Draws the board -inits the Dictionary
     * of positions to ImageViews (Ivar: positionsOfImages) -adds the ImageViews to the CheckersPane
     * in the relevant positions
     */
    public CheckersPane() {
        super();
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        this.getChildren().add(this.canvas);
        this.drawBoard(gc);
        this.initDictionary();
        this.positionPieces();
        Enumeration<String> keys = this.positionsOfImages.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            ImageView iv = this.positionsOfImages.get(key);
            this.getChildren().add(iv);
        }
    }

    /**
     * Note: zerglings are white, stalkers are black
     *
     * @param board
     */
    public CheckersPane(Board board) {
        super();
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        this.getChildren().add(this.canvas);
        this.drawBoard(gc);

        Image zerlingImage = new Image("zergling.jpeg");
        Image stalkerImage = new Image("stalker.jpeg");

        List<Square> squares = board.getGameState();
        for (Square square : squares) {
            if (square.isOccupied()) {
                PieceInterface piece = square.getOccupyingPiece();
                // I use row-1 because row in model is 1, same row in view is 0
                int row = square.getRowNumber() - 1;
                int col = square.getColumnNumber() - 1;
                String position = row + "," + col;

                ImageView iv;
                if (piece.isWhite()) {
                    iv = new ImageView(zerlingImage);
                    this.typeOfpieceAtPosition.put(position, this.zerglingString);
                } else {
                    iv = new ImageView(stalkerImage);
                    this.typeOfpieceAtPosition.put(position, this.stalkerString);
                }
                this.positionsOfImages.put(position, iv);
                iv.setFitWidth(this.squareWidth);
                iv.setFitHeight(this.squareHeight);
                this.getChildren().add(iv);
            }
            this.positionPieces();
        }
    }

    public void addComputerFinishedMoveListener(CheckersController cont) {
        this.listenersForComputerMove.add(cont);
    }

    public void addHumanFinishedMoveListener(CheckersController cont) {
        this.listenersForHumanMove.add(cont);
    }

    /**
     * @param row
     * @param col
     * @return the Color of the square given a row and column
     */
    private Color colorOfSquare(int row, int col) {
        /*
         * if one of the two is even and the other is odd, then the square is red. Otherwise it is
         * black.
         */
        if (row % 2 == 0 && col % 2 == 1 || row % 2 == 1 && col % 2 == 0) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    private void drawBoard(GraphicsContext gc) {
        double squareWidth = this.canvas.getWidth() / this.numberOfSquaresHorizontally;
        double squareHeight = this.canvas.getHeight() / this.numberOfSquaresHorizontally;

        for (int currentRow = 0; currentRow < this.numberOfSquaresHorizontally; currentRow++) {
            for (int currentCol = 0; currentCol < this.numberOfSquaresHorizontally; currentCol++) {
                Color c = this.colorOfSquare(currentRow, currentCol);
                gc.setFill(c);
                double xOffset = currentRow * squareWidth;
                double yOffset = currentCol * squareHeight;
                gc.fillRect(xOffset, yOffset, squareWidth, squareHeight);
            }
        }
    }

    private void fireComputerHasMoved() {
        Iterator<CheckersController> it = this.listenersForHumanMove.iterator();
        while (it.hasNext()) {// but there will only be one listener
            CheckersController cont = it.next();
            cont.computerFinishedMove();
        }
    }

    private void fireHumanHasMoved(ArrayList<String> moves) {
        Iterator<CheckersController> it = this.listenersForHumanMove.iterator();
        while (it.hasNext()) {// but there will only be one listener
            CheckersController cont = it.next();
            cont.humanFinishedMove(moves);
        }
    }

    public int getColForX(double x) {
        int col = (int) x / (int) this.squareWidth;
        return col;
    }

    public int getRowForY(double y) {
        int row = (int) y / (int) this.squareHeight;
        return row;
    }

    private double getXPositionForColumn(int col) {
        return col * this.squareWidth;
    }

    private double getYPositionForRow(int row) {
        return row * this.squareHeight;
    }

    public boolean imageHasBeenSelected() {
        return this.selectedImageView != null;
    }

    /**
     * Creates the dictionary "positionsOfImages", which contains the row/col position (key) of an
     * ImageView (value)
     */
    private void initDictionary() {
        String[] positionsOfZerglings = { "5,0", "5,2", "5,4", "5,6", "6,1", "6,3", "6,5", "6,7",
                "7,0", "7,2", "7,4", "7,6" };
        String[] positionsOfStalkers = { "0,1", "0,3", "0,5", "0,7", "1,0", "1,2", "1,4", "1,6",
                "2,1", "2,3", "2,5", "2,7" };
        /*
         * bottom pieces are zerglings
         */
        Image image = new Image("zergling.jpeg");
        for (int i = 0; i < positionsOfZerglings.length; i++) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(this.squareWidth);
            imageView.setFitHeight(this.squareHeight);
            this.positionsOfImages.put(positionsOfZerglings[i], imageView);
            this.typeOfpieceAtPosition.put(positionsOfZerglings[i], this.zerglingString);
        }

        /*
         * top pieces are stalkers
         */
        image = new Image("stalker.jpeg");
        for (int i = 0; i < positionsOfStalkers.length; i++) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(this.squareWidth);
            imageView.setFitHeight(this.squareHeight);
            this.positionsOfImages.put(positionsOfStalkers[i], imageView);
            this.typeOfpieceAtPosition.put(positionsOfStalkers[i], this.stalkerString);
        }
    }

    public void kingPieceAtPosition(String position) {
        System.out.println("kinged");
        String nameOfPiece = this.typeOfpieceAtPosition.get(position) == this.stalkerString ? this.stalkerString
                : this.zerglingString;
        ImageView imageToBeReplaced = this.positionsOfImages.get(position);
        Image image;
        image = (nameOfPiece == this.stalkerString) ? new Image("stalkerKing.jpg") : new Image(
                "zerglingKing.jpg");
        ImageView iv = new ImageView(image);
        this.positionsOfImages.put(position, iv);
        iv.setFitWidth(this.squareWidth);
        iv.setFitHeight(this.squareHeight);

        int row = Integer.parseInt(position.substring(0, 1));
        int col = Integer.parseInt(position.substring(2, 3));
        this.positionPieceGivenRowAndColumn(iv, row, col);

        this.getChildren().remove(imageToBeReplaced);
        this.getChildren().add(iv);
        /*
         * NOTE: EVENT FIRED TO THE CONTROLLER BELOW!! In here I signal the controller that the
         * animation of the computer's move has finished.
         */
        this.fireComputerHasMoved();// EVENT FIRED TO CONTROLLER
    }

    public void movePieceToPositionAndKingIt(ArrayList<String> positions) {
        String initialPosition = positions.get(0);
        ImageView pieceToMove = this.positionsOfImages.get(initialPosition);
        String typeOfPiece = this.typeOfpieceAtPosition.get(initialPosition);

        String nextPosition = positions.get(1);
        int row = Integer.parseInt(nextPosition.substring(0, 1));
        int col = Integer.parseInt(nextPosition.substring(2, 3));

        this.positionsOfImages.remove(initialPosition);
        this.positionsOfImages.put(nextPosition, pieceToMove);
        this.typeOfpieceAtPosition.remove(initialPosition);
        if (typeOfPiece == this.stalkerString) {
            this.typeOfpieceAtPosition.put(nextPosition, this.stalkerString);
        } else {
            this.typeOfpieceAtPosition.put(nextPosition, this.zerglingString);
        }
        /*
         * moving animation
         */
        final Timeline timeline = new Timeline();
        double x = this.getXPositionForColumn(col);
        double y = this.getYPositionForRow(row);
        final KeyValue kv_x = new KeyValue(pieceToMove.xProperty(), x);
        final KeyValue kv_y = new KeyValue(pieceToMove.yProperty(), y);
        final KeyFrame kf_x = new KeyFrame(Duration.millis(500), kv_x);
        final KeyFrame kf_y = new KeyFrame(Duration.millis(500), kv_y);
        timeline.getKeyFrames().add(kf_x);
        timeline.getKeyFrames().add(kf_y);
        if (positions.size() > 2) {// if there is 1 more jump after this one...
            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    CheckersPane.this.movePieceToPositionAndKingIt(new ArrayList<String>(positions
                            .subList(1, positions.size())));
                }
            });
        } else {// if this is the last jump
            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    String finalPosition = positions.get(positions.size() - 1);
                    CheckersPane.this.kingPieceAtPosition(finalPosition);
                }
            });
        }
        timeline.play();
    }

    /**
     *
     * @param positions
     */
    public void movePieceToPositions(ArrayList<String> positions) {
        String initialPosition = positions.get(0);
        ImageView pieceToMove = this.positionsOfImages.get(initialPosition);
        String typeOfPiece = this.typeOfpieceAtPosition.get(initialPosition);

        String nextPosition = positions.get(1);
        int row = Integer.parseInt(nextPosition.substring(0, 1));
        int col = Integer.parseInt(nextPosition.substring(2, 3));

        this.positionsOfImages.remove(initialPosition);
        this.positionsOfImages.put(nextPosition, pieceToMove);
        this.typeOfpieceAtPosition.remove(initialPosition);
        if (typeOfPiece == this.stalkerString) {
            this.typeOfpieceAtPosition.put(nextPosition, this.stalkerString);
        } else {
            this.typeOfpieceAtPosition.put(nextPosition, this.zerglingString);
        }
        /*
         * moving animation
         */
        final Timeline timeline = new Timeline();
        double x = this.getXPositionForColumn(col);
        double y = this.getYPositionForRow(row);
        final KeyValue kv_x = new KeyValue(pieceToMove.xProperty(), x);
        final KeyValue kv_y = new KeyValue(pieceToMove.yProperty(), y);
        final KeyFrame kf_x = new KeyFrame(Duration.millis(500), kv_x);
        final KeyFrame kf_y = new KeyFrame(Duration.millis(500), kv_y);
        timeline.getKeyFrames().add(kf_x);
        timeline.getKeyFrames().add(kf_y);
        if (positions.size() > 2) {// if there is 1 more jump after this one...
            // System.out.println("intermediate");
            // System.out.println("size: " + positions.size());
            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    CheckersPane.this.movePieceToPositions(new ArrayList<String>(positions.subList(
                            1, positions.size())));
                }
            });
        } else {// if this is the last jump
            // System.out.println("final.");
            // System.out.println("size: " + positions.size());

            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    /*
                     * In here I signal the controller that the animation of the computer's move has
                     * finished.
                     */
                    CheckersPane.this.fireComputerHasMoved();// EVENT FIRED TO CONTROLLER
                }
            });
        }
        timeline.play();
    }

    /**
     * Moves the selected image to the x y coords that the user specified The ImageView is only
     * moved if it has been selected in selectImage(double x, double y)
     *
     * @param x
     * @param y
     */
    public void moveSelectedImageToCurrentXY(double x, double y) {
        if (this.selectedImageView != null) {
            this.selectedImageView.setX(x - this.selectedImageView.getFitWidth() / 2);
            this.selectedImageView.setY(y - this.selectedImageView.getFitHeight() / 2);
        }
    }

    /**
     * Places the selected piece in the x, y position specified. The piece will appear in the
     * correct square corresponding to the x y position. Requests moving permission from the
     * controller NOTE: THIS IS FOR THE HUMAN MOVING THE PIECE. AN EVENT WILL BE FIRED SIGNALING THE
     * CONTROLLER THAT THE HUMAN HAS MOVED.
     * 
     * @param x
     * @param y
     */
    public void placeInSquareOfXY(double x, double y) {
        int col = (int) x / (int) this.squareWidth;
        int row = (int) y / (int) this.squareHeight;

        final Timeline timeline = new Timeline();
        double xDestination = this.getXPositionForColumn(col);
        double yDestination = this.getYPositionForRow(row);
        final KeyValue kv_x = new KeyValue(this.selectedImageView.xProperty(), xDestination);
        final KeyValue kv_y = new KeyValue(this.selectedImageView.yProperty(), yDestination);
        final KeyFrame kf_x = new KeyFrame(Duration.millis(200), kv_x);
        final KeyFrame kf_y = new KeyFrame(Duration.millis(200), kv_y);
        timeline.getKeyFrames().add(kf_x);
        timeline.getKeyFrames().add(kf_y);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                /*
                 * Notifying the controller that the human has made a move.
                 */
                ArrayList<String> moves = new ArrayList<String>();
                moves.add("1,2: I moved");
                CheckersPane.this.fireHumanHasMoved(moves);
            }
        });
        timeline.play();

        String keyToDelete = null;
        for (String key : this.positionsOfImages.keySet()) {
            if (this.positionsOfImages.get(key) == this.selectedImageView) {
                keyToDelete = key;
                break;
            }
        }
        this.positionsOfImages.remove(keyToDelete);
        String positionAsString = row + "," + col;
        this.positionsOfImages.put(positionAsString, this.selectedImageView);
        String piece = this.typeOfpieceAtPosition.get(keyToDelete);
        this.typeOfpieceAtPosition.remove(keyToDelete);
        this.typeOfpieceAtPosition.put(positionAsString, piece);

    }

    private void positionPieceGivenRowAndColumn(ImageView iv, int row, int col) {
        double x = col * this.squareWidth;
        double y = row * this.squareHeight;
        iv.setX(x);
        iv.setY(y);
    }

    /**
     * uses the position keys (row/col as String) of the images to correctly position the images in
     * the correct x, y position.
     */
    private void positionPieces() {
        Enumeration<String> keys = this.positionsOfImages.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            int row = Character.getNumericValue(key.charAt(0));
            int col = Character.getNumericValue(key.charAt(2));
            ImageView iv = this.positionsOfImages.get(key);
            this.positionPieceGivenRowAndColumn(iv, row, col);
        }
    }

    public void removePiecesAtPositions(ArrayList<String> positions) {
        for (String position : positions) {
            ImageView imageAtPosition = this.positionsOfImages.get(position);

            final Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(imageAtPosition.opacityProperty(), 0);
            KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();

            // removing pieces from those locations on the board
            // i.e. remove them from the positionsOfImages hashtable
            this.positionsOfImages.remove(position);
            this.typeOfpieceAtPosition.remove(position);

            // I ADDED THIS AT NIGHT BEWARE!!!!
            this.getChildren().remove(imageAtPosition);
        }
    }

    /**
     * Code for seeing whether the clicks were on a image. If so, the Ivar selectedImageView is set
     * to the ImageView that was pressed. The selected ImageView is moved in the method
     * moveSelectedImageToCurrentXY(double x, double y)
     *
     * @param x
     *            x coordinate of user's click
     * @param y
     *            y coordinate of user's click
     */
    public void selectImage(double x, double y) {

        for (String key : this.positionsOfImages.keySet()) {
            ImageView iv = this.positionsOfImages.get(key);
            String pieceAtPosition = this.typeOfpieceAtPosition.get(key);
            if (iv.isPressed() && pieceAtPosition.equals(this.typeOfPieceHumanPlaysWith)) {
                this.selectedImageView = iv;
                break;
            }
        }
    }

    public void setTypeOfPieceHumanPlaysWithAsBlack() {
        this.typeOfPieceHumanPlaysWith = "stalker";
    }

    public void setTypeOfPieceHumanPlaysWithAsWhite() {
        this.typeOfPieceHumanPlaysWith = "zergling";
    }

}

/*
 * public void showBoard(Board board) {
 * 
 * for (String key : this.positionsOfImages.keySet()) {
 * this.getChildren().remove(this.positionsOfImages.get(key)); this.positionsOfImages.remove(key); }
 * for (String key : this.typeOfpieceAtPosition.keySet()) { this.typeOfpieceAtPosition.remove(key);
 * }
 * 
 * // bottom code coppied from constructor Image zerlingImage = new Image("zergling.jpeg"); Image
 * stalkerImage = new Image("stalker.jpeg");
 * 
 * List<Square> squares = board.getGameState(); for (Square square : squares) { if
 * (square.isOccupied()) { PieceInterface piece = square.getOccupyingPiece(); // I use row-1 because
 * row in model is 1, same row in view is 0 int row = square.getRowNumber() - 1; int col =
 * square.getColumnNumber() - 1; String position = row + "," + col;
 * 
 * ImageView iv; if (piece.isWhite()) { iv = new ImageView(zerlingImage);
 * this.typeOfpieceAtPosition.put(position, this.zerglingString); } else { iv = new
 * ImageView(stalkerImage); this.typeOfpieceAtPosition.put(position, this.stalkerString); }
 * this.positionsOfImages.put(position, iv); iv.setFitWidth(this.squareWidth);
 * iv.setFitHeight(this.squareHeight); this.getChildren().add(iv); } this.positionPieces(); } }
 */