package homis.app;

import com.sun.glass.ui.Screen;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by troll173 on 11/26/15.
 */
public class TableHelper {

    public static void setColumns(ArrayList<String> columnNames,TableView tbl){

        for(int i=0 ; i<columnNames.size(); i++){
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(columnNames.get(i));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            col.setPrefWidth(Screen.getMainScreen().getWidth() / columnNames.size());
            tbl.getColumns().addAll(col);
        }
    }

    public static void populateTable(ArrayList record, ResultSet rs,TableView tbl) throws SQLException {
        ObservableList data = FXCollections.observableArrayList();

        while(rs.next()){
            //Iterate Row
            ObservableList row = FXCollections.observableArrayList();

            for(int i=0 ; i<record.size(); i++){
                //Iterate Column
             if(record.get(i).getClass().getName().equalsIgnoreCase("java.lang.String")){
                String me = record.get(i).toString();
                 row.add(rs.getString(me.trim().toString()));
             }else{
                 row.add(record.get(i));
             }
            }

            data.add(row);

        }

        tbl.setItems(data);



    }




}
