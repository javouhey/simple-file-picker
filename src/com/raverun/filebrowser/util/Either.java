package com.raverun.filebrowser.util;

import java.io.Serializable;

/**
 * Either is an expedient way to combine two arbitrary objects into one. <b>Its
 * use is not to be preferred</b> to creating a custom type, but it can be handy
 * in a pinch. If your {@link #equals}, {@link #hashCode}, {@link #toString},
 * and/or {@link #clone} behavior matter to you at all, you may not want to use
 * this (or you may at least wish to subclass).
 * <p>
 */
public class Either<A, B> implements Serializable, Cloneable {

    private static final long serialVersionUID = 747826592375603043L;

    /** Creates a new pair containing the given objects in order. */
    public static <A, B> Either<A, B> of(final A first, final B second) {
        return new Either<A, B>(first, second);
    }

    /** The first element of the pair. */
    public final A first;
    /** The second element of the pair. */
    public final B second;

    /** Cumbersome way to create a new pair (see {@link #of}. */
    public Either( final A first, final B second ) {
        this.first = first;
        this.second = second;
    }

    /** Optional accessor method for {@link #first}. */
    public A getFirst() {
        return first;
    }

    /** Optional accessor method for {@link #second}. */
    public B getSecond() {
        return second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Either<A, B> clone() {
        try {
            return (Either<A, B>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);

        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof Either<?, ?>) {
            final Either<?, ?> other = (Either<?, ?>) object;
            return eq(first, other.first) && eq(second, other.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        /*
         * This combination yields entropy comparable to constituent hashes and
         * ensures that hash(a,b) != hash(b,a).
         */
        return (hash(first, 0) + 0xDeadBeef) ^ hash(second, 0);
    }

    /**
     * This implementation returns the String representation of this pair in the
     * form {@code (string1, string2)}, where {@code string1} and {@code
     * string2} are the {@link Object#toString} representations of the elements
     * inside the pair.
     */
    @Override
    public String toString() {
        return String.format("(%s, %s)", first, second);
    }

    private static boolean eq(final Object a, final Object b) {
        return a == b || (a != null && a.equals(b));
    }

    private static int hash(final Object object, final int nullHash) {
        return (object == null) ? nullHash : object.hashCode();
    }
}
