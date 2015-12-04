package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {
    private final List<Square> gameState;
    private int movesSinceLastCapture = 0;
    private HashMap<PieceColor, Integer> pawnCountMap = new HashMap<>();
    private HashMap<PieceColor, Integer> kingCountMap = new HashMap<>();
    private boolean repeatedStateDraw = false;
    private final HashMap<List<Square>, Integer> stateCounter = new HashMap<>();

    public Board() {
        this.gameState = this.getStartingGameBoardState();
        this.pawnCountMap.put(PieceColor.BLACK, 12);
        this.pawnCountMap.put(PieceColor.WHITE, 12);
        initializeEmptyKingCountMap();
    }

    private void initializeEmptyKingCountMap() {
        this.kingCountMap.put(PieceColor.BLACK, 0);
        this.kingCountMap.put(PieceColor.WHITE, 0);
    }

    public Board(Board otherBoard) {
        this.gameState = new ArrayList<Square>();
        for (final Square square : otherBoard.getGameState()) {
            this.gameState.add(new Square(square));
        }

        this.pawnCountMap.put(PieceColor.BLACK, otherBoard.getNumberOfBlackPawns());
        this.pawnCountMap.put(PieceColor.WHITE, otherBoard.getNumberOfWhitePawns());

        this.kingCountMap.put(PieceColor.BLACK, otherBoard.getNumberOfBlackKings());
        this.kingCountMap.put(PieceColor.WHITE, otherBoard.getNumberOfWhiteKings());
    }

    public int getNumberOfWhiteKings() {
        return this.kingCountMap.get(PieceColor.WHITE);
    }

    public int getNumberOfBlackKings() {
        return this.kingCountMap.get(PieceColor.BLACK);
    }

    public int getNumberOfWhitePawns() {
        return this.pawnCountMap.get(PieceColor.WHITE);
    }

    public int getNumberOfBlackPawns() {
        return this.pawnCountMap.get(PieceColor.BLACK);
    }

    public int getKingCount(PieceColor color) {
        return this.kingCountMap.get(color);
    }

    public int getPawnCount(PieceColor color) {
        return this.pawnCountMap.get(color);
    }

    public Board(List<Integer> blackPositions, List<Integer> whitePositions) {
        this.pawnCountMap.put(PieceColor.WHITE, 0);
        this.pawnCountMap.put(PieceColor.BLACK, 0);

        this.gameState = new ArrayList<Square>(32);

        for (int position = 1; position <= 32; position++) {
            this.gameState.add(new Square(position, NullPiece.getInstance()));
            if (blackPositions.contains(position)) {
                this.setOccupyingPiece(position, new Piece(PieceColor.BLACK));
            } else if (whitePositions.contains(position)) {
                this.setOccupyingPiece(position, new Piece(PieceColor.WHITE));
            }
        }
    }

    private void decrementPieceCount(int position) {
        PieceInterface piece = this.getPiece(position);
        PieceColor pieceColor = piece.getColor();
        HashMap<PieceColor, Integer> mapToDecrement;

        if (piece.isKing()) {
            mapToDecrement = this.kingCountMap;
        } else {
            mapToDecrement = this.pawnCountMap;
        }

        int currentCount = mapToDecrement.get(pieceColor);
        currentCount--;
        mapToDecrement.replace(pieceColor, currentCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Board other = (Board) obj;
        if (this.gameState == null) {
            if (other.gameState != null) {
                return false;
            }
        } else if (!this.gameState.equals(other.gameState)) {
            return false;
        }
        if (this.getTotalNumberOfBlackPieces() != other.getTotalNumberOfBlackPieces()) {
            return false;
        }
        if (this.getTotalNumberOfWhitePieces() != other.getTotalNumberOfWhitePieces()) {
            return false;
        }
        return true;
    }

    public List<Square> getAdjacentSquares(Square square) {
        final List<Integer> squareNumbers = square.getAdjacentPositions();
        return this.getSquares((ArrayList<Integer>) squareNumbers);
    }

    public List<Square> getGameState() {
        return this.gameState;
    }

    public int getTotalNumberOfBlackPieces() {
        return this.getNumberOfBlackPawns() + this.getNumberOfBlackKings();
    }

    public int getTotalNumberOfWhitePieces() {
        return this.getNumberOfWhitePawns() + this.getNumberOfWhiteKings();
    }

    public PieceInterface getPiece(int position) {
        return this.getSquare(position).getOccupyingPiece();
    }

    public ArrayList<PieceInterface> getPieces(List<Integer> positions) {
        final ArrayList<PieceInterface> pieces = new ArrayList<>();
        for (final int position : positions) {
            pieces.add(this.getPiece(position));
        }

        return pieces;
    }

    public Square getSquare(int position) {
        return this.gameState.get(position - 1);
    }

    public ArrayList<Square> getSquares(ArrayList<Integer> positions) {
        final ArrayList<Square> squares = new ArrayList<>();
        for (final int position : positions) {
            squares.add(this.getSquare(position));
        }

        return squares;
    }

    public List<Square> getSquaresForPlayer(PieceColor color) {
        final List<Square> playersSquares = new ArrayList<>();
        for (final Square square : this.getGameState()) {
            if (square.isOccupied()) {
                if (square.getOccupyingPiece().getColor() == color) {
                    playersSquares.add(square);
                }
            }
        }
        return playersSquares;
    }

    /**
     * Returns squares with locations +9, -9, +7, -7 Returns only those squares on the board, i.e.
     * with a checkers number of 1-32 (array index of 0-31). The method returns squares that may be
     * on the other side of the board.
     *
     * @param startingSquare
     *            the square in question
     * @return squares one possibly one jump away, they may try to wrap around the board
     */
    public List<Square> getSquaresThatMightBeOneJumpAway(Square startingSquare) {
        final List<Square> squaresPossiblyOneJumpAway = new ArrayList<Square>();

        final int startingPosition = startingSquare.getPosition();
        final int[] possibleJumpPositions = { startingPosition + 9, startingPosition - 9,
                startingPosition + 7, startingPosition - 7 };

        for (int i = 0; i < possibleJumpPositions.length; i++) {
            if (MoveValidator.isOnBoard(possibleJumpPositions[i])) {
                squaresPossiblyOneJumpAway.add(this.getSquare(possibleJumpPositions[i]));
            }
        }
        return squaresPossiblyOneJumpAway;
    }

    private List<Square> getStartingGameBoardState() {
        final List<Square> startingGameBoard = new ArrayList<>(32);

        for (int i = 1; i <= 32; i++) {
            if (i <= 12) {
                startingGameBoard.add(new Square(i, new Piece(PieceColor.BLACK)));
            } else if ((i > 12) && (i < 21)) {
                startingGameBoard.add(new Square(i, NullPiece.getInstance()));
            } else {
                startingGameBoard.add(new Square(i, new Piece(PieceColor.WHITE)));
            }
        }

        return startingGameBoard;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.gameState == null) ? 0 : this.gameState.hashCode());
        result = (prime * result) + this.getTotalNumberOfBlackPieces();
        result = (prime * result) + this.getTotalNumberOfWhitePieces();
        return result;
    }

    private void incrementPieceCount(int position) {
        PieceInterface piece = this.getPiece(position);
        PieceColor pieceColor = piece.getColor();
        HashMap<PieceColor, Integer> mapToIncrement;

        if (piece.isKing()) {
            mapToIncrement = this.kingCountMap;
        } else {
            mapToIncrement = this.pawnCountMap;
        }

        int currentCount = mapToIncrement.get(pieceColor);
        currentCount++;
        mapToIncrement.replace(pieceColor, currentCount);
    }

    public boolean isDrawState() {
        // if (this.repeatedStateDraw || (this.movesSinceLastCapture >= 50)) {
        if (this.movesSinceLastCapture >= 50) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEndState(PieceColor color) {
        return this.playerHasLost(color) || this.isDrawState();
    }

    public void movePiece(MoveInterface move) {
        final PieceInterface pieceToMove = this.pickUpPiece(move.getStartingPosition());
        if (move instanceof Jump) {
            this.movesSinceLastCapture = 0;
            final Jump jump = (Jump) move;

            for (final int position : jump.getJumpedPositions()) {
                this.removePiece(position);
            }
        } else {
            this.movesSinceLastCapture++;
        }
        this.setOccupyingPiece(move.getEndingPosition(), pieceToMove);
        this.updateStateCounter();
    }

    private PieceInterface pickUpPiece(int position) {
        final PieceInterface pieceToPickUp = this.getPiece(position);
        this.removePiece(position);
        return pieceToPickUp;
    }

    public boolean playerHasLost(PieceColor color) {
        boolean outOfPieces = false;
        if (color == PieceColor.BLACK) {
            outOfPieces = this.getTotalNumberOfBlackPieces() == 0;
        } else {
            outOfPieces = this.getTotalNumberOfWhitePieces() == 0;
        }

        if (outOfPieces) {
            return true;
        }

        final boolean noAvailableMoves = MoveGenerator.getAllPossibleMoves(this, color).isEmpty();
        if (noAvailableMoves) {
            return true;
        } else {
            return false;
        }
    }

    public void removePiece(int position) {
        this.decrementPieceCount(position);
        this.getSquare(position).removeOccupyingPiece();
    }

    public void setOccupyingPiece(int position, PieceInterface pieceToSet) {
        this.getSquare(position).setOccupyingPiece(pieceToSet);
        this.incrementPieceCount(position);
    }

    private void updateStateCounter() {
        Integer count = this.stateCounter.get(this.gameState);

        if (count == null) {
            this.stateCounter.put(this.gameState, 1);
        } else {
            count++;
            this.stateCounter.put(this.gameState, count);
            if (count >= 4) {
                this.repeatedStateDraw = true;
            }
        }

    }

}
