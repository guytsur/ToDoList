package guytsur.todolistmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    public void onSaveInstanceState(Bundle savedState) {

        super.onSaveInstanceState(savedState);

        // Note: getValues() is a method in your ArrayAdapter subclass
        ArrayList<String> values = new ArrayList<String>();
        for (int i=0;i<listAdapter.getCount();i++){
            values.add(listAdapter.getItem(i));
        }
        savedState.putStringArrayList("savedList", values);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Guy's To Do List:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get Elements
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        final EditText userInput = (EditText) findViewById(R.id.userInput);
        mainListView = (ListView) findViewById(R.id.mainListView);

        //init array
        final ArrayList<String> todoList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, todoList);
        mainListView.setAdapter( listAdapter );

        if (savedInstanceState != null) {
            ArrayList<String> values = savedInstanceState.getStringArrayList("savedList");
            if (values != null) {
                for (int i = 0; i < values.size() ;i++){
                    listAdapter.add(values.get(i));
                    mainListView.setSelection(listAdapter.getCount() - 1);
                }
            }
        }
        final Context context = this;

        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {


                String job = todoList.get(pos);

                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                // set dialog message
                adb
                        .setTitle("Deleting a Job")
                        .setTitle(job)
                        .setMessage("Are you sure you want to delete this job?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                todoList.remove(pos);
                                listAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = adb.create();

                // show it
                alertDialog.show();

                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = userInput.getText().toString();
                listAdapter.add(text);
                mainListView.setSelection(listAdapter.getCount() - 1);
            }
        });



    }
}
