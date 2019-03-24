package Backgammon;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import Backgammon.Classes.Board;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static Backgammon.Classes.Board.findAllValidCombos;


public class Controller {

    @FXML
    public GridPane Q1;
    @FXML
    public GridPane Q2;
    @FXML
    public GridPane Q3;
    @FXML
    public GridPane Q4;
    @FXML
    public VBox blackBarVBox;
    @FXML
    public VBox whiteBarVBox;
    @FXML
    public VBox blackBearOffVBox;
    @FXML
    public VBox whiteBearOffVBox;
    @FXML
    private GridPane paneId;

    // Player commands textfield
    @FXML
    private TextField pCommands;
    @FXML
    private TextArea gameInfo;
    @FXML
    private Button infoButton;
    @FXML
    private HBox playerOne;
    @FXML
    private HBox playerTwo;
    @FXML
    private VBox doubleBox;
    @FXML
    private VBox diceBox;

    private Player[] players;
    private ArrayList<MoveCombo> moveList;
    private Boolean vis;
    private Boolean gameStart;
    private Boolean hasRolled;
    private int currentDoublingCube;


    public void initialize() {
        players = Main.players;
        VBox[] bar = {whiteBarVBox, blackBarVBox};
        VBox[] bearOff = {whiteBearOffVBox, blackBearOffVBox};
        GridPane[] quadrants = {Q2, Q1, Q3, Q4};    //unusual order is deliberate to help with setInitialPos logic
        Board.setInitialPos(quadrants, bar, bearOff, paneId);
        // Default gameInfo string to be displayed
        gameInfo.setText("\nGame commands:" +
                "\n1. /start to start the game" +
                "\n2. /next to pass turn to other player" +
                "\n3. /quit" +
                "\n4. /commands" +
                "\n5. /move (origin: int) (destination: int) Bar = 0 Bear-off = -1" +
                "\n6. /listMove (letter)  NOTE: The letter(s) should correspond to the letter(s) from the given move list" +
                "\n7. /valid (origin: int) (destination: int)" +
                "\n8. /cheat" +
                "\n9. /print" +
                "\n" +
                "Finally, click on the 'i' button above to open/close this section.\n");

        // Initialising the boolean variables
        vis = true;
        gameStart = false;
        hasRolled = false;

        playerOne.getChildren().add(new Text(players[0].getPlayerName() + "\nPips:" + players[0].getPipsLeft()));
        playerOne.getChildren().add(new ImageView(new Image("Backgammon/res/piece-white.png", 25, 25, false, false)));
        playerTwo.getChildren().add(new Text(players[1].getPlayerName() + "\nPips:" + players[1].getPipsLeft()));
        playerTwo.getChildren().add(new ImageView(new Image("Backgammon/res/piece-black.png", 25, 25, false, false)));

        infoButton.addEventHandler(MouseEvent.ANY, e -> { // Game info is displayed while mouse hovers over info button.
            EventType ev = e.getEventType();
            EventType ex = MouseEvent.MOUSE_EXITED;
            EventType ent = MouseEvent.MOUSE_ENTERED;
            if (!vis && (ev.equals(ex) || ev.equals(ent))) {
                gameInfo.setVisible(!ev.equals(ex));
            }
        });
    }

