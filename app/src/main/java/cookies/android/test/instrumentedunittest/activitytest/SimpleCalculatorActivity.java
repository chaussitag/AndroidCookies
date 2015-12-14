package cookies.android.test.instrumentedunittest.activitytest;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cookies.android.R;

/**
 * Created by daiguozhou on 2015/12/4.
 */
public class SimpleCalculatorActivity extends Activity {
    private static final String TAG = "CalculatorActivity";

    private SimpleCalculator mCalculator;

    private EditText mOperandOneEditText;
    private EditText mOperandTwoEditText;

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_caculator);
        mCalculator = new SimpleCalculator();
        mResultTextView = (TextView) findViewById(R.id.operation_result_text_view);
        mOperandOneEditText = (EditText) findViewById(R.id.operand_one_edit_text);
        mOperandTwoEditText = (EditText) findViewById(R.id.operand_two_edit_text);
    }

    /**
     * OnClick method that is called when the add {@link Button} is pressed.
     */
    public void onAdd(View view) {
        compute(SimpleCalculator.Operator.ADD);
    }

    /**
     * OnClick method that is called when the substract {@link Button} is pressed.
     */
    public void onSub(View view) {
        compute(SimpleCalculator.Operator.SUB);
    }

    /**
     * OnClick method that is called when the divide {@link Button} is pressed.
     */
    public void onDiv(View view) {
        try {
            compute(SimpleCalculator.Operator.DIV);
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "IllegalArgumentException", iae);
            mResultTextView.setText(getString(R.string.computationError));
        }
    }

    /**
     * OnClick method that is called when the multiply {@link Button} is pressed.
     */
    public void onMul(View view) {
        compute(SimpleCalculator.Operator.MUL);
    }

    private void compute(SimpleCalculator.Operator operator) {
        double operandOne;
        double operandTwo;
        try {
            operandOne = getOperand(mOperandOneEditText);
            operandTwo = getOperand(mOperandTwoEditText);
        } catch (NumberFormatException nfe) {
            Log.e(TAG, "NumberFormatException", nfe);
            mResultTextView.setText(getString(R.string.computationError));
            return;
        }

        String result;
        switch (operator) {
            case ADD:
                result = String.valueOf(mCalculator.add(operandOne, operandTwo));
                break;
            case SUB:
                result = String.valueOf(mCalculator.sub(operandOne, operandTwo));
                break;
            case DIV:
                result = String.valueOf(mCalculator.div(operandOne, operandTwo));
                break;
            case MUL:
                result = String.valueOf(mCalculator.mul(operandOne, operandTwo));
                break;
            default:
                result = getString(R.string.computationError);
                break;
        }
        mResultTextView.setText(result);
    }

    /**
     * @return the operand value which was entered in an {@link EditText} as a double
     */
    private static Double getOperand(EditText operandEditText) {
        String operandText = getOperandText(operandEditText);
        return Double.valueOf(operandText);
    }

    /**
     * @return the operand text which was entered in an {@link EditText}.
     */
    private static String getOperandText(EditText operandEditText) {
        String operandText = operandEditText.getText().toString();
        if (TextUtils.isEmpty(operandText)) {
            throw new NumberFormatException("operand cannot be empty!");
        }
        return operandText;
    }
}
