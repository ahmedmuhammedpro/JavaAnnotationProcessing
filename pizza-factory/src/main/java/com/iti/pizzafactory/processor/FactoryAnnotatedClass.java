package com.iti.pizzafactory.processor;

import com.iti.pizzafactory.annotations.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Holds the information about a class annotated with @Factory
 */
public class FactoryAnnotatedClass {

    private final TypeElement annotatedClassElement;
    private String qualifiedGroupClassName;
    private String simpleFactoryGroupName;
    private final String id;

    public FactoryAnnotatedClass(TypeElement classElement) throws ProcessingException {
        annotatedClassElement = classElement;
        Factory annotation = classElement.getAnnotation(Factory.class);
        id = annotation.id();

        if (isEmpty(id)) {
            throw new ProcessingException(classElement,
                    "id() in @%s for class %s is null or empty! that's not allowed",
                    Factory.class.getSimpleName(), classElement.getQualifiedName().toString());
        }

        // Get the full QualifiedTypeName
        try {
            Class<?> clazz = annotation.type();
            qualifiedGroupClassName = clazz.getCanonicalName();
            simpleFactoryGroupName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedGroupClassName = classTypeElement.getQualifiedName().toString();
            simpleFactoryGroupName = classTypeElement.getSimpleName().toString();
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Get the id as specified in {@link Factory#id()}.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Get the full qualified name of the type specified in {@link Factory#type()}
     * @return qualified name
     */
    public String getQualifiedFactoryGroupName() {
        return qualifiedGroupClassName;
    }

    /**
     * Get the simple name if the type specified in {@link Factory#type()}
     * @return simple name
     */
    public String getSimpleFactoryGroupName() {
        return simpleFactoryGroupName;
    }

    /**
     * The original element that was annotated with @Factory
     * @return the element annotated with @Factory
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }

}
