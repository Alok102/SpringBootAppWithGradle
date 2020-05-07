package com.alok.parser;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MethodDetails {
    @NotBlank
    private String name;

    @NotBlank
    private String fqcnClass;

    @PositiveOrZero
    private long startLine;

    @PositiveOrZero
    private long endLine;

    private List<String> parameters = Collections.emptyList();

    public MethodDetails() {
    }

    public MethodDetails(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFqcnClass() {
        return fqcnClass;
    }

    public void setFqcnClass(String fqcnClass) {
        this.fqcnClass = fqcnClass;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {

        if(parameters != null && !parameters.isEmpty())
            this.parameters = new LinkedList<>(parameters);
        else
            this.parameters= Collections.emptyList();
    }

    public long getStartLine() {
        return startLine;
    }

    public void setStartLine(long startLine) {
        this.startLine = startLine;
    }

    public long getEndLine() {
        return endLine;
    }

    public void setEndLine(long endLine) {
        this.endLine = endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDetails that = (MethodDetails) o;
        return startLine == that.startLine &&
                endLine == that.endLine &&
                name.equals(that.name) &&
                fqcnClass.equals(that.fqcnClass) &&
                parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fqcnClass, startLine, endLine, parameters);
    }

    @Override
    public String toString() {
        return "MethodDetails{" +
                "name='" + name + '\'' +
                ", fqcnClass='" + fqcnClass + '\'' +
                ", startLine=" + startLine +
                ", endLine=" + endLine +
                ", parameters=" + parameters +
                '}';
    }
}

