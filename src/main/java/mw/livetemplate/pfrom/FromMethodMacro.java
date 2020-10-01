package mw.livetemplate.pfrom;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.codeInsight.template.TextResult;
import com.intellij.codeInsight.template.macro.MacroBase;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.source.PsiParameterImpl;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

class FromMethodMacro extends MacroBase {

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
        var text = "hello";

        var parent = context.getPsiElementAtStartOffset().getParent();

        if (!(parent instanceof PsiClass)) {
            return new TextResult("");
        }

        var current = (PsiClass) parent;

        var fields = current.getFields();

        if (fields == null) {
            return new TextResult("");
        }

        var constructors = current.getConstructors();

        if (constructors == null || constructors.length == 0) {
            return new TextResult("");
        }

        var constructor = constructors[0];

        var parameters = constructor.getParameters();

        if (parameters == null || parameters.length == 0) {
            return new TextResult("");
        }

        var className = current.getName();

        var builder = new FromMethodBuilder().withClassName(className);

        for (JvmParameter parameter : parameters) {
            builder.withParam(Param.from(parameter.getName(),((PsiParameterImpl) parameter).getTypeElement().getType().getCanonicalText()));
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

class FromMethodBuilder {

    private String className;
    private List<Param> params=new ArrayList<>();
    StringBuilder str = new StringBuilder("");

    FromMethodBuilder withClassName(String className) {
        this.className = className;
        return this;
    }

    FromMethodBuilder withParam(Param param) {
        params.add(param);
        return this;
    }

    public String build(){
        var declarations = params.stream().map(it -> it.declaration()).collect(Collectors.joining(","));
        var names = params.stream().map(it -> it.name()).collect(Collectors.joining(","));
        return new FromMethodTemplate().build(className, declarations, names);
    }

}

class Param {

    private String name;
    private String type;

    public Param(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static Param from(String n, String t) {
        return new Param(n, t);
    }

    public String declaration() {
        return new String(type + " " + name);
    }

    public String name() {
     return name ;
     }


}

class FromMethodTemplate{
    static StringBuilder from=new StringBuilder();

    static String build(String className, String paramDecl,String names){
        InputStream resourceAsStream = FromMethodTemplate.class.getClassLoader().getResourceAsStream("FromTemplate.java");
        try {
            var content = new String(resourceAsStream.readAllBytes());
            System.out.println(content);

            content=content.replaceAll("CLASSNAME",className);
            content=content.replaceAll("PARAMS_DECLARATION",paramDecl);
            content=content.replaceAll("PARAMNAMES",names);
            return content;

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}