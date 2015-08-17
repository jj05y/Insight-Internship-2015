package classify.Adapters;

import android.content.Context;
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
import classify.Fragments.ChangeExerciseLabelsFragment;
import classify.Interfaces.Linker;


public class ChangeExercisesAdapter extends BaseAdapter {

    private final ChangeExerciseLabelsFragment parentFrag;
    private ArrayList<String> things;
    private Context c;
    private LabelsAndExercisesDatabaseHandler db;
    private Linker linker;
    private ChangeLabelsAdapter labelsAdapter;

    public ChangeExercisesAdapter(ArrayList<String> things, Context c, LabelsAndExercisesDatabaseHandler db, Linker linker, ChangeLabelsAdapter labelsAdapter, ChangeExerciseLabelsFragment parentFrag) {
        this.things = things;
        this.c = c;
        this.db = db;
        this.linker = linker;
        this.labelsAdapter = labelsAdapter;
        this.parentFrag = parentFrag;
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
                String thisExercise = things.get(i);
                if (!thisExercise.equals(C.EXERCISES.get(0))) {
                    db.deleteExercise(thisExercise);
                    linker.populateLabelsAndExercises();
                    setThings(C.EXERCISES);
                    labelsAdapter.setThings("");
                    parentFrag.setLabelsForTextBlank();
                } else {
                    Toast.makeText(c, "Can't Delete this Exercise", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return myInflatedView;
    }

    public void setThings (ArrayList<String> things) {
        this.things = things;
        notifyDataSetChanged();
    }
}
