package com.daiwei.exampleandroid;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ListView listView = findViewById(R.id.activity_list);

    listView.setAdapter(new ListAdapter() {
      @Override
      public boolean areAllItemsEnabled() {
        return false;
      }

      @Override
      public boolean isEnabled(int i) {
        return false;
      }

      @Override
      public void registerDataSetObserver(DataSetObserver dataSetObserver) {

      }

      @Override
      public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

      }

      @Override
      public int getCount() {
        return 1;
      }

      @Override
      public Object getItem(int i) {
        return null;
      }

      @Override
      public long getItemId(int i) {
        return i;
      }

      @Override
      public boolean hasStableIds() {
        return false;
      }

      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
        switch (i) {
          case 0:
            View itemView =
                getLayoutInflater().inflate(R.layout.activity_list_item, viewGroup, false);

            ((TextView) itemView.findViewById(R.id.activity_name))
                .setText(ReactiveProgrammingActivity.class.getSimpleName());

            itemView.setOnClickListener(view1 -> startActivity(new Intent(
                getApplicationContext(),
                ReactiveProgrammingActivity.class)));

            return itemView;
          default:
            throw new RuntimeException("Item " + i + " is not implemented.");
        }
      }

      @Override
      public int getItemViewType(int i) {
        return 0;
      }

      @Override
      public int getViewTypeCount() {
        return 1;
      }

      @Override
      public boolean isEmpty() {
        return false;
      }
    });
  }
}
