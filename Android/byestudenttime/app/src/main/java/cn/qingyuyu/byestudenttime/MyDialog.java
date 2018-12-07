package cn.qingyuyu.byestudenttime;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

/**
 * Created by Administrator on 2017\8\3 0003.
 */

public class MyDialog extends AlertDialog implements View.OnClickListener {
   private  Context cont;
    private AwesomeTextView messageTxt, titleTxt;
    private BootstrapButton okButton;
    private ListView lv;

    public MyDialog(Context cont) {
        super(cont);
        this.cont = cont;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialoglayout);
        setCanceledOnTouchOutside(false);
        messageTxt = findViewById(R.id.message);
        titleTxt = findViewById(R.id.title);
        okButton = findViewById(R.id.okbutton);
        lv=findViewById(R.id.devicelist);
        okButton.setOnClickListener(this);
    }

    public void setTitleTxt(String title) {

        titleTxt.setText(title);
    }

    public void setMessageTxt(String message) {
        messageTxt.setText(message);
    }

    public void setOkButtonTxt(String txt) {
        okButton.setText(txt);
    }

    public void setListViewListener(AdapterView.OnItemClickListener oi)
    {
        lv.setOnItemClickListener(oi);
    }
    public void setListAdapter(ArrayAdapter a)
    {
        lv.setAdapter(a);
    }
    public void showListView()
    {
        lv.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(View view) {
        this.dismiss();
    }
}
