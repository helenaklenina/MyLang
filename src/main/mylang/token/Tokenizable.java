package main.mylang.token;

import java.util.List;

import main.mylang.exception.NotImplementedException;

public interface Tokenizable {
    List<Token> tokenize() throws NotImplementedException;
}