    // Function for user input in the text field
    @FXML
    public void onEnter(ActionEvent e) throws IOException {
        String inputString = pCommands.getText().toLowerCase();
        if (inputString.equals(""))
            return;
        switch (inputString.split(" ")[0]) {
            case "/quit":
                Platform.exit();
                break;
            case "/commands":
                gameInfo.appendText("\nGame commands:" +
                        "\n1. /start to start the game" +
                        "\n2. /next to pass turn to other player" +
                        "\n3. /quit" +
                        "\n4. /commands" +
                        "\n5. /move (origin: int) (destination: int) NOTE: Bar = 0, Bear-off = -1" +
                        "\n6. /listMove (letter)  NOTE: The letter(s) should correspond to the letter(s) from the given move list" +
                        "\n7. /valid (origin: int) (destination: int)" +
                        "\n8. /cheat" +
                        "\n9. /print" +
                        "\n");
                pCommands.setText("");
                break;
            case "/valid":
                String[] splot2 = inputString.split(" ");
                System.out.println(Board.validMove(new Move(Integer.parseInt(splot2[1]) - 1, Integer.parseInt(splot2[2]) - 1, Board.currentTurn), -1));


                break;
            case "/move":
                pCommands.setText("");
                if (!hasRolled) {
                    gameInfo.appendText("\nPlease roll before you move");
                } else if (Board.currentMoves < Board.maxMoves) {
                    String[] splot = inputString.split(" ");
                    int org, dest;
                    try {
                        org = Integer.parseInt(splot[1]) - 1;
                        dest = Integer.parseInt(splot[2]) - 1;
                        if (org < -1 || dest < -3 || org > 23 || dest > 23)
                            throw new ArrayIndexOutOfBoundsException();
                    } catch (Exception ex) {
                        gameInfo.appendText("\nInvalid syntax. Expected /move int int");
                        break;
                    }
                    Move move = new Move(org, dest, Board.currentTurn);
                    Board.makeMove(move, -1);
                    gameInfo.appendText("\n" + move);

                    if (Board.currentMoves < Board.maxMoves)
                        printMoves();
                    else
                        gameInfo.appendText("\nYour move is now over. Please type /next to pass control");
                } else {
                    gameInfo.appendText("\nYou cannot move again, please type /next to allow the next player to move");
                }
                break;
            case "/start":
                pCommands.setText("");
                if (!gameStart) {
                    Board.rollStart(players);
                    animateRoll(Board.die.getDice1(), Board.die.getDice2());    //show dice in different place from

                    gameInfo.appendText("\n" + players[0].getPlayerName() + " rolled: " + Board.die.getDice1() + ", "
                            + players[1].getPlayerName() + " rolled: " + Board.die.getDice2() + "\n");
                    if (players[0].getColor() == Board.currentTurn)
                        gameInfo.appendText("\n" + players[0].getPlayerName() + "'s turn");

                    else
                        gameInfo.appendText("\n" + players[1].getPlayerName() + "'s turn");

                    moveList = findAllValidCombos();
                    printMoves(); // Printing the valid moves
                    gameStart = true;
                    hasRolled = true;
                }
                break;
            case "/roll":
                pCommands.setText("");
                if (!hasRolled && gameStart) {
                    Board.rollDice();
                    hasRolled = true;
                    animateRoll(Board.die.getDice1(), Board.die.getDice2());

                    //Printing of the results of the player's roll
                    if (players[0].getColor() == Board.currentTurn) {
                        gameInfo.appendText("\n" + players[0].getPlayerName() +
                                " rolled: " + Board.die.getDice1() + ", " + Board.die.getDice2() + "\n");
                    } else {
                        gameInfo.appendText("\n" + players[1].getPlayerName() +
                                " rolled: " + Board.die.getDice1() + ", " + Board.die.getDice2() + "\n");
                    }

                    moveList = findAllValidCombos();
                    printMoves(); // Printing the moves after roll
                } else if (!gameStart)
                    gameInfo.appendText("\nPlease use /start to start the game first");
                else {
                    gameInfo.appendText("\nYou cannot roll again\n");
                }
                break;
            case "/next":
                pCommands.setText("");
                if (!gameStart)
                    gameInfo.appendText("\nUse /start to start the game");
                    //Ensures the player doesn't skip their turn
                else if (Board.currentMoves < Board.maxMoves)
                    gameInfo.appendText("\nYou must use your allotted amount of moves");
                else {
                    Board.nextTurn();
                    // Printing the new player's turn
                    if (players[0].getColor() == Board.currentTurn)
                        gameInfo.appendText("\n" + players[0].getPlayerName() + "'s turn");
                    else
                        gameInfo.appendText("\n" + players[1].getPlayerName() + "'s turn");
                    hasRolled = false;
                    diceBox.getChildren().remove(0, diceBox.getChildren().size());
                }
                break;
            case "/double":
                doubleStakes();
                break;
            case "/test":       //produces IndexOutOfBoundsException when running too many at once
                //pCommands.setText("");
                gameInfo.appendText("\nRunning test...");
                new Thread(() -> {
                    test(Color.BLACK);
                    test(Color.WHITE);
                }).start();
                break;
            case "/cheat":      // Cheat commands cleans the board, then "re-initialises" it to the new board
                Board.clearBoard();
                Board.cheat();
                gameInfo.appendText("\nActivated cheat board. Please roll again\nSetting move to player 1");
                gameStart = true; // In case /cheat was used before game was started
                hasRolled = false;
                pCommands.setText("");
                break;
            case "/listmove": // Using the generated list of moves to move as required by the assignment
                if (Board.currentMoves < Board.maxMoves) {
                    String[] splot = inputString.split(" ");
                    String moveL = null;
                    try {
                        moveL = splot[1];
                    } catch (Exception ex) {
                        gameInfo.appendText("Expected syntax: /listmove letter");
                    }

                    int length = moveL.length();
                    // Gets the index for taking the move from the arrayList
                    int c = ((moveL.charAt(length - 1)) - 97) + (26 * (length - 1));
                    if (c < moveList.size() && c >= 0) {
                        MoveCombo mc = moveList.get(c);
                        for (int i = 0; i < mc.numMovesPerCombo; i++) {
                            Move move = mc.moves[i];
                            gameInfo.appendText("\n" + move);
                            Board.makeMove(move, c);
                        }

                        if (Board.currentMoves < Board.maxMoves)
                            printMoves();
                        else
                            gameInfo.appendText("\nYour move is now over. Please type /next to pass control");

                    } else
                        gameInfo.appendText("\nPlease select a move contained within the list i.e. use a correct letter.");
                } else {
                    gameInfo.appendText("\nYou cannot move again, please type /next to allow the next player to move");
                }
                pCommands.setText("");
                break;
            case "/print": // Printing the moves
                if (hasRolled && gameStart)
                    printMoves();
                else {
                    gameInfo.appendText("\nYou must roll before printing the list of moves");
                }
                break;
            default:
                gameInfo.appendText("\n" + pCommands.getText());
                pCommands.setText("");
                break;
        }
    }

