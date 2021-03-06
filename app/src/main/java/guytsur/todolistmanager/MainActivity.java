package guytsur.todolistmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String JOB_DATE_SEP = " @ ";
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    String job;
    private DatabaseReference mDatabase;
    private ArrayList<String> values;


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
        this.mDatabase = mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);
        // Get Elements
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        mainListView = (ListView) findViewById(R.id.mainListView);



        //init array
        final ArrayList<String> todoList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, todoList);
        mainListView.setAdapter( listAdapter );

        if (savedInstanceState != null) {
            Log.d("guyTag", "got instance");
            values = savedInstanceState.getStringArrayList("savedList");

        }
        else{

        Log.d("guyTag", "adding listener");
        ValueEventListener ArrayListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                values = dataSnapshot.getValue(t);
                if (values != null) {
                for (int i = 0; i < values.size() ;i++){
                    listAdapter.add(values.get(i));
                    mainListView.setSelection(listAdapter.getCount() - 1);
                }
            }   Log.d("guyTag", values.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("values").addListenerForSingleValueEvent(ArrayListener);
        if (values != null) {
                for (int i = 0; i < values.size() ;i++){
                    listAdapter.add(values.get(i));
                    mainListView.setSelection(listAdapter.getCount() - 1);
                }
            }
        }

        final Context context = this;


        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, final int pos, long id) {

                job = todoList.get(pos);
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);

                if (job.startsWith("Call") == true){
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_with_call,popupMenu.getMenu());
                }
                else{
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            // Handle the non group menu items here
                            case R.id.delete_button:
                                todoList.remove(pos);
                                listAdapter.notifyDataSetChanged();

                            case R.id.call_button:
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + job.split(" ")[1].split(JOB_DATE_SEP)[0]));
                                startActivity(intent);

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });


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
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(View v) {
                //listAdapter.add("Fuck yeah @ 11/11/11");
                //listAdapter.add("Call +200 @ 11/11/11");

                final EditText edittext = new EditText(MainActivity.this);
                final EditText date_text = new EditText(MainActivity.this);

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                edittext.setHint("Job");
                layout.addView(edittext);

                date_text.setHint("Date");
                date_text.setFocusable(false);
                layout.addView(date_text);

                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd/MM/yy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        date_text.setText(sdf.format(myCalendar.getTime()));
                    }

                };
                date_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(MainActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });



                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                // set dialog message
                adb
                        .setView(layout)
                        .setTitle("New Job")
                        .setPositiveButton("Create",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String line = edittext.getText().toString() + JOB_DATE_SEP + date_text.getText().toString();

                                Log.d("guyTag","writing...");
                                mDatabase.child("values").child(String.valueOf(listAdapter.getCount())).setValue(line);

                                listAdapter.add(line);
                                mainListView.setSelection(listAdapter.getCount());



                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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
            }
        });




    }
}
