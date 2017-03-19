package com.github.rillig.intellij.pluralinspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PluralCollectionNamesInspection extends BaseJavaLocalInspectionTool {

    private static final Set<String> COLL_CLASSES = new HashSet<>(Arrays.asList(
            "java.util.Collection",
            "java.util.List",
            "java.util.Map",
            "java.util.Set",
            "com.google.common.collect.ImmutableList",
            "com.google.common.collect.ImmutableMap",
            "com.google.common.collect.ImmutableSet"));

    @NotNull
    @Override
    public String[] getGroupPath() {
        return new String[]{"Java", "Code style issues"};
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.STYLE_GROUP_NAME;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Plural names for collection variables";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "PluralCollectionNames";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitVariable(PsiVariable variable) {
                super.visitVariable(variable);
                checkPlural(variable.getNameIdentifier(), variable.getType(), holder);
            }
        };
    }

    private static void checkPlural(PsiIdentifier name, PsiType type, @NotNull ProblemsHolder holder) {
        String strName = name.getText();
        if (strName != null && !strName.endsWith("s") && isCollection(type))
            holder.registerProblem(name, "Collection variable name should be plural");
    }

    private static boolean isCollection(PsiType type) {
        if (!(type instanceof PsiClassType))
            return false;

        String className = type.getCanonicalText().replaceFirst("<.*", "");
        return COLL_CLASSES.contains(className);
    }
}
