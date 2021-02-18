package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private EditText display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.editText);
        display.setShowSoftInputOnFocus(false); //disable keyboard

        //to clear the placeholder string if it equals "enter value"
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getString(R.string.display).equals(display.getText().toString())){
                    display.setText("");
                }
            }
        });
    }

    private void updateDisplay(String strToAdd){
        String oldStr = display.getText().toString();

        //grab the cursor position - add the strToAdd at that position
        int cursorPos = display.getSelectionStart();
        //split the oldStr in two two parts
        String leftStr = oldStr.substring(0,cursorPos);
        String rightStr = oldStr.substring(cursorPos);

        if(getString(R.string.display).equals(display.getText().toString())){
            display.setText(strToAdd);
        }else {
            display.setText(leftStr+strToAdd+rightStr);
        }
        //cursor position increased by 1 after new character added
        display.setSelection(cursorPos+1);
    }

    public void doublezeroBTN(View view){
        updateDisplay("00");
        int cursorPos = display.getSelectionStart();
        display.setSelection(cursorPos+1);
    }
    public void zeroBTN(View view){
        updateDisplay("0");
    }
    public void oneBTN(View view){
        updateDisplay("1");
    }
    public void twoBTN(View view){
        updateDisplay("2");
    }
    public void threeBTN(View view){
        updateDisplay("3");
    }
    public void fourBTN(View view){
        updateDisplay("4");
    }
    public void fiveBTN(View view){
        updateDisplay("5");
    }
    public void sixBTN(View view){
        updateDisplay("6");
    }
    public void sevenBTN(View view){
        updateDisplay("7");
    }
    public void eightBTN(View view){
        updateDisplay("8");
    }
    public void nineBTN(View view){
        updateDisplay("9");
    }

    public void clearBTN(View view){
       display.setText("");
    }

    public void divideBTN(View view){
        updateDisplay("/");
    }

    public void multiplyBTN(View view){
        updateDisplay("*");
    }

    public void addBTN(View view){
        updateDisplay("+");
    }

    public void subtractBTN(View view){
        updateDisplay("-");
    }

    public void pointBTN(View view){
        updateDisplay(".");
    }

    public void backspaceBTN(View view){
        int cursorPos=display.getSelectionStart();
        int displayLen=display.getText().length();

        if(cursorPos!=0 && displayLen!=0){
            //replace characters within selected string
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(cursorPos-1,cursorPos,"");
            display.setText(selection);
            display.setSelection(cursorPos-1);
        }
    }

//    public void bracketBTN(View view){
//        int cursorPos=display.getSelectionStart();
//        int openBr = 0;
//        int closeBr= 0;
//        int len = display.getText().length();
//
//        //(6+10)*7
//        for(int i=0; i < cursorPos; i++){
//            if(display.getText().toString().substring(i,i+1).equals("(")){
//                openBr++;
//            }
//            if(display.getText().toString().substring(i,i+1).equals(")")){
//                closeBr++;
//            }
//        }
//
//        if(openBr == closeBr || !display.getText().toString().substring(len-1,len).equals(")") ){
//            updateDisplay("(");
//        }
//        else if(closeBr < openBr || !display.getText().toString().substring(len-1,len).equals("(")){
//            updateDisplay(")");
//        }
//
//    }

    public void closeBracketBTN(View view){
        int cursorPos=display.getSelectionStart();
        int openBr = 0;
        int closeBr= 0;
        int len = display.getText().length();

        //(6+10)*7
        for(int i=0; i < cursorPos; i++){
            if(display.getText().toString().substring(i,i+1).equals("(")){
                openBr++;
            }
            if(display.getText().toString().substring(i,i+1).equals(")")){
                closeBr++;
            }
        }

        if(openBr==closeBr){
            return;
        }
        else if(openBr>closeBr||!display.getText().toString().substring(len-1,len).equals("(")){
            updateDisplay(")");
        }
    }

    public void openBracketBTN(View view){
        int len = display.getText().length();
        if(len==0){ //out of bound case
            updateDisplay("(");
        }
        else if(display.getText().toString().substring(len-1,len).equals("(") || display.getText().toString().substring(len-1,len).equals("+") ||
                display.getText().toString().substring(len-1,len).equals("-") || display.getText().toString().substring(len-1,len).equals("*")
                || display.getText().toString().substring(len-1,len).equals("/")){
            updateDisplay("(");
            
        }else if(display.getText().toString().substring(len-1,len).equals(".")){
            return;
        }else{
            updateDisplay("*(");
            int cursorPos = display.getSelectionStart();
            display.setSelection(cursorPos+1);
        }
    }

    public void equalsBTN(View view){
            String expression=display.getText().toString();

            char[] expToken = expression.toCharArray();

            //stack for operands
            Stack<Double> numberStack = new Stack<>();

            //stack for characters
            Stack<Character> operatorStack = new Stack<>();

            for(int i=0;i<expToken.length; i++){
                char ch = expToken[i];

                if(Character.isDigit(ch) || ch=='.'){
                    String buff="";
                    while((i < expToken.length) && (Character.isDigit(expToken[i]) || expToken[i] == '.')) {
                        buff += expToken[i++];
                    }
                    i--;
                    numberStack.push(Double.parseDouble(buff));
                }
                else if(ch == '(') {
                    operatorStack.push(ch);
                }
                else if(ch == ')') {
                    while(operatorStack.peek() != '(') {
                        double output = performOperation(numberStack.pop(), numberStack.pop(), operatorStack.pop());
                        numberStack.push(output);
                    }
                    operatorStack.pop();
                }
                else if(isOperator(ch)) {
                    while(!operatorStack.isEmpty() && (precedence(ch) < precedence(operatorStack.peek()))) {
                        double output = performOperation(numberStack.pop(), numberStack.pop(), operatorStack.pop());
                        numberStack.push(output);
                    }
                    operatorStack.push(ch);
                }
            }
            while(!operatorStack.isEmpty()) {
                double output = performOperation(numberStack.pop(), numberStack.pop(), operatorStack.pop());
                numberStack.push(output);
            }

            double result=numberStack.peek();
            display.setText(Double.toString(result));

            int cursorPos=display.getSelectionStart();
            display.setSelection(cursorPos+display.getText().length());
    }


    //helping functions
    public int precedence(char op) {
        if(op == '*' || op == '/') {
            return 2;
        }else if(op == '+' || op == '-') {
            return 1;
        }
        return -1;
    }

    public boolean isOperator(char op) {
        return (op == '+' || op == '-' || op == '*' || op == '/');
    }

    public double performOperation(Double a, Double b, Character op) {
        switch(op) {
            case '+' : return a+b;
            case '-' : return b-a;
            case '*' : return a*b;
            case '/' :
                if(a == 0) {
                    throw new UnsupportedOperationException("Cannot divide by 0");
                }else {
                    return b/a;
                }
        }
        return 0;
    }


}
