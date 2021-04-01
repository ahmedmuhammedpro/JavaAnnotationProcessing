package com.iti.pizzafactory.processor;

import com.iti.pizzafactory.annotations.Factory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class holds all {@link FactoryAnnotatedClass}s that belongs to one factory. In other words
 * this class holds a list with all @Factory annotated classes. This class also checks if the id of
 * each @Factory annotated class is unique.
 */
public class FactoryGroupedClasses {

    /** Will be added to the name of the generated factory class. */
    private static final String SUFFIX = "Factory";

    private final String qualifiedClassName;
    private Map<String, FactoryAnnotatedClass> itemsMap =
            new LinkedHashMap<>();

    public FactoryGroupedClasses(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    /**
     * Adds an annotated class to this factory.
     *
     * @param toInsert the item to be added
     * @throws ProcessingException if another annotated class with the same id is
     * already present.
     */
    public void add(FactoryAnnotatedClass toInsert) throws ProcessingException {
        FactoryAnnotatedClass existing = itemsMap.get(toInsert.getId());
        if (existing != null) {
            // Already existing
            throw new ProcessingException(toInsert.getTypeElement(),
                    "Conflict: the class %s is annotated with @%s with id = '%s' but %s already use the same id",
                    toInsert.getTypeElement().getQualifiedName().toString(), Factory.class.getSimpleName(),
                    toInsert.getId(), existing.getTypeElement().getQualifiedName());
        }

        itemsMap.put(toInsert.getId(), toInsert);
    }

    public void generateCode(Elements elementUtil, Filer filer) throws IOException {
        TypeElement superClassName = elementUtil.getTypeElement(qualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        String qualifiedFactoryClassName = qualifiedClassName + SUFFIX;
        PackageElement pkg = elementUtil.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(superClassName.asType()));

        // Check if id is null
        method.beginControlFlow("if (id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        // Generate items map
        if (!itemsMap.isEmpty()) {
            Iterator<FactoryAnnotatedClass> iterator = itemsMap.values().iterator();
            FactoryAnnotatedClass firstItem = iterator.next();
            method.beginControlFlow("if (id.equals($S))", firstItem.getId())
                    .addStatement("return new $L()", firstItem.getTypeElement().getQualifiedName().toString());

            while (iterator.hasNext()) {
                FactoryAnnotatedClass item = iterator.next();
                method.nextControlFlow("else if (id.equals($S))", item.getId())
                        .addStatement("return new $L()", item.getTypeElement().getQualifiedName().toString());
            }

            method.nextControlFlow("else")
                    .addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ")
                    .endControlFlow();
        }

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName).addMethod(method.build()).build();

        // Write file
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);

    }

}
