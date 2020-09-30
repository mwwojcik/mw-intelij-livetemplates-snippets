package mw.livetemplate.pfrom;
import com.intellij.codeInsight.template.macro.MacroBase;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

class FromMethodMacro extends MacroBase{
    public FromMethodMacro() {
        super("fromMethod", "fromMethod()");
    }

    /**
     * Strictly to uphold contract for constructors in base class.
     */
    private FromMethodMacro(String name, String description) {
        super(name, description);
    }

    @Override
    protected Result calculateResult(@NotNull Expression[] params, ExpressionContext context, boolean quick) {


        context.getPsiElementAtStartOffset();
        return new TextResult(text);
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        // Might want to be less restrictive in future
        return true;
    }
}
