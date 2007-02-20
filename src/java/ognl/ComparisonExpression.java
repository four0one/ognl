/**
 * 
 */
package ognl;

import ognl.enhance.UnsupportedCompilationException;


/**
 *  Base class for types that compare values.
 */
public abstract class ComparisonExpression extends BooleanExpression
{

    public ComparisonExpression(int id) {
        super(id);
    }

    public ComparisonExpression(OgnlParser p, int id) {
        super(p, id);
    }
    
    public abstract String getComparisonFunction();
    
    public String toGetSourceString(OgnlContext context, Object target)
    {
        try {
            
            Object value = getValueBody(context, target);
            
            if (value != null && Boolean.class.isAssignableFrom(value.getClass()))
                _getterClass = Boolean.TYPE;
            else if (value != null)
                _getterClass = value.getClass();
            else
                _getterClass = Boolean.TYPE;
            
            // iterate over children to make numeric type detection work properly
            
            OgnlRuntime.getChildSource(context, target, _children[0]);
            
            OgnlRuntime.getChildSource(context, target, _children[1]);
            
            //System.out.println("comparison expression currentType: " + context.getCurrentType() + " previousType: " + context.getPreviousType());
            
            boolean conversion = OgnlRuntime.shouldConvertNumericTypes(context);
            
            String result = conversion ? getComparisonFunction() + "(" : "";
            
            result += OgnlRuntime.getChildSource(context, target, _children[0], conversion) 
            + " " 
            + (conversion ? ", " : getExpressionOperator(0)) + " " 
            + OgnlRuntime.getChildSource(context, target, _children[1], conversion);
            
            result += conversion ? ")" : "";
            
            return result;
        } catch (NullPointerException e) {
            
            // expected to happen in some instances
            
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        } catch (Throwable t) {
            if (UnsupportedCompilationException.class.isInstance(t))
                throw (UnsupportedCompilationException)t;
            else
                throw new RuntimeException(t);
        }
    }
}