package classify.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cmw.R;

import java.io.File;
import java.util.ArrayList;

import classify.Activities.ConfirmDeleteDialog;
import classify.Adapters.ManageDBAdapter;
import classify.DatabaseClasses.Detail;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.Row;


public class ManageDBFragment extends Fragment {

    private static final int DELETE_ALL = 3;
    public static final int CONFIRM_DELETE = 7;
    private View myInfatedView;
    private Linker linker;
    private RepsDatabaseHandler db;
    ManageDBAdapter adapter;
    ArrayList<Row> rows;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInfatedView = inflater.inflate(R.layout.fragment_manage_db, container, false);

        ListView rowsList = (ListView) myInfatedView.findViewById(R.id.listView_manage_db);
        linker = (Linker) getActivity();
        db = linker.getRepsDb();
        rows = db.getRowStrings();

        adapter = new ManageDBAdapter(getActivity(), rows, this, db);
        rowsList.setAdapter(adapter);

        Button deleteAllRows = (Button) myInfatedView.findViewById(R.id.button_delete_all_rows);
        Button deleteAllPredictedLabels = (Button) myInfatedView.findViewById(R.id.button_remove_predicted_labels);

        deleteAllRows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ConfirmDeleteDialog.class);
                startActivityForResult(i, DELETE_ALL);
            }
        });

        deleteAllPredictedLabels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeAllPredictedLabels();
            }
        });


        return myInfatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case DELETE_ALL:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<Detail> details = db.getAllDetails();
                    for (Detail d : details) {
                        String fname = d.getVideoFile();
                        int rowID = d.getId();
                        File file = new File(fname);
                        file.delete();
                        db.deleteDetail(rowID);
                    }
                    Toast.makeText(getActivity(), "Deleted All", Toast.LENGTH_SHORT).show();
                    rows = db.getRowStrings();
                    adapter.updateRows(rows);

                }
                break;
            case CONFIRM_DELETE:
                if (resultCode == Activity.RESULT_OK) {
                    int pos = data.getIntExtra("pos", 0);
                    Log.d("pos", "from activity result, wanna delete pos " + pos);
                    File file = new File(rows.get(pos).getFileName());
                    file.delete();

                    db.deleteDetail(rows.get(pos).getRowID());
                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                    //updata the listOfReps
                    rows = db.getRowStrings();
                    adapter.updateRows(rows);

                }
                break;
        }
    }
}