    // Toggles the text area when info Button is clicked
    @FXML
    public void infoB() {
        gameInfo.setMouseTransparent(vis);
        gameInfo.setVisible(!vis);
        vis = !vis;
    }

    // precursor to eventual feature of user being able to make their moves through the GUI
    // as well as through the commands textField
    public void click(MouseEvent event) {
        VBox box = (VBox) event.getSource();
        Strip strip = Board.getStrip(box);
        if (strip == null)
            return;
        if (strip.quantity == 0) {
            if (box.getChildren().size() > 0)
                return;
            Text txt = new Text("No pieces\nleft in\nthis strip");
            box.setStyle("-fx-background-color: red;-fx-opacity: .5");
            box.getChildren().add(txt);
            PauseTransition removeAfter = new PauseTransition(Duration.seconds(2));
            PauseTransition removeColour = new PauseTransition(Duration.seconds(2));
            removeAfter.setOnFinished(e -> box.getChildren().clear());
            removeColour.setOnFinished((e -> box.setStyle(null)));

            removeAfter.play();
            removeColour.play();
        }
    }

    private void doubleStakes() {
        if (doubleBox.getChildren().isEmpty()) {
            doubleBox.getChildren().add(new DoublingCube().imgView);
            currentDoublingCube = 2;
        } else if (currentDoublingCube < 64) {
            doubleBox.getChildren().remove(0);
            currentDoublingCube *= 2;
            doubleBox.getChildren().add(new DoublingCube(currentDoublingCube).imgView);
        } else {
            System.out.println("Can't double anymore");
            doubleBox.getChildren().remove(0);     //I'm assuming we're limiting ourselves to what fits on a normal die
        }                                               //and not letting the players keep doubling as much as they want,
        //so that final remove() is only temporary, for demonstration purposes

    }

    public void clickToDouble() {
        doubleStakes();
    }

