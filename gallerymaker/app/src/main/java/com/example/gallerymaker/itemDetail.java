package com.example.gallerymaker;
import android.app.AppComponentFactory;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallerymaker.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class itemDetail extends AppCompatActivity {
    private ListView_ImageList profile_image_lIst = new ListView_ImageList();
    public static final int UPDATE_ITEM = 2;
    public static final int EDIT = 1;
    private TextView name;
    private TextView phone_number;
    private ImageView img;
    private int imgIdx;
    private TextView memo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detailview);

        // item의 정보를 가져와 detail 화면으로 보여주기
        Intent intent = getIntent();
        this.name = (TextView)findViewById(R.id.detail_name);
        this.phone_number = (TextView)findViewById(R.id.detail_phoneNumber);
        this.img = (ImageView) findViewById(R.id.detail_img);
        this.memo = (TextView) findViewById(R.id.detail_memo);

        this.imgIdx = intent.getIntExtra("img", 0);
        phone_number.setText( intent.getStringExtra ("phone_number") );
        name.setText( intent.getStringExtra ("name") );
        img.setImageResource( profile_image_lIst.getImg ( imgIdx ) );
        memo.setText( getMemo ( getJson(), intent.getStringExtra ("memo") ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    // edit 버튼 클릭시 화면 전환
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.edit_bar:
                Log.d("edit_bar at itemDetail", "clicked");
                Intent intent = new Intent(itemDetail.this, EditItemActivity.class);

                intent.putExtra("name", this.name.getText().toString());
                intent.putExtra("phone_number", this.phone_number.getText().toString());
                intent.putExtra("img", this.imgIdx);
                intent.putExtra("memo", this.memo.getText().toString());
                intent.putExtra("isBlock", intent.getStringExtra("isBlock"));
                startActivityForResult(intent, UPDATE_ITEM);
                return true;
            default: return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("itemdetail", "callback" + resultCode);
        if(resultCode == EDIT) {
            imgIdx = data.getIntExtra("img", 0);
            img.setImageResource(profile_image_lIst.getImg(imgIdx));
            name.setText(data.getStringExtra("name"));
            phone_number.setText(data.getStringExtra("phone_number"));
            memo.setText(getMemo(getJson(), data.getStringExtra("memo")));
        }
    }

    public String getJson() {
        String json = "";
        try {
            InputStream is = getResources().getAssets().open("phone_Book.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    public String getMemo(String json, String phone_number)
    {
        try{
            JSONArray phoneBook_list = new JSONArray(json);
            for(int i = 0; i < phoneBook_list.length(); i++) {
                JSONObject item = phoneBook_list.getJSONObject(i);
                if ( item.getString("phone_number").equals(phone_number) ) {
                    return item.getString("memo");
                }
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return "empty";
    }
}
