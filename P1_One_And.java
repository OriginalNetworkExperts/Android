import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText edt1, edt2;
    Button btnAdd, btnSub, btnMul, btnDiv, btnRem;
    RadioGroup rgp;
    RadioButton rbtInt, rbtFloat;
    TextView txtReuslt;
    boolean bType = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt1 = (EditText) findViewById(R.id.edt1);
        edt2 = (EditText) findViewById(R.id.edt2);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnMul = (Button) findViewById(R.id.btnMul);
        btnDiv = (Button) findViewById(R.id.btnDiv);
        btnRem = (Button) findViewById(R.id.btnRem);
        rgp = (RadioGroup) findViewById(R.id.rgp);
        rbtInt = (RadioButton) findViewById(R.id.rbtInt);
        rbtFloat = (RadioButton) findViewById(R.id.rbtFloat);
        txtReuslt = (TextView) findViewById(R.id.txtResult);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bType == true) {
                    int setNum1 = Integer.parseInt(edt1.getText().toString());
                    int setNum2 = Integer.parseInt(edt2.getText().toString());
                    Integer result = setNum1 + setNum2;
                    txtReuslt.setText(result.toString());
                } else {
                    double setNum1 = Double.parseDouble(edt1.getText().toString());
                    double setNum2 = Double.parseDouble(edt2.getText().toString());
                    Double result = setNum1 + setNum2;
                    txtReuslt.setText(result.toString());
                }
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bType == true) {
                    int setNum1 = Integer.parseInt(edt1.getText().toString());
                    int setNum2 = Integer.parseInt(edt2.getText().toString());
                    Integer result = setNum1 - setNum2;
                    txtReuslt.setText(result.toString());
                } else {
                    double setNum1 = Double.parseDouble(edt1.getText().toString());
                    double setNum2 = Double.parseDouble(edt2.getText().toString());
                    Double result = setNum1 - setNum2;
                    txtReuslt.setText(result.toString());
                }
            }
        });

        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bType == true) {
                    int setNum1 = Integer.parseInt(edt1.getText().toString());
                    int setNum2 = Integer.parseInt(edt2.getText().toString());
                    Integer result = setNum1 * setNum2;
                    txtReuslt.setText(result.toString());
                } else {
                    double setNum1 = Double.parseDouble(edt1.getText().toString());
                    double setNum2 = Double.parseDouble(edt2.getText().toString());
                    Double result = setNum1 * setNum2;
                    txtReuslt.setText(result.toString());
                }
            }
        });

        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bType == true) {
                    int setNum1 = Integer.parseInt(edt1.getText().toString());
                    int setNum2 = Integer.parseInt(edt2.getText().toString());
                    Integer result = setNum1 / setNum2;
                    txtReuslt.setText(result.toString());
                } else {
                   double setNum1 = Double.parseDouble(edt1.getText().toString());
                   double setNum2 = Double.parseDouble(edt2.getText().toString());
                   Double result = setNum1 / setNum2;
                   txtReuslt.setText(result.toString());
                }
            }
        });

        btnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bType == true) {
                    int setNum1 = Integer.parseInt(edt1.getText().toString());
                    int setNum2 = Integer.parseInt(edt2.getText().toString());
                    Integer result = setNum1 % setNum2;
                    txtReuslt.setText(result.toString());
                } else {
                    double setNum1 = Double.parseDouble(edt1.getText().toString());
                    double setNum2 = Double.parseDouble(edt2.getText().toString());
                    Double result = setNum1 % setNum2;
                    txtReuslt.setText(result.toString());
                }
            }
        });

        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.rbtInt){
                    Toast.makeText(MainActivity.this, "Int", Toast.LENGTH_SHORT).show();
                    bType = true;
                }else if(checkedId == R.id.rbtFloat){
                    Toast.makeText(MainActivity.this, "Float", Toast.LENGTH_SHORT).show();
                    bType = false;
                }
            }
        });

    }


}