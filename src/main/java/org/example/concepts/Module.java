package org.example.concepts;

public abstract class Module extends AbstractEntity {

    protected Module(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    // Module-specific abstract builder
    public static abstract class ModuleBuilder<T extends Module, B extends ModuleBuilder<T, B>>
            extends AbstractEntity.AbstractBuilder<T, B> {
        public abstract T build();
    }

    public abstract Patient processData(Patient patient);
}

