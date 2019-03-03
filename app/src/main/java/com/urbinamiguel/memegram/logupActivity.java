package com.urbinamiguel.memegram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class logupActivity extends AppCompatActivity {
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private TextView txtLogin;
    private Button btnLogUp;
    private ConstraintLayout mainContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logup);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtPasswordVerification);
        txtLogin = findViewById(R.id.txtLogIn);
        btnLogUp = findViewById(R.id.btnLogUp);
        mainContainer = findViewById(R.id.mainContainer);



        txtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                changeButtonClickableStatus(event);
                return false;
            }
        });

        txtEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                changeButtonClickableStatus(event);
                return false;
            }
        });


        //ACCION A EJECUTAR CUANDO SE ESCRIBE EN EN INPUT
        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                changeButtonClickableStatus(event);
                return false;
            }
        });

        txtConfirmPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                changeButtonClickableStatus(event);
                return false;
            }
        });


        btnLogUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(logupActivity.this,R.anim.click_animation));
                logUp();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity();
            }
        });



    }


    private void changeActivity(){
        startActivity(new Intent(logupActivity.this, loginActivity.class));
        finish();
    }



    private void changeButtonClickableStatus(KeyEvent event){
        //ACCION A EJECUTAR CUANDO SE SULETE UNA TECLA
        if(event.getAction() == KeyEvent.ACTION_UP){
            //VERIFICAR LOS CAMPOS DE TEXTO
            if(txtPassword.getText().toString().length()>0 && txtEmail.getText().toString().length() > 0
                && txtConfirmPassword.getText().toString().length()>0 && txtName.getText().toString().length()>0){
                //SI NO ESTAN VACIOS, HABILITAR EL BOTON
                btnLogUp.setEnabled(true);
            }else{
                //SI ESTAN VACIOS DESHABILITAR BOTONES
                btnLogUp.setEnabled(false);
            }
        }
    }

    private void logUp(){

        if(validatePassword()){
            //OBTENER CREDENCIALES
            String name = txtName.getText().toString();
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            registerUser(name, email, password);
        }
    }

    private boolean validatePassword(){
        //VERIFICAR QUE LAS CONTRASENIAS COINCIDAN
        String password = txtPassword.getText().toString();
        String passwordConfirmacion = txtConfirmPassword.getText().toString();

        Log.e("Password.--->", password);
        Log.e("PasswordConfirm-->", passwordConfirmacion);


        if(passwordConfirmacion.equals(password)){
            return true;
        }else{
            txtConfirmPassword.setError(getResources().getString(R.string.distinctPasswords));
            return false;
        }

    }


    private void registerUser(String name, String email, String password){

        final ProgressDialog progressDialog = new ProgressDialog(logupActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.logingUp));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //IP LOCAL PARA DISPOSITIVOS VIRTUALES 10.0.2.2
        //REALIZAR PETICION POR POST
        AndroidNetworking.post("https://calm-headland-52897.herokuapp.com/nuevo_usuario.php")
                .addBodyParameter("nombre", name)
                .addBodyParameter("userEmail", email)
                .addBodyParameter("password", password)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    String responseStr = "null";
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        //RESPUESTA OK, 200

                        try {
                            responseStr = response.getString("response");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(mainContainer, R.string.errorLogin, Snackbar.LENGTH_LONG).show();
                        }

                        if(responseStr.equals("Registrado")){
                            //REGISTRO CORRECTO CAMBIAR A LOGIN Y MOTRAR MENSAJE
                            Toast.makeText(logupActivity.this,R.string.registered, Toast.LENGTH_LONG).show();
                            changeActivity();
                        }else{
                            Snackbar.make(mainContainer, responseStr, Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        //ERROR EN PETICION. MOSTRAR MENSAJE
                        progressDialog.dismiss();

                        Snackbar.make(mainContainer, R.string.errorLogin, Snackbar.LENGTH_LONG).show();
                    }
                });

    }






}
