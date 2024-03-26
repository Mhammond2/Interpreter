//Names: Matt Hammond, Tony Seo, Robert Coulter
//Course Number: CS 4308
//Professor: Jose Garrido
//Section: W01
//import java.io.BufferedReader;

//import java.io.FileReader;

import java.io.*;
import java.util.*;
import java.io.FileWriter;
public class ProjectScanner
{
    //Number Codes
//constant type: 1000 - 1002 int, float, string
int INT = 1000;
int FLOAT = 1001;
int STRING = 1002;
//identifier: 2000
int IDENTIFIER = 2000;
//keyword: 3000 - 3012
    String[] keywords = {"true", "false", "=", "<=", "<", ">=", ">", "==", "~=", "+", "-", "*", "/"};

    public LinkedList<Token> getTokenList() {
        return tokenList;
    }
    public void setTokenList(LinkedList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    //token list
    LinkedList<Token> tokenList;
    LinkedList<Token> temp;
    Map<String, Token> varMap = new HashMap<String, Token>(); //program's variables
    private int lineNumber;

    public void Parse(LinkedList<Token> tokenList) {
        /*boolean error = false;
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\User\\Documents\\CS_4308_CPL\\Project\\Outputs.txt"));
            System.setOut(out);*/
            
        ///////////////////////////////////////////////////////////////////parser (STACK MACHINE)
        Stack<Token> exprStack = new Stack(); //expression stack
        Stack<Token> opStack = new Stack(); //operator stack
        Boolean isAssignment = false;
        Boolean isBoolean = false;
        Boolean notBool = false;
        for(int i = 0; i < tokenList.size(); i++)
        {
            Token current = tokenList.get(i);
            int type = current.getCode();
            //check identifiers for value
            if(type == IDENTIFIER)
            {
               if(varMap.containsKey(current.getSymbol())) //if id has been used
               {
                  if(opStack.empty() && isAssignment)
                  {
                     exprStack.push(current);
                  }
                  else
                  {
                     exprStack.push(varMap.get(current.getSymbol()));
                  }
               }
               else
               {
                  try
                  {
                     if(tokenList.get(i+1).getCode() == 3002)
                     {
                        varMap.put(current.getSymbol(), current);
                        exprStack.push(current);
                        isAssignment = true;
                     }
                  }
                  catch(NullPointerException e)
                  {
                     System.out.println("variable not assigned");
                  }
               }
            }
            else if(isOperator(current))
            {
               if(current.getCode() == 3002 && opStack.empty()) //assignment symbol in right place
               {
                  try
                  {
                     if(tokenList.get(i-1).getCode() != IDENTIFIER)
                     {
                        System.out.println("invalid assignment statement"); //has an identifier
                        System.exit(0);
                     }
                  }
                  catch(NullPointerException e)
                  {
                     System.out.println("invalid assignment statement");
                  }
                  opStack.push(current);
               }
               try
               {
                  Token operand1 = tokenList.get(i-1);
                  Token operand2 = tokenList.get(i+1);
                  if(isOperand(operand1) && isOperand(operand2))
                  {
                     if(isBooleanOp(current))
                     {
                        if(isBoolean(operand1) && isBoolean(operand2)) //Boolean Comparison
                        {
                           opStack.push(current);
                           isBoolean = true;
                        }
                        else
                        {
                           System.out.println("Non boolean operand");
                           System.exit(0);
                        }
                     }
                     else if((operand1.getCode() == 1002)^(operand2.getCode() == 1002))
                     {
                        System.out.println("incompatable types");
                     }
                     else
                     {
                        notBool = true;
                        opStack.push(current);
                     }
                  }
               }
               catch(NullPointerException e) //error message if two operands are not found with operator
               {
                  System.out.println("Missing Operand");
               }
            }
            else if(isOperand(current) || (type == 3000) || (type == 3001))
            {
               exprStack.push(current);
            }
            if(notBool && isBoolean) //error message for boolean and arithmetic expression
            {
               System.out.println("incompatible types");
            }
        }
        
        /////////////Evaluate
        while(!opStack.empty())
        {
            Token expr2 = exprStack.pop(); //picks up the first operand
            Token expr1 = exprStack.pop(); //picks up the second operand
            Token op = opStack.pop(); //pcks up the next operator
            
            if(op.getSymbol().equals("+")) //searches for operator oporation
            {//operation start
               if(expr1.getCode() == FLOAT || expr2.getCode() == FLOAT) //prevents loss of decimal data
               {
                  float result = Float.valueOf(expr1.getSymbol()) + Float.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
               else
               {
                  int result = Integer.valueOf(expr1.getSymbol()) + Integer.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
            }//operation end
            else if(op.getSymbol().equals("-")) //same structure for all other operators
            {
               if(expr1.getCode() == FLOAT || expr2.getCode() == FLOAT)
               {
                  float result = Float.valueOf(expr1.getSymbol()) - Float.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
               else
               {
                  int result = Integer.valueOf(expr1.getSymbol()) - Integer.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
            }
            else if(op.getSymbol().equals("*"))
            {
               if(expr1.getCode() == FLOAT || expr2.getCode() == FLOAT)
               {
                  float result = Float.valueOf(expr1.getSymbol()) * Float.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
               else
               {
                  int result = Integer.valueOf(expr1.getSymbol()) * Integer.valueOf(expr2.getSymbol());
                  exprStack.push(new Token(numCode("" + result), "" + result));
               }
            }
            else if(op.getSymbol().equals("/"))
            {
               float result = Float.valueOf(expr1.getSymbol()) / Float.valueOf(expr2.getSymbol());
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals("<="))
            {
               boolean result;
               
               float a = Float.valueOf(expr1.getSymbol());
               float b = Float.valueOf(expr2.getSymbol());
               result = (a <= b);
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals("<"))
            {
               boolean result;
               
               float a = Float.valueOf(expr1.getSymbol());
               float b = Float.valueOf(expr2.getSymbol());
               result = (a < b);
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals(">="))
            {
               boolean result;
                
               float a = Float.valueOf(expr1.getSymbol());
               float b = Float.valueOf(expr2.getSymbol());
               result = (a >= b);
               
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals(">"))
            {
               boolean result;
               
               float a = Float.valueOf(expr1.getSymbol());
               float b = Float.valueOf(expr2.getSymbol());
               result = (a > b);
               
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals("=="))
            {
               boolean result;
               if(isBoolean(expr1))
               {
                  boolean a = Boolean.valueOf(expr1.getSymbol());
                  boolean b = Boolean.valueOf(expr2.getSymbol());
                  result = (a == b); 
               }
               else 
               {
                  float a = Float.valueOf(expr1.getSymbol());
                  float b = Float.valueOf(expr2.getSymbol());
                  result = (a == b);
               }
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals("~="))
            {
               boolean result;
               if(isBoolean(expr1))
               {
                  boolean a = Boolean.valueOf(expr1.getSymbol());
                  boolean b = Boolean.valueOf(expr2.getSymbol());
                  result = (a != b); 
               }
               else 
               {
                  float a = Float.valueOf(expr1.getSymbol());
                  float b = Float.valueOf(expr2.getSymbol());
                  result = (a != b);
               }
               exprStack.push(new Token(numCode("" + result), "" + result));
            }
            else if(op.getSymbol().equals("=")) //assignment operator
            {
               exprStack.push(expr2);
               varMap.remove(expr1.getSymbol()); //stores variable in program memory for access in later lines
               varMap.put(expr1.getSymbol(), expr2);
            }
        }
        
        //prints results of lines that are not assigning varibles
        System.out.println("\nThis is line number " + lineNumber + "\n");
        if(!isAssignment)
        {
            System.out.println(exprStack.pop().getSymbol());
        }
        
        /////////////////////////////////////prints variables for test
        System.out.println("Current variables: ");
        for (String name: varMap.keySet())
        {
            String key = name.toString();
            String value = varMap.get(name).getSymbol().toString();  
            System.out.println(key + " = " + value);
        }
     
    }
    //methods to help with visualizing number codes
    boolean isOperator(Token t)
    {
      return (t.getCode() > 3001 && t.getCode() < 3013);
    }
    boolean isOperand(Token t)
    {
      return (t.getCode() > 999 && t.getCode() < 1003);
    }
    boolean isBooleanOp(Token t)
    {
      return (t.getCode() > 3002 && t.getCode() < 3009);
    }
    boolean isBoolean(Token t)
    {
      return (t.getCode() == 3000 || t.getCode() == 3001);
    }
    
    public static void main(String args[]) throws IOException
    {
        ProjectScanner s = new ProjectScanner();
        File input = new File("Inputs.txt"); //replace with txt location using appropriate escape characters
        BufferedReader reader = new BufferedReader(new FileReader(input)); //////////////////////input source code
        StringBuilder stringBuilder;
        String buffer;
        int lineCount = 0;
        boolean read = true;

        while(read == true) //continues while there is something to read
        {
            lineCount++;
            buffer = reader.readLine(); //reads one line
            if(!(buffer == null)) //if nothing can be read then stop
            {
                stringBuilder = new StringBuilder(buffer.length()); //make builder large enough for the source line
                stringBuilder.append(buffer); //Fill builder with source code
                buffer = ""; //clear buffer for later
                String content = stringBuilder.toString(); //read source code line becomes a string
                s.scanLine(content, lineCount);
                s.Parse(s.getTokenList());
            }
            else
            {
                read = false;
            }
        }
        reader.close();

    }
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////Scanner
    void scanLine(String line, int lineNum) //primary method
    {
        tokenList = new LinkedList<Token>(); //empty old source code
        String token;
        lineNumber = lineNum;
        line = line.trim();
        do //loop for clearing all tokens from line
        {
            token = findToken(line);
            tokenList.add(new Token(numCode(token), token.trim()));
            if(line.contains(" ")) //if one token remains
            {
                line = line.substring(token.length()+1);
            }
            else
            {
                line = ""; //wipes last token when collected
            }
        }while(line.length() != 0); //if the source code line is empty
        //////////////////////////////////////////
        /*
        System.out.println("Line Number: " + lineNumber);
        for(int i = 0; i < tokenList.size(); i++)
        {
            Token t = tokenList.get(i); /////////////////line 70-77 for test
            t.print();
        }*/
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public static String findToken(String line) //cuts token out of source code line
    {
        //start from line index
        for(int i = 0; i < line.length(); i++) //check for spaces
        {
            if(line.charAt(i) == ' ')
            {
                return line.substring(0,i);
            }
        }
        return line;
    }

    int isKeyword(String token) //lookup feature to check keywords list
    {
        for(int i = 0; i < keywords.length; i++)
        {
            if(token.equals(keywords[i]))
            {
                return i;
            }
        }
        return -1;
    }

    int numCode(String token) //number code to define token
    {
        int code;
        if((code = isKeyword(token)) > -1)
        {
            return code + 3000; //specific keyword from list
        }
        if(Character.isLetter(token.charAt(0)))
        {
            return IDENTIFIER; //identifier
        }
        if(token.charAt(0) == '"' && token.charAt(token.length()-1) == '"')
        {
            return STRING; //string literal
        }
        try //attempt to make an int
        {
            Integer.parseInt(token);
            return INT; //int
        }
        catch(NumberFormatException e)
        {
        }
        try //attempt to make a float
        {
            Float.parseFloat(token);
            return FLOAT; //float
        }
        catch(NumberFormatException e)
        {
        }
        return -1;
    }

    int getLine() //returns source code line number
    {
        return lineNumber;
    }
}

class Token
{
    private int numCode;
    private String symbol;
    Token(int code, String val)
    {
        numCode = code; //saves number code
        symbol = val; //saves the symbol scanned
    }
    int getCode()
    {
        return numCode;
    }
    String getSymbol()
    {
        return symbol;
    }
    void print() //////////////////////////////////////////////////////for testing only
    {
        System.out.println("Symbol Scanned: " + symbol);
        System.out.println("NumberCode: " + numCode + "\n");
    }
}