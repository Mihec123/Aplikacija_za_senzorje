package com.example.aplikacijasenzorji;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by jbonk on 6/16/2017.
 */

public class SimpleTreeViewAdapter extends TreeViewAdapter{

    public SimpleTreeViewAdapter(Context context, TreeNode root) {
        super(context, root);
    }

    @Override
    public View createTreeView(Context context, final TreeNode node, Object data, int level, boolean hasChildren) {
        Log.d("123","createTreeView");
        View view = View.inflate(context,R.layout.tree_view_item,null);
        Log.d("123","createTreeView prezvel");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        view.setLayoutParams(layoutParams);
        Log.d("123","createTreeView prezvel1");
        TextView textView = (TextView)view.findViewById(R.id.textView);
        Log.d("123","createTreeView prezvel2");
        textView.setText(((String)data));

        Log.d("123","createTreeView prezvel");
        return view;
    }

}
