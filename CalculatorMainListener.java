import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class CalculatorMainListener extends CalculatorBaseListener {
    Deque<Integer> numbers = new ArrayDeque<>();
    Deque<Integer> supportDequeue = new ArrayDeque<>();

    private Integer getResult() {
        return numbers.peek();
    }

    @Override
    public void enterExpression(CalculatorParser.ExpressionContext ctx) {
        super.enterExpression(ctx);
    }

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        Integer value = numbers.pop();
        for(int i = 1; i < ctx.getChildCount(); i += 2) {
            if(Objects.equals(ctx.getChild(i).getText(), "+")){
                value = value + numbers.pop();
            }
            else{
                value = value - numbers.pop();
            }
        }
        numbers.add(value);
        super.exitExpression(ctx);
    }

    @Override
    public void enterIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        super.enterIntegralExpression(ctx);
    }

    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        if(ctx.MINUS() != null){
            numbers.add(-1 * Integer.valueOf(ctx.INT().toString()));
        }
        else{
            numbers.add(Integer.valueOf(ctx.INT().toString()));
        }
        super.exitIntegralExpression(ctx);
    }

    @Override
    public void enterMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        super.enterMultiplyingExpression(ctx);
    }

//    @Override
//    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) { multiplying on one dequeue
//        Integer value = numbers.removeLast();
//        Integer prev = value;
//        Integer curr;
//        for(int i = ctx.getChildCount()-2; i>0; i-=2) {
//            curr = numbers.removeLast();
//            if(Objects.equals(ctx.getChild(i).getText(), "*")){
//                value *= curr;
//            }
//            else {
//                value = value * curr / (prev * prev);
//            }
//            prev = curr;
//
//        }
//        numbers.add(value);
//        super.exitMultiplyingExpression(ctx);
//    }

    public void exitMultiplyingExpression(CalculatorParser.MultiplyingExpressionContext ctx) {
        for(int i = ctx.getChildCount()-1; i>=0; i-=2) {
            supportDequeue.add(numbers.removeLast());
        }
        Integer value = supportDequeue.removeLast();
        for(int i = 1; i < ctx.getChildCount(); i += 2) {
            if(Objects.equals(ctx.getChild(i).getText(), "*")){
                value *= supportDequeue.removeLast();
            }
            else{
                value /= supportDequeue.removeLast();
            }
        }
        numbers.add(value);
        super.exitMultiplyingExpression(ctx);
    }

    @Override
    public void enterPowExpression(CalculatorParser.PowExpressionContext ctx) {
        super.enterPowExpression(ctx);
    }

    @Override
    public void exitPowExpression(CalculatorParser.PowExpressionContext ctx) {
        Integer value  = 1;
        Integer prev = numbers.removeLast();
        Integer curr;
        for(int i = ctx.getChildCount()-2; i>0; i-=2) {
            curr = numbers.removeLast();
            if(Objects.equals(ctx.getChild(i).getText(), "^")){
                prev = ((int)Math.pow((double)curr, (double)prev));

            }
            else {
                value *= (int)Math.sqrt(prev);
                prev = curr;
            }
        }
        value *= prev;
        numbers.add(value);
        super.exitPowExpression(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    public static void main(String[] args) throws Exception {
        //CharStream charStreams = CharStreams.fromFileName("./example.txt");
        String expression = "-2*4+3^3sqrt16/2+10";
        Integer result = calc(expression);

        System.out.println("Expression:  " + expression);
        System.out.println("Result = " + result);
    }
    public static Integer calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static Integer calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorMainListener mainListener = new CalculatorMainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
    }


}