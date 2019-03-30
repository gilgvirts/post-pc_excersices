
        package com.user.ex1;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText textField;
    TextView inputDisplay;
    final String inp_id = "inp";
    final String disp_id = "disp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textField = (EditText) findViewById(R.id.input_insert);
        Button send = (Button) findViewById(R.id.input_send);
        inputDisplay = (TextView) findViewById(R.id.input_display);
        send.setOnClickListener(this);
        if (savedInstanceState!= null){
            if(savedInstanceState.get(inp_id) != null){
                String inp = savedInstanceState.getString(inp_id);
                textField.setText(inp);
            }
            if(savedInstanceState.get(disp_id) != null){
                String inserted = savedInstanceState.getString(disp_id);
                inputDisplay.setText(inserted);
            }
        }

    }
    @Override
    public void onClick(View view){
        inputDisplay.setText(textField.getText());
        textField.setText("");
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(inp_id,  textField.getText().toString());
        outState.putString(disp_id, inputDisplay.getText().toString());
    }

}