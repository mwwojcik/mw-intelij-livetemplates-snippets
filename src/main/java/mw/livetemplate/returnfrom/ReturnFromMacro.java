package mw.livetemplate.returnfrom;

import com.intellij.codeInsight.template.*;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.codeInsight.template.macro.MacroUtil;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiParameterImpl;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

class ReturnFromMacro extends MacroBase {

    public ReturnFromMacro() {
        super("returnFrom", "returnFrom()");
    }

    /**
     * Strictly to uphold contract for constructors in base class.
     */
    private ReturnFromMacro(String name, String description) {
        super(name, description);
    }

    @Override
    protected Result calculateResult(@NotNull Expression[] params, ExpressionContext context, boolean quick) {
        var text = "hello";


        var parent = context.getPsiElementAtStartOffset().getParent().getParent();

        if (!(parent instanceof PsiMethod)) {
            return new TextResult("");
        }

        var current = (PsiMethod) parent;

        var returnType = current.getReturnType();

        if (returnType == null) {
            return new TextResult("");
        }

      /*  Method fromMethod = null;
        try {
            fromMethod = returnType.getClass().getMethod("from");
        } catch (NoSuchMethodException e) {
            return new TextResult("");
        }*/

        var methods = PsiTypesUtil.getPsiClass(returnType).getMethods();

        if (Objects.isNull(methods)||methods.length==0) {
            return new TextResult("");
        }
        var from = Arrays.stream(methods).filter(it -> it.getName().equals("from")).findFirst();

        if(from.isEmpty()){
            return new TextResult("");
        }

        PsiMethod fromMethod=from.get();

        var parameters = fromMethod.getParameters();

        if (parameters == null || parameters.length == 0) {
            return new TextResult("");
        }


        var typeName = returnType.getCanonicalText();

        var builder = new ReturnFromBuilder().withTypeName(typeName);

        for (JvmParameter parameter : parameters) {
            builder.withParam(Param.from(((PsiParameterImpl) parameter).getTypeElement().getType().getCanonicalText(),((PsiParameterImpl) parameter).getTypeElement().getType().getCanonicalText()));
        }

        text =builder.build();

        return new TextResult(text);
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        // Might want to be less restrictive in future
        return true;
    }
}


class ReturnFromBuilder {

    private String typeName;
    private List<Param> params=new ArrayList<>();
    StringBuilder str = new StringBuilder("");

    ReturnFromBuilder withTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    ReturnFromBuilder withParam(Param param) {
        params.add(param);
        return this;
    }

    public String build(){
        var expressions = params.stream().map(it -> it.expression()).collect(Collectors.joining(","));
        return new ReturnFromTemplate().build(typeName, expressions);
    }

}

class Param {

    private String expression;
    private String type;

    private Param(String expression, String type) {

        this.expression = expression;
        this.type = type;
    }

    public static Param from(
            String expression, String type){
            return new Param(expression,type);
        }


    String type(){
        return type;
    }

    String expression(){
        return expression;
    }
}

class ReturnFromTemplate{
    static StringBuilder from=new StringBuilder();

    static String build(String typename, String types){
        InputStream resourceAsStream = ReturnFromTemplate.class.getClassLoader().getResourceAsStream("ReturnFromTemplate.java");
        try {
            var content = new String(resourceAsStream.readAllBytes());

            content=content.replaceAll("TYPENAME",typename);
            content=content.replaceAll("PARAMETERS",types);
            return content;

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
