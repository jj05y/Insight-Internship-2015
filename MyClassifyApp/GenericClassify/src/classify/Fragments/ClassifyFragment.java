package classify.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cmw.R;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import classify.Adapters.ClassifyRepsListAdapter;
import classify.Adapters.ClassifyExerciseListAdapter;
import classify.Constants.C;
import classify.DatabaseClasses.DatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForClassify;


public class ClassifyFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;
    private DatabaseHandler db;
    private Classifier classifier;

    private ClassifyRepsListAdapter repsAdapter;


    private ArrayList<ItemForClassify> items;
    private HashMap<Integer, Boolean> isTrainingData;

    private Button classifyButton;
    private Button verifyButton;
    ListView listOfReps;

    private ListView listOfExercises;
    private ClassifyExerciseListAdapter exerciseAdapter;

    private int currentlyViewedExercise;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_classify, container, false);
        linker = (Linker) getActivity();
        db = linker.getDb();
        items = new ArrayList<>();

        isTrainingData = new HashMap<>();

        listOfReps = (ListView) myInflatedView.findViewById(R.id.listView_classify);
        listOfExercises = (ListView) myInflatedView.findViewById(R.id.list_view_classify_choose_exercise);

        // Buttons
        classifyButton = (Button) myInflatedView.findViewById(R.id.button_classify);
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                classifyButton.setText("Please Wait");
                classifyButton.setEnabled(false);
                verifyButton.setEnabled(false);

                doClassification();

                items = db.getItemsForClassify(currentlyViewedExercise);
                repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
                listOfReps.setAdapter(repsAdapter);

                classifyButton.setText("Classify");
                classifyButton.setEnabled(true);
                verifyButton.setEnabled(true);

            }
        });

        verifyButton = (Button) myInflatedView.findViewById(R.id.button_verify_after_clasass);
        verifyButton.setEnabled(false);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new VerifyClassificationFragment()).commit();
            }
        });



        // List of exercises
        ArrayList<String> exercises = new ArrayList<>();
        for (String exercise : C.EXERCISES) {
            if (exercise.equals(C.EXERCISES[0])) continue;
            exercises.add(exercise);
        }

        exerciseAdapter = new ClassifyExerciseListAdapter(exercises, getActivity());
        listOfExercises.setAdapter(exerciseAdapter);

        listOfExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentlyViewedExercise = i+1; //cos we've ignored label not set
                Log.d("item", "looking for ex = " + C.EXERCISES[i + 1]);
                items = db.getItemsForClassify(currentlyViewedExercise);
                repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
                listOfReps.setAdapter(repsAdapter);
            }
        });



        //list of reps
        repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
        listOfReps.setAdapter(repsAdapter);



        return myInflatedView;
    }

    private void doClassification() {

        Dataset trainData = new DefaultDataset();
        Dataset classifyData = new DefaultDataset();
        ArrayList<Integer> correspondingRowIDs = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            String[] featureBits = items.get(i).getFeatureString().split(",");
            double[] features = new double[featureBits.length];

            for (int j = 0; j < features.length; j++) {
                features[j] = Double.parseDouble(featureBits[j]);
            }

            Instance inst = new DenseInstance(features, C.LABELS[items.get(i).getActualLabel()]);
            if (isTrainingData.get(i)) {
                trainData.add(inst);
            }else {
                classifyData.add(inst);
                correspondingRowIDs.add(items.get(i).getRowID());
            }


        }

        classifier = new RandomForest(C.NUM_TREES, false, C.NUM_ATTRIBUTES, new Random());
        classifier.buildClassifier(trainData);
        for (int i = 0; i < correspondingRowIDs.size(); i++) {
            Object predictedLabel = classifier.classify(classifyData.get(i));
            db.updatePredictedLabel(correspondingRowIDs.get(i), linker.findIndex(predictedLabel));
        }

    }




}
