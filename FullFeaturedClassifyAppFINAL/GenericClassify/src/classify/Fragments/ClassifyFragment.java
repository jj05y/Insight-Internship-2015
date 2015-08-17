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
import android.widget.Toast;

import com.cmw.R;

import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import classify.Adapters.ClassifyRepsListAdapter;
import classify.Adapters.ClassifyExerciseListAdapter;
import classify.Constants.C;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Interfaces.Linker;
import classify.ListItems.ItemForClassify;


public class ClassifyFragment extends Fragment {

    private View myInflatedView;
    private Linker linker;
    private RepsDatabaseHandler db;
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
    private int exercise;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_classify, container, false);
        linker = (Linker) getActivity();
        db = linker.getRepsDb();
        items = new ArrayList<>();



        listOfReps = (ListView) myInflatedView.findViewById(R.id.listView_classify);
        listOfExercises = (ListView) myInflatedView.findViewById(R.id.list_view_classify_choose_exercise);

        // Buttons
        classifyButton = (Button) myInflatedView.findViewById(R.id.button_classify);
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (items == null) {
                    Toast.makeText(getActivity(), "Choose an Exercise First", Toast.LENGTH_SHORT).show();
                } else {

                    classifyButton.setText("Please Wait");
                    classifyButton.setEnabled(false);
                    verifyButton.setEnabled(false);

                    doClassification();

                    items = db.getItemsForClassify(currentlyViewedExercise);
                    setUpIsTrainingData();


                    repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
                    listOfReps.setAdapter(repsAdapter);

                    classifyButton.setText("Classify");
                    classifyButton.setEnabled(true);
                    verifyButton.setEnabled(true);
                }

            }
        });

        verifyButton = (Button) myInflatedView.findViewById(R.id.button_verify_after_clasass);
        verifyButton.setEnabled(false);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("exercise", exercise);
                VerifyClassificationFragment verifyClassificationFragment = new VerifyClassificationFragment();
                verifyClassificationFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,verifyClassificationFragment).commit();
            }
        });



        // List of exercises
        ArrayList<String> exercises = new ArrayList<>();
        for (String exercise : C.EXERCISES) {
            if (exercise.equals(C.EXERCISES.get(0))) continue;
            exercises.add(exercise);
        }

        exerciseAdapter = new ClassifyExerciseListAdapter(exercises, getActivity());
        listOfExercises.setAdapter(exerciseAdapter);

        listOfExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentlyViewedExercise = i + 1; //cos we've ignored label not set
                Log.d("item", "looking for ex = " + C.EXERCISES.get(i + 1));
                items = db.getItemsForClassify(currentlyViewedExercise);
                setUpIsTrainingData();
                verifyButton.setEnabled(false);
                exercise = i + 1;

                repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
                listOfReps.setAdapter(repsAdapter);
            }
        });




        //list of reps
        repsAdapter = new ClassifyRepsListAdapter(items, isTrainingData, getActivity());
        listOfReps.setAdapter(repsAdapter);



        return myInflatedView;
    }

    private void setUpIsTrainingData() {
        isTrainingData = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getActualLabel() != 0) {
                isTrainingData.put(i, true);
            } else {
                isTrainingData.put(i, false);
            }
            Log.d("classy", "\tgetting " + i + " \t value " + isTrainingData.get(i));

        }
    }

    private void doClassification() {

        Dataset trainData = new DefaultDataset();
        Dataset classifyData = new DefaultDataset();
        ArrayList<Integer> correspondingRowIDs = new ArrayList<>(); //these are all the rows that are being classified

        for (int i = 0; i < items.size(); i++) {
            String[] featureBits = items.get(i).getFeatureString().split(",");
            double[] features = new double[featureBits.length];

            for (int j = 0; j < features.length; j++) {
                features[j] = Double.parseDouble(featureBits[j]);
            }

            String instLabel = C.LABELS_EXERCISE_MAP.get(C.EXERCISES.get(exercise)).get(items.get(i).getActualLabel());
            Instance inst = new DenseInstance(features, instLabel);
            Log.d("traindata", "\tgetting " + i + " \titems size: " + items.size());
            if (isTrainingData.get(i)) {
                trainData.add(inst);
            }else {
                classifyData.add(inst);
                correspondingRowIDs.add(items.get(i).getRowID());
                Log.d("classy","added item " + i + " with rowID: " + items.get(i).getRowID() );
            }


        }

        classifier = new RandomForest(C.NUM_TREES, false, C.NUM_ATTRIBUTES, new Random());
        classifier.buildClassifier(trainData);
        for (int i = 0; i < correspondingRowIDs.size(); i++) {
            Object predictedLabel = classifier.classify(classifyData.get(i));
            int intForDb = C.LABELS_EXERCISE_MAP.get(C.EXERCISES.get(exercise)).indexOf(predictedLabel);
            db.updatePredictedLabel(correspondingRowIDs.get(i), intForDb);
            Log.d("classy", "updating row " + correspondingRowIDs.get(i) + " with " + predictedLabel);
        }

    }




}
