package com.bitc.eatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitc.eatnow.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        // Firebase 초기화
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(view -> {

            final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("잠시 기다려주세요");
            mDialog.show();

            // 데이터 읽기
            table_user.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Check if already user phone exist in db
                    if(snapshot.child(edtPhone.getText().toString()).exists()) {

                        // 이미 존재 -> 해당 edtPhone값의 정보를 User 객체에 저장 후 edtPassword값과 비교
                        mDialog.dismiss();
                        User user = snapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());   // set Phone
                        assert user != null;
                        if(user.getPassword().equals(edtPassword.getText().toString())) {
                            {
                                // user의 정보를 Common 클래스의 currentUse객체에 저장한 후 액티비티 이동
                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                Common.currentUser = user;
                                startActivity(homeIntent);
                                finish();
                            }
//                            Toast.makeText(SignIn.this,"로그인되었습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignIn.this,"비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // edtPhone값이 없다
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this,"존재하지 않는 계정입니다", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    // 실패시
                    Toast.makeText(SignIn.this, "error occur: "+error.toException(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}