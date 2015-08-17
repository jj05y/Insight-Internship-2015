package classify.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cmw.R;

import java.util.ArrayList;

import classify.Constants.C;
import classify.DatabaseClasses.LabelsAndExercisesDatabaseHandler;
import classify.Interfaces.Linker;


public class ChangeLabelsAdapter extends BaseAdapter {

    private ArrayList<String> things;
    private Context c;
    private LabelsAndExercisesDatabaseHandler db;
    private Linker linker;
    private String exercise;


    public ChangeLabelsAdapter(ArrayList<String> things, Context c, LabelsAndExercisesDatabaseHandler db, Linker linker) {
        this.things = things;
        this.c = c;
        this.db = db;
        this.linker = linker;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View myInflatedView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myInflatedView = inflater.inflate(R.layout.list_item_add_exercises_labels, null);
        } else {
            myInflatedView = convertView;
        }

        TextView text = (TextView) myInflatedView.findViewById(R.id.text_label_or_exer);
        text.setText(things.get(i));


        Button removeButton = (Button) myInflatedView.findViewById(R.id.button_remove_ex_label);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String thisLabel = things.get(i);
                if (thisLabel.equals(C.AUTO_LABEL) || thisLabel.equals(C.LABEL_NOT_SET)) {
                    Toast.makeText(c, "Can't delete this label", Toast.LENGTH_SHORT).show();
                } else {
                    db.deleteLabel(exercise, things.get(i));
                    linker.populateLabelsAndExercises();
                    setThings(exercise);
                }
            }
        });


        return myInflatedView;
    }

    public void setThings (String exercise) {
        if (exercise.equals("")) {
            things = new ArrayList<String>();
        } else {
            do {
                things = C.LABELS_EXERCISE_MAP.get(exercise);
            } while (things == null);
        }
        Log.d("setThings", "exercise: " + " and things size: " + things.size());
        this.exercise = exercise;
        notifyDataSetChanged();
    }
}
