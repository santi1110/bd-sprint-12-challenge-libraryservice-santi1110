package com.bloomtech.library.services;

import com.bloomtech.library.models.checkableTypes.Checkable;

public class NonExistentType extends Checkable {
    public NonExistentType(String isbn, String title) {
        super(isbn, title);
    }
}
