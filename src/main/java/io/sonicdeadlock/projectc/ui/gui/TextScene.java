package io.sonicdeadlock.projectc.ui.gui;

import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 10/8/2016.
 */
public class TextScene extends Scene {
    private static final Logger LOGGER = LogManager.getLogger(TextScene.class);
    private List<String> currentCharacters = new ArrayList<>();
    private int cursorIndex=-1;
    private Text sceneText = new Text(10,10,"_");
    private Scene scene;
    private ObjectProperty<EventHandler<? super ExecuteEvent>> onExecute;
    public TextScene(@NamedArg("root") Parent root) {
        super(root,700,400, Color.BLACK);
        scene=root.getScene();
        setOnKeyPressed(this::processKeyEvent);
        sceneText.setStroke(Color.LAWNGREEN);
        sceneText.maxHeight(100);

    }

    public Text getText(){
        return sceneText;
    }

    private void processKeyEvent(KeyEvent keyEvent){
        LOGGER.trace(keyEvent.getCode());
        String input = keyEvent.getText();
        if(keyEvent.getCode()==KeyCode.BACK_SPACE){
            if(cursorIndex>=0){
                currentCharacters.remove(cursorIndex);
                LOGGER.trace("User hit backspace so removing character at cursor index");
                if(cursorIndex>0)
                cursorIndex--;
            }
        }else if(isArrowKey(keyEvent)){
            if(currentCharacters.size()>0){
                LOGGER.trace("Move cursor");
                switch (keyEvent.getCode()){
                    case LEFT:
                        if(cursorIndex>0)
                            cursorIndex--;
                        break;
                    case RIGHT:
                        if(cursorIndex<currentCharacters.size()-1)
                            cursorIndex++;
                        break;
                }
            }else{
                LOGGER.info("Todo Move player");
                //TODO: move player
            }
        }else if(keyEvent.getCode()==KeyCode.ENTER){
            getOnExecute().handle(new ExecuteEvent(getCurrentText().toString()));
            currentCharacters.clear();
            cursorIndex=-1;
        }
        else if(!input.isEmpty()){
            if(cursorIndex==currentCharacters.size()-1)
                currentCharacters.add(input);
            else
                currentCharacters.add(cursorIndex+1,input);

            cursorIndex++;
        }
        updateText();


    }

    private static boolean isArrowKey(KeyEvent keyEvent){
        return //keyEvent.getCode() == KeyCode.UP ||keyEvent.getCode()==KeyCode.DOWN ||
                keyEvent.getCode()==KeyCode.LEFT || keyEvent.getCode()==KeyCode.RIGHT;
    }

    private void updateText(){
        sceneText.setText(getCurrentText().insert(cursorIndex+1,'_').toString());
    }

    private StringBuilder getCurrentText(){
        StringBuilder sb = new StringBuilder(currentCharacters.size());
        currentCharacters.forEach(sb::append);
        return sb;
    }

    public EventHandler<? super ExecuteEvent> getOnExecute() {
        return onExecute.get();
    }

    public ObjectProperty<EventHandler<? super ExecuteEvent>> onExecuteProperty() {
        return onExecute;
    }

    public void setOnExecute(EventHandler<? super ExecuteEvent> onExecute) {

        if(this.onExecute==null)
            this.onExecute = new ObjectPropertyBase<EventHandler<? super ExecuteEvent>>() {
//
                @Override
                public Object getBean() {
                    return scene;
                }

                @Override
                public String getName() {
                    return "onExecute";
                }
            };
        this.onExecute.set(onExecute);
    }
}