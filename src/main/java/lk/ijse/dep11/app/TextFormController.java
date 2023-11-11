package lk.ijse.dep11.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Optional;


public class TextFormController {

    public AnchorPane root;
    public HTMLEditor htmlEditor;
    public MenuBar menuBar;
    public Menu menuFile;
    public MenuItem menuItemNew;
    public MenuItem menuItemOpen;
    public MenuItem menuItemSave;
    public MenuItem menuItemSaveAs;
    public MenuItem menuItemClose;
    public Menu menuHelp;
    public MenuItem menuItemAbout;
    public Label lblFooter;

    public File fileAddress;
    public TextArea txtBody;
    private static boolean isEdited = false;

//--------------------------------------------------
//--------------Initialize Method ------------------
//--------------------------------------------------

    public void initialize(){



    }


 //------------------------------------
//--------------New ------------------
//-------------------------------------

    public void menuItemNewOnAction(ActionEvent actionEvent) {
        if(isEdited) {
            ButtonType buttonNo = new ButtonType("No");
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the existing file before create new Text file",buttonNo,ButtonType.YES);
            Optional<ButtonType> button = confirm.showAndWait();
            if (button.isEmpty() || button.get() == ButtonType.NO) {

                return;
            }
            if (button.get() == ButtonType.YES) {
                menuItemSaveOnAction(actionEvent);
            }
        }
        AppInitializer.observableTitle.set("untitled");
        txtBody.setText("");
        isEdited = false;

    }
//-------------------------------------
//--------------Open ------------------
//-------------------------------------
    public void menuItemOpenOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open a text file");

        File file = fileChooser.showOpenDialog(txtBody.getScene().getWindow());

        if(file == null) return;

        fileAddress = file;
        String fileName = String.valueOf(file);
        AppInitializer.observableTitle.set(fileName.substring(fileName.lastIndexOf('/')+1));
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = fis.readAllBytes();
        fis.close();
        String content = new String(bytes);
        txtBody.setText(new String(bytes));
    }
//-------------------------------------------
//--------------Save ------------------------
//-------------------------------------------
    public void menuItemSaveOnAction(ActionEvent actionEvent) {

        try {
            if(!isEdited) return;
            if (AppInitializer.observableTitle.getValue().equals("untitled")) {
                Alert inform = new Alert(Alert.AlertType.INFORMATION, "There is no text to save",ButtonType.CLOSE);
                inform.show();
                return;
            }
            if (AppInitializer.observableTitle.getValue().equals("*untitled")) {
                menuItemSaveAsOnAction(actionEvent);


            } else {
                FileOutputStream fos = new FileOutputStream(fileAddress, false);

                String text = txtBody.getText();
                byte[] bytes = text.getBytes();
                fos.write(bytes);
                fos.close();
                isEdited = false;
                AppInitializer.observableTitle.set(AppInitializer.observableTitle.get().substring(1));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
//----------------------------------------
//--------------Save As ------------------
//----------------------------------------
    public void menuItemSaveAsOnAction(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save a text File");

            File file =fileChooser.showSaveDialog(txtBody.getScene().getWindow());
            if(file == null)return;
            fileAddress =file;
            FileOutputStream fos = new FileOutputStream(file,false);

            String text = txtBody.getText();
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.close();
            String fileName = String.valueOf(file);
            AppInitializer.observableTitle.set(fileName.substring(fileName.lastIndexOf('/')+1));
            isEdited = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//---------------------------------------
//--------------check is Edited ----------
//----------------------------------------
    public void txtBodyOnKeyReleased(KeyEvent keyEvent) {
        isEdited = true;
        if (AppInitializer.observableTitle.get().charAt(0) == '*')return;
        AppInitializer.observableTitle.set("*".concat(AppInitializer.observableTitle.get()));
    }

//---------------------------------------
//--------------close -------------------
//----------------------------------------

    public void menuItemCloseOnAction(ActionEvent actionEvent) {
        if (isEdited) {

            ButtonType buttonSaveClose = new ButtonType("Save and Close");
            ButtonType buttonClose = new ButtonType("Close without Save");

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, String.format("Save changers to the Document %s before Closing", AppInitializer.observableTitle)
                    ,ButtonType.CANCEL,buttonSaveClose,buttonClose);
            Optional<ButtonType> button =confirm.showAndWait();
            if(button.isEmpty() || button.get() == ButtonType.CANCEL) return;
            if (button.get() == buttonSaveClose) {
                menuItemSaveOnAction(actionEvent);
                if(isEdited) return;
            }
        }
        Platform.exit();
    }
}
