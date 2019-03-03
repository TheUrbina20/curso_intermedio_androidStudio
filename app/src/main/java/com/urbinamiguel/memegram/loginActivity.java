package com.urbinamiguel.memegram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//IMPORT ANDROID NETWORKING
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;


public class loginActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private ConstraintLayout viewMain;
    private TextView txtLogUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //OBTENER LOS ELEMENTOS DE LA VISTA
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogIn);
        viewMain = findViewById(R.id.mainContainer);
        txtLogUp = findViewById(R.id.txtLogUp);
        //INICIALIZAR ANDROIDNETWORKING
        AndroidNetworking.initialize(getApplicationContext());

        //ACCION A EJECUTAR BOTON CUANDO SE DA CLICK
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //INICIAR ANIMACION CON EL RECURSO CREADO
                v.startAnimation(AnimationUtils.loadAnimation(loginActivity.this, R.anim.click_animation));
                loginUser();
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
        //ACCION A EJECUTAR CUANDO SE ESCRIBE EN EN INPUT
        txtEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                changeButtonClickableStatus(event);
                return false;
            }
        });

        //ACCION A EJECUATAR CUANDO SE DA CLICK SOBRE LA IME ACTION
        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_GO){
                    loginUser();
                }

                return false;
            }
        });

        txtLogUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToLogUp();
            }
        });

    }

    private void changeToLogUp(){
        startActivity(new Intent(loginActivity.this, logupActivity.class));
        finish();
    }




    private void changeButtonClickableStatus(KeyEvent event){
        //ACCION A EJECUTAR CUANDO SE SULETE UNA TECLA
        if(event.getAction() == KeyEvent.ACTION_UP){
            //VERIFICAR LOS CAMPOS DE TEXTO
            if(txtPassword.getText().toString().length()>0 && txtEmail.getText().toString().length() > 0){
                //SI NO ESTAN VACIOS, HABILITAR EL BOTON
                btnLogin.setEnabled(true);
            }else{
                //SI ESTAN VACIOS DESHABILITAR BOTONES
                btnLogin.setEnabled(false);
            }
        }
    }

    private boolean userCanLogIn(String email, String password){
        //VALIDAR LOS CAMPOS DE TEXTO
        //SI ESTAN VACIOS, AGREGARLES ERROR
        if(email.length() < 1){
            txtEmail.setError(getResources().getString(R.string.emptyInput));
        }
        if(password.length() < 1){
            txtPassword.setError(getResources().getString(R.string.emptyInput));
        }
        //RETORNAR SI AMBOS TIENEN CONTENIDO O NO
        if(email.length()>0 && password.length()>0){
            return true;
        }else{
            return false;
        }

    }


    public void loginUser(){

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        //VERIFICAR SI LOS CAMPOS DE TEXTO TIENEN DATOS
        if(userCanLogIn(email, password)){
            //LLAMAR A FUNCION PARA AUTENTICAR AL USUARIO
            authenticateUser(email, password);
        }
    }




    private void authenticateUser(String email, String password){

        final ProgressDialog progressDialog = new ProgressDialog(loginActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.logingIN));
        progressDialog.setCancelable(false);
        progressDialog.show();

        //IP LOCAL PARA DISPOSITIVOS VIRTUALES 10.0.2.2
        //REALIZAR PETICION POR POST
        AndroidNetworking.post("https://calm-headland-52897.herokuapp.com")
        //AndroidNetworking.post("http://10.0.2.2")
                .addBodyParameter("userEmail", email)
                .addBodyParameter("password", password)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    String userName = "null";
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        //RESPUESTA OK, 200

                        try {
                            userName = response.getString("nombre");
                            Log.e("Response", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(viewMain, R.string.errorLogin, Snackbar.LENGTH_LONG).show();
                        }

                        if(userName == "null" ){
                            Snackbar.make(viewMain, R.string.credentialError, Snackbar.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(loginActivity.this, R.string.loginSuccess,Toast.LENGTH_LONG).show();
                            changeActivity();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        //ERROR EN PETICION. MOSTRAR MENSAJE
                        progressDialog.dismiss();
                        Log.e("Error", error.toString());
                        Snackbar.make(viewMain, R.string.errorLogin, Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    public void changeActivity(){

    }









    //ORDEN DE LOS ELEMENTOS
    //ENTRADA DE doInBackgroud, RETURN DEL doInBackground Y ENTRADA DEL onPostExecute
    private class example extends AsyncTask<String, Boolean, Boolean>{
        //EJECUCION EN PRIMER PLANO (UI)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //MULTIPLE ENTRADA DE PARAMETROS DEL MISMO TIPO
        //PROCESO EN SEGUNDO PLANO
        @Override
        protected Boolean doInBackground(String... args) {

            return true;
        }


        //PROCESO EN PRIMER PLANO (UI)
        @Override
        protected void onPostExecute(Boolean validUser) {
            super.onPostExecute(validUser);
        }
    }

}
