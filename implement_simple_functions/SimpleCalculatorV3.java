/*
 * 简单的字符串计算器
 * author: LIN ZEYANG
 * timestamp: 2020/09/06 11:30 AM
 * V3
 * 迭代日志：修复V2的一些bug, 支持使用圆括号括号 "(", ")"
 * 特性：
 * 1.支持识别负数。
 * 2.支持使用嵌套圆括号。
 * 3.除零警报。
 * 4.括号不匹配警报。
 * */

/*
Write down your numeric expression. Press Enter to submit, Please.
4/(5+34/43.4-9.0/0.3+4.3)/(22-5.5*44.3+7.6)
4/(5+34/43.4-9.0/0.3+4.3)/(22-5.5*44.3+7.6) = 9.382744E-4
Write down your numeric expression. Press Enter to submit, Please.
7.9*2.1-6.0+3.9/1.2-2*4/0.8+0.03
7.9*2.1-6.0+3.9/1.2-2*4/0.8+0.03 = 3.8700001
Write down your numeric expression. Press Enter to submit, Please.
-3/(-4)/(6.7/(2.3+0.1/0.9)+0.65)+6.4/7
-3/(-4)/(6.7/(2.3+0.1/0.9)+0.65)+6.4/7 = 1.133021
Write down your numeric expression. Press Enter to submit, Please.
-7*(3.2/3)
-7*(3.2/3) = -7.466667
Write down your numeric expression. Press Enter to submit, Please.
-7*(-3.2/-1)
-7*(-3.2/-1) = -22.4
Write down your numeric expression. Press Enter to submit, Please.
-3/(-4)/(6.7/(2.3+0.1/0.9)*0.65)*6.4/7
-3/(-4)/(6.7/(2.3+0.1/0.9)*0.65)*6.4/7 = 0.3796403
Write down your numeric expression. Press Enter to submit, Please.
-3/(2.2)
-3/(2.2) = -1.3636364
Write down your numeric expression. Press Enter to submit, Please.
-7+(4.2/21)+2)
Illegal Input
Write down your numeric expression. Press Enter to submit, Please.
-3/(-4)/(6.7/(-1.0+0.9/0.9)*0.65)*6.4/7
Divided by zero!
* */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class SimpleCalculatorV3 {
    static ArrayList<String> commonOprs = new ArrayList<String>();

    static{
        commonOprs.add("*");
        commonOprs.add("/");
        commonOprs.add("+");
        commonOprs.add("-");
    }

    public static void main(String[] args) throws IOException {
        while (true){
            repeatedTestFunction();
        }
    }

    public static void repeatedTestFunction() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Write down your numeric expression. Press Enter to submit, Please.");
        String inputExpression;
        inputExpression = br.readLine();
        inputExpression = inputExpression.replace(" ", "");
        inputExpression = inputExpression.replace("+-", "-");
        inputExpression = inputExpression.replace("--", "+");
        String oriExpression = inputExpression;
        int iterCount = checkValidation(inputExpression);
        if ( iterCount == -1)
            System.out.println("Illegal Input");
        else{
            for (int i = 0; i < iterCount; i++){
                inputExpression = eliminateBracket(inputExpression);
            }
            System.out.println(oriExpression + " = " + sequentialCalculateOneExpressionWithoutBracket(inputExpression));
        }
    }

    public static String eliminateBracket(String str){
        int leftBracketIndex = 0;
        int rightBracketIndex = 0;
        for (int i = 0; i < str.length(); i++){
            if (str.charAt(i) == ')'){
                rightBracketIndex = i;
                break;
            }
            if (str.charAt(i) == '('){
                leftBracketIndex = i;
            }
        }
        String LeftExpression = str.substring(0, leftBracketIndex);
        String RightExpression = str.substring(rightBracketIndex+1);
        String ProcessingExpression = sequentialCalculateOneExpressionWithoutBracket(str.substring(leftBracketIndex+1, rightBracketIndex));
        String resultExpression = LeftExpression + ProcessingExpression + RightExpression;
//        System.out.println(resultExpression);
        return resultExpression;
    }

    public static String sequentialCalculateOneExpressionWithoutBracket(String str){
        Stack<Float> nums = new Stack<Float>();
        Stack<String> oprs = new Stack<String>();
        String inProcessingStr = new String(str.trim());
        int tmpIndex = findFirstOprIndex(inProcessingStr);
        float operandPrev;
        float operandNext;
        float tmpResult;
        String tmpOpr;
        do {
            if (tmpIndex != -1){
//                System.out.print(tmpIndex + " --- " + inProcessingStr + " ----- ");
//                System.out.println(inProcessingStr.substring(0, tmpIndex));

                operandPrev = Float.valueOf(inProcessingStr.substring(0, tmpIndex));
                nums.push(operandPrev);
                oprs.push(inProcessingStr.substring(tmpIndex, tmpIndex + 1));
                inProcessingStr = inProcessingStr.substring(tmpIndex + 1);

                if (oprs.peek().equals("*") || oprs.peek().equals("/")){
//                    System.out.println("enter */");
                    tmpIndex = findFirstOprIndex(inProcessingStr);
                    if (tmpIndex == -1) {
                        operandNext = Float.valueOf(inProcessingStr.trim());
                        inProcessingStr = "";
                    }
                    else {
                        operandNext = Float.valueOf(inProcessingStr.substring(0, tmpIndex));
                        inProcessingStr = inProcessingStr.substring(tmpIndex);
                    }
                    if (oprs.peek().equals("*")){
                        tmpResult = operandPrev * operandNext;
                        inProcessingStr = String.valueOf(tmpResult) + inProcessingStr;
                        nums.pop();
                        oprs.pop();
                    }
                    else{

                        if (Math.abs(operandNext) < 0.00001f){
                            System.out.println("Divided by zero!");
                            System.exit(1);
                        }
                        tmpResult = operandPrev / operandNext;
                        inProcessingStr = String.valueOf(tmpResult) + inProcessingStr;
                        nums.pop();
                        oprs.pop();
                    }
                }

            }
//            System.out.println(inProcessingStr);

            tmpIndex = findFirstOprIndex(inProcessingStr);
        } while(tmpIndex != -1);
//        System.out.println(inProcessingStr);
        nums.push(Float.valueOf(inProcessingStr));

        reverseStack(nums);
        reverseStack(oprs);

//        while(nums.size() > 0) System.out.println(nums.pop());
//        while(oprs.size() > 0) System.out.println(oprs.pop());
//        for (float num : nums){
//            System.out.print(num + " ");
//        }
//        for (String opr : oprs){
//            System.out.print(opr + " ");
//        }
        while (nums.size() > 1) {
            operandPrev = nums.pop();
            operandNext = nums.pop();
            tmpOpr = oprs.pop();
            if (tmpOpr.equals("+"))
                tmpResult = operandPrev + operandNext;
            else
                tmpResult = operandPrev - operandNext;
            nums.push(tmpResult);
//            System.out.println(tmpResult + " = " + operandPrev + tmpOpr + operandNext);

        }
//        while(nums.size() > 0) System.out.println(nums.pop());
//        System.exit(2);
        String result = nums.pop().toString();
        return result;
    }

    public static int findFirstOprIndex(String str){
        int ptr = 0;
        int tmpIndex = -1;
        for (int i = 0; i < str.length(); i++){
            if (str.charAt(i) == '-' || str.charAt(i) == '+')
                if (i == 0)
                    continue;
                else if (str.charAt(i-1) == 'E' || str.charAt(i-1) == 'e')
                    continue;
                else
                    return i;
            if (str.charAt(i) == '*' || str.charAt(i) == '/')
                return i;
        }
        return tmpIndex;
    }

    public static int checkValidation(String str){
        Stack<String> bracketStack = new Stack<String>();
        int count = 0;
        for (int i = 0; i < str.length(); i++){
            if (str.charAt(i) == '('){
                bracketStack.push("(");
                count++;
            }
            if (str.charAt(i) == ')'){
                if (bracketStack.size() > 0)
                    bracketStack.pop();
                else
                    return -1;
            }
        }
        if (bracketStack.empty())
            return count;
        else
            return -1;
//        for (String opr : commonOprs){
//            if (str.startsWith(opr) || str.endsWith(opr)) return false;
//        }
//        return true;
    }

    public static void reverseStack(Stack s){
        Queue tmpQueue = new LinkedList();
        while(s.size() > 0) tmpQueue.offer(s.pop());
        while(tmpQueue.size() > 0) s.push(tmpQueue.poll());
    }
}
