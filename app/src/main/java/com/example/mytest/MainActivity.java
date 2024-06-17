package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;

import com.example.mytest.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.upload_fragment, new upload()) // 여기서 Upload 클래스를 참조합니다.
                    .commit();
        }
    }

    // Fragment에서의 결과를 전달 받음
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        upload fragment = (upload) getSupportFragmentManager().findFragmentById(R.id.upload_fragment); // 역시 클래스 이름 수정
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
