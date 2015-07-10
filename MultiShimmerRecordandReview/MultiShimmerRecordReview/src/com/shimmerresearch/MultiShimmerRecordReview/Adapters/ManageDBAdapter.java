package com.shimmerresearch.MultiShimmerRecordReview.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shimmerresearch.MultiShimmerRecordReview.ListItems.Row;
import com.shimmerresearch.MultiShimmerRecordReview.DatabaseClasses.DatabaseHandler;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.io.File;
import java.util.ArrayList;


public class ManageDBAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Row> rows;
    private DatabaseHandler db;

    public ManageDBAdapter(Context context, ArrayList<Row> rows, DatabaseHandler db) {
        this.rows = rows;
        this.context = context;
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
            myInflatedView = inflater.inflate(R.layout.row_item, null);
        } else {
            myInflatedView = convertView;
        }

        TextView rowText = (TextView) myInflatedView.findViewById(R.id.text_database_row);
        rowText.setText(rows.get(pos).getRowString());

        Button deleteButton = (Button) myInflatedView.findViewById(R.id.button_delete_row);
        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //delete the row and file,
                db.deleteDetail(rows.get(pos).getRowID());
                File file = new File(rows.get(pos).getFileName());
                file.delete();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                //updata the list
                rows = db.getRowStrings();
                notifyDataSetChanged();
            }
        });


        return myInflatedView;
    }
}
