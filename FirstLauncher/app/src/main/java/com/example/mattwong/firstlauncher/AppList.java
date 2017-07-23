package com.example.mattwong.firstlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Wong on 7/22/2017.
 */

public class AppList extends ActionBarActivity{

    private PackageManager manager;
    private List<App> apps;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list_activiy);

        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<App>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i,0);
        for(ResolveInfo ri : availableActivities){
            App app = new App();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }
    }

    private void loadListView(){
        list = (ListView) findViewById(R.id.apps_list);

        ArrayAdapter<App> adapter = new ArrayAdapter<App>(this, R.layout.activity_apps_list_activiy, apps){
            @Override
            public View getView(int postition, View convertView, ViewGroup parent){
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.activity_apps_list_activiy,null);
                }

                ImageView appIcon = (ImageView) convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(apps.get(postition).icon);

                TextView appLabel = (TextView) convertView.findViewById(R.id.app_label);
                appLabel.setText(apps.get(postition).label);

                TextView appName = (TextView) convertView.findViewById(R.id.app_name);
                appName.setText(apps.get(postition).name);

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).name.toString());
                AppList.this.startActivity(i);
            }
        });
    }

}
