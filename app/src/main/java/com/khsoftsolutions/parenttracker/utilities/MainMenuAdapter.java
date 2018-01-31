package com.khsoftsolutions.parenttracker.utilities;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khsoftsolutions.parenttracker.R;
import com.khsoftsolutions.parenttracker.objects.MenuItem;

import java.util.List;

/**
 * Created by myxroft on 14/09/2017.
 */

public class MainMenuAdapter extends BaseAdapter {

    private List<MenuItem> menuItems;
    private Activity activity;

    public MainMenuAdapter(List<MenuItem> items, Activity act){
        this.menuItems = items;
        this.activity = act;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public MenuItem getItem(int i) {
        return menuItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MenuItem currItem = menuItems.get(i);

        TextView title;
        ImageView imgItem;

       if(view == null){

           view = activity.getLayoutInflater().inflate(R.layout.list_item_main,null);

           title = (TextView)view.findViewById(R.id.tvMenuItem);
           imgItem = (ImageView)view.findViewById(R.id.imgMenuItem);

           title.setText(currItem.MENU_ITEM_NAME);
           imgItem.setImageResource(currItem.MENU_ITEM_FILE);
       }
       else{
           title = (TextView)view.findViewById(R.id.tvMenuItem);
           imgItem = (ImageView)view.findViewById(R.id.imgMenuItem);

           title.setText(currItem.MENU_ITEM_NAME);
           imgItem.setImageResource(currItem.MENU_ITEM_FILE);
       }

       return view;
    }
}
