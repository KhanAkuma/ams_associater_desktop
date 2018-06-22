package ams;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.Pane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML
    Button associationButton;

    @FXML
    Pane mainPane;

    @FXML
    TextArea firstAssociationsList;

    @FXML
    TextArea secondAssociationsList;

    @FXML
    TextField wordField;

    @FXML
    private void associate(ActionEvent event) {

        String firstAssociations;
        StringBuilder secondAssociations;
        if(wordField.getText().length() > 0){
            firstAssociations = getAssociations(wordField.getText());
        } else {
            firstAssociations = "";
        }

        firstAssociationsList.setText(firstAssociations);

        String unformatWord;
        List<String> terms = new LinkedList<>();
        while(firstAssociations.contains("\n")){
            unformatWord = firstAssociations.split("\n")[0];
            unformatWord = unformatWord.replaceAll(" ", "");
            unformatWord = unformatWord.replaceAll("Ä", "Ae");
            unformatWord = unformatWord.replaceAll("Ö", "Oe");
            unformatWord = unformatWord.replaceAll("Ü", "Ue");
            unformatWord = unformatWord.replaceAll("ä", "ae");
            unformatWord = unformatWord.replaceAll("ö", "oe");
            unformatWord = unformatWord.replaceAll("ü", "ue");
            unformatWord = unformatWord.replaceAll("ß", "ss");
            if (!terms.contains(unformatWord)) {
                terms.add(unformatWord);
            }
            firstAssociations = firstAssociations.substring(firstAssociations.indexOf("\n")+1);
        }

        secondAssociations = new StringBuilder();
        for (String term : terms){
            secondAssociations.append(getAssociations(term));
        }

        secondAssociationsList.setText(secondAssociations.toString());

    }

    private String getAssociations(String word){

        word = word.replaceAll(" ", "");
        word = word.replaceAll("Ä", "Ae");
        word = word.replaceAll("Ö", "Oe");
        word = word.replaceAll("Ü", "Ue");
        word = word.replaceAll("ä", "ae");
        word = word.replaceAll("ö", "oe");
        word = word.replaceAll("ü", "ue");
        word = word.replaceAll("ß", "ss");

        StringBuilder associationList;

        String serverResponse;
        try {
            WebResource webResource = Client.create().resource("https://www.openthesaurus.de/synonyme/" +
                    "search?q="+word+"&format=application/json");

            serverResponse = webResource.get(String.class);
        } catch (UniformInterfaceException e) {
            e.printStackTrace();
            return "";
        }

        JSONObject json;
        String synsets;
        List<String> terms = new LinkedList<>();

        try {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(serverResponse);
            synsets = json.get("synsets").toString();

            int loops = 0;
            while(synsets.contains("\"term\":\"")){
                terms.add(synsets.split("\"term\":\"")[1].split("\"")[0]);
                synsets = synsets.substring(synsets.indexOf(terms.get(loops))+1);
                loops++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(terms.size() > 0){
            associationList = new StringBuilder();
            for (String term : terms){
                if(!term.equals(word)) {
                    associationList.append(term).append("\n");
                }
            }
            return associationList.toString();
        } else {
            return "";
        }
    }

}
