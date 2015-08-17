package classify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cmw.R;

import java.util.ArrayList;
import java.util.HashMap;

import classify.Constants.C;
import classify.ListItems.ItemForClassify;

/**
 * Created by joe on 21/07/15.
 */
public class ClassifyRepsListAdapter extends BaseAdapter {

    private ArrayList<ItemForClassify> things;
    private HashMap<Integer, Boolean> isTrainingData;
    private Context context;

    public ClassifyRepsListAdapter(ArrayList<ItemForClassify> things, HashMap<Integer, Boolean> isTrainingData, Context context) {
        this.things = things;
        this.context = context;
        this.isTrainingData = isTrainingData;
    }

    @Override
    public int getCount() {
        return things.size();
    }

    @Override
    public Object getItem(int i) {
        return things.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View myInflatedView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myInflatedView = inflater.inflate(R.layout.row_item_classify, null);
        } else {
            myInflatedView = convertView;
        }

        int rowID = things.get(pos).getRowID();
        String name = things.get(pos).getName();
        int actualLabel = things.get(pos).getActualLabel();
        int predictedLabel = things.get(pos).getPredictedLabel();
        int rep = things.get(pos).getRep();
        int exercise = things.get(pos).getExercise();

        TextView nameText = (TextView) myInflatedView.findViewById(R.id.text_classify_row_name);
        nameText.setText(name + " - Rep: " + rep);
        TextView actualLabelText = (TextView) myInflatedView.findViewById(R.id.text_classify_row_actual_label);
        actualLabelText.setText(C.LABELS_EXERCISE_MAP.get(C.EXERCISES.get(exercise)).get(actualLabel));

        TextView predictedLabelText = (TextView) myInflatedView.findViewById(R.id.text_classify_row_predicted_label);

        if (predictedLabel == 0) {
            predictedLabelText.setText(" ");
        } else {
            predictedLabelText.setText(C.LABELS_EXERCISE_MAP.get(C.EXERCISES.get(exercise)).get(predictedLabel));
        }
        ;

        RadioButton toTrainRB = (RadioButton) myInflatedView.findViewById(R.id.rb_training_data);
        RadioButton toClassifyRB = (RadioButton) myInflatedView.findViewById(R.id.rb_to_classify);

        if (actualLabel == 0) {
            toClassifyRB.setChecked(true);
            toTrainRB.setChecked(false);
        } else {
            toClassifyRB.setChecked(false);
            toTrainRB.setChecked(true);
        }

        toTrainRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toTrainRB.setChecked(true);
                toClassifyRB.setChecked(false);
                isTrainingData.put(pos, true);
                //this is a tad sketchy,,, we're working of relative index's (pos) ,, not the rowID,
            }
        });
        toClassifyRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toTrainRB.setChecked(false);
                toClassifyRB.setChecked(true);
                isTrainingData.put(pos, false);
            }
        });


        return myInflatedView;
    }
}
