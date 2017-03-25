package com.github.rillig.intellij.pluralinspection;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ui.ListTable;
import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.psi.*;
import com.intellij.util.containers.OrderedSet;
import com.siyeh.ig.ui.UiUtils;
import java.util.Locale;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

public class PluralCollectionNamesInspection extends BaseJavaLocalInspectionTool {

    public OrderedSet<String> collectionTypes = new OrderedSet<>(Arrays.asList(
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

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        ListTable table = new ListTable(new ListWrappingTableModel(collectionTypes, "Collection types"));
        return UiUtils.createAddRemoveTreeClassChooserPanel(table, "Choose collection type");
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

    private void checkPlural(PsiIdentifier name, PsiType type, @NotNull ProblemsHolder holder) {
        String strName = name.getText();
        if (strName != null && !isPluralName(strName) && isCollection(type))
            holder.registerProblem(name, "Collection variable name should be plural");
    }

    private boolean isPluralName(String name) {
        String lcname = name.toLowerCase(Locale.ROOT);
        return lcname.endsWith("s")
                || lcname.endsWith("list")
                || lcname.endsWith("set")
                || lcname.endsWith("map");
    }

    private boolean isCollection(PsiType type) {
        if (!(type instanceof PsiClassType))
            return false;

        String className = type.getCanonicalText().replaceFirst("<.*", "");
        return collectionTypes.contains(className);
    }
}
