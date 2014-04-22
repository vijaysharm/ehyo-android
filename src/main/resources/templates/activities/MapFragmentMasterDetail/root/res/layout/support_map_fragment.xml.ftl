<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical" >    
   
    <TabHost
        android:id="@android:id/tabhost"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent">        
        <LinearLayout
            android:orientation="vertical"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent" >
            
            <TabWidget
                android:id="@android:id/tabs"
                android:visibility="gone"
                android:layout_width="fill_parent"
                android:layout_height="wrap_content" />
            
            <FrameLayout
                android:id="@android:id/tabcontent"
                android:layout_width="fill_parent"
                android:layout_height="fill_parent" />
            
        </LinearLayout>
    </TabHost>
    
</LinearLayout>
