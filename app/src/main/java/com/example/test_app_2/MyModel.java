package com.example.test_app_2;

public class MyModel {
    String foreignWords, nativeWords;

    public MyModel(String foreignWords, String nativeWords) {
        this.foreignWords = foreignWords;
        this.nativeWords = nativeWords;
    }

    public String getForeignWords() {
        return foreignWords;
    }

    public void setForeignWords(String foreignWords) {
        this.foreignWords = foreignWords;
    }

    public String getNativeWords() {
        return nativeWords;
    }

    public void setNativeWords(String nativeWords) {
        this.nativeWords = nativeWords;
    }
}
