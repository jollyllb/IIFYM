package com.example.kareem.IIFYM_Tracker.ViewComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.kareem.IIFYM_Tracker.Database.SQLiteConnector;
import com.example.kareem.IIFYM_Tracker.Models.Food;
import com.example.kareem.IIFYM_Tracker.Models.Weight;
import com.example.kareem.IIFYM_Tracker.R;

import java.util.ArrayList;

/**
 * Created by Kareem on 9/13/2016.
 */

public class adapterSavedItem extends ArrayAdapter<Food> {

    private SQLiteConnector DB_SQLite;

    private ArrayList<Food> arrOriginalItems;
    private ArrayList<Food> arrFilteredItems;
    private Filter filter;

    public adapterSavedItem(Context context, ArrayList<Food> items) {
        super(context, 0, items);
        arrOriginalItems = new ArrayList(items);
        arrFilteredItems = new ArrayList(items);

        DB_SQLite = new SQLiteConnector(getContext());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data list_item for this position
        Food food = getItem(position);
        long id = food.getId();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.lblName);
        TextView brand = (TextView) convertView.findViewById(R.id.lblBrand);
        TextView calories = (TextView) convertView.findViewById(R.id.lblCalories);
        TextView carbs = (TextView) convertView.findViewById(R.id.lblCarbs);
        TextView protein = (TextView) convertView.findViewById(R.id.lblProtein);
        TextView fat = (TextView) convertView.findViewById(R.id.lblFat);
        TextView portion = (TextView) convertView.findViewById(R.id.lblPortionDetails);

        // Populate the data into the template view using the data object
        name.setText(food.getName());
        brand.setText(food.getBrand());
        calories.setText(String.valueOf(food.getCalories()));
        carbs.setText(String.valueOf(food.getCarbs()));
        protein.setText(String.valueOf(food.getProtein()));
        fat.setText(String.valueOf(food.getFat()));

        float   serving_number;
        Weight  weight;

        if (food.getPortionType() == 0) { // Serving
            serving_number = DB_SQLite.retrieveServing(food);
            if (serving_number == 1.0f) {
                portion.setText(serving_number + " Serving");
            } else {
                portion.setText(serving_number + " Servings");
            }
        } else if (food.getPortionType() == 1) { // Weight
            weight = DB_SQLite.retrieveWeight(food);
            portion.setText(weight.getAmount() + " " + weight.getUnit().Abbreviate());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Override public Filter getFilter() {
        if (filter == null)
            filter = new myFilter();

        return filter;
    }

    private class myFilter extends Filter {

        @Override protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if(prefix == null || prefix.length() == 0) {
                ArrayList<Food> list = new ArrayList(arrOriginalItems);
                results.values = list;
                results.count = list.size();
            }
            else {
                final ArrayList<Food> list = new ArrayList(arrOriginalItems);
                final ArrayList<Food> nlist = new ArrayList();
                int count = list.size();

                for (int i=0; i<count; i++){
                    final Food food = list.get(i);
                    final String value = food.getName().toLowerCase();

                    if(value.startsWith(prefix)){
                        nlist.add(food);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked") @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            arrFilteredItems = (ArrayList<Food>)results.values;

            clear();
            int count = arrFilteredItems.size();
            for (int i=0; i<count; i++)
            {
                Food food = arrFilteredItems.get(i);
                add(food);
            }
        }
    }
}
