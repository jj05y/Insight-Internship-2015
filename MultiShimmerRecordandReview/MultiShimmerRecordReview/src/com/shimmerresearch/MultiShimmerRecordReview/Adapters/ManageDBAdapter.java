package com.shimmerresearch.MultiShimmerRecordReview.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.shimmerresearch.MultiShimmerRecordReview.Activities.ConfirmDeleteDialog;
import com.shimmerresearch.MultiShimmerRecordReview.Activities.MainActivity;
import com.shimmerresearch.MultiShimmerRecordReview.ListItems.Row;
import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;


public class ManageDBAdapter extends BaseAdapter {

    public static final int CONFIRM_DELETE = 7;
    private Context context;
    private ArrayList<Row> rows;
    Fragment parentFrag;

    public ManageDBAdapter(Context context, ArrayList<Row> rows, Fragment parentFrag) {
        this.context = context;
        this.rows = rows;
        this.parentFrag = parentFrag;
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

                Intent i = new Intent(context, ConfirmDeleteDialog.class);
                i.putExtra("pos", pos);
                parentFrag.startActivityForResult(i, CONFIRM_DELETE);



            }
        });


        return myInflatedView;
    }

    public void updateRows(ArrayList<Row> rows) {
        this.rows = rows;
        notifyDataSetChanged();
    }


}
