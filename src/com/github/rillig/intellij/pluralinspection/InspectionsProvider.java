package com.github.rillig.intellij.pluralinspection;

import com.intellij.codeInspection.InspectionToolProvider;

public class InspectionsProvider implements InspectionToolProvider {
    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{PluralCollectionNamesInspection.class};
    }
}
