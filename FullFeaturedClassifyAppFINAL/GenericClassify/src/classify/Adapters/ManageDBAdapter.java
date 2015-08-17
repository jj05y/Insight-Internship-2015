package classify.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.cmw.R;

import java.util.ArrayList;

import classify.Activities.ConfirmDeleteDialog;
import classify.DatabaseClasses.RepsDatabaseHandler;
import classify.Fragments.ManageDBFragment;
import classify.ListItems.Row;


public class ManageDBAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Row> rows;
    Fragment parentFrag;
    RepsDatabaseHandler db;

    public ManageDBAdapter(Context context, ArrayList<Row> rows, Fragment parentFrag, RepsDatabaseHandler db) {
        this.context = context;
        this.rows = rows;
        this.parentFrag = parentFrag;
        this.db = db;
    }


    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int i) {


        return rows.get(i);
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
            myInflatedView = inflater.inflate(R.layout.row_item_manage_db, null);
        } else {
            myInflatedView = convertView;
        }

        TextView rowText = (TextView) myInflatedView.findViewById(R.id.text_database_row);
        rowText.setText(rows.get(pos).getRowString());

        Button deleteRepButton = (Button) myInflatedView.findViewById(R.id.button_delete_row);
        deleteRepButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //delete the row and file,

                Intent i = new Intent(context, ConfirmDeleteDialog.class);
                i.putExtra("pos", pos);
                parentFrag.startActivityForResult(i, ManageDBFragment.CONFIRM_DELETE);


            }
        });

        Button deletePredictedLabel = (Button) myInflatedView.findViewById(R.id.button_delete_pred_label);
        deletePredictedLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updatePredictedLabel(rows.get(pos).getRowID(), 0);
                Toast.makeText(context, "Predicted Label Removed", Toast.LENGTH_SHORT).show();
            }
        });


        return myInflatedView;
    }

    public void updateRows(ArrayList<Row> rows) {
        this.rows = rows;
        notifyDataSetChanged();
    }


}
