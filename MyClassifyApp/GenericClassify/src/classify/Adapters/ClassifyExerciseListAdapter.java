package classify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cmw.R;

import java.util.ArrayList;

/**
 * Created by joe on 25/06/15.
 */
public class ClassifyExerciseListAdapter extends BaseAdapter {

    private ArrayList<String> exercises;
    private Context c;

    public ClassifyExerciseListAdapter(ArrayList<String> exercises, Context c) {
        this.exercises = exercises;
        this.c = c;
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Object getItem(int i) {
        return exercises.get(i);
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
            myInflatedView = inflater.inflate(R.layout.list_item_classify_exer_list, null);
        } else {
            myInflatedView = convertView;
        }

        TextView text = (TextView) myInflatedView.findViewById(R.id.text_classify_exercise_list);
        text.setText(exercises.get(i));
        return myInflatedView;
    }

}



