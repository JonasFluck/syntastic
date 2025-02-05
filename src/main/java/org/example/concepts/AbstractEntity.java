package org.example.concepts;

public abstract class AbstractEntity {

    // Protected constructor to enforce use of Builder
    protected AbstractEntity(AbstractBuilder<?, ?> builder) {
        // Initialization logic
    }

    // Abstract builder class
    public static abstract class AbstractBuilder<T extends AbstractEntity, B extends AbstractBuilder<T, B>> {
        protected abstract B self(); // Method to return 'this' for chaining
        public abstract T build(); // Method to construct the entity
    }
}


