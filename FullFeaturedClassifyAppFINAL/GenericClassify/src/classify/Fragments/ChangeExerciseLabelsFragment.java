package classify.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmw.R;

import java.util.ArrayList;

import classify.Adapters.ChangeExercisesAdapter;
import classify.Adapters.ChangeLabelsAdapter;
import classify.Constants.C;
import classify.DatabaseClasses.LabelsAndExercisesDatabaseHandler;
import classify.Interfaces.Linker;

public class ChangeExerciseLabelsFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;
    private LabelsAndExercisesDatabaseHandler db;
    private String currExercise;
    private TextView labelsForText;

    private ChangeExercisesAdapter exerciseAdapter;
    private ChangeLabelsAdapter labelAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_change_exercise_labels, container, false);

        linker = (Linker) getActivity();
        db = linker.getLabelsExerciseDb();
        currExercise = "";


        ListView exerList = (ListView) myInflatedView.findViewById(R.id.listView_exercise_change);
        ListView labelList = (ListView) myInflatedView.findViewById(R.id.listView_label_change);

        labelsForText = (TextView) myInflatedView.findViewById(R.id.text_labels_for);

        EditText editNewExercise = (EditText) myInflatedView.findViewById(R.id.editText_add_exercise);
        EditText editNewLabel = (EditText) myInflatedView.findViewById(R.id.editText_add_label);



        labelAdapter = new ChangeLabelsAdapter(new ArrayList<>(), getActivity(), db, linker);
        exerciseAdapter = new ChangeExercisesAdapter(C.EXERCISES, getActivity(), db, linker, labelAdapter, this);


        exerList.setAdapter(exerciseAdapter);
        labelList.setAdapter(labelAdapter);

        exerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currExercise = C.EXERCISES.get(i);
                labelAdapter.setThings(currExercise);
                labelsForText.setText("Labels for: " + currExercise);
            }
        });

        Button addExercise = (Button) myInflatedView.findViewById(R.id.button_add_exercise);
        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linker.closeKeyboard();
                String newExercise = editNewExercise.getText().toString();
                editNewExercise.setText("");
                if (!newExercise.equals("")) {
                    db.addLabel(newExercise, C.LABEL_NOT_SET);
                    db.addLabel(newExercise, C.AUTO_LABEL);
                    linker.populateLabelsAndExercises();
                    exerciseAdapter.setThings(C.EXERCISES);
                    labelAdapter.setThings(currExercise);
                } else {
                    Toast.makeText(getActivity(), "Enter a name for new exercise", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button addLabel = (Button) myInflatedView.findViewById(R.id.button_add_label);
        addLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linker.closeKeyboard();
                String newLabel = editNewLabel.getText().toString();
                if (!newLabel.equals("")) {
                    editNewLabel.setText("");
                    if (currExercise.equals("") || labelAdapter.isEmpty()) { //stop you adding a label to a non existent exercise
                        Toast.makeText(getActivity(), "Choose an exercise first", Toast.LENGTH_SHORT).show();
                    } else {
                        db.addLabel(currExercise, newLabel);
                        linker.populateLabelsAndExercises();
                        exerciseAdapter.setThings(C.EXERCISES);
                        labelAdapter.setThings(currExercise);
                    }
                } else {
                    Toast.makeText(getActivity(), "Enter a name for new label", Toast.LENGTH_SHORT).show();
                }


        }
    }

    );


    return myInflatedView;
}



    public void setLabelsForTextBlank() {
        labelsForText.setText("Labels for: ");
    }
}