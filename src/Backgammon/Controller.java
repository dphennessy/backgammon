package Backgammon;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


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
    private GridPane textAreaGrid;

    //Player commands textfield
    @FXML
    private TextField pCommands;
    @FXML
    private TextArea gameInfo;
    @FXML
    private Button infoButton;

    private StringBuilder textString = new StringBuilder();
    private Boolean vis = true;
    private VBox[] slitArray;

    public static class Board {


    }

    public class slit {
    }

    public void initialize() {
        GridPane[] p = {Q1, Q2, Q3, Q4};
        slitArray = new VBox[24];                    // MOVE THIS TO CLASS
        int offset = 0;
        for (GridPane pane : p) {
            for (int i = 0; i < pane.getChildren().size(); i++) {
                slitArray[i + 6 * offset] = (VBox) pane.getChildren().get(i);
                //System.out.println(i+6*offset);
            }
            offset++;
        }


        Image black = new Image("Backgammon/res/piece-black.png");
        Image white = new Image("Backgammon/res/piece-white.png");
        ImageView[] startB = new ImageView[15];
        ImageView[] startW = new ImageView[15];

        for (int i = 0; i < 15; i++) {
            startB[i] = new ImageView();
            startB[i].setImage(black);
        }
        for (int i = 0; i < 15; i++) {
            startW[i] = new ImageView();
            startW[i].setImage(white);
        }

        slitArray[0].getChildren().addAll(startB[0], startB[1], startB[2], startB[3], startB[4]);
        slitArray[4].getChildren().addAll(startW[0], startW[1], startW[2]);
        slitArray[6].getChildren().addAll(startW[3], startW[4], startW[5], startW[6], startW[7]);
        slitArray[11].getChildren().addAll(startB[5], startB[6]);
        slitArray[12].getChildren().addAll(startW[8], startW[9], startW[10], startW[11], startW[12]);
        slitArray[16].getChildren().addAll(startB[7], startB[8], startB[9]);
        slitArray[18].getChildren().addAll(startB[10], startB[11], startB[12], startB[13], startB[14]);
        slitArray[23].getChildren().addAll(startW[13], startW[14]);

        //Default gameInfo string to be displayed
        textString.append(pCommands.getText()).append("\n").append("Game commands\n______________\n1." +
                " \\Quit\n2. \\Commands\n").
                append("Game information will be displayed here\nInput \\ before commands \n");
        gameInfo.setText(textString.toString());
        gameInfo.setEditable(false);
        textAreaGrid.setMouseTransparent(true);


        /* Event handlers on button
            Shows gameInfo text area when mouse hovers over the info button
            Disappears when mouse leaves
            Done this way as I couldn't get the CSS code to work, left in application.css commented out
            if someone wants to take a look
         */
        infoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                if (!vis)
                    gameInfo.setVisible(true);
            }
        });

        infoButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                if (!vis)
                    gameInfo.setVisible(false);
            }
        });
    }

    /*Function for user input in the text field */
    @FXML
    public void onEnter(ActionEvent e) {
        String inputString = pCommands.getText().toLowerCase();
        //User wants to end program
        if (inputString.equals("\\quit")) {
            Platform.exit();
        }
        //User wants command list
        else if (inputString.equals("\\commands")) {
            textString.append("\n").append("\\commands\n").append(pCommands.getText()).append("\n").append("Game commands\n______________\n1." +
                    " \\Quit\n2. \\Commands");
            gameInfo.setText(textString.toString());
            pCommands.setText("");
        }
        //User makes a moves
        else if (inputString.startsWith("\\move ")) {
            /*
            makeMove function here
             */
            System.out.println("Function not present");
        } else if (inputString.equals("\\test")) {
            /*Put test function here for sprint 1 test requirement*/
        }
        //Echoes user input per sprint requirements
        else if (!inputString.equals("")) {
            textString.append("\n").append(pCommands.getText());
            gameInfo.setText(textString.toString());
            gameInfo.setScrollTop(640);
            pCommands.setText("");
        }
    }

    /*Function for the information button
        Changes visibility of the text area
     */
    @FXML
    public void infoB() {
        if (vis) {
            gameInfo.setVisible(false);
            infoButton.setStyle("-fx-background-color: yellow");
            vis = false;
        } else {
            gameInfo.setVisible(true);
            infoButton.setStyle("-fx-background-color: lightgrey");
            vis = true;
        }
    }

    /*public void click(MouseEvent event) {
        VBox box = (VBox) event.getSource();
        Image image = new Image("Backgammon/res/piece-black.png");
        ImageView img = new ImageView();
        img.setImage(image);
        System.out.println(event.getSource());
        box.getChildren().add(img);
        img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<>() {

            @Override
            public void handle(MouseEvent event) {
                //something
            }
        });

        String id = event.getPickResult().getIntersectedNode().getId();
        System.out.println(id);

    }*/

    public void click(MouseEvent event) {
        VBox box = (VBox) event.getSource();
        if(box.getChildren().size()==0)
            System.out.println("No pieces left in strip");
        else
            box.getChildren().remove(box.getChildren().size() - 1);
    }

}