    private void test(Color color) {
        Piece testPiece = new Piece(color);
        int x = 0, y = 23, z = 1;
        if (color == Color.WHITE) {
            x = 23;
            y = 0;
            z = -1;
        }
        try {
            Platform.runLater(() -> Board.Bar.insert(testPiece));
            Thread.sleep(800);
            Platform.runLater(() -> Board.Bar.remove(testPiece.color));
            int finalX = x;
            Platform.runLater(() -> Board.insertToStrip(testPiece, finalX));
            for (int i = x; i != y; i += z) {
                Move m = new Move(i, i + z, color);
                Thread.sleep(500);
                Platform.runLater(() -> Board.testMove(m));
            }
            Thread.sleep(500);
            int finalY = y;
            Platform.runLater(() -> Board.getStrip(finalY).pop());
            Platform.runLater(() -> Board.BearOff.insert(testPiece));
            Thread.sleep(1500);
            Platform.runLater(() -> Board.BearOff.remove(testPiece.color));

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void animateRoll(int n1, int n2) {
        Random rand = new Random();
        DiceFace[] dice1s = new DiceFace[7];       //combine both into one 2d array
        DiceFace[] dice2s = new DiceFace[7];      // no particular reason it's 7, other than that's just what I felt looked best

        dice1s[6] = new DiceFace(n1);   //making sure final images match up with numbers printed to info board
        dice2s[6] = new DiceFace(n2);

        new Thread(() -> {
            int n;
            for (int i = 5; i >= 0; i--) {             //use named named variable instead of all these 5/6/7's?
                do {                                        // included just so it wouldn't generate
                    n = rand.nextInt(6) + 1;         // repeat numbers in a row
                } while (n == dice1s[i + 1].number);
                dice1s[i] = new DiceFace(n);
                do {                                        // included just so it wouldn't generate
                    n = rand.nextInt(6) + 1;         // repeat numbers in a row
                } while (n == dice2s[i + 1].number);
                dice2s[i] = new DiceFace(n);
            }

            // Fills backwards so that the no repeat numbers logic would still work when we give it a definite final roll outcome
            //i.e. a preset dice1s[6] and dice2s[6]

            for (int i = 0; i < 7; i++) {
                try {
                    if (i == 0 && !diceBox.getChildren().isEmpty())                           // if they're already there removes existing dice
                        Platform.runLater(() -> diceBox.getChildren().remove(0, diceBox.getChildren().size()));
                    int finalI = i;   //Without this you get the error "Variable used in lambda expression should be final or effectively final"
                    //Before this was avoided by using a forEach loop, but can't really do that now with two dice arrays involved
                    Platform.runLater(() -> diceBox.getChildren().add(dice1s[finalI].imgView));
                    Platform.runLater(() -> diceBox.getChildren().add(dice2s[finalI].imgView));
                    Thread.sleep(150 + (40 * i));   // pauses for a longer amount of time after each change
                    if (i != dice1s.length - 1)
                        Platform.runLater(() -> diceBox.getChildren().remove(0, 2)); // doesn't remove the final result
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            if (dice1s[6].number == dice2s[6].number) {             //when it's doubles it shows two extra copies of the number
                DiceFace extra1 = new DiceFace(dice1s[6].number);
                DiceFace extra2 = new DiceFace(dice2s[6].number);   //necessary because can't add duplicate imgViews
                Platform.runLater(() -> diceBox.getChildren().addAll(extra1.imgView, extra2.imgView));
            }


        }).start();
    }

    //Printing the valid moves
    private void printMoves() throws IOException {
        int x = Board.currentTurn.getValue();
        if (Main.players[x].getPiecesLeft() == 0) { // Ends the game if the player bore off their last piece
            int y = x == 0 ? 1 : 0;
            endGame(Main.players[x], Main.players[y]);
            return;
        }
        ArrayList<MoveCombo> validMoveCombos = findAllValidCombos();
        // System.out.println("\n\nJust to double-check; \n - currentTurn: " + Board.currentTurn.toString() + ".\n - Found valid moves for: " + validMoves.get(0).color);
        System.out.println("\n-------- List Start --------");
        int i = 0;
        gameInfo.appendText("\n\nPossible Moves:\n--------------------");
        if (validMoveCombos.size() > 0) {
            for (MoveCombo mc : validMoveCombos) {
                String letterCode = (i < 26) ? Character.toString('A' + i) : Character.toString('A' + (i / 26) - 1) + Character.toString('A' + i % 26);
                System.out.print(letterCode + ":  ");
                gameInfo.appendText("\n" + letterCode + ":  ");
                for (int j = 0; j < mc.numMovesPerCombo; j++) {
                    System.out.print(mc.moves[j].isHitToString() + " ");
                    gameInfo.appendText(mc.moves[j].isHitToString() + " ");
                }
                System.out.println();
                i++;
            }
            System.out.println("--------- List End ---------");
        } else {
            gameInfo.appendText("\nThere were no possible moves");
            Board.nextTurn();
            Player player = players[0].getColor() == Board.currentTurn ? players[0] : players[1];
            gameInfo.appendText("\n" + player.getPlayerName() + "'s turn");
            hasRolled = false;
        }
    }

    private void endGame(Player winner, Player loser) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("winscreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 715);
        scene.getStylesheets().addAll(this.getClass().getResource("application.css").toExternalForm());
        Winscreen controller = loader.getController();
        controller.setup(winner, loser);
        Main.window.setScene(scene);
    }

}