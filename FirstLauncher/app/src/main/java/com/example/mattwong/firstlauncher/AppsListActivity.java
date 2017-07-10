package com.example.mattwong.firstlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AppsListActivity extends AppCompatActivity {

    private PackageManager manager;
    private List<App> apps;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list_activiy);
    }

    private void loadApps(){
        manager = getPackageManager();
        apps= new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory((Intent.CATEGORY_LAUNCHER));

        List<ResolveInfo> avaliableActivities = manager.queryIntentActivities(i,0);
        for(ResolveInfo ri : avaliableActivities){
            App app = new App();
            app.label = ri. activityInfo.packageName; //get app package
            app.name=ri.loadLabel(manager);
            app.icon=ri.loadIcon(manager);
            apps.add(app);
        }
    }

    private void loadListview(){
        list = (ListView) findViewById(R.id.List);

        ArrayAdapter<App> adapter = new ArrayAdapter<App>(this, R.layout.app, apps){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                convertView = getLayoutInflater().inflate(R.layout.app, null);
                ImageView appIcon = (ImageView) convertView.findViewById((R.id.icon));
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appName = (TextView) convertView.findViewById((R.id.name));
                appName.setText(apps.get(position).name);

                return covertView;
            }
        };
        list.setAdapter(adapter);
    }

    private void addClickListener(){
        list.setOnClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent i = manager.getLaunchIntentForPackage(apps.get(position).label.toString()))
            }
        }
    }
}
